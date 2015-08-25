/*
 * Site.java
 *
 * Created on April 28, 2007, 4:37 PM
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
public class Site extends attribute{
    private static ArrayList retrieved;
    
    public static synchronized Site getSite(String givenName,Connection conn)throws ItemNotInDbException{
        Site tmp;
        if(retrieved != null){
            for(Iterator<Site> it = retrieved.iterator(); it.hasNext();){
                tmp = it.next();
                if(tmp.name.compareToIgnoreCase(givenName)==0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new Site(givenName,conn);
            retrieved.add(tmp);
            return tmp;
        }else{
            retrieved = new ArrayList<Site>();
            tmp = new Site(givenName,conn);
            retrieved.add(tmp);
            return tmp;
        }
    }//getSite
    
    private Site(String siteName,Connection conn)throws ItemNotInDbException{
        name = siteName;
        id = this.retrieveID("Sites","SiteName","SiteID",conn);
        if(id < 0)
            throw new ItemNotInDbException(name,"Sites");
    }// public Site
}// class Site
