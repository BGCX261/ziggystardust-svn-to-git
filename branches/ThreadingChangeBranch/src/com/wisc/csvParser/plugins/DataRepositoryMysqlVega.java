/*
 *  This file is part of ZiggyStardust.
 *
 *  ZiggyStardust is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ZiggyStardust is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wisc.csvParser.plugins;

import com.wisc.csvParser.*;
import java.sql.*;
import org.jdom.*;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;
import javax.swing.JPanel;
import com.wisc.VegaLibrary.*;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;


/**
 *
 * @author lawinslow
 */
public class DataRepositoryMysqlVega extends TimerTask implements IDataRepository{
    
    public enum events{
        newValue,
        duplicateValue,
        started,
        stopped,
        configured
    };

    public static final String DB_USER_TAG = "DatabaseUser";
    public static final String DB_PASS_TAG = "DatabasePassword";
    public static final String DB_PATH_TAG = "DatabasePath";
    public static final String DB_NAME_TAG = "DatabaseName";
    
    private final String encryptPassword = "yi=09McnqUAYdge;D84/i$8LAT1d{d";    
    
    private Connection conn;
    private int dbVersion;
    private Vector<ValueObject> valBuffer = new Vector<ValueObject>();
    private boolean connectionBroken;
    private Timer timer;
    private Vector<MysqlRepositoryListener> listeners =
            new Vector<MysqlRepositoryListener>();
    
    //Database specific info, fill with default values
    private String dbName = "vega";
    private String dbServerPath = "localhost";
    private String dbUser = "vegaUser";
    private String dbPass = "vegaPassword";
    private IDataRepository child;
    
    private String panelID;
    private JPanelMysqlVega statusPanel;
    
    private Logger logger = Logger.getLogger(DataRepositoryMysqlVega.class.getName());
    
    public DataRepositoryMysqlVega(){
        panelID = getRepositoryShortname() + 
            Integer.toString((new java.util.Random()).nextInt());
    }   
    
    /** Creates a new instance of DataRepositoryMysqlVega */
    public DataRepositoryMysqlVega(Element xml) throws Exception{
        this();
        configure(xml);
    }
    public void configure(Element xml)throws Exception{
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(encryptPassword);
        //If these throw an exception, the xml is malformed
        //  don't catch exception here.
        this.setDBUser(xml.getChildText(DB_USER_TAG));
        String isEncrypted = xml.getChild(DB_PASS_TAG).getAttributeValue("encrypted");
        if(isEncrypted == null || isEncrypted.compareToIgnoreCase("false")==0){
            this.setDBPassword(xml.getChildText(DB_PASS_TAG));
        }else{
            this.setDBPassword(
                    textEncryptor.decrypt(xml.getChildText(DB_PASS_TAG)));
        }
        this.setDBServerPath(xml.getChildText(DB_PATH_TAG));
        this.setDBName(xml.getChildText(DB_NAME_TAG));
        
        raiseEvent(events.configured);
    }
    
    @Override
    public Element getSettingsXml(){
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(encryptPassword);
        
        Element e = new Element(IDataRepository.DATA_REPOSITORY_TAG);
        e.setAttribute("type",DataRepositoryMysqlVega.class.getName());
        e.addContent(new Element(DB_USER_TAG).setText(this.getDBUser()));
        e.addContent(new Element(DB_PATH_TAG).setText(this.getDBServerPath()));
        e.addContent(new Element(DB_NAME_TAG).setText(this.getDBName()));
        //save encrypted pass. Add encrypted attribute
        // so that we can differentiate between old (unencrypted) and new format
        e.addContent(new Element(DB_PASS_TAG)
                .setText(textEncryptor.encrypt(this.getDBPass()))
                .setAttribute("encrypted", "true"));
        return e;
    }
    

    public void setChildRepository(IDataRepository repo){
        child = repo;
    }
    public IDataRepository getChildRepository(){
        return child;
    }
    public boolean NewValue(ValueObject val){
        if(!isStarted()){
            if(connectionBroken){
                addValueToBuffer(val);
                return true;
            }else{
                if(!Start()){
                    connectionBroken = true;
                    startConnectionBrokenWatch();
                    return NewValue(val);
                }
            }
        }
        
        try{
            Value.insertValue(val,conn);
            this.raiseEvent(events.newValue);
        }catch(Exception e){
            if(e instanceof com.wisc.VegaLibrary.ItemNotInDbException){
                logger.error(e.getMessage());
                return false;
            }else if(e instanceof java.net.SocketException){
                connectionBroken = true;
                this.addValueToBuffer(val);
                this.Stop();
                this.startConnectionBrokenWatch();
            }else{
                logger.error("MySQL Repository Failure:");
                logger.error(e.getMessage()+e.getCause());
            }
        }

        return true;
    }
    public boolean NewRow(ArrayList<ValueObject> row){
        boolean response = true;
        for(ValueObject val:row){
                response = NewValue(val);
        }
        
        return response;
    }
    public boolean Start(){
        
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://"+
                    dbServerPath+"/"+
                    dbName,
                    dbUser,
                    dbPass);
            
            getDBVersion();
            connectionBroken = false;
            raiseEvent(events.started);
            return true;
        }catch(SQLException sql){
            sql.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
            
        }

    }
    
    public void getDBVersion(){
        try{
            PreparedStatement versionCmd = conn.prepareStatement(
                "SELECT `Value` FROM `DatabaseInformation` " +
                " WHERE Information='version'");
        
            ResultSet rs = versionCmd.executeQuery();
            rs.next();
            dbVersion = Integer.parseInt(rs.getString("Value"));
        }catch(Exception ex){
            //All versions after 1 have 'DatabaseInformation' table
            dbVersion = 1;
        }
        //Now set the dbVersion on all objects that will need it later.
        VegaVersionInfo.dbVersion = dbVersion;
        
    }
    
    public boolean Stop(){
        if(conn != null){
            try{
                conn.close();
                raiseEvent(events.stopped);
            }catch(SQLException se){
                //do nothing for now.
            }
        }
        return true;
    }
    public boolean isStarted(){
        if(conn != null){
            try{
                return !conn.isClosed();                
            }catch(SQLException se){
                return false;
            }
        }else{
            return false;
        }
    }


    public JPanel getStatusJPanel(){
        if(statusPanel==null){
            statusPanel = new JPanelMysqlVega(this);
        }
        return statusPanel;
    }
    public String getPanelID(){
        return panelID;
    }
    public String getRepositoryShortname(){
        return "Mysql_Vega";
    }
    public String getRepositoryDescription(){
        return "This repository puts all passed values into a mysql based" +
                "Vega datamodel repository.";
    }
    public String toString(){
        return getRepositoryShortname();
    }
    
    public void startConnectionBrokenWatch(){
        timer = new Timer();
        timer.scheduleAtFixedRate(this,30000,30000);
    }
    public void run(){
        if(connectionBroken){
            logger.warn("Connection Broken. Attempt to reconnect.");
            if(Start()){
                timer.cancel();
                connectionBroken = false;
                logger.warn("Reconnect successful. Flushing cached vals.");
                flushCachedValues();
            }else{
                logger.warn("reconnect failed.");
            }
        }
    }
    
    private synchronized void addValueToBuffer(ValueObject val){
        valBuffer.add(val);
        propertyChanged();
    }
    private void flushCachedValues(){
        for(ValueObject v:valBuffer){
            NewValue(v);
        }
        valBuffer.clear();
        propertyChanged();
    }
    
    public String getDBUser(){
        return dbUser;
    }
    public String getDBServerPath(){
        return dbServerPath;
    }
    public String getDBName(){
        return dbName;
    }
    public String getDBPass(){
        return dbPass;
    }
    public void setDBUser(String user){
        dbUser = user;
    }
    public void setDBName(String name){
        dbName = name;
    }
    public void setDBServerPath(String path){
        dbServerPath = path;
    }
    public void setDBPassword(String pass){
        dbPass = pass;
    }
    
    private void propertyChanged(){
        if(statusPanel != null)
            statusPanel.updateDisplay();
    }
    public void addEventListener(MysqlRepositoryListener l){
        listeners.add(l);
    }
    public void removeEventListener(MysqlRepositoryListener l){
        listeners.remove(l);
    }
    private void raiseEvent(events e){
        for(MysqlRepositoryListener l:listeners)
            l.eventRaised(e);
    }
    
    public interface MysqlRepositoryListener{
        public void eventRaised(events e);
    };
    
}