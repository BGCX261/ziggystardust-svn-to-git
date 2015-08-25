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

import javax.swing.JPanel;
import org.jdom.Element;
import com.wisc.csvParser.*;
import java.util.*;
import java.net.URL;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

        

/**
 *
 * @author lawinslow
 */
public class DataParserGleonServiceXML extends GenericDataSource{

    public static final String SOURCES_TAG = "XmlSources";
    public static enum events{
        started,
        stopped,
        updated,
        valsParsed
                
    }
    private Timer t;
    private JPanelGleonXML configPanel;
    private ArrayList<XmlSource> sources = new ArrayList<XmlSource>();
    private ArrayList<IEventHandler> handlers = new ArrayList<IEventHandler>();
    private Logger logger = Logger.getLogger(DataParserGleonServiceXML.class);
    
    
    public DataParserGleonServiceXML(){
        
    }
    
    @Override
    protected boolean privateStop() {
        if(this.isStarted() && t != null){
            t.cancel();
            t = null;            
        }

        logger.info("Module Stopped");
        return true;
    }

    @Override
    protected boolean privateStart() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                retrieveDataAllSites();
            }
        
        }, 0, 1000*60*10);
        logger.info("Module Started");
        return true;
    }

    @Override
    public String getParserDescription() {
        return "Parser which periodically polls certain locations " + 
                "and downloads new data available in the Vega XML format";
    }

    @Override
    public String getParserShortname() {
        return "GLEON XML Harvester";
    }

    @Override
    public void configure(Element e) throws Exception {
        logger.trace("Configuring GLEON XML Service harvester");
        for(Element s:(List<Element>)e.getChild(SOURCES_TAG).getChildren()){
            sources.add(new XmlSource(s));
        }
        raiseEvent(events.updated);
    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataParser.DATA_PARSER_TAG);
        e.setAttribute("type",DataParserGleonServiceXML.class.getName());
        Element sourcesSettings = new Element(SOURCES_TAG);
        e.addContent(sourcesSettings);
        
        for(XmlSource s:sources){
            sourcesSettings.addContent(s.getSettingsXml());
        }
        
        return e;
    }

    @Override
    public JPanel getStatusJPanel() {
        if(configPanel==null){
            logger.trace("Lazilyl initiate GLEONXML Status Panel.");
            configPanel = new JPanelGleonXML(this);
        }
        return configPanel;
    }
    
    private void retrieveDataAllSites(){
        for(XmlSource s:sources){
            s.tryRetrieval();
        }
    }
    
    
    public void addSource(String name, String url){
        logger.info("Adding source name:"+name);
        sources.add(new XmlSource(name,url));
        raiseEvent(events.updated);
    }
    
    public ArrayList<XmlSource> getSources(){
        return sources;
    }
    
    
    @Override
    public String toString(){
        return this.getParserShortname();
    }
    
    public interface IEventHandler{
        public void eventRaised(events e);
    }
    
    public void addEventHandler(IEventHandler handler){
        handlers.add(handler);
    }
    public void removeEventHandler(IEventHandler handler){
        handlers.remove(handler);
    }
    public void raiseEvent(events e){
        for(IEventHandler i:handlers){
            i.eventRaised(e);
        }
    }
    
    public class XmlSource{
        public static final String XML_SOURCE_TAG = "XmlSource";
        public static final String SOURCE_NAME_TAG = "SourceName";
        public static final String SOURCE_URL_TAG = "SourceUrl";
        public static final String LAST_VAL_DATE_TAG = "LastValueDate";
        private final int MAX_REQUEST_LENGTH = 1;
        private String url;
        private String sourceName;
        private Date lastDateRetrieved;
        
        private Date asyncStart;
        private Date asyncEnd;
        
        public XmlSource(Element e){
            this.configure(e);
            
        }
        public XmlSource(String name,String url){
            sourceName = name;
            this.url = url;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -10);
            lastDateRetrieved = cal.getTime();
        }
        
        public Element getSettingsXml(){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Element e = new Element(XML_SOURCE_TAG);
            e.addContent(new Element(SOURCE_NAME_TAG).setText(sourceName));
            e.addContent(new Element(SOURCE_URL_TAG).setText(url));
            e.addContent(new Element(LAST_VAL_DATE_TAG)
                    .setText(format.format(lastDateRetrieved)));
            
            return e;
        }
        
        public void configure(Element e){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sourceName = e.getChildText(SOURCE_NAME_TAG);
            url = e.getChildText(SOURCE_URL_TAG);
            try{
                lastDateRetrieved = format.parse(e.getChildText(LAST_VAL_DATE_TAG));
            }catch(java.text.ParseException pe){
                // on error, just set to default
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -10);
                lastDateRetrieved = cal.getTime();
            }
        }
        
        
        /**
         * Generic retrieval call. Gets data up until 3 days in the past.
         * 
         */
        public void tryRetrieval(){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-3);
            tryRetrievalCustom(lastDateRetrieved,cal.getTime());
        }
        
        /**
         * Attemps retrieval of data from supplied start date
         * to supplied end date. Does it on separate thread
         * 
         * @param start Earliest data to retrieve
         * @param end Latest data to retrieve, cannot be more than 2 days after start
         */
        public void tryRetrievalCustomAsync(Date start, Date end){
            asyncStart = start;
            asyncEnd = end;
            t.schedule(new TimerTask(){

                @Override
                public void run() {
                    tryRetrievalCustom(asyncStart,asyncEnd);
                }
                
            }, 100);//start in 1/10 of a second
            
        }
        
        /**
         * Attemps retrieval of data from supplied start date
         * to supplied end date.
         * @param start Earliest data to retrieve
         * @param end Latest data to retrieve, cannot be more than 2 days after start
         * @return Number of values retrieved
         */
        public int tryRetrievalCustom(Date start, Date end){
            
            start = new Date(start.getTime());
            end = new Date(end.getTime());
            
            int counter = 0;
            Date tempEnd = new Date();
            tempEnd.setTime(start.getTime() + 1000*60*60*24*MAX_REQUEST_LENGTH);
            
            while(end.getTime() - tempEnd.getTime() > 0){
                counter = counter + internalRetrievalCustom(start,tempEnd);
                
                tempEnd.setTime(tempEnd.getTime() + 1000*60*60*24*MAX_REQUEST_LENGTH);
                start.setTime(start.getTime() + 1000*60*60*24*MAX_REQUEST_LENGTH);
            }
            return counter;
            
        }
        
        
        private int internalRetrievalCustom(Date start, Date end){

            SAXBuilder sax = new SAXBuilder();
            SimpleDateFormat format = 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat yylFormat = 
                    new SimpleDateFormat("yyyy-MM-dd");
            Document doc = null;
            
            logger.info("Attempt retrieval between start:"+format.format(start)
                    +" end:"+format.format(end));
            
            try{
                doc = sax.build(new URL(url +
                        "?start=" + format.format(start)+ 
                        "&end=" + format.format(end) +
                        "&sampledate=" + yylFormat.format(start)));
            }catch(Exception e){
                e.printStackTrace();
                return 0;
            }
            
            try{
                if(doc.getRootElement().getChild("Values").getChildren().size() <=0){
                    return 0;
                }

                  
                Hashtable<String,ValueObject> metadataList =
                        new Hashtable<String,ValueObject>();


                //TODO: Remove ability to handle n MetaDataList tags.
                // YYL for some reason has more than one. 
                for(Object e:doc.getRootElement().getChildren("MetaDataList")){
                    metadataList.putAll(parseMetadataXml((Element)e));
                }

                
                //go through and parse all values
                // counters are useful in debugging
                int valCounter = 0;
                int noKeyCounter = 0;
                int noValueCounter = 0;
                Element el;
                String key;
                Date timestamp;
                double value;
                ValueObject newVal = null;
                
                List<Object> vals = doc.getRootElement().getChild("Values").getChildren();
                
                logger.info("Retrieved "+vals.size()+" values for "+sourceName);
                
                for(Object e:vals){
                    el = (Element)e;
                    key = el.getChildText("MetaDataKey").toLowerCase();
                    
                    //TODO:YYL Threw null pointer exception here, why?
                    // Hmm, MetaDataKey is empty sometimes, need to talk to 
                    // hsiu-mei
                    if(key == null || key.compareToIgnoreCase("")==0){
                        noKeyCounter +=1;
                        continue;
                    }

                    //null pointer exeption with YYL data here?
                    try{
                        newVal = metadataList.get(key).cloneValueless();
                    }catch(NullPointerException ex){
                        ex.printStackTrace();
                        System.out.println("key:"+key);
                        System.out.println(metadataList==null);
                        System.out.println(key==null);
                        //metadata list is null? Hmm
                        System.out.println(metadataList.get(key)==null);
                    }
                    timestamp = format.parse(el.getChildText("TimeStamp"));
                    //Value may be empty, just ignore right now,
                    // may add some sort of 'empty value' flag or indicator
                    if(el.getChildText("Value").compareToIgnoreCase("") == 0){
                        noValueCounter +=1;
                        continue;
                    }
                        
                    value = Double.parseDouble(el.getChildText("Value"));
                    if(timestamp.compareTo(lastDateRetrieved) > 0){
                        lastDateRetrieved = timestamp;
                    }
                    
                    newVal.setValue(value);
                    newVal.setTimeStamp(timestamp);
                    
                    getRepository().NewValue(newVal);
                    
                    valCounter = valCounter + 1;
                }
                
                logger.info("Parsed Vals at "+sourceName+":" + valCounter);
                logger.info("No value count:" + noValueCounter);
                logger.info("No key count:"+noKeyCounter);
                return valCounter;
                
            }catch(Exception e){
                e.printStackTrace();

                return 0;
            }
        }

        private Hashtable<String,ValueObject> parseMetadataXml(Element metadataColl){

                Hashtable<String,ValueObject> metadataList =
                        new Hashtable<String,ValueObject>();

                //parse all the metadata
                for(Object e:metadataColl.getChildren()){
                    Element el = (Element)e;
                    ValueObject val = new ValueObject();

                    //These are all required values
                    val.setAggMethod(el.getChildText("AggregationMethod"));
                    val.setAggSpan(el.getChildText("AggregationSpan"));
                    val.setUtcOffset(Double.parseDouble(el.getChildText("UTCOffset")));
                    val.setSite(el.getChildText("Site"));
                    val.setSource(el.getChildText("Source"));
                    val.setUnit(el.getChildText("Unit"));
                    val.setVariable(el.getChildText("Variable"));


                    if(el.getChild("OffsetType")!= null && el.getChild("OffsetValue")!=null){
                        val.setOffsetType(el.getChildText("OffsetType"));
                        val.setOffsetValue(Double.parseDouble(el.getChildText("OffsetValue")));
                    }

                    //One small exception for Balaton
                    // TODO: Remove all exceptions
                    if(val.getUnit().compareToIgnoreCase("deg C")==0){
                        val.setUnit("C");
                    }

                    metadataList.put(el.getChildText("Key").toLowerCase(), val);


                }
                return metadataList;
        }
        
        @Override
        public String toString(){
            return sourceName;
        }
            
        
        
    }
    
}
