/*
 * AggMethod.java
 *
 * Created on June 25, 2007, 10:37 AM
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
public class AggMethod extends attribute{
    
    private static ArrayList retrievedTypes;
    
    public static synchronized AggMethod getAggMethod(String offName,Connection conn){
        AggMethod tmp;
        if(retrievedTypes != null){
            for(Iterator<AggMethod> types = retrievedTypes.iterator(); types.hasNext();){
                tmp = types.next();
                if(offName.compareToIgnoreCase("")==0 || offName == null){
                    return new AggMethod(offName,conn);
                }else if(tmp.name.compareToIgnoreCase(offName)==0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new AggMethod(offName,conn);
            if(offName != null){
                retrievedTypes.add(tmp);
            }
            return tmp;
        }else{
            retrievedTypes = new ArrayList<Method>();
            tmp = new AggMethod(offName,conn);
            if(offName != null){
                retrievedTypes.add(tmp);
            }
            return tmp;
        }
    }
    private AggMethod(String name,Connection conn){
        if(name == null || name.compareToIgnoreCase("") ==0){
            id = -1;
        }else{
            this.name = name;
            if(VegaVersionInfo.dbVersion >1){
                id = retrieveID("AggMethods","AggMethodName","AggMethodID",conn);                
            }else{
                id = retrieveID("AggMethods","AggMethod","AggMethodID",conn);
            }

        }
    }
    
}
