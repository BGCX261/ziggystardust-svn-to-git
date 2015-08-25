/*
 * ItemNotInDbException.java
 *
 * Created on April 16, 2007, 8:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wisc.VegaLibrary;

/**
 *
 * @author lawinslow
 */
public class ItemNotInDbException extends Exception{
    private String item;
    private String table;
    /** Creates a new instance of ItemNotInDbException */
    public ItemNotInDbException(String item, String table) {
        this.item = item;
        this.table = table;
        
    }
    public String getMessage(){
        return "Could not get "+item+" from "+table;
    }
    
}
