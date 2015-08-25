/*
 *  This file is part of ZiggyStardust.
 *
 *  ZiggyStardust is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ZiggyStardust is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wisc.csvParser.plugins;

import com.wisc.csvParser.IDataParser;
import com.wisc.csvParser.IDataRepository;
import com.wisc.csvParser.ValueObject;
import javax.swing.JPanel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import org.apache.log4j.Logger;




/**
 * Parses XML files uploaded from different GLEON sites by this
 * and other software. Monitors a specific directory for new files.
 * XML must follow the format below
 * <ValueObjects>
 *  <ValueObject>XML format following
 *  <Code>ValueObject.getAsXml()</Code> method.
 * </ValueObject
 * </ValueObjects>
 *
 * @author lawinslow
 */
public class DataParserNOAAformat implements IDataParser{

    /**
     * XML Tag used in persisting settings
     */
    public static final String MONITORED_PATH_TAG = "MonitoredPath";


    /**
     * Different events this data parser can raise
     */
    public static enum events{
        fileParsed,
        valParsed,
        reconfigured,
        started,
        stopped
    }


    private Timer timer;
    private String monitoredPath;
    private String misfitFilesSubdirectory = "flawedFiles";
    private String panelID;
    private IDataRepository child;
    private JPanelNOAAformat statusPanel;
    private boolean isStarted = false;

    private long valuesParsed = 0;

    private ArrayList<IGLEONCentralHandler> handlers
            = new ArrayList<IGLEONCentralHandler>();

    private Hashtable<String,ValueObject> metaKey
            = new Hashtable<String,ValueObject>();

    private Logger logger =
            Logger.getLogger(DataParserGLEONCentral.class.getName());
    

    public DataParserNOAAformat(){
        logger.info("Building GLEON NOAA format data parser.");
        timer = new Timer();
        panelID = this.getParserShortname() +
                Integer.toString(new java.util.Random().nextInt());

    }

    public void parseFiles(){

        File[] newFiles = (new File(monitoredPath)).listFiles();
        for(File f:newFiles){
            if(!f.isDirectory())
                parseFile(f);
        }

    }
    private void parseFile(File f){

        try{
            if(f.canWrite() && !beingWritten(f)){
                SAXBuilder builder = new SAXBuilder();

                Document doc = builder.build(f);
                Element root = doc.getRootElement();
                
                SimpleDateFormat dFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                java.util.Date d = dFormat.parse(root.getChildText("date"));


                List<Element> values = root.getChild("met").getChildren();
                ValueObject tmp;
                String name;
                for(Element e:values){

                    //if we stop in the middle of parsing a file, just get out
                    // of here. Do NOT delete the file. Not fully parsed yet.
                    if(timer==null || !isStarted){
                        return;
                    }

                    //If we have the key in metadata, then clone the value and
                    // assign the value and timestamp and pass it on
                    name = e.getName();
                    if(metaKey.containsKey(name)){
                        tmp = metaKey.get(name).cloneValueless();
                        tmp.setValue(Double.parseDouble(e.getValue()));
                        tmp.setTimeStamp(d);
                    }else{
                        //If it isn't in the cache, just ignore.
                        continue;
                    }

                    if(!child.NewValue(tmp)){
                        logger.error("Child returned false, likely metadata " +
                                "error in file: " + f.getName());

                        this.sendToFlawedFilesDir(f);
                        //go on to next file
                        return;
                    }

                    valuesParsed++;
                    raiseEvent(events.valParsed);
                    if(valuesParsed%1000 == 0){
                        logger.info(valuesParsed
                                + " values have been parsed from XML files.");
                    }
                }
                //Success. Delete file.
                deleteFile(f);
            }
        }catch(JDOMException jdome){
            logger.error("Error while building XML document. " +
                    "Likely XML formatting issue.",jdome);

            this.sendToFlawedFilesDir(f);
        }catch(java.text.ParseException pe){
            logger.error("Error parsing date/time format in NOAA file.");
            this.sendToFlawedFilesDir(f);
            
        }catch(IOException ioe){
            logger.fatal("Unexpected exception:",ioe);
        }
    }

    private void sendToFlawedFilesDir(File f){
        //move file to island of misfit toys
        File newF = new File(monitoredPath + File.separator
                + misfitFilesSubdirectory + File.separator + f.getName());

        if(!newF.getParentFile().exists())
            newF.getParentFile().mkdir();

        if(f.canWrite()){//else don't move. Perhaps file still being uploaded
            logger.error("MoveFileTo:"+newF.getPath());
            f.renameTo(newF);
        }
    }

    private boolean beingWritten(File f){
        long size = f.length();
        try{
            Thread.sleep(2000);
        }catch(Exception e){
            return false;
        }

        return size != f.length();
    }

    private void deleteFile(File f){
        logger.info("Deleting xml file: " + f.getName());
        f.delete();
    }

    public String getMonitoredPath(){
        return monitoredPath;
    }

    public void setMonitoredPath(String path){
        monitoredPath = path;
        raiseEvent(events.reconfigured);
    }

    public long getCountParsed(){
        return valuesParsed;
    }

    public void raiseEvent(events e){
        for(IGLEONCentralHandler h:handlers){
            h.eventRaised(e);
        }
    }

    public void addEventHandler(IGLEONCentralHandler h){
        handlers.add(h);
    }

    @Override
    public boolean Start() {
        boolean result;
        if(child !=null && child.Start()){
            timer = new Timer();
            timer.scheduleAtFixedRate(
                    new TimerTask(){
                    @Override
                    public void run(){
                        parseFiles();
                    }
                }
                        , 10000, 10000
            );
            isStarted = true;
            raiseEvent(events.started);
            return true;
        }else{
            isStarted = false;
            return false;
        }

    }

    @Override
    public boolean Stop() {


        if(timer != null){
            timer.cancel();
        }
        timer = null;
        isStarted = false;
        raiseEvent(events.stopped);

        if(child != null){
            child.Stop();
        }
        return true;
    }

    @Override
    public void configure(Element e) throws Exception {

        monitoredPath = "C:/Users/lawinslow/Documents/test";

        ValueObject tmp = new ValueObject();
        tmp.setSource("UMBIO").setSite("UMBIO Lake").setAggMethod("INST").setAggSpan("00:00:00");
        tmp.setUtcOffset(-5);

        this.metaKey.put("rrh",tmp.setVariable("Relative Humidity").setUnit("%"));
        tmp = tmp.cloneValueless();
        this.metaKey.put("wdir1", tmp.setVariable("Wind Direction").setUnit("deg"));
        tmp = tmp.cloneValueless();
        this.metaKey.put("wspd1", tmp.setVariable("Wind Speed").setUnit("m/s"));
        tmp = tmp.cloneValueless();
        this.metaKey.put("atmp1", tmp.setVariable("Air Temperature").setUnit("C"));
        tmp = tmp.cloneValueless();
        this.metaKey.put("baro1", tmp.setVariable("Barometric Pressure").setUnit("mbar"));
        tmp = tmp.cloneValueless();
        this.metaKey.put("srad1", tmp.setVariable("Solar Radiation").setUnit("W/m^2"));




        //if(e.getChild(MONITORED_PATH_TAG)!=null){
        //    monitoredPath = e.getChildText(MONITORED_PATH_TAG);
        //}

        raiseEvent(events.reconfigured);
    }

    @Override
    public String getParserDescription() {
        return "This module parses the NOAA XML data format.";
    }

    @Override
    public String getParserShortname() {
        return "NOAA Format Parser";
    }

    @Override
    public IDataRepository getRepository() {
        return child;
    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataParser.DATA_PARSER_TAG);
        e.setAttribute(IDataParser.DATA_PARSER_TYPE_TAG,
                DataParserNOAAformat.class.getName());
        //e.addContent(new Element(MONITORED_PATH_TAG).setText(monitoredPath));
        return e;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void setRepository(IDataRepository repository) {
        child = repository;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }

    @Override
    public JPanel getStatusJPanel() {
        logger.trace("Info panel Requested.");
        return new javax.swing.JPanel();
        /*if(statusPanel==null){
            logger.trace("Building status panel object.");
            statusPanel = new JPanelNOAAformat(this);
        }
        return statusPanel;
        */
    }

    @Override
    public String toString(){
        return this.getParserShortname();
    }

    public interface IGLEONCentralHandler{
        public void eventRaised(events e);
    }



}
