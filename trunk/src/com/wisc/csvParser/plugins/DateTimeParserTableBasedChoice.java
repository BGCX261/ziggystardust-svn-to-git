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
 * @author glocke-ou
 */
public class DateTimeParserTableBasedChoice implements IDateTimeParser {

    public static final String PARSER_NAME = "CSI_TABLEBASED_CHOICE_CSV";
    public static final String YEAR_INDEX_TAG = "YearIndex";
    public static final String MONTH_INDEX_TAG = "MonthIndex";
    public static final String DAY_INDEX_TAG = "DayIndex";
    public static final String HOUR_INDEX_TAG = "HourIndex";
    public static final String MINUTES_INDEX_TAG = "MinutesIndex";
    public static final String SECONDS_INDEX_TAG = "SecondsIndex";

    private int yearIndex;
    private int monthIndex;
    private int dayIndex;
    private int hourIndex;
    private int minutesIndex;
    private int secondsIndex;

    private double tmpYear;
    private double tmpMonth;
    private double tmpDay;
    private double tmpHour;
    private double tmpMinutes;
    private double tmpSeconds;
    private Calendar cal = Calendar.getInstance();
    /** Creates a new instance of DateTimeParserCSI */

    public DateTimeParserTableBasedChoice() {
        yearIndex = 0;
        monthIndex = 1;
        dayIndex = 2;
        hourIndex = 3;
        minutesIndex = 4;
        secondsIndex = -1;
    }

    public DateTimeParserTableBasedChoice(Element configXml)throws Exception {
        configure(configXml);
    }

    public void configure(Element configXml)throws Exception{
        /*   XML Input should look like this. Index is zero based.
        *   <DateTimeFormat type="CSI_TABLEBASED_CHOICE_CSV">
        *        <YearIndex>0</YearIndex>
        *        <MonthIndex>1</MonthIndex>
        *        <DayIndex>2</DayIndex>
        *        <HourIndex>3</HourIndex>
        *        <MinutesIndex>4</MinutesIndex>
        *        <SecondsIndex>-1</SecondsIndex>
        *    </DateTimeFormat>
        */


        yearIndex = Integer.parseInt(
                    configXml.getChild(YEAR_INDEX_TAG).getText());
        monthIndex = Integer.parseInt(
                    configXml.getChild(MONTH_INDEX_TAG).getText());
        dayIndex = Integer.parseInt(
                    configXml.getChild(DAY_INDEX_TAG).getText());
        if(configXml.getChild(HOUR_INDEX_TAG) != null){
            hourIndex = Integer.parseInt(
                    configXml.getChild(HOUR_INDEX_TAG).getText());
        }else{
            hourIndex = -1;
        }
        if(configXml.getChild(MINUTES_INDEX_TAG) != null){
            minutesIndex = Integer.parseInt(
                    configXml.getChild(MINUTES_INDEX_TAG).getText());
        }else{
            minutesIndex = -1;
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
                .setAttribute("type", DateTimeParserTableBasedChoice.class.getName());
        e.addContent(new Element(YEAR_INDEX_TAG)
                .setText(Integer.toString(yearIndex)));
        e.addContent(new Element(MONTH_INDEX_TAG)
                .setText(Integer.toString(monthIndex)));
        e.addContent(new Element(DAY_INDEX_TAG)
                .setText(Integer.toString(dayIndex)));
        if(hourIndex >=0){
            e.addContent(new Element(HOUR_INDEX_TAG)
                    .setText(Integer.toString(hourIndex)));
        }
        if(minutesIndex >=0){
            e.addContent(new Element(MINUTES_INDEX_TAG)
                    .setText(Integer.toString(minutesIndex)));
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
        tmpMonth = 0;
        tmpDay = 0;
        tmpHour = 0;
        tmpMinutes = 0;
        tmpSeconds = 0;

        //parse year, month, day, and if defined, hour, minutes and seconds
        tmpYear = Double.parseDouble(row[yearIndex]);
        tmpMonth = Double.parseDouble(row[monthIndex]);
        tmpDay = Double.parseDouble(row[dayIndex]);
        if(hourIndex != -1)
            tmpHour = Double.parseDouble(row[hourIndex]);
        if(minutesIndex != -1)
            tmpMinutes = Double.parseDouble(row[minutesIndex]);
        if(secondsIndex != -1)
            tmpSeconds = Double.parseDouble(row[secondsIndex]);

        cal.set((int)tmpYear,(int)tmpMonth,(int)tmpDay,(int)tmpHour,
                (int)tmpMinutes,(int)tmpSeconds);
        // Months indexed from zero
        cal.add(cal.MONTH, -1);
        
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
        return "Year,month,day,hour,minutes,seconds Parser";
    }

    public String getParserDescription(){
        return "Parses the 4 digit year, month, day of month, HHmm format output " +
                "used by many Campbell Scientific Dataloggers, a decimal seconds" +
                " value. " +
                "Must specify year,month,day,optional hour and minutes index, and seconds.";
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int daynumIndex) {
        this.dayIndex = daynumIndex;
    }

    public int getHourIndex() {
        return hourIndex;
    }

    public void setHourIndex(int hourIndex) {
        this.hourIndex = hourIndex;
    }

    public int getMinutesIndex() {
        return minutesIndex;
    }

    public void setMinutesIndex(int minutesIndex) {
        this.minutesIndex = minutesIndex;
    }

    public int getYearIndex() {
        return yearIndex;
    }

    public void setYearIndex(int yearIndex) {
        this.yearIndex = yearIndex;
    }

    public int getMonthIndex() {
        return monthIndex;
    }

    public void setMonthIndex(int monthIndex) {
        this.monthIndex = monthIndex;
    }

    public int getSecondsIndex() {
        return secondsIndex;
    }

    public void setSecondsIndex(int secondsIndex) {
        this.secondsIndex = secondsIndex;
    }
    
    public Dialog getSettingsDialog() {
        return new JDialogDateTimeParserTableBasedChoice(null,true,this);
    }
}
