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
import org.jdom.*;
import com.wisc.csvParser.notificationProviders.IProviderUser;
import com.wisc.csvParser.plugins.*;
/**
 *
 * @author lawinslow
 */
public class DataFilterFactory {
    private static Hashtable<String,String> filterList;
    
    public static IDataRepository getDataFilter(String type) throws Exception{
        Class c = Class.forName(type);
        IDataRepository toReturn = (IDataRepository)c.newInstance();
        
        try{
            IProviderUser tmp = (IProviderUser)toReturn;
            tmp.setNotificationProvider(GlobalProgramSettings.provider);
        }catch(Exception e){
            
        }
        
        return toReturn;
    }
    
    public static IDataRepository getDataFilter(Element e) throws Exception{
        IDataRepository toReturn = getDataFilter(e.getAttributeValue("type"));
        toReturn.configure(e);
        return toReturn;
    }
    /**
     * List of available filters, used for drop down boxes to allow user
     * to add new filters.
     * @return Table with list of all data filters, Name is key,
     * value is fully qualified class name
     */
    public static Hashtable<String,String> getFilters(){
        if(filterList == null){
            //just do this manually for now.
            filterList = new Hashtable<String,String>();
            filterList.put("DataTurbine", 
                    DataRepositoryDT.class.getName());
            filterList.put("Mysql Repository",
                    DataRepositoryMysqlVega.class.getName());
            filterList.put("GLEON Repository",
                    DataFilterGleonDist.class.getName());
            filterList.put("Apprise Filter",
                    DataFilterApprise.class.getName());
            filterList.put("Buoyancy Freq Generator",
                    DataGeneratorBuoyancyFrequency.class.getName());
            
            //Some of these are in testing stage and should only be used by 
            // the developer and other testers
            if(System.getenv("gleon_central") != null){
                filterList.put("Stream Monitor", 
                        DataFilterStreamMonitor.class.getName());
                filterList.put("Autoprof Pivoter", 
                        DataFilterPivotProfilerData.class.getName());
            }
        }
        return filterList;
    }
}
