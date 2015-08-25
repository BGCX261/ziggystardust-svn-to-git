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

package com.wisc.csvParser;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import java.io.*;
import java.util.ArrayList;
import com.wisc.csvParser.notificationProviders.*;
import com.wisc.csvParser.plugins.JDialogUpdateZiggy;
import java.beans.PropertyChangeListener;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * @author  lawinslow
 */
public class ZiggyStardust {
    
    /** The path where all program settings are stored. */
    private String autoSettingsPath = System.getProperty("user.home") + "/ZiggySettings.xml";

    /** True if running with no GUI in headless mode (possibly as service). */
    private static boolean runAsService = false;
    /** Force application to attempt autoload of settings from supplied file path. */
    private static boolean forceAutoload = false;
    /** Path used when ZiggyStardust is forced to autload settings. */
    private static String forceAutoloadPath;
    /** Logger object. */
    private static Logger logger = Logger.getLogger("ZiggyStardust");

    

    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private ArrayList<DataChain> chains = new ArrayList<DataChain>();
    private CoreGUI appGUI;
    private boolean isLoaded = false;
    private int startupVersion = 131; // Right now, just hardcoding version number each time...
    private boolean alreadyPoppedUp = false;

    /** Creates new core application object for ZiggyStardust. */
    public ZiggyStardust() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                    new TimerTask(){
                    @Override
                    public void run(){
                        if (checkCurrentVersion() && !alreadyPoppedUp) {
                            JDialogUpdateZiggy jd = new JDialogUpdateZiggy(null,true);
                            jd.setVisible(true);
                            alreadyPoppedUp = true;
                        }
                    }
                }, 0, 86400000); // Check every day
                

        try{
            DOMConfigurator.configure("log4jConfiguration.xml"); 
        }catch(Exception e){
            try{
                DOMConfigurator.configure("dist/log4jConfiguration.xml"); 
            }catch(Exception ex){
                //do nothing
            }
        }
        
        logger.info("Logger Setup. Starting App.");       
        
        
        //If running as a service, we want it to properly cleanup
        // on stop. Must add shutdown hook in order to be properly notified.
        if(ZiggyStardust.isRunAsService()){
            logger.info("Running as headless server. Create shutdown hook.");
            ShutdownHook shutdownHook = new ShutdownHook();
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }else{
            logger.info("Not running headless. Building GUI.");
            appGUI = new CoreGUI(this);
            appGUI.setVisible(true);
        }
        
        setupSettings();
        
        GlobalProgramSettings.vocabProvider = 
            new com.wisc.csvParser.vocabProviders.GLEONVocabProvider();
        GlobalProgramSettings.vocabProvider.updateLocalVocab();
        
    }

    private boolean checkCurrentVersion() {
        try {
            URL file = new URL("http://ziggystardust.googlecode.com/files/ziggyVersion.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.openStream()));
            int newVersion = Integer.parseInt(reader.readLine());

            if (newVersion > startupVersion) {
                reader.close();
                return true;
            } else {
                reader.close();
                return false;
            }
        } catch(Exception e) {
            return false; // Fail gracefully
        }
    }
    
    private void setupSettings(){       
            logger.info("Attempting to auto-load settings.");
            if (forceAutoload) {
                this.loadSettingsFromFile(forceAutoloadPath);
            } else {
                this.loadSettingsFromFile(autoSettingsPath);
            }
    }

    public void saveAndClose(){
        
        //If we're running as a service, do NOT autosave settings
        // they can not have changed while running as a service
        if(!ZiggyStardust.isRunAsService()){
            logger.trace("Saving settings.");
            this.saveSettings();
        }else{
            logger.trace("It is running as a service.");
        }
            
        logger.trace("Stopping chains before closing.");
        for(DataChain c:chains){
            c.Stop();
        }
        //Dispose of GUI component.
        if(appGUI != null){
            appGUI.setVisible(false);
            appGUI.dispose();
        }
        
        if(!ZiggyStardust.runAsService){
            //It hangs here if running in headless mode, but it hangs
            // if it DOESN'T have this line in non-headless mode. Hmmm.           
            System.exit(0);
        }
    }
    /**
     * Persists all important objects and saves settings to XML file
     * specified in the settings dialog (or by the command line argument). If
     * settings path not specified,
     */
    public void saveSettings(){
        if (forceAutoload) {
            this.saveSettingsToFile(forceAutoloadPath);
        } else {
            this.saveSettingsToFile(autoSettingsPath);
        }
        //saveSettingsToFile(autoSettingsPath);
    }

    /**
     * Save settings to specific file path as given. All currently created
     * settings and DataChains are saved.
     * @param filePath Path to file location to save settings.
     */
    public void saveSettingsToFile(String filePath){
        
        logger.trace("Begin saving settings to file.");
        FileOutputStream fos;

        Document d = new Document();
        
        Element root = new Element("Chains");
        XMLOutputter out = new XMLOutputter();
        
        root.addContent(GlobalProgramSettings.provider.getSettingsXml());
    
        logger.trace("Get XML settings for each chain.");
        for(DataChain j:chains){
            root.addContent(j.getSettingsXml());
        }
        d.setRootElement(root);
          
        logger.trace("Write settings to:" + filePath);
        try{
            fos = new FileOutputStream(filePath);    
            out.output(d, fos);
            fos.flush();
            fos.close();
            logger.trace("Settings file written and closed.");
        }catch(FileNotFoundException e){
            logger.error(e.getStackTrace());
        }catch(IOException e){
            logger.error(e.getStackTrace());
        }
    }

    /**
     * Save settings to specific file path as given. All currently created
     * settings and DataChains are saved.
     * @param filePath Path to file location to save settings.
     */
    public void saveChainToFile(String filePath, DataChain chain){

        logger.trace("Begin saving settings to file.");
        FileOutputStream fos;

        Document d = new Document();

        Element root = new Element("DataChain");
        XMLOutputter out = new XMLOutputter();

        root.addContent(GlobalProgramSettings.provider.getSettingsXml());

        logger.trace("Get XML settings for selected chain.");
        root.addContent(chain.getSettingsXml());
        d.setRootElement(root);

        logger.trace("Write settings to:" + filePath);
        try{
            fos = new FileOutputStream(filePath);
            out.output(d, fos);
            fos.flush();
            fos.close();
            logger.trace("Settings file written and closed.");
        }catch(FileNotFoundException e){
            logger.error(e.getStackTrace());
        }catch(IOException e){
            logger.error(e.getStackTrace());
        }
    }
    
    /**
     * Add DataChain to main application. DataChain must be fully build but not
     * necessarily started (although it can be).
     *
     * @param chain DataChain object to add to main application.
     */
    public void addChain(DataChain chain){
        //DataChain must be properly formed
        if(chain.getSourceParser()==null){
            //TODO: Do something here
        }
        chains.add(chain);
        support.firePropertyChange("chainadded", null, chain);
    }
    /**
     * Disposes of and removes DataChain object from application. This is
     * a non-reversable operation. The data chain's processes will be stopped
     * and it will be discarded (and its body returned to nature).
     *
     * @param chain DataChain to dispose of and remove from application.
     */
    public void removeChain(DataChain chain){
        //Clean up any running processes, memory, etc.
        chain.dispose();
        chains.remove(chain);
        support.firePropertyChange("chainremoved",chain,null);
    }

    public void removeAllChains(){
        for(DataChain chain:chains) {
            chain.dispose();
            support.firePropertyChange("chainremoved",chain,null);
        }
        chains.clear();
    }


    public void loadSettingsFromFile(String filePath){
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try{
            if(!(new File(filePath)).exists()){
                logger.error("Settings file does not exist. \n" +
                        "FILE:" +filePath);
                return;
            }
            doc = builder.build(new File(filePath));
            Element np = doc.getRootElement().getChild(
                    INotificationProvider.NOTIFICATION_PROVIDER_TAG);
            if(np != null){
                try{
                    Class c = Class.forName(np.getAttributeValue("type"));
                    GlobalProgramSettings.provider = 
                            (INotificationProvider)c.newInstance();
                    GlobalProgramSettings.provider.configure(np);
                    GlobalProgramSettings.provider.start();
                    
                }catch(Exception cnf){
                    cnf.printStackTrace();
                    GlobalProgramSettings.provider = new NullNotificationProvider();
                }
            }
            
            //TODO: Add settings for vocab provider. nothing right now


            for(Object el:doc.getRootElement().getChildren(DataChain.DATA_CHAIN_TAG)){
                if(el != null){
                    if (isLoaded) {
                        this.addChain(new DataChain((Element)el, true));
                    } else {
                        this.addChain(new DataChain((Element)el));
                    }
                }
            }


            
        }catch(JDOMException je){
            logger.error("Error parsing XML Settings File.\n" + je.getMessage());
        }catch(IOException ie){
            logger.error("Error reading XML Settings File.\n" + ie.getMessage());
        }
    }
    
    
    /**
     * Boolean indicating if this program should be running invisibly
     * with not GUI or not. Can be used to run program as a service.
     * 
     * @return true if program is running with GUI
     */
    public static boolean isRunAsService(){
        return runAsService;
    }
    
    /**
     * Main method. Launches ZiggyStardust application. A few potential 
     * command line options can change startup behavior.
     *
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        
        for(String s:args){
            if(s.toLowerCase().compareTo("--service")==0){
                runAsService = true;
            }else if(s.toLowerCase().compareTo("--autoload")==0){
                forceAutoload = true;
            }else if(s.toLowerCase().contains("--settings:")){
                try{
                    forceAutoloadPath = s.split(":")[1];                        
                }catch(Exception e){
                    logger.error("Must specify settings path when forcing autoload");
                }
            }else if(s.contains("--help") || s.contains("")){
                printCommandlineHelp();
                return;
            }
        }
        
        if(forceAutoload && forceAutoloadPath == null){
            logger.error("Must specify settings path when forcing autoload");
            return;
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ZiggyStardust();
            }
        });
    }

    public static void printCommandlineHelp(){
        System.out.println("ZiggyStardust Help:");
        System.out.print("--service\n" +
                        " Runs application as headless. No GUI. \n" +
                        " No settings can be changed in this mode.\n" +
                        "\n" +
                        "--settings:<filePath>\n" +
                        " Forces application to load settings from specified file.\n" +
                        " Fails if file does not exist.\n" +
                        "\n" +
                        "--autoload\n" +
                        " Forces application to autoload settings from setting \n" +
                        " file specified with --settings: or saved in application \n" +
                        " settings stored in registry.");

    }
    
    class ShutdownHook extends Thread {
        @Override
        public void run() {
            saveAndClose();
        }
    }

    /**
     * Adds property change listener to handle data chain remove and add events.
     * @param listener Listener to be added to notification list.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes property change listener to handle data chain remove and add events.
     * @param listener Listener to be removed from notification list.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener){
        support.removePropertyChangeListener(listener);
    }

    public boolean isLoadChain() {
        return isLoaded;
    }

    public void setLoadChain(boolean l) {
        isLoaded = l;
    }
}

