/*
 * Unit.java
 *
 * Created on June 25, 2007, 5:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wisc.VegaLibrary;

import java.sql.*;
import java.util.*;
/**
 *
 * @author lawinslow
 */
public class Unit extends attribute{


    private static ArrayList retrievedTypes;
    
    public static synchronized Unit getUnit(String offName,Connection conn){
        Unit tmp;
        if(retrievedTypes != null){
            for(Iterator<Unit> types = retrievedTypes.iterator(); types.hasNext();){
                tmp = types.next();
                if(offName.compareToIgnoreCase("")==0 || offName == null){
                    return new Unit(offName,conn);
                }else if(tmp.name.compareToIgnoreCase(offName)==0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new Unit(offName,conn);
            if(offName != null){
                retrievedTypes.add(tmp);
            }
            return tmp;
        }else{
            retrievedTypes = new ArrayList<Method>();
            tmp = new Unit(offName,conn);
            if(offName != null){
                retrievedTypes.add(tmp);
            }
            return tmp;
        }
    }
    private Unit(String name,Connection conn){
        if(name == null || name == ""){
            id = -1;
        }else{
            this.name = name;
            if(VegaVersionInfo.dbVersion >1){
                id = retrieveID("Units","UnitName","UnitID",conn);                
            }else{
                id = retrieveID("Units","UnitShort","UnitID",conn); 
            }

        }
    }
    
}
