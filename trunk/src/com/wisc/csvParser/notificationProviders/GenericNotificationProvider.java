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

package com.wisc.csvParser.notificationProviders;


import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Hashtable;

/**
 *
 * @author law
 */
public abstract class GenericNotificationProvider implements INotificationProvider {
    private int PARSE_INTERVAL = 60000;
    //Linked list queue of unparsed notification events. 
    private LinkedList<NotificationEvent> eventQueue = new LinkedList<NotificationEvent>();
    //Hashtable of unique events to check before sending new notification
    private Hashtable<String,NotificationEvent> uniqueEvents = 
            new Hashtable<String,NotificationEvent>();
    
    private Timer t;//Timer that executes periodic event parsing

    @Override
    public void newEvent(NotificationEvent event) {
        eventQueue.offer(event);
    }
    
    private synchronized void parseQueuedEvents(){
        NotificationEvent tmp;
        while(eventQueue.peek() != null){
            tmp = eventQueue.poll();
            
            if(uniqueEvents.containsKey(tmp.eventEntity + tmp.eventType)){
                
                //Check that it has been 24 hours since this type of event
                // for this entity was detected. If so, replace cached entity
                // and send notification
                if(tmp.eventDetectionDateTime.getTime() - 
                    uniqueEvents.get(tmp.eventEntity + tmp.eventType)
                    .eventDetectionDateTime.getTime()  
                    > 24*60*60*1000){
                            uniqueEvents.put(tmp.eventEntity + tmp.eventType, tmp);
                            sendEventMessage(tmp);
                }
            }else{
                uniqueEvents.put(tmp.eventEntity + tmp.eventType, tmp);
                sendEventMessage(tmp);
            }
        }
    }
    
    protected abstract void sendEventMessage(NotificationEvent event);
    
    @Override
    public boolean start(){
        t = new Timer();
        
        //anonymous timertask class
        TimerTask parseTask = new TimerTask(){
            public void run(){
                parseQueuedEvents();
            }
        };
        
        t.scheduleAtFixedRate(parseTask, 0, PARSE_INTERVAL);
        return true;
    }
    
    @Override
    public boolean stop(){
        t.cancel();
        parseQueuedEvents();
        return true;
    }

}
