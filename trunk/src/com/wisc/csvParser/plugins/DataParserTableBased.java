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
import org.jdom.*;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JPanel;
import java.util.Vector;
import org.apache.log4j.Logger;
import javax.swing.JOptionPane;

/**
 * This class, which is an instance of IDataParser, parses data in a table-based
 * file.
 * 
 * @author James Wyuen, Ryan Kroiss
 */
public class DataParserTableBased implements IDataParser {
    

    public enum events {

        started,
        stopped,
        startDataParse,
        stopDataParse,
        finishDataParse,
        reconfigured,
        newMalformedRow,
        rowParsed,
    }
    /**
     * The delimiter that separates data in the file.
     */
    public static final String DELIMITER = ",";
    private List<TableBasedFile> tableFiles =
            new ArrayList<TableBasedFile>();
    private Timer timer;
    private JPanelTableBased statusJPanel;
    private volatile long rowsParsedTotal = 0;
    /**
     * The tag in the XML that indicates a new TableBasedFile
     */
    public static final String FILE_GROUP_TAG = "File";
    private ArrayList<TableBasedEventHandler> eventHandlers =
            new ArrayList<TableBasedEventHandler>();
    private Hashtable<Integer, String> malformedRows = new Hashtable<Integer, String>();
    private String panelID;
    private IDataRepository myRepository;
    private Logger logger = Logger.getLogger(DataParserTableBased.class);

    /**
     * Constructor that initializes tableFiles from the given Element
     * 
     * @param configXml the XML file to construct the List of TableBasedFiles
     * @throws java.lang.Exception
     */
    public DataParserTableBased(Element configXml) throws Exception {
        this();
        configure(configXml);

    }

    /**
     * Constructs a JPanelTableBased with this DataParserTableBased
     * 
     * @throws java.lang.Exception
     */
    public DataParserTableBased() throws Exception {
        panelID = getParserShortname() + 
            Integer.toString((new java.util.Random()).nextInt());
    }

    /**
     * Creates the List of TableBasedFiles from the given Element
     * 
     * @param configXml the XML file to construct the List of TableBasedFiles 
     * from
     * @throws java.lang.Exception
     */
    @Override
    public void configure(Element configXml) throws Exception {
        List<Element> l = configXml.getChildren(FILE_GROUP_TAG);
        for (Element e : l) {
            tableFiles.add(new TableBasedFile(e, this));
        }
        raiseEvent(events.reconfigured);
    }

    /**
     * Stops the parser if it is running; clears the list of TableBasedFiles; 
     * and recreates the List of TableBasedFiles from the given Element
     * 
     * @param configXml
     * @throws java.lang.Exception
     */
    public void reconfigure(Element configXml) throws Exception {
        //Stop this if running
        Stop();
        // clear this of its specific settings.
        tableFiles.clear();
        //then configure with supplied xml
        configure(configXml);
        raiseEvent(events.reconfigured);
    }

    /**
     * 
     * @return
     */
    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataParser.DATA_PARSER_TAG);
        e.setAttribute("type", DataParserTableBased.class.getName());
        for (TableBasedFile f : tableFiles) {
            e.addContent(f.getSettingsXml());
        }
        return e;
    }

    /**
     * If the repository of this data parser is null, return false.  If the 
     * repository isn't started, return false.  Otherwise, start each of the 
     * tableFiles at a fixed rate of 30 seconds.
     * 
     * @return true if the parser has been started
     */
    @Override
    public boolean Start() {
        timer = new Timer();
        if (this.getRepository() == null) {
            return false;
        }
        //if we can't start the children, return false
        if (!this.getRepository().Start()) {
            return false;
        }

        for (TableBasedFile f : tableFiles) {
            timer.scheduleAtFixedRate(f.getTimerTask(), 0, 30000);
        }
        raiseEvent(events.started);
        return true;
    }

    /**
     * Stops the parser.  Returns true.
     * 
     * @return true
     */
    @Override
    public boolean Stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (this.getRepository() != null) {
            this.getRepository().Stop();
        }
        raiseEvent(events.reconfigured);
        raiseEvent(events.stopped);
        return true;
    }

    /**
     * Returns true if the parser has started and false otherwise
     * 
     * @return true if the parser has started
     */
    @Override
    public boolean isStarted() {
        return timer != null;
    }

    @Override
    public String getParserDescription() {
        return "ToDo: add description here.";
    }

    /**
     * Returns the short name of this parser - "DataParserCSITableBased"
     * 
     * @return short name of this parser
     */
    @Override
    public String getParserShortname() {
        return "DataParserCSITableBased";
    }

    @Override
    public String toString() {
        return getParserShortname();
    }

    @Override
    public void setRepository(IDataRepository repo) {
        myRepository = repo;
    }

    @Override
    public IDataRepository getRepository() {
        return myRepository;
    }

    @Override
    public JPanel getStatusJPanel() {
        if(statusJPanel==null){
            logger.trace("Lazily create status JPanel.");
            statusJPanel = new JPanelTableBased(this);
        }
        return statusJPanel;
    }

    /**
     * Returns the panelID of this DataParser
     * 
     * @return panelID of this DataParser
     */
    @Override
    public String getPanelID() {
        return panelID;
    }

    
    public Vector<String> getMonitoredFiles() {
        Vector<String> toReturn = new Vector<String>();
        for (TableBasedFile f : tableFiles) {
            toReturn.add(f.getDataFilePath());
        }
        return toReturn;
    }
    
    public void removeMonitoredFile(int index){
        if(this.isStarted())
            return;
        
        tableFiles.remove(index);
        this.raiseEvent(events.reconfigured);
    }
    
    public TableBasedFile getMonitoredFile(int index){
        return tableFiles.get(index);
    }
    
    public void addMonitoredFile(TableBasedFile tbf){
        tableFiles.add(tbf);
    }

    /**
     * Returns the total number of rows that have been parsed
     * 
     * @return total number of rows that have been parsed
     */
    public long getRowsParsedTotal() {
        return rowsParsedTotal;
    }

    public Hashtable<Integer, String> getMalformedRows() {
        return malformedRows;
    }
    
    public TableBasedFile getNewFileObject(){
        return new TableBasedFile(this);
    }

//    public long getNumMalformedRows() {
//        return numMalformedRows;
//    }
    public class TableBasedFile {

        public static final String COLUMNS_TAG = "Columns";
        public static final String COLUMN_TAG = "Column";
        public static final int COLUMN_MISMATCH_LIMIT = 2;
        public static final String SOURCE_FILE_TAG = "SourceFile";
        public static final String HEADER_ROW_TAG = "HeaderRowCount";
        
        private IDateTimeParser dtParser;
        private ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
        private String dataFilePath;
        private long lastModified = 0;
        private int lastRowParsed = 0;
        private int headerRowCount = 0;
        private DataParserTableBased parent;

        public TableBasedFile(Element configXml, DataParserTableBased dptb) throws Exception {
            configure(configXml);
            parent = dptb;
            
            
        }
        
        public TableBasedFile(DataParserTableBased dptb){
            parent = dptb;
            dataFilePath = "No File Selected.";
            dtParser = new DateTimeParserTableBased();
        }

        /**
         * Returns the number of malformedRows in this TableBasedFile
         * 
         * @return number of malformed rows
         */
        public Hashtable<Integer, String> getMalformedRows() {
            return malformedRows;
        }

        public void configure(Element configXml) throws Exception {
            //Create a datetime parser from the xml specifying datetime parser
            dataFilePath = configXml.getChild(SOURCE_FILE_TAG).getText();

            dtParser = DateTimeParserFactory.getDateTimeParser(
                    configXml.getChild(IDateTimeParser.DATE_TIME_FORMAT_TAG));

            //if defined in config file, set it. Otherwise use default of 0.
            if(configXml.getChild(HEADER_ROW_TAG)!=null){
                this.setHeaderRows(
                        Integer.parseInt(configXml.getChildText(HEADER_ROW_TAG)));
            }

            List<Element> columnElements = configXml.getChild(COLUMNS_TAG).getChildren(COLUMN_TAG);

            for (int i=0;i<columnElements.size();i++) {
                columns.add(i,new DataColumn(columnElements.get(i)));
            }
        }

        public Element getSettingsXml() {
            Element e = new Element(FILE_GROUP_TAG);
            Element colRoot = new Element(COLUMNS_TAG);
            e.addContent(new Element(SOURCE_FILE_TAG).setText(dataFilePath));
            e.addContent(dtParser.getSettingsXml());
            e.addContent(colRoot);
            e.addContent((new Element(HEADER_ROW_TAG)).setText(
                    Integer.toString(headerRowCount)));

            for (DataColumn col : columns) {
                colRoot.addContent(col.getSettingsXml());
            }

            return e;
        }


        /**
         * Returns number of header rows the file has. Used to properly
         * skip irrelevant (non-data containing) headers.
         * @return Number of rows, zero means no header
         */
        public int getHeaderRows() {
            return headerRowCount;
        }

        /**
         * Sets the number of header rows for the file. Default is 0. Header
         * rows will simply be skipped.
         * @param headerRows
         */
        public void setHeaderRows(int headerRows) {
            if(headerRows <0)
                throw new IllegalArgumentException(
                        "Cannot have a negative header row count");
            
            this.headerRowCount = headerRows;
        }
        
        /**
         * Returns the dataFilePath for this TableBasedFile
         * 
         * @return data file path
         */
        public String getDataFilePath() {
            return dataFilePath;
        }
        
        public IDateTimeParser getDateTimeParser(){
            return dtParser;
        }
        

        
        public ArrayList<DataColumn> getColumns(){
            return columns;
        }
        
        public void addColumn(DataColumn col){
            columns.add(col);
        }
        
        public void removeColumn(int index){
            columns.remove(index);
        }

        public void setDateTimeParser(IDateTimeParser dtp) {
            dtParser = dtp;
        }

        /**
         * Changes the file that's to be monitored by this module and
         * resets the pointer and timestamp info (which is file specific)
         *
         * @param path Full path of the file to be monitored as a string.
         */
        public void setDataFilePath(String path){
            //Reset row pointer and file last modified date/time
            // otherwise it will use that info from the old file.
            setRowPointer(0);
            setLastModifiedPointer(0);
            dataFilePath = path;
            raiseEvent(events.reconfigured);
        }

        public void setRowPointer(int pointer){
            lastRowParsed = pointer;
            raiseEvent(events.reconfigured);
        }

        /**
         * Set the internal "File Last Modified" index to a custom value.
         * Often used to set it to zero when changing the monitored file.
         * @param newDateAsLong The new index as a long
         */
        public void setLastModifiedPointer(long newDateAsLong){
            lastModified = newDateAsLong;
            raiseEvent(events.reconfigured);
        }

        /**
         * Returns true if the file specified by the data file path exists and 
         * false otherwise
         * 
         * @return true if the file exists
         */
        private boolean fileExists() {
            return new File(dataFilePath).exists();
        }

        private boolean fileModified() {
            File toWatch = new File(dataFilePath);
            return toWatch.lastModified() > lastModified;
        }

        private boolean fileBeingActivelyWritten() {

            long length = new File(dataFilePath).length();
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
            //do nothing
            }
            return length != new File(dataFilePath).length();

        }

        public void checkFile() {


            raiseEvent(events.startDataParse);

            if (!fileExists() || !fileModified() || fileBeingActivelyWritten()) {
                raiseEvent(events.stopDataParse);
                return;
            }
            
            if (timer == null || !isStarted()) {
                return;
            }

            try {
                parseNewFileContent();
            } catch (ColumnNumberMismatchException cnme) {
                    statusJPanel.showMessageDialog(
                            "Excessive Column Mismatch \n" +
                            "Row Num:" + Integer.toString(cnme.lineNumber) +
                            " FileName:" + cnme.fileName);                
            }catch(Exception e){
                e.printStackTrace();
                    //parent.getStatusJPanel().showMessageDialog("Unknown error occured" +
                    //" while parsing text file.");

            } finally {
                raiseEvent(events.stopDataParse);
            }
//            try {
////                System.out.println("Parsing File Content.");
//                parseNewFileContent();
//            } catch (Exception e) {
//                //ToDo: Handle file parse error.
//                e.printStackTrace();
//
//            }
        }

        private void parseNewFileContent() throws Exception {

            File toRead = new File(dataFilePath);
            BufferedReader inputStream = null;

            try {
                inputStream = new BufferedReader(new FileReader(toRead));

                int currentRow = 0;
                String inLine;
                String[] splitLine;
                int colMalformNum = 0;

                while ((inLine = inputStream.readLine()) != null) {
                    if(!isStarted()){
                        break;
                    }
                    //Skip header rows
                    if(currentRow < headerRowCount){
                        currentRow++;
                        continue;
                    }
                    //Quickly skip rows that were already done.
                    if (currentRow < lastRowParsed) {
                        currentRow++;
                        continue;
                    } else {
                        currentRow++;
                    }


                    try {//Try parsing the individual lines
                        splitLine = inLine.split(DELIMITER,-1);



                        myRepository.NewRow(parseRow(splitLine));

                        //if we get here, reset colMismatchNum to zero.
                        colMalformNum = 0;
                        rowsParsedTotal++;
                        raiseEvent(DataParserTableBased.events.rowParsed);
                    }catch (Exception e) {
                        if (e instanceof ColumnNumberMismatchException ||
                                e instanceof java.text.ParseException) {
                            //In this case, we want to continue, but make note of this problem.
                            if (colMalformNum < COLUMN_MISMATCH_LIMIT) {
                                colMalformNum++;
//                                numMalformedRows++;
                                raiseEvent(DataParserTableBased.events.newMalformedRow);
                                malformedRows.put(currentRow, inLine);
                                continue;
                            } else {

                                ColumnNumberMismatchException exp = 
                                        new ColumnNumberMismatchException(
                                        "Excessive Column Mismatch Error");
                                exp.lineNumber = currentRow;
                                exp.fileName = this.getDataFilePath();
                                throw exp;
                            }
                        } else {
                            throw e;
                        }
                    }
                }//readLine while loop

                lastRowParsed = currentRow;
                lastModified = toRead.lastModified();
                
            } catch (Exception e) {
                throw e;
            } finally {
                if(inputStream != null){
                    inputStream.close();
                }
            }
        }

        public ArrayList<ValueObject> parseRow(String[] values)
                throws ColumnNumberMismatchException, NumberFormatException {

            if (values.length != columns.size()) {
                throw new ColumnNumberMismatchException(
                        "Expected:" + columns.size() +
                        " Passed:" + values.length);
            }


            ArrayList<ValueObject> output = new ArrayList<ValueObject>(values.length);
            Date rowDate = null;
            try {
                rowDate = dtParser.ParseDate(values);
                if(rowDate.after(new Date())){
                    logger.error(values);
                }
            } catch (Exception e) {
                parent.Stop();
                JOptionPane.showMessageDialog(statusJPanel, "Error parsing the date!"
                        + "\nMake sure you've set up your DateTime settings correctly.");
                logger.error("Error parsing the date: ");
                e.printStackTrace();
                throw new NumberFormatException();
            }

            ValueObject tmp;
            for (int i = 0; i < values.length; i++) {
                try{
                    if (!columns.get(i).ignoreColumn()) {
                        tmp = columns.get(i).getNewValue();
                        //This trims and compares to nan, and also removes double quotes
                        //in the future, probably a better way to deal with NaN and 
                        //NaN surrounded by double quotes
                        if (values[i].trim().replace("\"", "").compareToIgnoreCase("nan") == 0 ||
                                values[i].replace("\"","").compareToIgnoreCase("-inf") == 0) {
                            tmp.setValue(Double.NaN);
                        } else {
                            tmp.setValue(Double.valueOf(values[i]));
                        }

                        tmp.setTimeStamp(rowDate);
                        output.add(tmp);
                        tmp = null;
                    }
                
                }catch(NumberFormatException nfe){
                    //ignore for now.
                }
            }
            return output;
        }

        public TimerTask getTimerTask() {
            return new TimerTask() {

                @Override
                public void run() {
                    checkFile();
                }
            };
        }
    }

    public void addEventHandler(TableBasedEventHandler toAdd) {
        eventHandlers.add(toAdd);
    }

    public void removeEventHandler(TableBasedEventHandler toRmv) {
        eventHandlers.remove(toRmv);
    }

    public void raiseEvent(events e) {
        for (TableBasedEventHandler h : eventHandlers) {
            h.eventRaised(e);
        }
    }

    public interface TableBasedEventHandler {
        public void eventRaised(DataParserTableBased.events v);
    }

}