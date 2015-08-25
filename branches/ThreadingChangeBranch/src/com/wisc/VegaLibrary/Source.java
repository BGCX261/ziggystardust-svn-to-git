/*
 * Source.java
 *
 * Created on April 28, 2007, 4:40 PM
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
public class Source extends attribute{
    private static ArrayList retrieved;
    
    public static synchronized Source getSource(String givenName,Connection conn)throws ItemNotInDbException{
        Source tmp;
        if(retrieved != null){
            for(Iterator<Source> it = retrieved.iterator(); it.hasNext();){
                tmp = it.next();
                if(tmp.name.compareToIgnoreCase(givenName)==0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new Source(givenName,conn);
            retrieved.add(tmp);
            return tmp;
        }else{
            retrieved = new ArrayList<Source>();
            tmp = new Source(givenName,conn);
            retrieved.add(tmp);
            return tmp;
        }
    }//getSource
        
    private Source(String sourceName,Connection conn)throws ItemNotInDbException{
        name = sourceName;
        id = this.retrieveID("Sources","SourceName","SourceID",conn);
        if(id < 0)
            throw new ItemNotInDbException(name,"Sources");
    }
}
