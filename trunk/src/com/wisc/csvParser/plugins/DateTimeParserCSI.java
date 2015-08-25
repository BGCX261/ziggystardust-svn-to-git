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
import java.awt.Dialog;
import java.util.Date;
import org.jdom.*;
import java.util.Calendar;
/**
 *
 * @author lawinslow
 */
public class DateTimeParserCSI implements IDateTimeParser {
    
    public static final String PARSER_NAME = "CSIYEAR_DAYNUM_TYPE";
    public static final String YEAR_INDEX_TAG = "YearIndex";
    public static final String DAYNUM_INDEX_TAG = "DaynumIndex";
    public static final String TIME_INDEX_TAG = "TimeIndex";
    public static final String SECONDS_INDEX_TAG = "SecondsIndex";
    
    private int yearIndex;
    private int daynumIndex;
    private int timeIndex;
    private int secondsIndex;
    
    private int tmpYear;
    private int tmpDaynum;
    private int tmpTime;
    private double tmpSeconds;
    private Calendar cal = Calendar.getInstance();
    /** Creates a new instance of DateTimeParserCSI */
    
    public DateTimeParserCSI(){
        yearIndex = 1;
        daynumIndex = 2;
        timeIndex = 3;
        secondsIndex = 4;
    }
    public DateTimeParserCSI(Element configXml)throws Exception {
        configure(configXml);
    }
    public void configure(Element configXml)throws Exception{
        /*   XML Input should look like this. Index is zero based.
        *   <DateTimeFormat type="CSIYEAR_DAYNUM_TYPE">
        *        <YearIndex>1</YearIndex> 
        *        <DaynumIndex>2</DaynumIndex> 
        *        <TimeIndex>3</TimeIndex> 
        *    </DateTimeFormat>
        */ 
       
        
        yearIndex = Integer.parseInt(
                    configXml.getChild(YEAR_INDEX_TAG).getText());
        daynumIndex = Integer.parseInt(
                    configXml.getChild(DAYNUM_INDEX_TAG).getText());
        if(configXml.getChild(TIME_INDEX_TAG) != null){
            timeIndex = Integer.parseInt(
                    configXml.getChild(TIME_INDEX_TAG).getText());
        }else{
            timeIndex = -1;
        }
        if(configXml.getChild(SECONDS_INDEX_TAG) != null) {
            secondsIndex = Integer.parseInt(
                configXml.getChild(SECONDS_INDEX_TAG).getText());
        }else{
            secondsIndex = -1;
        }


    }
    public Element getSettingsXml(){
        Element e = new Element(IDateTimeParser.DATE_TIME_FORMAT_TAG)
                .setAttribute("type", DateTimeParserCSI.class.getName());
        e.addContent(new Element(YEAR_INDEX_TAG)
                .setText(Integer.toString(yearIndex)));
        e.addContent(new Element(DAYNUM_INDEX_TAG)
                .setText(Integer.toString(daynumIndex)));
        if(timeIndex >=0){
            e.addContent(new Element(TIME_INDEX_TAG)
                    .setText(Integer.toString(timeIndex)));
        }
        if(secondsIndex >=0){
            e.addContent(new Element(SECONDS_INDEX_TAG)
                    .setText(Integer.toString(secondsIndex)));
        }
        return e;
        
        
        
    }
    
    public Date ParseDate(String[] row) throws NumberFormatException {
        //First reset temporary objects
        cal.clear();
        tmpYear = 0;
        tmpDaynum = 0;
        tmpTime = 0;
        tmpSeconds = 0;
        
        //parse year, daynum, and if defined, time
        tmpYear = Integer.parseInt(row[yearIndex]);
        tmpDaynum = Integer.parseInt(row[daynumIndex]);
        if(timeIndex != -1)
            tmpTime = Integer.parseInt(row[timeIndex]);
        if(secondsIndex != -1)
            tmpSeconds = Double.parseDouble(row[secondsIndex]);
        cal.set(tmpYear,0,0,(int)(tmpTime /100.0),tmpTime%100,(int)tmpSeconds);
        cal.add(Calendar.DAY_OF_YEAR,tmpDaynum);

        //Below adds the fraction of the seconds field to the cal up to the ms
        int tmp = (int) tmpSeconds;
        double fraction = tmpSeconds - tmp;
        double ms = fraction*1000;
        int milli = (int) ms;
        cal.add(Calendar.MILLISECOND,milli);

        return cal.getTime();
        
    }
    @Override
    public String getParserShortname(){
        return "Year,daynum,time Parser,seconds";
    }
    public String getParserDescription(){
        return "Parses the 4 digit year, julian day, HHmm format output " +
                "used by many Campbell Scientific Dataloggers, a decimal seconds" +
                " value. " +
                "Must specify year,daynum,optional time index, and seconds.";
    }

    public int getDaynumIndex() {
        return daynumIndex;
    }

    public void setDaynumIndex(int daynumIndex) {
        this.daynumIndex = daynumIndex;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public int getYearIndex() {
        return yearIndex;
    }

    public void setYearIndex(int yearIndex) {
        this.yearIndex = yearIndex;
    }

    public int getSecondsIndex() {
        return secondsIndex;
    }

    public void setSecondsIndex(int secondsIndex) {
        this.secondsIndex = secondsIndex;
    }

    public Dialog getSettingsDialog() {
        return new JDialogDateParserCSIArray(null,true,this);
    }
    
}
    
