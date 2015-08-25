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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;


public class UploadHandler implements Runnable{
    String filePath;
    String ftpHostname;
    String username;
    String password;
    String serverDirectory;
    public FTPClient ftp;
    public InputStream fis;
    private static Logger logger = Logger.getLogger(UploadHandler.class);

    //indicates failure, in case thread is halted
    public boolean exitState = false; 

    UploadHandler(String filePath,
        String ftpHostname,String username, String password,
        String serverDirectory){
        this.filePath = filePath;
        this.ftpHostname = ftpHostname;
        this.username = username;
        this.password = password;
        this.serverDirectory = serverDirectory;
    }

    @Override
    public void run(){
        
        File file;

        try{
            file = new File(filePath);
            //If we can't write to the file, we won't be able to delete it later
            // This must mean it is in either a weird state, or is *currently*
            // being written to.
            if(!file.canWrite()){
                Thread.sleep(5000);
                if(!file.canWrite()){
                    logger.error("Filed to gain write access to file:"+filePath);
                    exitState = false;
                    return;
                }
            }
            fis = new BufferedInputStream(new FileInputStream(filePath));

        }catch(FileNotFoundException e){
            logger.error("File not found:"+filePath);
            exitState = false;
            return;
        }catch(java.lang.InterruptedException ie){
            logger.error("Upload thread halted or interrupted on file:"+filePath);
            //do nothing for now.
            exitState = false;
            return;
        }

        ftp = new FTPClient();
        ftp.setDefaultTimeout(20*1000);
        boolean uploadSuccessful = false;


        try{

            ftp.enterLocalPassiveMode();
            //Set connection timeout to 30 seconds
            //ftp.setConnectTimeout(30*1000);
            ftp.connect(ftpHostname);
            ftp.login(username, password);
            logger.trace("FTP Logged In and connected");

            //We are connected, yay.
            //Check that connect completed successfully
            if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())){
                exitState = false;
                return;
            }

            //Timeout this FTP Client in 10 min to prevent ftp upload stalling


            //Setup connection mode
            
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            logger.trace("FTP Timeout Set and File Type Set");

            //change to correct upload directory and upload file
            ftp.changeWorkingDirectory(serverDirectory);
            ftp.storeUniqueFile(file.getName(), fis);
            logger.trace("File Uploaded");

            //not sure if this is necessary, but it seems to work fine.
            if(FTPReply.isPositiveIntermediate(ftp.getReplyCode())){
                System.out.println("Completing pending command");
                ftp.completePendingCommand();
            }

            //If we get here, upload was successful
            uploadSuccessful = true;

        }catch(Exception e){
            try{
                ftp.disconnect();
            }catch(Exception de){
                e.printStackTrace();
            }
            logger.error("Error uploading file:",e);
        }finally{

            try{
                fis.close();
                logger.trace("File closed");
                ftp.logout();
                logger.trace("FTP Logged Out");
                ftp.disconnect();
                logger.trace("FTP Disconnected");


                if(uploadSuccessful){
                    logger.info("Deleting file:" + new File(filePath).getName());

                    if(!(new File(filePath)).delete()){
                        logger.error("Can't delete file for some reason");
                    }
                    
                    //wait for the file to delete.
                    Thread.sleep(1000);
                    exitState = true;
                    return;
                }

            }catch(Exception e){
                logger.error("Exception on file upload cleanup:",e);
                exitState = false;
                return;
            }
        }
        exitState = false;
        return;

    } //end run
}//close UploadHandler Object