/*
 * value.java
 *
 * Created on April 15, 2007, 6:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wisc.VegaLibrary;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.TimeZone;
import java.util.Calendar;
import java.sql.*;
import com.wisc.csvParser.ValueObject;
import org.apache.log4j.Logger;


/**
 *
 * @author lawinslow
 */
public class Value {
    //private Connection conn;
    private Source source;
    private Site site;
    private Variable variable;
    private int method;
    private OffsetType offsettype;
    private AggMethod aggmethod;
    private AggSpan aggspan;
    private OffsetValue offsetvalue;
    private double value;
    private int utcoffset;
    private java.util.Date date;
    private Stream stream;
    private Unit unit;
    
    //This is for some record keeping and some user feedback
    private static long newValues = 0;
    private static long duplicates = 0;
    
    private static Logger logger = Logger.getLogger(Value.class.getName());
            
    public Value(String valXml,java.util.Date dateI,double valueI,Connection conn){
        
        DocumentBuilder builder;
        Document doc;
        NodeList nl;
        
        //populate the two supplied variables that come directly from RBNB
        value = valueI;
        date = dateI;
        
        try{
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            //java.io.InputStream is = new java.io.InputStream();
            java.io.InputStream is = new java.io.ByteArrayInputStream(valXml.getBytes());
            doc = builder.parse(is);
            
        }catch(Exception e){
            System.out.println("XML Attempt:"+valXml);
            System.out.println("XML Parse Error:"+e.getMessage());
            return;
        }
        
        //get the UTCOffset, should be an integer
        nl = doc.getElementsByTagName("UTCOffset");
        utcoffset = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
        try{
            //get the Site, should be a string
            nl = doc.getElementsByTagName("Site");
            site = Site.getSite(nl.item(0).getFirstChild().getNodeValue(),conn);
            
            //get the Variable, should be a string
            nl = doc.getElementsByTagName("Variable");
            variable = Variable.getVariable(nl.item(0).getFirstChild().getNodeValue(),conn);
            
            nl = doc.getElementsByTagName("Unit");
            unit = Unit.getUnit(nl.item(0).getFirstChild().getNodeValue(),conn);
            
            //get the offset Value, should be a double
            nl = doc.getElementsByTagName("Offset");
            
            if(nl.getLength() < 1 || nl.item(0).getFirstChild() == null){
                offsetvalue = new OffsetValue(null);
            }else{
                offsetvalue = new OffsetValue(nl.item(0).getFirstChild().getNodeValue());
            }
            
            //get the aggregation method and span
            nl = doc.getElementsByTagName("AggMeth");
            aggmethod = aggmethod.getAggMethod(nl.item(0).getFirstChild().getNodeValue(),conn);
            
            nl = doc.getElementsByTagName("AggSpan");
            
            aggspan = new AggSpan(nl.item(0).getFirstChild().getNodeValue());
            
            
            nl = doc.getElementsByTagName("OffsetType");
            if(nl.getLength() < 1 || nl.item(0).getFirstChild() == null){
                offsettype = OffsetType.getOffsetType(null,conn);
            }else{
                offsettype = OffsetType.getOffsetType(nl.item(0).getFirstChild().getNodeValue(),conn);
            }
            
            nl = doc.getElementsByTagName("Source");
            source = Source.getSource(nl.item(0).getFirstChild().getNodeValue(),conn);
            
            nl = doc.getElementsByTagName("Sensor");
            if(nl.getLength() < 1 || nl.item(0).getFirstChild() == null){
                method = Integer.MIN_VALUE;
            }else{
                try{
                    method = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
                }catch(Exception e){
                    method = Integer.MIN_VALUE;
                    System.out.println("Invalid Sensor ID:"+nl.item(0).getFirstChild().getNodeValue());
                }
            }
            
        }catch(ItemNotInDbException ex){
            System.out.println(ex.getMessage());
            return;
        }
        
        stream = Stream.getStream(site,variable,method,source,offsettype,offsetvalue,aggmethod,aggspan,unit,1,conn);
        
        
        try{
            
            Timestamp ts = new Timestamp(date.getTime());
            String tmz;
            if(utcoffset<0){
                tmz = "GMT" + Integer.toString(utcoffset);
            }else{
                tmz = "GMT+"+Integer.toString(utcoffset);
            }
            
            java.text.SimpleDateFormat hack = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            TimeZone tz = TimeZone.getTimeZone(tmz);
            Calendar cal = Calendar.getInstance(tz);
            
            hack.setTimeZone(tz);
            PreparedStatement newVal = conn.prepareStatement("INSERT INTO `values`(" +
                    "value,datetime,utcoffset,streamID) VALUES(?,'"+hack.format(date)+"',?,?)");
            newVal.setDouble(1,value);
            
            
            //newVal.setDate(2,new java.sql.Date(date.getTime()),cal);
            
            //newVal.setTimestamp(2,ts,cal);
            newVal.setInt(2,utcoffset);
            newVal.setInt(3,stream.getID());
            try{
                newVal.execute();
                
                newValues+=1;
                if(newValues%1000 == 0)
                    System.out.println(site.getName()+" Number new: "+newValues+" Date: "+ts.toString());
                //System.out.println("New Value");
                //System.out.println("Site:"+site.getName()+" Variable:"+variable.getName());
            }catch(SQLException sx){
                duplicates += 1;
                if(duplicates % 1000 == 0)
                    System.out.println(site.getName()+" Number duplicates: "+duplicates+" Date: "+ts.toString());
                
                //System.out.println("Value Exists");
                //System.out.println("Site:"+site.getName()+" Variable:"+variable.getName());
                newVal.close();
            }
            
            newVal.close();
            
        }catch(Exception e){
            System.out.println("Original XML is missing Stream data: "+valXml);
            System.out.println(e.getMessage());
        }
        conn = null;
    }//Value constructor
    
    public static synchronized void insertValue(ValueObject val,Connection conn) throws ItemNotInDbException{
        if(Double.compare(val.getValue(), Double.NaN)==0)
            return;
        
        Source source;
        Site site;
        Variable variable;
        Unit unit;
        AggMethod aggmethod;
        AggSpan aggspan;
        double utcoffset;
        Stream stream;
        
        int sensorID;
        OffsetType offsettype;
        OffsetValue offsetvalue;
        int repID;

        //get the required fields first.
        utcoffset = val.getUtcOffset();
        site = Site.getSite(val.getSite(),conn);
        unit = Unit.getUnit(val.getUnit(),conn);
        variable = Variable.getVariable(val.getVariable(),conn);
        source = Source.getSource(val.getSource(),conn);
        aggmethod = AggMethod.getAggMethod(val.getAggMethod(),conn);
        aggspan = new AggSpan(val.getAggSpan());
        
        
        offsetvalue = new OffsetValue(val.getOffsetValue());
        offsettype = OffsetType.getOffsetType(val.getOffsetType(),conn);
        sensorID = val.getSensorID();
        repID = val.getDuplicateID();
        
        if(VegaVersionInfo.dbVersion > 1){
            stream = Stream.getStream(site,variable,sensorID,source,
                    offsettype,offsetvalue,aggmethod,aggspan,unit,repID,conn);
        }else{
            stream = Stream.getStream(site,variable,sensorID,source,
                    offsettype,offsetvalue,aggmethod,aggspan,unit,1,conn);
        }
        
        try{
            
            Timestamp ts = new Timestamp(val.getTimeStamp().getTime());
            String tmz;
            if(utcoffset<0){
                tmz = "GMT" + Double.toString(utcoffset);
            }else{
                tmz = "GMT+"+ Double.toString(utcoffset);
            }
            
            java.text.SimpleDateFormat hack = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            TimeZone tz = TimeZone.getTimeZone(tmz);
            Calendar cal = Calendar.getInstance(tz);
            
            //THis does HORRIBLE things. I have almost never figured
            // out this timezone thing. So painful!!!
            //hack.setTimeZone(tz);
            PreparedStatement newVal;
            if(VegaVersionInfo.dbVersion > 1){
                newVal = conn.prepareStatement("INSERT INTO `values`(" +
                        "value,datetime,streamid) VALUES(?,'"+
                        hack.format(val.getTimeStamp())+"',?)");
            }else{
                newVal = conn.prepareStatement("INSERT INTO `values`(" +
                        "value,datetime,streamid,utcoffset) VALUES(?,'"+
                        hack.format(val.getTimeStamp())+"',?,?)");
            }
            
            
            newVal.setDouble(1,val.getValue());
            newVal.setInt(2,stream.getID());
            if(VegaVersionInfo.dbVersion==1){
                newVal.setDouble(3,utcoffset);
            }
            
            try{
                newVal.execute();
                
                newValues+=1;
                if(newValues%1000 == 0)
                    if(logger.isInfoEnabled())
                        logger.info(site.getName()+" Number new: "
                                +newValues+" Date: "+ts.toString());
            }catch(SQLException sx){
                //if(sx.getMessage().contains("for key 2")){
                    //if(logger.isDebugEnabled()){
                        //logger.debug("Duplicate at site:" + site.getName());
                        //logger.debug("and var:"+variable.getName());
                    //}
                //}
                
                duplicates += 1;
                if(duplicates % 1000 == 0)
                    if(logger.isInfoEnabled())
                        logger.info(site.getName()+" Number duplicates: "+duplicates+" Date: "+ts.toString());
                
                newVal.close();
            }
            
            newVal.close();
            
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        conn = null;
        
    }

    public static long getNewValueCount(){
        return newValues;
    }
    public static long getDuplicateValueCount(){
        return duplicates;
    }
}//Class


