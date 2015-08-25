/*
 * attribute.java
 *
 * Created on April 28, 2007, 4:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wisc.VegaLibrary;
import java.sql.*;
import java.util.*;
import org.omg.PortableInterceptor.SUCCESSFUL;

/**
 *
 * @author lawinslow
 */
public abstract class attribute{
    protected int id;
    protected String name;
    private boolean valid;

    protected int retrieveID(String table, String nameCol, String idCol,Connection conn){
        if(name.compareToIgnoreCase("") ==0){
            valid = false;
            return -1;
        }
        try{
            Statement st = conn.createStatement();
            String sql = "SELECT `"+nameCol+"`,`"+idCol+"` FROM `"+table+
                    "` WHERE `"+nameCol+"`='"+name+"'";
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                this.valid = true;
                int tmp = rs.getInt(idCol);
                st.close();
                return tmp;
            }else{
                this.valid = false;
                st.close();
                return -1;
            }//if

        }catch(SQLException sx){
            System.out.println(sx.getMessage());
            return -1;
        }//catch

    }//retrieveID

    public String getName(){
        return name;
    }
    public int getID(){
        return id;
    }
    public boolean isValid(){
        return valid;
    }
    public String getSqlWherePart(){
        if(id < 0){
            return " is null ";
        }else{
            return " = " + id + " ";
        }
    }

}
