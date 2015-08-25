/*
 * Variable.java
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
public class Variable extends attribute{
    private static ArrayList retrieved;

    public static synchronized Variable getVariable(String givenName,Connection conn)throws ItemNotInDbException{
        Variable tmp;
        if(retrieved != null){
            for(Iterator<Variable> it = retrieved.iterator(); it.hasNext();){
                tmp = it.next();
                if(tmp.name.compareToIgnoreCase(givenName) == 0){
                    return tmp;
                }
            }//for loop
            //if we get here, none were matches, create a new one
            tmp = new Variable(givenName,conn);
            retrieved.add(tmp);
            return tmp;
        }else{
            retrieved = new ArrayList<Variable>();
            tmp = new Variable(givenName,conn);
            retrieved.add(tmp);
            return tmp;
        }
    }//getVariable
    
    private Variable(String varName,Connection conn)throws ItemNotInDbException{
        name = varName;
        id = this.retrieveID("Variables","VariableName","VariableID",conn);
        if(id < 0)
            throw new ItemNotInDbException(name,"Variables");
    }// public Variable
}// class Variable

