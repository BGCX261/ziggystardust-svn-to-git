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
import com.wisc.ziggy.test.DataRepositoryNull;

/**
 *
 * @author lawinslow
 */
public class DataRepositoryFactory {
    public static Hashtable<String,String> repositories;
    
    public static IDataRepository getDataRepository(String type)throws Exception{
        Class c = Class.forName(type);
        return (IDataRepository)c.newInstance();
    }
    
    public static IDataRepository getDataRepository(Element e)throws Exception{
        IDataRepository toReturn = getDataRepository(e.getAttributeValue("type"));
        toReturn.configure(e);
        return toReturn;
    }
    
    public static Hashtable<String,String> getRepositories(){
        if(repositories == null){
            repositories = new Hashtable<String,String>();
            repositories.put("MysqlDirect", DataRepositoryMysqlVega.class.getName());
            repositories.put("GLEON Uploader", DataFilterGleonDist.class.getName());                
            repositories.put("DataTurbine", DataRepositoryDT.class.getName());
           // if(System.getenv("gleon_central") != null || System.getenv("gleon_testing")!=null){
                repositories.put("Null Repo", DataRepositoryNull.class.getName());
           // }
            
        }
        return repositories;
    }


}
