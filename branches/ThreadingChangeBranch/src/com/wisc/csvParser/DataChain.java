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
import java.beans.PropertyChangeListener;
import java.util.Vector;
import java.util.List;
import javax.swing.*;
import org.jdom.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;
import java.beans.PropertyChangeSupport;

/**
 * This is the object which holds and deals with the data chains within ZS.
 *
 * @author lawinslow
 */
public class DataChain {
    /** Main DataChain tag holding all persisted attributes and subobjects. */
    public static final String DATA_CHAIN_TAG = "DataChain";
    /** XML tag that holds filter collection. */
    public static final String FILTERS_TAG = "Filters";
    /** XML Tag for tag storing started/stopped state. */
    public static final String ISSTARTED_TAG = "Started";
    /** XML Tag for data chain name. */
    public static final String DATA_CHAIN_NAME_TAG = "ChainName";
    /**
     * Enumeration defining direction to move filter in chain
     * (up or down in chain)
     */
    public static enum direction{
        UP,
        DOWN
    }
    /** The logger object for this object. */
    private Logger logger = Logger.getLogger("CoreGUI");

    /** Name to display at top of tab. For human consumption only. */
    private String chainName = "";
    /** The primary data source at the top of the data chain. Required to run. */
    private IDataParser sourceParser = null;
    /**
     * Intermediate filters and repositories between the data source and
     * terminal repository. No intermediates are required.
     */
    private Vector<IDataRepository> intermediateFilters 
            = new Vector<IDataRepository>();
    /** Final and Terminal repository for the data. Cannot be null to run.*/
    private IDataRepository finalRepository = null;
    /** The UI display for this data chain. Can be null if running headless. */
    private JPanel overviewTab = null;
    /** Property change support to handle property change events and listeners. */
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    /** Creates a new instance of DataChain */
    public DataChain(){
        this(null);
    }
    /**
     * Builds a new instance of DataChain and builds chains and sets settings
     * according to those supplied in the XML Element object.
     * @param e XML element object with persisted settings to reload.
     */
    public DataChain(Element e) {
        try{
            if(e != null){
                configure(e);
            }
        }catch(Exception ex){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            ex.printStackTrace(pw);
            pw.flush();
            sw.flush();

            JOptionPane.showMessageDialog(null, "Error Parsing XML Settings. "
                    + sw.toString());
        }
    }
    public void configure(Element xml)throws Exception{

        //setup final repository object.
        this.setFinalRepository(
                DataRepositoryFactory.getDataRepository(
                xml.getChild(IDataRepository.DATA_REPOSITORY_TAG)));


        //setup data source object.
        this.setSourceParser(
                DataParserFactory.getParser(
                xml.getChild(IDataParser.DATA_PARSER_TAG)));
        if(xml.getChild(DATA_CHAIN_NAME_TAG) != null){
            this.setChainName(xml.getChild(DATA_CHAIN_NAME_TAG).getText());            
        }

        //setup filter objects if any. 
        List<Element> filtersTmp = 
                xml.getChild(FILTERS_TAG).getChildren(IDataRepository.DATA_REPOSITORY_TAG);
        
        for(Element e:filtersTmp){
            if(e!= null){
                this.addIntermediateFilter(
                        DataFilterFactory.getDataFilter(e));
            }
        }
        if(xml.getChild(ISSTARTED_TAG) != null){
            logger.info("Chain set to autostart. Starting....");
            this.Start();
        }
    }

    /**
     * Gets the master parser object for this chain.
     * 
     * @return Master data source object at the top of this chain.
     */
    public IDataParser getSourceParser(){
        return sourceParser;
    }

    /**
     * Sets the master data parser for this chain. Replaces the existing object
     * if already defined.
     *
     * @param parser Parser to set as primary data source.
     */
    public void setSourceParser(IDataParser parser){
        sourceParser = parser;
        if(parser == null){
            throw new IllegalArgumentException(
                    "Data parser cannot be set to null.");
        }

        //If we have intermidate filters, set first as 
        // repository to the initial source
        // otherwise, if finalRepository exists, set final repository
        // otherwise, do nothing, will be associated properly later.
        if(intermediateFilters.size() >0){
            sourceParser.setRepository(intermediateFilters.get(0));
        }else if(finalRepository != null){
            sourceParser.setRepository(finalRepository);
        }
        support.firePropertyChange("chain",null,parser);
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    /**
     * Set the terminal repository object for this chain.
     *
     * @param repo The object to set as terminal repository. Cannot be null.
     */
    public void setFinalRepository(IDataRepository repo){
        if(repo==null){
            throw new IllegalArgumentException(
                    "The terminal repository cannot be set to null.");
        }
        finalRepository = repo;
        if(intermediateFilters.size() >0){
            intermediateFilters.get(intermediateFilters.size()-1)
                    .setChildRepository(finalRepository);
        }else if(sourceParser != null){
            sourceParser.setRepository(finalRepository);
        }
        support.firePropertyChange("chain", null, repo);
    }

    /**
     * Returns the terminal repository object for this chain. Cannot be null
     * for this chain to properly operate. 
     * @return This chain's terminal repository object.
     */
    public IDataRepository getFinalRepository(){
        return this.finalRepository;
    }

    /**
     * Returns true if this DataChain object is fully instantiated, false if
     * it is missing the minumum required contents to function.
     * @return True if DataChain is fully instantiated, otherwise false.
     */
    public boolean isValidDataChain(){
        return this.getFinalRepository()!=null && this.getSourceParser()!=null;
    }

    public void addIntermediateFilter(IDataRepository filter){
        if(intermediateFilters.size() > 0){
            intermediateFilters.get(intermediateFilters.size()-1)
                    .setChildRepository(filter);
            intermediateFilters.add(filter);
        }else{
            intermediateFilters.add(filter);
            if(sourceParser != null)
                sourceParser.setRepository(filter);
        }
        
        if(finalRepository != null){
            filter.setChildRepository(finalRepository);
        }
        support.firePropertyChange("chain", null, filter);
    }
    public void moveIntermediateFilter(IDataRepository filter,direction d){
        int index = intermediateFilters.indexOf(filter);
        if((index == -1) || 
                (index == 0 && d == direction.UP) ||
                (index == intermediateFilters.size()-1 && d == direction.DOWN)){
            //direction is invalid for location or filter not selected
            return;
        }
        
        if(d == direction.UP){
            
            IDataRepository above = intermediateFilters.get(index -1);
            IDataRepository cur = intermediateFilters.get(index);
            IDataRepository cursChild = cur.getChildRepository();
            
            intermediateFilters.setElementAt(cur, index-1);
            intermediateFilters.setElementAt(above,index);
            cur.setChildRepository(above);
            above.setChildRepository(cursChild);
            
            if(index-1 <= 0){
                sourceParser.setRepository(cur);
            }else{
                intermediateFilters.get(index-2).setChildRepository(cur);
            }
        }else if(d==direction.DOWN){
            IDataRepository cur = intermediateFilters.get(index);
            IDataRepository below = intermediateFilters.get(index+1);
            IDataRepository belowsChild = below.getChildRepository();
            
            intermediateFilters.setElementAt(cur, index+1);
            intermediateFilters.setElementAt(below, index);
            cur.setChildRepository(belowsChild);
            below.setChildRepository(cur);
            
            if(index-1 >= 0){
                intermediateFilters.get(index-1).setChildRepository(below);
            }else{
                sourceParser.setRepository(below);
            }
        }
        support.firePropertyChange("chain", null, null);
        
    }
    public void removeIntermediateFilter(IDataRepository toRmv){
        int index = intermediateFilters.indexOf(toRmv);
        if(index <0){
            return;
        }
        
        IDataRepository child = toRmv.getChildRepository();
        if(index ==0){
            sourceParser.setRepository(child);
        }else{
            intermediateFilters.get(index -1).setChildRepository(child);
        }
        intermediateFilters.remove(toRmv);
        support.firePropertyChange("chain", toRmv, null);
    }
    public Vector<IStatusPanel> getObjectChain(){
        Vector<IStatusPanel> toReturn = new Vector<IStatusPanel>();
        if(sourceParser != null){
            toReturn.add(sourceParser);            
        }
        toReturn.addAll(intermediateFilters);
        
        if(finalRepository != null){
            toReturn.add(finalRepository);            
        }
        
        return toReturn;
    }

    /**
     * Graphical UI component to display state and allow alterations by a
     * human.
     * @return A JPanel properly initialized for this data chain.
     */
    public JPanel getJPanel(){
        if(overviewTab == null){
            overviewTab = new DataChainJPanel(this);
        }
        return overviewTab;
    }

    /**
     * Returns an XML Element object with all settings to persist for this and
     * all child objects.
     * @return An XML Element object with settings to persist.
     */
    public Element getSettingsXml(){
        Element e = new Element(DATA_CHAIN_TAG);
        Element filterElement = new Element(FILTERS_TAG);
        e.addContent(sourceParser.getSettingsXml());
        e.addContent(finalRepository.getSettingsXml());
        e.addContent(filterElement);
        e.addContent(new Element(DATA_CHAIN_NAME_TAG).setText(chainName));
        
        for(IDataRepository r:intermediateFilters){
            filterElement.addContent(r.getSettingsXml());
        }
        if(sourceParser.isStarted()){
            e.addContent(new Element(ISSTARTED_TAG));
        }
        
        return e;
    }
    
    /**
     * Flushes all cached data and stops the entire data chain.
     * 
     * @return True if data chain is successfully stopped. False if an exception
     * occured.
     */
    public boolean Stop(){
        if(sourceParser != null){
            boolean result = sourceParser.Stop();
            if(result){
                support.firePropertyChange("isrunning", true, false);
            }
            return result;
        }else{
            return true;
        }
    }
    
    /**
     * Starts entire data chain. This may include creating database connections, opening
     * file handles, etc.
     * 
     * @return True if the chain successfully started, false if it was unable
     * to start.
     */
    public boolean Start(){
        if(sourceParser != null){
            boolean result = sourceParser.Start();
            if(result){
                 support.firePropertyChange("isrunning", false, true);
            }
            return result;
        }else{
            return false;
        }
    }

    /**
     * Returns true if data chain is currently running. Otherwise false.
     * 
     * @return True if data chain is currently running. 
     */
    public boolean isStarted(){
        //This can be called before sourceParser is instantiated. Just check
        // to prevent null pointer exception
        return sourceParser != null? sourceParser.isStarted():false;
    }

    /**
     * Add property change listener from listener list.
     * @param p PropertyChangeListener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener p){
        support.addPropertyChangeListener(p);
    }

    /**
     * Remove listener from list of property change listeners.
     * @param p Listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener p){
        support.removePropertyChangeListener(p);
    }

    public void dispose(){
        this.Stop();
    }
    

    
    
}
