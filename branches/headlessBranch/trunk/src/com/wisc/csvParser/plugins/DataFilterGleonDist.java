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
    private static final String TEMP_PATH_TAG = "TempPath";
    
    /**
     * The number of values cached before forcing a new file to be written.
     */
    public static int VALUE_LIMIT = 10000;
    private JPanelGleonDist statusPanel;
    private String tempFileDirectory;
    private Element docRoot;
    private int valCount = 0;
    private int filesUploadedCount = 0;
    private Timer timer = new Timer();
    private Date lastNewValueTime;
    
    private String gleonFtpPath = "data.gleonrcn.org";//default gleon ftp server
    private String gleonFtpUser = "gleonUser";//default gleon user
    private String gleonFtpPass = "pass4gleon";//default gleon password
    private String gleonFilePath = "/gleonData/";//default upload path
    
    private String panelID;//unique panel id 
    private IDataRepository child;
    private ArrayList<IEventHandler> handlers = new ArrayList<IEventHandler>();
    private boolean started = false;
    
    //static boolean to prevent concurrent uploads by different instances
    //TODO: Fix this, there must be a better way, perhaps a static, synchronized
    //method for uploads
    private static boolean isUploading = false;
    
    
    public DataFilterGleonDist(){
        docRoot = new Element(DOCUMENT_ROOT_TAG);
        
        panelID = getRepositoryShortname() + 
            Integer.toString((new java.util.Random()).nextInt());

        //ased the temp file directory on the random 'panelID' so that
        // we have an instance specific directory. A shared directory could
        // cause files to be mixed up if they are going to different places
        tempFileDirectory = System.getProperty("java.io.tmpdir")
                +"\\gleonCache\\"+panelID+"\\";
        
        lastNewValueTime = new Date();
        timer.scheduleAtFixedRate(this, 10000, 1000);
    }
    
    public void setTempDirectory(String tempDirectory){
        tempFileDirectory = tempDirectory;
    }
    
    public void setUploadPath(String path){
        this.gleonFilePath = path;
    }

    public String getUploadPath(){
        return this.gleonFilePath;
    }

    private synchronized void NewValueLocal(ValueObject v){
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
            e.printStackTrace();
        }
        raiseEvent(events.newFile);
    }
    
    /**
     * This checks the cache folder and attempts to upload any saved files
     * that have yet to be uploaded.
     */
    public void uploadFiles(){
        
        if(!isUploading){
            isUploading = true;
            
            File tmpDir = new File(tempFileDirectory);
            File[] filesToUpload = tmpDir.listFiles();
            
            
            if(filesToUpload == null || filesToUpload.length <1){
                isUploading = false;
                return;
            }
            uploadFile(filesToUpload[0].getAbsolutePath());
            isUploading = false;
        }

    }
    
    private void uploadFile(String filePath){
        
        FileInputStream fis;
        File file;
        
        try{
            file = new File(filePath);
            fis = new FileInputStream(filePath);
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        
        FTPClient ftp = new FTPClient();
        boolean uploadSuccessful = false;
        
        try{
            
            
            ftp.connect(gleonFtpPath);
            ftp.login(gleonFtpUser, gleonFtpPass);
            
            //We are connected, yay.
            if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())){
                return;
            }
            
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(gleonFilePath);
            ftp.storeFile(file.getName(), fis);
            
            if(FTPReply.isPositiveIntermediate(ftp.getReplyCode())){
                System.out.println("Completing pending command");
                ftp.completePendingCommand();
            }
            
            filesUploadedCount++;
            raiseEvent(events.fileUploaded);
            uploadSuccessful = true;
            
        }catch(Exception e){
            if(e instanceof java.net.ConnectException){
                e.printStackTrace();
            }else{
                e.printStackTrace();
            }

        }finally{
            
            try{
                fis.close();
                ftp.logout();
                ftp.disconnect();
                
                if(uploadSuccessful){
                    new File(filePath).delete();
                    System.out.println("Deleting file:" + filePath);
                    Thread.sleep(1000);
                }
                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    
    public int getCachedFilesCount(){
        File tmpDir = new File(tempFileDirectory);
        return tmpDir.listFiles().length;
    }
    public int getUploadedFilesCount(){
        return filesUploadedCount;
    }
    
    public void setUsername(String username){
        gleonFtpUser = username;
    }
    public void setPassword(String pass){
        gleonFtpPass = pass;
    }
    public void setFtpHost(String host){
        gleonFtpPath = host;
    }
    public String getUsername(){
        return gleonFtpUser;
    }
    public String getPassword(){
        return gleonFtpPass;
    }
    public String getFtpHost(){
        return gleonFtpPath;
    }
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
        
        gleonFtpUser = e.getChildText(FTP_USER_TAG);
        gleonFtpPass = e.getChildText(FTP_PASS_TAG);
        gleonFtpPath = e.getChildText(FTP_HOST_TAG);

        if(e.getChild(TEMP_PATH_TAG) != null){
            tempFileDirectory = e.getChildText(TEMP_PATH_TAG);
        }
        
        raiseEvent(events.reconfigured);
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
    public Element getSettingsXml() {
        Element e = new Element(IDataRepository.DATA_REPOSITORY_TAG);
        e.setAttribute("type",DataFilterGleonDist.class.getName());
        
        e.addContent(new Element(FTP_USER_TAG).setText(gleonFtpUser));
        e.addContent(new Element(FTP_PASS_TAG).setText(gleonFtpPass));
        e.addContent(new Element(FTP_HOST_TAG).setText(gleonFtpPath));
        e.addContent(new Element(TEMP_PATH_TAG).setText(tempFileDirectory));


        return e;
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
    
    public class UploadHandler{
        
        
    }
}
