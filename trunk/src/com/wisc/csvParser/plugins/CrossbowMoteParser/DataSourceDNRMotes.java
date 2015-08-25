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
package com.wisc.csvParser.plugins.CrossbowMoteParser;

import com.wisc.csvParser.*;
import javax.swing.JPanel;
import org.jdom.Element;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.ArrayList;
import java.sql.*;
import org.apache.log4j.Logger;
import org.nfunk.jep.JEP;
import java.text.SimpleDateFormat;
import org.apache.log4j.BasicConfigurator;


/**
 *
 * @author lawinslow
 */
public class DataSourceDNRMotes implements IDataParser{

    public enum events{
        started,
        stopped,
        newMessage,
        dataParsed
    }


    private static int DB_QUERY_INTERVAL = 60000;

    private ArrayList<Mote> motes = new ArrayList<Mote>();
    private String dbHost = "localhost";//localhost default
    private String dbName = "task";
    private String dbUser = "tele";
    private String dbPass = "tiny";


    private ArrayList<DataSourceDNRMotes.IEventHandler> handlers
            = new ArrayList<DataSourceDNRMotes.IEventHandler>();
    private String lastMessage = "";
    private JPanel settingPane = null;
    private IDataRepository repo;
    private String panelID;
    private Timer t;
    private int rowPointer;
    
    
    
    private Connection conn;
    
    private Logger logger = Logger.getLogger(DataSourceDNRMotes.class.getName());
    
    
    public DataSourceDNRMotes(){
        panelID = getParserShortname() + (new Random()).nextInt();
        BasicConfigurator.configure();
    }

    @Override
    public boolean Start() {
        if(this.isStarted()){
            return true;
        }
        
        try{
            
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://"+dbHost+"/"+dbName;
            
            conn = DriverManager.getConnection(url, dbUser, dbPass);
        }catch(java.sql.SQLException ex){
            logger.error("Error on db conn initialization:",ex);
            lastMessage = "Please check db conn settings.";
            this.raiseEvent(events.newMessage);
            return false;
        }catch(ClassNotFoundException ex){
            logger.error("jdbc driver exception on postgresql conn:",ex);
            return false;
        }
        
        t = new Timer();
        t.schedule(new TimerTask(){

            @Override
            public void run() {
                parseTable(rowPointer);
            }
            
        }, 10000, DB_QUERY_INTERVAL);

        raiseEvent(events.started);
        return true;
    }

    @Override
    public boolean Stop() {
        if(t!=null){
            try{
                conn.close();
                conn = null;
            }catch(SQLException e){
                logger.error("Error on closing postgresql conn",e);
            }
            t.cancel();
            t = null;
        }
        raiseEvent(events.stopped);
        return true;
        
    }

    public boolean testDB(){
        try{
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://"+dbHost+"/"+dbName;

            conn = DriverManager.getConnection(url, dbUser, dbPass);
            return true;
        }catch(java.sql.SQLException ex){
            logger.error("Error on db conn initialization:",ex);
            lastMessage = "Please check db conn settings.";
            this.raiseEvent(events.newMessage);
            return false;
        }catch(ClassNotFoundException ex){
            logger.error("jdbc driver exception on postgresql conn:",ex);
            return false;
        }
    }

    @Override
    public boolean isStarted() {
        return t!=null;
    }

    @Override
    public String getParserDescription() {
        return "This is a special parser designed to deal with data from the DNR" +
                " mote project.";
    }

    @Override
    public String getParserShortname() {
        return "DNR Mote Parser";
    }



    @Override
    public void setRepository(IDataRepository repository) {
        repo = repository;
    }

    @Override
    public IDataRepository getRepository() {
        return repo;
    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataParser.DATA_PARSER_TAG);
        e.setAttribute(IDataParser.DATA_PARSER_TYPE_TAG,
                DataSourceDNRMotes.class.getName());


        e.addContent(new Element("dbName").setText(dbName));
        e.addContent(new Element("dbUser").setText(dbUser));
        e.addContent(new Element("dbPass").setText(dbPass));
        e.addContent(new Element("dbHost").setText(dbHost));

        for(Mote m:motes){
            e.addContent(m.getSettingsXml());
        }
        
        return e;
    }

    @Override
    public void configure(Element e) throws Exception {

        this.setDbName(e.getChildText("dbName"));
        this.setDbHost(e.getChildText("dbHost"));
        this.setDbPass(e.getChildText("dbPass"));
        this.setDbUser(e.getChildText("dbUser"));

        for(Object obj:e.getChildren("Mote")){
            Mote m = new Mote();
            m.configure((Element)obj);
            motes.add(m);
        }
    }

    @Override
    public JPanel getStatusJPanel() {
        if(this.settingPane == null){
            this.settingPane = new JPanelDNRMotes(this);
        }
        return this.settingPane;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }
    
    private void parseTable(int rowPointer){
        for(Mote m:motes){
            try{
                m.setDB(conn);
                m.parseData();
            }catch(SQLException squeal){
                squeal.printStackTrace();
            }
        }
        
    }
    
    public void addMote(Mote mote){
        motes.add(mote);
    }

    public ArrayList<Mote> getMotes(){
        return motes;
    }

    public boolean removeMote(Mote mote){
        return motes.remove(mote);
    }
    
    private void internalNewValue(ValueObject val){
        repo.NewValue(val);
    }
    
    private void internalNewRow(ArrayList<ValueObject> vals){
        repo.NewRow(vals);
    }
    
    public Mote getNewMote(){
        return new Mote();
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    @Override
    public String toString(){
        return this.getParserShortname();
    }

    public String getMessage(){
        return lastMessage;
    }

    public void addEventHander(IEventHandler i){
        handlers.add(i);
    }

    public void removeEventHandler(IEventHandler i){
        handlers.remove(i);
    }
    
    public void raiseEvent(events e){
        for(IEventHandler h:handlers){
            h.handleEvent(e);
        }
    }

    public interface IEventHandler{
        public void handleEvent(events e);
    }
    
    
    /**
     * 
     */
    public class Mote{

        private String moteName = "Unnamed Mote";
        private Connection conn;
        private String table;
        private int nodeID;
        private Hashtable<String,ColumnObject> columns=
               new Hashtable<String,ColumnObject>();
        
        private java.util.Date datePointer;
       
        public Mote(){
            
        }
        
        public void setDB(Connection conn){
            this.conn = conn;
        }
        
        public void setTable(String tableName){
            this.table = tableName;
        }

        public String getTable(){
            return table;
        }
        
        public void setNodeID(int id){
            nodeID = id;
        }

        public int getNodeID(){
            return nodeID;
        }
        
        public void addColumn(ColumnObject col){
            columns.put(col.columnName, col);
        }

        public boolean removeColumn(ColumnObject col){
            columns.remove(col.columnName);
            return true;
        }

        public Hashtable getColumns(){
            return columns;
        }

        public String getMoteName(){
            return moteName;
        }

        public void setMoteName(String n){
            moteName = n;
        }

        public java.util.Date getDatePointer() {
            return datePointer;
        }

        public void setDatePointer(java.util.Date datePointer) {
            this.datePointer = datePointer;
        }


        
        public Element getSettingsXml(){
            SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Element e = new Element("Mote");

            e.addContent((new Element("MoteName")).setText(this.getMoteName()));
            e.addContent((new Element("TableName")).setText(this.getTable()));
            e.addContent((new Element("MoteID"))
                    .setText(Integer.toString(this.getNodeID())));

            if(datePointer != null){
                e.addContent(new Element("DatePointer").setText(
                        simp.format(datePointer)));
            }

            Element columnsXml = new Element("Columns");

            for(ColumnObject col:columns.values()){
                columnsXml.addContent(col.getSettingsXml());
            }
            e.addContent(columnsXml);
            return e;
            
        }
        
        public void configure(Element e){
            SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            this.setMoteName(e.getChildText("MoteName"));
            this.setTable(e.getChildText("TableName"));
            this.setNodeID(Integer.parseInt(e.getChildText("MoteID")));
            
            try{
                if(e.getChild("DatePointer")!=null){
                    this.setDatePointer(simp.parse(e.getChildText("DatePointer")));
                }
            }catch(Exception exc){
                this.setDatePointer(new Date(0));
            }

            Element colXml;
            ColumnObject newCol;
            for(Object obj:e.getChild("Columns").getChildren("Column")){
                colXml = (Element)obj;
                newCol = new ColumnObject();
                newCol.configure(colXml);
                this.addColumn(newCol);
            }
        }
        
        public void parseFromDate(java.util.Date date)throws SQLException{
            Statement getData = conn.createStatement();
           
            String sql = "SELECT result_time,";
            java.util.Enumeration e = columns.keys();

            while(e.hasMoreElements()){
                sql += (String)e.nextElement();
                if(e.hasMoreElements()){
                    sql+=",";
                }
            }
            SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sql += " FROM " + table + " WHERE result_time > '" 
                    + simp.format(date) + "' AND nodeid = '"+  nodeID + "'"
                    + " ORDER BY result_time";
           
            ResultSet rs = getData.executeQuery(sql);
            
            java.util.Date lastDate = null;
            
            java.util.Set<String> cols = columns.keySet();
            ColumnObject colInfo;
            double val;
            ValueObject newVal;
            ArrayList<ValueObject> newRow;
            
            while(rs.next()){
                newRow = new ArrayList<ValueObject>();
                lastDate = new java.util.Date(rs.getTimestamp("result_time").getTime());
                for(String col:cols){
                    colInfo = columns.get(col);
                    double tmp = (double)rs.getInt(col);
                    colInfo.equation.addVariable("x",(double)rs.getInt(col));
                    
                    newVal = colInfo.templateValue.cloneValueless();
                    tmp = colInfo.equation.getValue();
                    newVal.setValue(colInfo.equation.getValue());
                    newVal.setTimeStamp(lastDate);
                    newRow.add(newVal);
                }
                internalNewRow(newRow);
            }
            if(lastDate != null){
                //Add a second, db keeps milliseconds, system only
                //deals down to 1 sec.
                datePointer = lastDate;
            }
            
            rs.close();
            getData.close();
            
        }
        
        public void parseData()throws SQLException{
            if(datePointer !=null){
                parseFromDate(datePointer);
            }else{
                //set date sometime in the far past;
                parseFromDate(new Date(0));
            }
        }
        
        public ColumnObject getNewColumn(){
            return new ColumnObject();
        }
        
        @Override
        public String toString(){
            return moteName;
        }

        /**
         * 
         */
        public class ColumnObject{
            public JEP equation = new JEP();
            public String columnName = "New Column";//Default val
            public ValueObject templateValue = new ValueObject();
            public String equationAsString = "x";

            public ColumnObject(){
                equation.parseExpression("x");
                equation.addVariable("x", 0);
            }

            @Override
            public String toString(){
                return columnName;
            }

            public Element getSettingsXml(){
                Element e = new Element("Column");
                e.addContent((new Element("Equation")).setText(equationAsString));
                e.addContent(templateValue.getMetadataXml());
                e.addContent((new Element("ColumnName")).setText(columnName));
                return e;
            }

            public void configure(Element e){
                columnName = e.getChildText("ColumnName");
                equationAsString = e.getChildText("Equation");
                equation.parseExpression(equationAsString);
                templateValue = new ValueObject(e.getChild(ValueObject.METADATA_TAG));
            }

            @Override
            public ColumnObject clone(){

                ColumnObject clone = new ColumnObject();
                clone.templateValue = this.templateValue.clone();
                clone.columnName = this.columnName;
                clone.equationAsString = this.equationAsString;

                //do what is required to make a copy of the JEP object
                clone.equation = new JEP();
                clone.equation.addVariable("x", 0);
                clone.equation.parseExpression(equationAsString);
                
                return clone;
            }
        }
    }
}
