/*
 * OffsetValue.java
 *
 * Created on April 28, 2007, 4:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wisc.VegaLibrary;

/**
 *
 * @author lawinslow
 */
public class OffsetValue{
    private boolean isNull; 
    private double value;
    
    public OffsetValue(double offDouble){
        if(Double.compare(offDouble, Double.NaN)==0){
            isNull = true;
        }else{
            value = offDouble;
            isNull = false;                
        }
    }
    public OffsetValue(String offString){
        if(offString == null || offString.compareToIgnoreCase("")==0){
            isNull = true;
        }else{
            try{
                value = Double.parseDouble(offString);
                isNull = false;
            }catch(NumberFormatException nfe){
                System.out.println("Invalid OffsetValue: "+offString);
                isNull = true;
            }
        }//if offString is null or empty 
    }//OffsetValue constructor
    public String getSqlWherePart(){
        if(isNull || Double.compare(value, Double.NaN)==0){
            return " IS null ";
        }else{
            return " = " + value + " ";
        }// if isNull 
    }// getSqlWherePart()
    public double getValue(){
        return value;
    }// getValue()
    public boolean isNull(){
        return isNull;
    }// isValid()
}// class OffsetValue
