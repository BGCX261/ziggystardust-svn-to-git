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
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jdom.Element;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import com.wisc.csvParser.notificationProviders.*;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;


/**
 *
 * @author user
 */
public class DataFilterStreamMonitor implements IDataRepository,IProviderUser{

    /**
     * Tag used to surround streams state in XML
     */
    public static String STREAMS_TAG = "Streams";
    /**
     * Tag used to store individual stream state in XML
     */
    public static String SINGLE_STREAM_TAG = "StreamInfo";
    /**
     * Tag used to store last data arrival date/time
     */
    public static String LAST_TIME_SEEN_TAG = "lastseendatetime";
    /**
     * 
     */
    public static String SITE_NAME_TAG = "site";
    public static String AVERAGE_ARRIVAL_TAG = "avgarrivalinterval";
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    /**
     * collect and store 30 last seen intervals. This takes a long time
     * to fill for things that collect once per day (30 days of course) but
     * is almost too short for things that collect every 2 minutes. May have to
     * make dynamic in the future
     */
    public static int LAST_SEEN_RUNNING_AVE_COUNT = 30;
    /**
     * Default starting 'last data arrival interval'. use 3 days
     * as that is the longest interval I've heard of in the GLEON network
     * (likely wouldn't work for manually collected data)
     */
    public static long LAST_SEEN_MILLS_SEED = 3*24*60*60*1000; 
    /**
     * if we haven't seen data in (last time seen) + (leeway)*(ave interval)
     * then we can say it's been too long. Tried 2, many false positives
     * 3.25 now default, should catch if we miss more than three times
     */
    public static double LAST_SEEN_LEEWAY = 3.25;
    /**
     * 
     */
    public static String NOT_SEEN_EVENT_TYPE = "Expected new data interval exceeded";
    
    /**
     * Enumeration of events this module has
     */
    public static enum events{
        sitesChanged,
        newDataSeen,
        started,
        stopped
    }
    
    private String panelID;
    private JPanelStreamMonitor statusPane;
    private IDataRepository child;
    private Timer t;
    private INotificationProvider notificationProvider;
    private Map<String,Date> lastTimeSeen = Collections.synchronizedMap(new HashMap<String,Date>());
    private List<IEventListener> listeners = new ArrayList<IEventListener>();
    private Logger logger = Logger.getLogger(DataFilterStreamMonitor.class.getName());
    
    //stores the values for the running average
    // needs to be synchronized as we've got multiple threads going on here. (checkStream thread)
    private Map<String,long[]> lastCollectionIntervalArrays = 
            Collections.synchronizedMap(new HashMap<String,long[]>());
    
    //pointer to the next location to fill in the running average array
    // needs to be synchronized as we've got multiple threads going on here. (checkStream thread)
    private Map<String,Integer> lastTimeSeenPointer = 
            Collections.synchronizedMap(new HashMap<String,Integer>());
    
    public DataFilterStreamMonitor(){
        logger.trace("Building new DataFilter object");
        
        panelID = getRepositoryShortname() + 
                Integer.toString((new java.util.Random()).nextInt());
        

        
    }
    
    @Override
    public String getRepositoryDescription() {
        return "Monitors incoming streams and sends notification " +
                "when data do not arrive within expected timeframe.";
    }

    @Override
    public String getRepositoryShortname() {
        return "Stream Monitor";
    }

    @Override
    public void configure(Element e) throws Exception {
        
        if(e.getChild(STREAMS_TAG) == null){
            return;
        }
        
        List<Element> cachedStreams = e.getChild(STREAMS_TAG).getChildren(SINGLE_STREAM_TAG);
        for(Element stream:cachedStreams){
            String siteName = stream.getChildText(SITE_NAME_TAG);
            long[] arrivalTimes = new long[LAST_SEEN_RUNNING_AVE_COUNT];
            Date lastSeenDate;
            SimpleDateFormat frmt = new SimpleDateFormat(DATE_FORMAT);
            
            lastSeenDate = frmt.parse(stream.getChildText(LAST_TIME_SEEN_TAG));
            
            Arrays.fill(arrivalTimes,
                    Long.parseLong(stream.getChildText(AVERAGE_ARRIVAL_TAG)));
            
            lastTimeSeen.put(siteName.toLowerCase(), lastSeenDate);
            lastCollectionIntervalArrays.put(siteName.toLowerCase(), arrivalTimes);
            lastTimeSeenPointer.put(siteName.toLowerCase(), 0);
            
        }
        this.raiseEvent(events.sitesChanged);
    }
    
    @Override
    public Element getSettingsXml() {
        logger.trace("Return XML formatted settings and state.");
        Element e = new Element(IDataRepository.DATA_REPOSITORY_TAG);
        e.setAttribute("type",DataFilterStreamMonitor.class.getName());
        
        Element streams = new Element(STREAMS_TAG);
        e.addContent(streams);
        
        Date tmpLastDate;
        SimpleDateFormat frmt = new SimpleDateFormat(DATE_FORMAT);
        
        for(String site:lastTimeSeen.keySet()){
            Element streamInfo = new Element(SINGLE_STREAM_TAG);
            
            tmpLastDate = lastTimeSeen.get(site);
            long avg = 0;
            for(long l:lastCollectionIntervalArrays.get(site)){
                avg += l;
            }
            avg = avg/LAST_SEEN_RUNNING_AVE_COUNT;            
            
            streamInfo.addContent((new Element(SITE_NAME_TAG)).addContent(site));
            streamInfo.addContent((new Element(LAST_TIME_SEEN_TAG))
                    .addContent(frmt.format(tmpLastDate)));
            streamInfo.addContent((new Element(AVERAGE_ARRIVAL_TAG))
                    .addContent(Long.toString(avg)));
            
            streams.addContent(streamInfo);
        }
        
        return e;
    }

    @Override
    public boolean Start() {
        if(child.Start()){
            t = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run() {
                    checkStreams();
                }
                
            };
            t.schedule(task, 60000*120, 60000);
            raiseEvent(events.started);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean Stop() {
        child.Stop();
        if(t!= null){
            t.cancel();            
        }

        t = null;
        raiseEvent(events.stopped);
        return true;
    }

    @Override
    public boolean isStarted() {
        return t != null;
    }

    @Override
    public boolean NewRow(ArrayList<ValueObject> newRow) {
        for(int i = 0;i<newRow.size();i++){
            internalNewValue(newRow.get(i));
        }
        if(child!=null){
            return child.NewRow(newRow);
        }
        return true;
    }

    @Override
    public boolean NewValue(ValueObject newValue) {
        internalNewValue(newValue);
        if(child != null){
            return child.NewValue(newValue);
        }
        return true;
    }

    @Override
    public IDataRepository getChildRepository() {
        return child;
    }

    @Override
    public void setChildRepository(IDataRepository child) {
        this.child = child;
    }

    @Override
    public JPanel getStatusJPanel() {
        if(statusPane==null){
            logger.trace("Lazily create stream monitor status panel.");
            statusPane = new JPanelStreamMonitor(this);
        }
        return statusPane;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }
    
    @Override
    public String toString(){
        return this.getRepositoryShortname();
    }
    
    private void checkStreams(){
        logger.trace("Checking for AWOL streams.");
        // if we haven't seen data in 'last time seen' plus 'leeway'*'ave interval'
        //then we can say it's been too long
        for(String s:lastCollectionIntervalArrays.keySet()){
            long intervalAve = 0;
            long leewayIntervalAve = 0;
            for(long l:lastCollectionIntervalArrays.get(s)){
                intervalAve += l;
            }
            intervalAve = (intervalAve/LAST_SEEN_RUNNING_AVE_COUNT);
            leewayIntervalAve = Math.round(intervalAve * LAST_SEEN_LEEWAY);
            
            long timeSinceLast = (new Date()).getTime() - lastTimeSeen.get(s).getTime();
            
            if((new Date()).getTime() > (lastTimeSeen.get(s).getTime()
                    + leewayIntervalAve)){
                //we have not seen new values in the expected plus leeway time
                // send a notification
                NotificationEvent evnt = new NotificationEvent();
                evnt.eventDateTime = new Date();
                evnt.eventDetectionDateTime = new Date();
                evnt.eventEntity = s;
                evnt.eventType = NOT_SEEN_EVENT_TYPE;
                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(1);
                evnt.eventMessage = "New data have not arrived in " +
                        format.format(timeSinceLast/1000.0/60) + " minutes. " +
                        "On average, data from " + s + " are " +
                        "expected every " + format.format(intervalAve/60.0/1000.0) + " " +
                        "minutes.";
                //for(long l:lastCollectionIntervalArrays.get(s)){
                //    evnt.eventMessage += "\n" + Double.toString(l/1000.0/60.0) + " min";
                //}
                notificationProvider.newEvent(evnt);
                
                logger.trace("New Event:\n" + evnt.eventMessage);
               
            }
        }
        
    }
    

    
    
    
    private void internalNewValue(ValueObject val){
        //This saves the timestamp of the last time a value
        // from each site passed through this module. This of course
        // doesn't check the timestamp of the value
        if(lastTimeSeen.get(val.getSite().toLowerCase()) != null){
            
            if((new Date()).getTime() - 
                    lastTimeSeen.get(val.getSite().toLowerCase()).getTime()  > 60000){
                
                //do a bunch of stuff here, we have a new round of data
                logger.info("We have a new round of data from a site. Add new" +
                    " interval for " + val.getSite());
                
                //get the time since we last saw data
                long timeBetween = (new Date()).getTime() - 
                        lastTimeSeen.get(val.getSite().toLowerCase()).getTime();
                
                //set the 'last seen' to now
                lastTimeSeen.put(val.getSite().toLowerCase(), new Date());
                
                //set the next array location to the amount of time since we saw 
                // the last value
                lastCollectionIntervalArrays.get(val.getSite().toLowerCase())[
                        lastTimeSeenPointer.get(val.getSite().toLowerCase())] 
                        = timeBetween;
                if(lastTimeSeenPointer.get(val.getSite().toLowerCase()) + 1 == 
                        LAST_SEEN_RUNNING_AVE_COUNT){
                    //if adding one will take us past the end of the array
                    // just set to zero
                    lastTimeSeenPointer.put(val.getSite().toLowerCase(), 0);
                }else{
                    //otherwise just increment
                    lastTimeSeenPointer.put(val.getSite().toLowerCase(),
                            lastTimeSeenPointer.get(val.getSite().toLowerCase())+1);
                }
                
                raiseEvent(events.newDataSeen);
                
            }else{
                //not really a new round of data, just some more in the same round
                // or at least that's what we'll assume, just update 'lastTimeSeen'
                lastTimeSeen.put(val.getSite().toLowerCase(),new Date());
            }
        }else{//We must not have ever seen data from this site before
            logger.info("Never seen data for " + val.getSite() + ", initialize" +
                    " important variables.");
            
            
            //initialize all maps to default values
            lastTimeSeen.put(val.getSite().toLowerCase(), new Date());
            long[] runAve = new long[LAST_SEEN_RUNNING_AVE_COUNT];
            Arrays.fill(runAve, LAST_SEEN_MILLS_SEED);
            lastCollectionIntervalArrays.put(val.getSite().toLowerCase(), runAve);
            lastTimeSeenPointer.put(val.getSite().toLowerCase(),0);
            
            raiseEvent(events.sitesChanged);
        }
    }

    @Override
    public void setNotificationProvider(INotificationProvider provider) {
        notificationProvider = provider;
    }
    
    public void raiseEvent(events e){
        for(IEventListener el:listeners){
            el.raiseEvent(e);
        }
    }
    
    public void addEventListener(IEventListener listen){
        listeners.add(listen);
    }
    
    public void removeEventListener(IEventListener listen){
        listeners.remove(listen);
    }
    
    public Vector<String> getSites(){
        Vector<String> sites = new Vector<String>(lastTimeSeen.keySet());
        return sites;
    }
    
    public void removeSite(String site){
            lastTimeSeen.remove(site);
            lastCollectionIntervalArrays.remove(site);
            lastTimeSeenPointer.remove(site);
            raiseEvent(events.sitesChanged);
    }
    
    public interface IEventListener{
        public void raiseEvent(events e);
    }

}
