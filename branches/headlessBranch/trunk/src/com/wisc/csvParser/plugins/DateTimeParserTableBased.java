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
import java.text.SimpleDateFormat;
import java.*;

/**
 *
 * @author jwyuen, Ryan, lawinslow
 */
public class DateTimeParserTableBased implements IDateTimeParser {
    
    public static final String PARSER_NAME = "CSI_TABLEBASED_CSV";
    /**
     * XML Tag for the date/time format string
     */
    public static final String FORMAT_STRING_TAG = "FormatString";
    /**
     * XML tag to store date/time column index
     */
    public static final String DATE_TIME_COLUMN_INDEX_TAG = "DateTimeColumnIndex";
    
    private String formatString;
    private int columnIndex;
    SimpleDateFormat dateFormatter;
    
    public DateTimeParserTableBased(){
        setColumnIndex(0);
        setFormatString("yyyy-MM-dd HH:mm:ss");
    }
    /**
     * Instantiates and immediately configures new instance.
     * 
     * @param configXml Settings stored in XML Element
     * @throws java.lang.Exception Settings XML Format error
     */
    public DateTimeParserTableBased(Element configXml) throws Exception{
        configure(configXml);
    }
    /**
     * Configures the DateTimeParser using the supplied XML
     * 
     * @param configXml XML Element corresponding to this parser. Supplied
     * by this parser on close via <code>getSettingsXml()</code>
     * @throws java.lang.Exception Thrown when supplied XML does not have the 
     * proper parameters or stored values
     */
    @Override
    public void configure(Element configXml)throws Exception{
        /*   XML Input should look like this. Index is zero based.
        *   <DateTimeFormat type='CSIString'>
	*       <FormatString>yyyy-MM-dd HH:mm:ss</FormatString>
        *       <DateTimeColumnIndex>0</DateTimeColumnIndex>
	*   </DateTimeFormat>
        */ 
        
        formatString = configXml.getChild(FORMAT_STRING_TAG).getText();
        columnIndex = Integer.parseInt(configXml
                .getChild(DATE_TIME_COLUMN_INDEX_TAG).getText());
        
        dateFormatter = new SimpleDateFormat(formatString);
                
    }
    
    /**
     * Returns settings stored in XML form.
     * 
     * @return Settings XML Element with current settings stored.
     */
    @Override
    public Element getSettingsXml(){
        Element e = new Element(IDateTimeParser.DATE_TIME_FORMAT_TAG)
                .setAttribute("type",DateTimeParserTableBased.class.getName());
        e.addContent(new Element(FORMAT_STRING_TAG)
                .setText(formatString));
        e.addContent(new Element(DATE_TIME_COLUMN_INDEX_TAG)
                .setText(Integer.toString(columnIndex)));
        
        return e;
    }
    
    /**
     * Parses date based on configured format and supplied data row.
     * 
     * @param row String array of full row of data, already split on delimiter
     * @return Date object derived from supplied text and format
     * @throws java.lang.Exception Thrown if format does not match
     * text provided
     */
    @Override
    public Date ParseDate(String[] row) throws Exception {
        
        return dateFormatter.parse(row[columnIndex].replace("\"", ""));
    }

    /**
     * Gets Parser Description
     * 
     * @return Short description of what the parser does.
     */
    @Override
    public String getParserDescription() {
        return "Parses the date using Java SimpleDateFormat and the supplied "+
                "format string.";
    }

    /**
     * Gets Parser Short Name
     * 
     * @return Short, descriptive name of parser.
     */
    @Override
    public String getParserShortname() {
        return "JavaSimpleDateFormat Formatter";
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
        //re-build the SimpleDateFormat object
        dateFormatter = new SimpleDateFormat(formatString);
    }

    public Dialog getSettingsDialog() {
        return new JDialogDateParserTableBased(null,true,this);
    }
    
    

 }
