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
import java.util.Hashtable;
import org.jdom.Element;
import com.wisc.csvParser.plugins.*;

/**
 *
 * @author lawinslow
 */
public class DataParserFactory {
    private static Hashtable<String,String> parsers;

    public static IDataParser getParser(String type)throws Exception{
        Class c = Class.forName(type);
        return (IDataParser)c.newInstance();
    }
    public static IDataParser getParser(Element e) throws Exception{
        IDataParser toReturn = getParser(e.getAttributeValue("type"));
        toReturn.configure(e);
        return toReturn;
    }
    public static Hashtable<String,String> getParsers(){
        //if the parser collection is already created, just return it.
        if(parsers != null){
            return parsers;
        }
        // parser collection not created, create it
        parsers = new Hashtable<String,String>();
        parsers.put("CSV with ArrayID",DataParserCSIArrayID.class.getName());
        parsers.put("Table Based CSV",DataParserTableBased.class.getName());

        //if gleon_central is defined, add the hidden modules
        if(System.getenv("gleon_central") != null || System.getenv("gleon_testing")!=null){
            parsers.put("GLEON CENTRAL(ADMIN ONLY)",
                    DataParserGLEONCentral.class.getName());
            parsers.put("DNR Parser(alpha version)",
                    DataSourceDNRMotes.class.getName());
            parsers.put("GLEON XML Service Harvester",
                    DataParserGleonServiceXML.class.getName());
        }
        return parsers;
    }
}
