/*
 * OffsetType.java
 *
 * Created on April 28, 2007, 4:39 PM
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
public class OffsetType extends attribute{
    private static ArrayList retrievedTypes;
    
    public static synchronized OffsetType getOffsetType(String offName,Connection conn){
        OffsetType tmp;
        if(retrievedTypes != null){
            for(Iterator<OffsetType> types = retrievedTypes.iterator(); types.hasNext();){
                tmp = types.next();
                if(offName == null || offName.compareToIgnoreCase("")==0){
                    return new OffsetType(offName,conn);
                }else if(tmp.name.compareToIgnoreCase(offName)==0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new OffsetType(offName,conn);
            if(offName != null){
                retrievedTypes.add(tmp);
            }
            return tmp;
        }else{
            retrievedTypes = new ArrayList<Method>();
            tmp = new OffsetType(offName,conn);
            if(offName != null){
                retrievedTypes.add(tmp);
            }
            return tmp;
        }
    }
    private OffsetType(String name,Connection conn){
        if(name == null || name == ""){
            this.name="";
            id = -1;
        }else{
            this.name = name;
            if(VegaVersionInfo.dbVersion >1){
                id = retrieveID("OffsetTypes","OffsetName","OffsetTypeID",conn);
            }else{
                id = retrieveID("OffsetTypes","OffsetName","OffsetType",conn);
            }

        }
    }
}