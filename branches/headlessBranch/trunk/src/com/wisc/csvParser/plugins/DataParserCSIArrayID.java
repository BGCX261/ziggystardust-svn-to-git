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

import com.wisc.csvParser.*;
import org.jdom.*;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author lawinslow
 */
public class DataParserCSIArrayID implements IDataParser {
    
    public enum events{
        started,
        stopped,
        reconfigured,
        newMalformedRow,
        startDataParse,
        stopDataParse,
        finishDataParse,
        rowParsed
    }
            

    public static final String SOURCE_FILE_TAG = "SourceFile";
    public static final String ARRAY_ID_INDEX_TAG = "ArrayIDIndex";
    public static final String ARRAY_ID_GROUP_TAG = "ArrayID";
    public static final String DELIMITER = ",";
    public static final int COLUMN_MISMATCH_LIMIT = 2;
    private String dataFilePath;
    private int arrayIDIndex;
    private Hashtable<Integer, ArrayIDObject> arrayIdCollection =
            new Hashtable<Integer, ArrayIDObject>();
    
    private Timer timer;
    private long lastModified = 0;
    private int lastRowParsed = 0;
    private long rowsParsedTotal = 0;
    private JPanelCSIArrayID statusPanel;
    private Hashtable<Integer, String> malformedRows = new Hashtable<Integer, String>();
    private ArrayList<CSIArrayEventHandler> eventHandlers =
            new ArrayList<CSIArrayEventHandler>();
    
    private String panelID;
    private IDataRepository myRepository;
    private Logger logger = Logger.getLogger(DataParserCSIArrayID.class.getName());

    public DataParserCSIArrayID() {
        panelID = this.getParserShortname() + 
            Integer.toString((new java.util.Random()).nextInt());
    }

    /** Creates a new instance of DataParserCSIArrayID */
    public DataParserCSIArrayID(Element configXml) throws Exception {
        this();
        configure(configXml);
    }

    public DataParserCSIArrayID(Element configXml, IDataRepository repo) throws Exception {
        this();
        configure(configXml);
        setRepository(repo);
    }

    @Override
    public void configure(Element configXml) throws Exception {
        dataFilePath = configXml.getChild(SOURCE_FILE_TAG).getText();
        arrayIDIndex = Integer.parseInt(
                configXml.getChild(ARRAY_ID_INDEX_TAG).getText());


        List<Element> ids = configXml.getChildren(ARRAY_ID_GROUP_TAG);
        for (Element e : ids) {
            arrayIdCollection.put(e.getAttribute("id").getIntValue(),
                    new ArrayIDObject(e));
        }
        raiseEvent(events.reconfigured);

    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataParser.DATA_PARSER_TAG);
        e.setAttribute("type",DataParserCSIArrayID.class.getName());
        e.addContent(new Element(SOURCE_FILE_TAG).setText(dataFilePath));
        e.addContent(new Element(ARRAY_ID_INDEX_TAG).setText(Integer.toString(arrayIDIndex)));
        for (ArrayIDObject arid : arrayIdCollection.values()) {
            e.addContent(arid.getSettingsXml());
        }
        return e;
    }

    @Override
    public String getParserShortname() {
        return "DataParserCSIArrayID";
    }

    @Override
    public String getParserDescription() {
        return "ToDo: add description here.";
    }

    @Override
    public JPanel getStatusJPanel() {
        if(statusPanel==null){

             statusPanel = new JPanelCSIArrayID(this);
        }

        return statusPanel;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }
    
    public Hashtable<Integer,String> getMalformedRows(){
        return malformedRows;
    }

    @Override
    public boolean Start() {
        
        if(myRepository != null && myRepository.Start()){
            timer = new Timer();
            timer.scheduleAtFixedRate(
                    new TimerTask(){
                    @Override
                    public void run(){
                        checkFile();
                    }
                }, 0, 30000);
            raiseEvent(events.started);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean Stop() {
        if(myRepository!=null){
            return StopLocal() && myRepository.Stop();            
        }else{
            return StopLocal();
        }

            
    }
    
    private boolean StopLocal(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;                   
        }
        raiseEvent(events.stopped);
        return true;
    }

    @Override
    public boolean isStarted() {
        return timer != null;
    }

    @Override
    public void setRepository(IDataRepository repo) {
        myRepository = repo;
    }

    @Override
    public IDataRepository getRepository() {
        return myRepository;
    }

    @Override
    public String toString() {
        return getParserShortname();
    }

    public void checkFile() {
        
        raiseEvent(events.startDataParse);
        if (!fileExists() || !fileModified() || fileBeingActivelyWritten()) {
            raiseEvent(events.stopDataParse);
            return;
        }
        
        //Do NOT run if this module isn't 'started'. User must have clicked stop
        if(!isStarted()){
            raiseEvent(events.stopDataParse);
            return;
        }

        try {
            parseNewFileContent();
        } catch (Exception e) {
            if(e instanceof ColumnNumberMismatchException){
                ColumnNumberMismatchException cnme = 
                        (ColumnNumberMismatchException)e;
                statusPanel.showMessageDialog("Excessive Column Mismatch \n" +
                        "Row Num:"+Integer.toString(cnme.lineNumber) + 
                        " FileName:"+cnme.fileName);
                
            }else{
                e.printStackTrace();
                statusPanel.showMessageDialog("Unknown error occured" +
                        " while parsing text file.");
            }
        }finally{
            raiseEvent(events.stopDataParse);
        }
    }

    private boolean fileExists() {
        return new File(dataFilePath).exists();
    }

    private boolean fileModified() {
        File toWatch = new File(dataFilePath);
        return toWatch.lastModified() > lastModified;
        //ideas for accessing file creation date
        // could use JNI or FileTimes library that rrk downloaded
        // java doesn't have a method for accessing file creation date
    }

    private boolean fileBeingActivelyWritten() {

        long length = new File(dataFilePath).length();
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
        //do nothing
        }
        return length != new File(dataFilePath).length();

    }

    private void parseNewFileContent() throws Exception {

        File toRead = new File(dataFilePath);
        BufferedReader inputStream = null;

        try {
            //toRead.setWritable(false);
            inputStream = new BufferedReader(new FileReader(toRead));


            int currentRow = 0;
            String inLine;
            String[] splitLine;
            int tmpArrayId;
            int colMalformNum = 0;

            while ((inLine = inputStream.readLine()) != null) {
                //If this module switches started state, interrupt 
                // parsing process. User must have stopped chain.
                if(!this.isStarted()){
                    break;
                }
                
                //Quickly skip rows that were already done.
                if (currentRow < lastRowParsed) {
                    currentRow++;
                    continue;
                } else {
                    currentRow++;
                }


                try {//Try parsing the individual lines
                    splitLine = inLine.split(DELIMITER);
                    tmpArrayId = Integer.parseInt(splitLine[arrayIDIndex]);

                    if (myRepository != null) {
                        myRepository.NewRow(
                                arrayIdCollection.get(tmpArrayId).parseRow(splitLine));

                    }
                    
                    //if we get here, reset colMismatchNum to zero.
                    rowsParsedTotal++;
                    raiseEvent(events.rowParsed);
                    colMalformNum = 0;
                } catch (Exception e) {
                    if (e instanceof ColumnNumberMismatchException ||
                            e instanceof java.text.ParseException ||
                            e instanceof java.lang.NumberFormatException ||
                            e instanceof java.lang.NullPointerException) {
                        //In this case, we want to continue, but make note of this problem.
                        if (colMalformNum < COLUMN_MISMATCH_LIMIT) {
                            colMalformNum++;
                            malformedRows.put(currentRow, inLine);
                            raiseEvent(events.newMalformedRow);
                            continue;
                        } else {
                            ColumnNumberMismatchException exp 
                                    = new ColumnNumberMismatchException("Excessive Column Mismatch Error");
                            exp.fileName = this.getFileMonitored();
                            exp.lineNumber = currentRow;
                            throw exp;
                        }
                    } else {
                        throw e;
                    }
                }
            }//readLine while loop

            lastRowParsed = currentRow;
            lastModified = toRead.lastModified();
            
        } catch (Exception e) {
            throw e;
        } finally {
            //toRead.setWritable(true);
            if(inputStream != null){
                inputStream.close();
            }
            
        }
        toRead.setWritable(true);
    }

    //attempt parse of an individual row. nothing is caught. 
    public void parseRow(String inLine) throws Exception {

        String[] splitLine;
        int tmpArrayId;

        splitLine = inLine.split(DELIMITER);
        tmpArrayId = Integer.parseInt(splitLine[arrayIDIndex]);

        if (myRepository != null) {
            myRepository.NewRow(
                    arrayIdCollection.get(tmpArrayId).parseRow(splitLine));
        }
    }
    
        //Custom public methods
    public void reconfigure(Element configXml) throws Exception {
        if (isStarted()) {
            Stop();
        }

        lastRowParsed = 0;
        lastModified = 0;
        malformedRows.clear();
        arrayIdCollection.clear();

        configure(configXml);
    }
    
    public void setRowPointer(int newPointer){
        lastRowParsed = newPointer;
    }

    public String getFileMonitored() {
        return dataFilePath;
    }
    public void setFileMonitored(String f){
        dataFilePath = f;
        this.raiseEvent(events.reconfigured);
    }
    public long getRowsParsedTotal(){
        return rowsParsedTotal;
    }
    
    public void addEventHandler(CSIArrayEventHandler toAdd){
        eventHandlers.add(toAdd);
    }
    public void removeEventHandler(CSIArrayEventHandler toRmv){
        eventHandlers.remove(toRmv);
    }
    public void raiseEvent(events e){
        for(CSIArrayEventHandler h:eventHandlers){
            h.eventRaised(e);
        }
    }

    public void addArrayID(Integer id, DataParserCSIArrayID.ArrayIDObject idObj){
        arrayIdCollection.put(id, idObj);
        raiseEvent(events.reconfigured);
    }
    
    public void removeArrayID(Integer id){
        arrayIdCollection.remove(id);
        raiseEvent(events.reconfigured);
    }
    
    public DataParserCSIArrayID.ArrayIDObject getNewArrayIDObject(int id){
        
        return new ArrayIDObject(id);
    }
    
    public Hashtable<Integer, DataParserCSIArrayID.ArrayIDObject> getArrayIdCollection() {
        return arrayIdCollection;
    }

    public class ArrayIDObject {

        public static final String COLUMNS_TAG = "Columns";
        public int id;
        public IDateTimeParser dtParser;
        public ArrayList<DataColumn> columns = new ArrayList<DataColumn>();

        public ArrayIDObject(int id){
            dtParser = new DateTimeParserCSI();
            this.id = id;
        }
        public ArrayIDObject(Element configXml) throws Exception {
            //Create a datetime parser from the xml specifying datetime parser
            dtParser = DateTimeParserFactory.getDateTimeParser(
                    configXml.getChild(IDateTimeParser.DATE_TIME_FORMAT_TAG));

            id = Integer.parseInt(configXml.getAttributeValue("id"));
            List<Element> columnElements = configXml.getChild(COLUMNS_TAG).getChildren(DataColumn.COLUMN_TAG);

            for (int i=0;i<columnElements.size();i++) {
                columns.add(i,new DataColumn(columnElements.get(i)));
            }
        }

        public Element getSettingsXml() {
            Element e = new Element(ARRAY_ID_GROUP_TAG);
            Element columnElements = new Element(COLUMNS_TAG);
            e.setAttribute("id", Integer.toString(id));
            e.addContent(dtParser.getSettingsXml());

            for (DataColumn c : columns) {
                columnElements.addContent(c.getSettingsXml());
            }
            e.addContent(columnElements);
            return e;
        }

        public ArrayList<ValueObject> parseRow(String[] values)
                throws Exception {

            if (values.length != columns.size()) {
                throw new ColumnNumberMismatchException(
                        "Expected:" + columns.size() +
                        " Passed:" + values.length);
            }

            ArrayList<ValueObject> output = new ArrayList<ValueObject>(values.length);
            Date rowDate = dtParser.ParseDate(values);
            ValueObject tmp;
            for (int i = 0; i < values.length; i++) {
                if (!columns.get(i).ignoreColumn()) {
                    tmp = columns.get(i).getNewValue();
                    tmp.setValue(Double.valueOf(values[i]));
                    tmp.setTimeStamp(rowDate);
                    output.add(tmp);
                    tmp = null;
                }
            }
            return output;
        }
    
        @Override
        public String toString(){
            return Integer.toString(id);
        }
    }

    public interface CSIArrayEventHandler{
        public void eventRaised(DataParserCSIArrayID.events v);
    }

}
