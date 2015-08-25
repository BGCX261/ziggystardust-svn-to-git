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

package com.wisc.csvParser;
import com.wisc.csvParser.plugins.*;
import org.jdom.*;
import java.util.Hashtable;

/**
 *
 * @author lawinslow
 */
public class DateTimeParserFactory {
    
    private static Hashtable<String,String> parserList;
    
    public static IDateTimeParser getDateTimeParser(String type) throws Exception{
        try{
            Class c = Class.forName(type);
            IDateTimeParser toReturn = (IDateTimeParser)c.newInstance();

            return toReturn;
        }catch(ClassNotFoundException e){
            //do nothing, lets see if the backwards compatability portion
            // picks it up.
        }

        //Here for backwards compatability.
        if(type.equals("CSIString")){
            return new DateTimeParserTableBased();
        }
        else if (type.equals("CSIYEAR_DAYNUM_TYPE")) {
            return new DateTimeParserCSI();
        }
        else {
            throw new ClassNotFoundException();
        }
    }
    public static IDateTimeParser getDateTimeParser(Element e) throws Exception{
        IDateTimeParser toReturn = getDateTimeParser(e.getAttributeValue("type"));
        toReturn.configure(e);
        return toReturn;
    }
    
    public static Hashtable<String,String> getDateTimeParsers(){
        
        //currently just do this manually, Haven't figured out the best way 
        // to get 
        if(parserList == null){
            parserList = new Hashtable<String,String>();
            parserList.put("CSI year,daynum,time","com.wisc.csvParser.plugins.DateTimeParserCSI");
            parserList.put("Java Date Format","com.wisc.csvParser.plugins.DateTimeParserTableBased");
        }
        
        return parserList;
    }
    
    public static void main(String[] args){
        Element tmp = new Element(IDateTimeParser.DATE_TIME_FORMAT_TAG);
        tmp.addContent(new Element(
                DateTimeParserTableBased.DATE_TIME_COLUMN_INDEX_TAG)
                .setText("0"));
        tmp.addContent(new Element(
                DateTimeParserTableBased.FORMAT_STRING_TAG)
                .setText("yyyy-MM-dd HH:mm:ss"));
        tmp.setAttribute("type","com.wisc.csvParser.plugins.DateTimeParserTableBased");
        try{
            IDateTimeParser coolTest = DateTimeParserFactory
                    .getDateTimeParser(tmp.getAttributeValue("type"));
            coolTest.configure(tmp);
            
            String[] s = {"2008-12-1 13:05:00","02347"};
            System.out.println(coolTest.ParseDate(s).toString());
        }catch(Exception e){
            e.printStackTrace();
        }
 
        
    }
}

