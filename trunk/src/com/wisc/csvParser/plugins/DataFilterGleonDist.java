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
import org.jdom.Document;
import org.apache.commons.net.ftp.*;
import java.util.*;
import org.jdom.output.XMLOutputter;
import java.io.*;
import java.util.Timer;
import org.apache.log4j.Logger;

        
/**
 *
 * @author lawinslow
 */
public class DataFilterGleonDist extends java.util.TimerTask implements IDataRepository{
    
    
    /**
     * Event enum. Simple way to create different events that can occur.
     */
    public enum events{
        newFile,
        fileUploaded,
        connectionFailed,
        connectionStarted,
        reconfigured,
        started,
        stopped
    }
    
    private static final String DOCUMENT_ROOT_TAG = "ValueObjects";
    private static final String FTP_HOST_TAG = "FtpHost";
    private static final String FTP_USER_TAG = "FtpUser";
    private static final String FTP_PASS_TAG = "FtpPass";
    private static final String FTP_PATH_TAG = "FtpPath";
    //private static final String TEMP_PATH_TAG = "TempPath"; // will be ignored regardless
    private static final String TEMP_SUBPATH_TAG = "TempSubPath"; // if this exists, just load and store using this, concactinated, if it doesn't
    // then just create a new path using this tag

    //15 minutes should be a reasonable upload timeout for one file, max 3MB
    private static final long UPLOAD_TIMEOUT_MILLS = 15*60*1000;

    /**
     * The number of values cached before forcing a new file to be written.
     */
    public static int VALUE_LIMIT = 10000;
    private JPanelGleonDist statusPanel;
    private String tempFileDirectory;
    private String tempSubDirectory; // need to use this
    private Element docRoot;
    private int valCount = 0;
    private int filesUploadedCount = 0;
    private Timer timer = new Timer();
    private Date lastNewValueTime;
    
    private String ftpHost = "data.gleon.org";//default gleon ftp server
    private String ftpUser = "gleonUser";//default gleon user
    private String ftpPass = "pass4gleon";//default gleon password
    private String ftpFilePath = "gleonData/";//default upload path
    
    private String panelID;//unique panel id 
    private IDataRepository child;
    private ArrayList<IEventHandler> handlers = new ArrayList<IEventHandler>();
    private boolean started = false;
    private static Logger logger = Logger.getLogger(DataFilterGleonDist.class);


    /**
     * This module caches the data in xml files and using ftp, uploads
     * to a server of the user's choosing. Used originally in the GLEON
     * network, gleon is a name used throughout TODO: Remove 'GLEON' names
     */
    public DataFilterGleonDist(){
        docRoot = new Element(DOCUMENT_ROOT_TAG);
        
        panelID = getRepositoryShortname() + 
            Integer.toString((new java.util.Random()).nextInt());

        //ased the temp file directory on the random 'panelID' so that
        // we have an instance specific directory. A shared directory could
        // cause files to be mixed up if they are going to different places
        tempSubDirectory = "\\gleonCache\\"+panelID+"\\";
        this.setTempDirectory(System.getProperty("java.io.tmpdir")
                +tempSubDirectory);
        lastNewValueTime = new Date();
        
        timer.scheduleAtFixedRate(this, 10000, 1000);
    }
    
    public void setTempDirectory(String tempDirectory){
        tempFileDirectory = tempDirectory;
    }

    public String getTempDirectory(){
        return this.tempFileDirectory;
    }
    
    public void setUploadPath(String path){
        this.ftpFilePath = path;
    }

    public String getUploadPath(){
        return this.ftpFilePath;
    }

    private synchronized void NewValueLocal(ValueObject v){
        if(v.getTimeStamp().after(new Date())){
            logger.error("Date in future!");
        }
        valCount++;
        docRoot.addContent(v.getAsXml());
        lastNewValueTime = new Date();
        if(valCount > VALUE_LIMIT)
            writeOut();
    }
    /**
     * Only allow one thread to attempt write out at once.
     * May not really need to be synchronized.
     */
    private synchronized void writeOut(){
        //first, make sure our temp directory exists
        new File(tempFileDirectory).mkdirs();


        XMLOutputter out = new XMLOutputter();
        Document doc = new Document();
        Date d = new Date();
        String newFileName = tempFileDirectory + "valsToSend"
                + Long.toString(d.getTime()) + ".xml";

        
        doc.addContent(docRoot);
        docRoot = new Element(DOCUMENT_ROOT_TAG);
        valCount = 0;

        try{
            java.io.FileWriter writer = new java.io.FileWriter(newFileName);
            out.output(doc, writer);
            writer.flush();

            writer.close();
            

        }catch(java.io.IOException e){
            logger.error(e.getMessage());
        }
        raiseEvent(events.newFile);
    }
    
    /**
     * This checks the cache folder and attempts to upload any saved files
     * that have yet to be uploaded.
     */
    public void uploadFiles(){
        File tmpDir = new File(tempFileDirectory);
        File[] filesToUpload = tmpDir.listFiles();
        


        if(filesToUpload == null || filesToUpload.length <1){
            return;
        }

        //upload first file in directory.
        if(uploadFile(filesToUpload[0].getAbsolutePath(),ftpHost,
                ftpUser,ftpPass,ftpFilePath)){
            filesUploadedCount++;
            raiseEvent(events.fileUploaded);
        }
    }

    //Synchronized and static because the program can only upload one file
    // at a time. If a second attempt at the same time is made from another
    // thread, it waits until the previous one exits
    private static synchronized boolean uploadFile(String filePath,
            String ftpHostname,String username, String password,
            String serverDirectory){

        UploadHandler up = new UploadHandler(filePath,ftpHostname,
                username, password, serverDirectory);

        Thread th = new Thread(up);

        Date start = new Date();
        th.start();

        try{
            while(true){
                Thread.sleep(100);
                if(!th.isAlive()){
                    break;
                }
                if((new Date()).getTime()-start.getTime() > 10*60*1000){
                    up.fis.close();
                    up.ftp.abort();
                    logger.error("File Upload timed out:" + filePath);
                    break;
                }
            }
                
            th.isAlive();
            th.join(UPLOAD_TIMEOUT_MILLS);  
            
        }catch(InterruptedException ie){//means it timed out of course
            return false;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return up.exitState;
    }
    
    public int getCachedFilesCount(){
        File tmpDir = new File(tempFileDirectory);
        return tmpDir.listFiles().length;
    }
    public int getUploadedFilesCount(){
        return filesUploadedCount;
    }
    
    public void setUsername(String username){
        ftpUser = username;
    }
    public void setPassword(String pass){
        ftpPass = pass;
    }
    public void setFtpHost(String host){
        ftpHost = host;
    }
    public String getUsername(){
        return ftpUser;
    }
    public String getPassword(){
        return ftpPass;
    }
    public String getFtpHost(){
        return ftpHost;
    }
    @Override
    public void run(){
        //If the last value came over 1 minute (60 sec) ago, write out
        if(valCount >0 && new Date().getTime() - lastNewValueTime.getTime() > 60*1000)
            writeOut();
    }
    
    @Override
    public boolean NewRow(ArrayList<ValueObject> newRow) {
        if(child != null){
            child.NewRow(newRow);
        }
        for(ValueObject v:newRow){
            NewValueLocal(v);
        }
        return true;
    }

    @Override
    public boolean NewValue(ValueObject newValue) {
        if(child!= null){
            child.NewValue(newValue);
        }
        NewValueLocal(newValue);
        return true;
    }
    
    @Override
    public boolean Start() {
        if((child != null && child.Start()) ||
                child == null){
            timer = new Timer();
            timer.scheduleAtFixedRate(
                    new TimerTask(){
                        @Override
                        public void run(){
                            uploadFiles();
                        }
                    }
                , 0, 10000
            );
            started = true;
            raiseEvent(events.started);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean Stop() {
        writeOut();
        //Take this out, makes it very slow to close program
        //   could be a real problem on slow internet connections.
        //uploadFiles();
        if(timer != null){
            timer.cancel();
            timer = null;           
        }

        started = false;
        raiseEvent(events.stopped);
        if(child != null){
            return true && child.Stop();
        }else{
            return true;
        }
    }

    @Override
    public void configure(Element e) throws Exception {
        
        this.setUsername(e.getChildText(FTP_USER_TAG));
        this.setPassword(e.getChildText(FTP_PASS_TAG));
        this.setFtpHost(e.getChildText(FTP_HOST_TAG));

        if(e.getChild(TEMP_SUBPATH_TAG) != null){
            this.setTempDirectory(System.getProperty("java.io.tmpdir") + 
                    e.getChildText(TEMP_SUBPATH_TAG));
        } else {
            this.setTempDirectory(System.getProperty("java.io.tmpdir") + 
                    tempSubDirectory);
        }

        if(e.getChild(FTP_PATH_TAG)!=null){
            this.setUploadPath(e.getChildText(FTP_PATH_TAG));
        }
        
        raiseEvent(events.reconfigured);
    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataRepository.DATA_REPOSITORY_TAG);
        e.setAttribute("type",DataFilterGleonDist.class.getName());
        
        e.addContent(new Element(FTP_USER_TAG).setText(this.getUsername()));
        e.addContent(new Element(FTP_PASS_TAG).setText(this.getPassword()));
        e.addContent(new Element(FTP_HOST_TAG).setText(this.getFtpHost()));
        e.addContent(new Element(TEMP_SUBPATH_TAG).setText(tempSubDirectory));
        e.addContent(new Element(FTP_PATH_TAG).setText(this.getUploadPath()));

        return e;
    }

    @Override
    public IDataRepository getChildRepository() {
        return child;
    }

    @Override
    public String getRepositoryDescription() {
        return "This Filter saves the data as text files and sends it automatically " +
                "to a central GLEON repository";
    }

    @Override
    public String getRepositoryShortname() {
        return "GLEON Upload";
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void setChildRepository(IDataRepository child) {
        this.child = child;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }

    @Override
    public JPanel getStatusJPanel() {
        if(statusPanel==null){
            statusPanel = new JPanelGleonDist(this);
        }
        return statusPanel;
    }
    
    @Override
    public String toString(){
        return getRepositoryShortname();
    }
    
    public void dispose(){
        
    }
    
    public void addEventHandler(IEventHandler e){
        handlers.add(e);
    }
    
    public void raiseEvent(events e){
        for(IEventHandler h:handlers)
            h.eventRaised(e);
    }
    
    public interface IEventHandler{
        public void eventRaised(events e);
    }
    

}// close DataFilterGleonDist object
