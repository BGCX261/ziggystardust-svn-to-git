/*
 * Method.java
 *
 * Created on April 28, 2007, 4:41 PM
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
public class Method extends attribute{
    private static ArrayList retrievedMeths;

    public static synchronized Method getMethod(String methName,Connection conn){
        Method tmp;
        if(retrievedMeths != null){
            for(Iterator<Method> meths = retrievedMeths.iterator(); meths.hasNext();){
                tmp = meths.next();
                if(methName == null || methName.compareToIgnoreCase("") == 0){
                    return new Method(methName,conn);
                } else if(tmp.name.compareToIgnoreCase(methName) == 0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new Method(methName,conn);
            if(methName != null){
                retrievedMeths.add(tmp);
            }
            return tmp;
        }else{
            retrievedMeths = new ArrayList<Method>();
            tmp = new Method(methName,conn);
            if(methName != null){
                retrievedMeths.add(tmp);
            }
            return tmp;
        }
    }
    private Method(String methName,Connection conn){
        name = methName;
        id = this.retrieveID("Methods","MethodName","MethodID",conn);
    }
}

