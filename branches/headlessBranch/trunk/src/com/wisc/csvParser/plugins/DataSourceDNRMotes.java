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


/**
 *
 * @author lawinslow
 */
public class DataSourceDNRMotes implements IDataParser{

    private static int DB_QUERY_INTERVAL = 60000;

    private JPanel settingPane = null;
    private IDataRepository repo;
    private String panelID;
    private Timer t;
    private int rowPointer;
    private ArrayList<Mote> motes = new ArrayList<Mote>();
    
    private Connection conn;
    
    private Logger logger = Logger.getLogger(DataSourceDNRMotes.class.getName());
    
    
    public DataSourceDNRMotes(){
        panelID = getParserShortname() + (new Random()).nextInt();
    }

    @Override
    public boolean Start() {
        if(this.isStarted()){
            return true;
        }
        
        try{
            
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://198.150.174.48/task";
            
            conn = DriverManager.getConnection(url, "tele","tiny");
        }catch(java.sql.SQLException ex){
            logger.error(ex.getMessage());
            return false;
        }catch(ClassNotFoundException ex){
            logger.error(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        
        t = new Timer();
        t.schedule(new TimerTask(){

            @Override
            public void run() {
                parseTable(rowPointer);
            }
            
        }, 10000, DB_QUERY_INTERVAL);
        
        return true;
    }

    @Override
    public boolean Stop() {
        if(t!=null){
            try{
                conn.close();
                conn = null;
            }catch(SQLException e){
                logger.error(e.getMessage());
            }
            t.cancel();
            t = null;
        }
        return true;
        
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
    public void configure(Element e) throws Exception {
        
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
        e.setAttribute("type",DataSourceDNRMotes.class.getName());
        
        return e;
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
    
    
    private void internalNewValue(ValueObject val){
        repo.NewValue(val);
    }
    
    private void internalNewRow(ArrayList<ValueObject> vals){
        repo.NewRow(vals);
    }
    
    public Mote getNewMote(){
        return new Mote();
    }
    
    public void setupHardcoded(){

        DataSourceDNRMotes.Mote m1 = this.getNewMote();
        DataSourceDNRMotes.Mote m4 = this.getNewMote();
        DataSourceDNRMotes.Mote m7 = this.getNewMote();
        DataSourceDNRMotes.Mote m8 = this.getNewMote();
        DataSourceDNRMotes.Mote m9 = this.getNewMote();
        

        
        
        m1.setNodeID(1);
        m4.setNodeID(4);
        m7.setNodeID(7);
        m8.setNodeID(8);
        m9.setNodeID(9);
        
        m1.setTable("mda300_results");
        m4.setTable("mda300_results");
        m7.setTable("mda300_results");
        m8.setTable("mda300_results");
        m9.setTable("mda300_results");
        
        
        ValueObject depthValue = new ValueObject();
        depthValue.setAggMethod("Inst");
        depthValue.setAggSpan("00:00:00");
        depthValue.setVariable("DEPTH");
        depthValue.setSite("CB Open Water");
        depthValue.setSource("DNR");
        depthValue.setUnit("m");
        depthValue.setUtcOffset(-6);        
        
        ValueObject condValue = depthValue.cloneValueless()
                .setVariable("CONDUCTIVITY").setUnit("uS/cm");
        ValueObject tempValue = depthValue.cloneValueless()
                .setVariable("WATER_TEMP").setUnit("C");
        ValueObject sensBattVal = depthValue.cloneValueless()
                .setVariable("SENSOR_BATT").setUnit("V");
        ValueObject precipVal = depthValue.cloneValueless()
                .setVariable("PRECIP").setUnit("cm");
        
        
        //we'll do m1
        DataSourceDNRMotes.Mote.ColumnObject dep = m1.getNewColumn();
        DataSourceDNRMotes.Mote.ColumnObject cond = m1.getNewColumn();
        DataSourceDNRMotes.Mote.ColumnObject wTemp = m1.getNewColumn();
        DataSourceDNRMotes.Mote.ColumnObject sensBatt = m1.getNewColumn();
        DataSourceDNRMotes.Mote.ColumnObject mprBatt = m1.getNewColumn();
        
        
        JEP j = new JEP();
        j.addVariable("x",0);
        j.parseExpression("((x*2.5/4096) - 0.5)*(0.5)");

        
        dep.columnName = "adc0";
        dep.templateValue = depthValue;
        dep.equation = j;
        dep.equationAsString = "((x*2.5/4096) - 0.5)*(0.5)";
        m1.addColumn(dep);
        
        
        
        //m1.addColumn(col);
        this.addMote(m1);
        
    }

    @Override
    public String toString(){
        return this.getParserShortname();
    }
    
    
    /**
     * 
     */
    public class Mote{
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
        
        public void setNodeID(int id){
            nodeID = id;
        }
        
        public void addColumn(ColumnObject col){
            columns.put(col.columnName, col);
        }
                
        
        public Element getSettingsXml(){
            Element e = new Element("mote");
            
            
            return e;
            
        }
        
        public void configure(Element e){
            
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
            SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        
        /**
         * 
         */
        public class ColumnObject{
            public JEP equation;
            public String columnName;
            public ValueObject templateValue;
            public String equationAsString;
        }
    }

    public static void main(String[] args) throws InterruptedException{
        DataSourceDNRMotes test = new DataSourceDNRMotes();
        
        DataSourceDNRMotes.Mote m = test.getNewMote();
        m.setNodeID(1);
        m.setTable("mda300_results");
        DataSourceDNRMotes.Mote.ColumnObject col = m.getNewColumn();
        ValueObject val = new ValueObject();
        
        val.setAggMethod("Inst");
        val.setAggSpan("00:00:00");
        val.setVariable("DEPTH");
        val.setSite("CB N");
        val.setSource("DNR");
        val.setUnit("m");
        val.setUtcOffset(-6);
        

        col.columnName = "adc0";


        col.templateValue = val;
        JEP j = new JEP();
        j.addVariable("x",0);

        j.parseExpression("((x*2.5/4096) - 0.5)*(0.5)");

        col.equation = j;
        
        m.addColumn(col);
        test.addMote(m);
        
        test.Start();
        
        Thread.sleep(100000);
        
        
        
        
    }
}
