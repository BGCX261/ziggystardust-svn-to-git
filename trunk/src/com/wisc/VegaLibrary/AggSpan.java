/*
 * AggSpan.java
 *
 * Created on June 25, 2007, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wisc.VegaLibrary;



/**
 *
 * @author lawinslow
 */
public class AggSpan {
    private String spanString;

    /** Creates a new instance of AggSpan
     * @param span
     */
    public AggSpan(String span){
        spanString = span;
        
    }// constructor
    
    public String getSqlWherePart(){
        return " = '" + spanString + "' ";
    }// getSqlWherePart()
    public String getSpan(){
        return spanString;
    }
    
}
