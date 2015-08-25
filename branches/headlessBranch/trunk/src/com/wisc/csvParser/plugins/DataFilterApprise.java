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

import com.wisc.csvParser.IDataRepository;
import com.wisc.csvParser.ValueObject;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jdom.Element;
import java.util.LinkedList;
import java.util.List;
import com.wisc.csvParser.notificationProviders.*;
import org.apache.log4j.Logger;


/**
 *
 * @author lukewinslow
 */
public class DataFilterApprise implements IDataRepository,IProviderUser{
    
    public static final double ERROR_RATE_CUTOFF =0.1;
    public static final String FILTERED_VARIABLES_TAG = "FilteredVariables";
    public static final String VARIABLE_TAG = "Variable";
    public static final String FILTERED_VALUES_TAG = "FilteredValues";
    public static final String VALUE_TAG = "Value";
    public static final String DELETE_BELOW_TAG = "DeleteBelow";
    public static final String DELETE_ALL_TAG = "DeleteAllSameVar";    
    
    public enum events{
        started,
        stopped,
        reconfigured,
        valueFiltered,
        newVariableSeen
    }
    
    public enum removeTypes{
        removeErrorOnly,
        removeAllBelow,
        removeAllSameVariable
    }
    
    
    private JPanelFilterApprise statusPanel;
    private IDataRepository child;
    private double[] valuesToFilter = {-99999};
    private ArrayList<String> variablesToFilter = new ArrayList<String>();
    private ArrayList<IEventHandler> handlers = new ArrayList<IEventHandler>();
    private ArrayList<String> variablesSeen = new ArrayList<String>();
    private LinkedList<String> lastFiltered = new LinkedList<String>();
    private removeTypes removalType = removeTypes.removeErrorOnly;
    private long countFiltered = 0;
    private String panelID;
    private boolean isStarted = false;
    private INotificationProvider notifProvider;
    
    private Logger logger = Logger.getLogger(DataFilterApprise.class.getName());
    
    /**
     * Main data filter object constructor. 
     */
    public DataFilterApprise(){
        logger.info("Build Apprise Filter object.");
        panelID = getRepositoryShortname() + 
                Integer.toString((new java.util.Random()).nextInt());
        
    }
    
    private void newVariableCheck(ValueObject val){
        boolean newVariable = true;
        for(String s:variablesSeen){
            if(s.equalsIgnoreCase(val.getVariable()))
                newVariable = false;
        }
        if(newVariable){
            variablesSeen.add(val.getVariable());
            raiseEvent(events.newVariableSeen);
        }
            
    }
    private void newVariableCheck(ArrayList<ValueObject> vals){
        for(ValueObject val:vals){
            newVariableCheck(val);
        }
    }
    
    private void newBadValue(ValueObject val){
        countFiltered++;
        logger.info("New bad value at " + val.getSite() + " number " + countFiltered);
        
        lastFiltered.offer(val.getTimeStamp().toString());
        if(lastFiltered.size()>30)
            lastFiltered.poll();
        
        raiseEvent(events.valueFiltered);
    }
    
    public long getCountFiltered(){
        return countFiltered;
    }
    
    public double[] getValuesToRemove(){
        return valuesToFilter;
    }
    
    public ArrayList<String> getVariablesToFilter(){
        return variablesToFilter;
    }
    
    public Object[] getFilteredValues(){
        return lastFiltered.toArray();
    }
    
    public void setVariablesToFilter(ArrayList<String> vars){
        variablesToFilter = vars;
    }
    
    public void setValuesToRemove(double[] vals){
        valuesToFilter = vals;
    }
    
    public void setRemoveType(removeTypes type){
        removalType = type;
    }
    
    public removeTypes getRemoveType(){
        return removalType;
    }
    
    @Override
    public boolean NewRow(ArrayList<ValueObject> newRow) {
        newVariableCheck(newRow);
        ValueObject bad = null;
        
        for(ValueObject v:newRow){
            if(isFiltered(v) && isBadValue(v)){
                bad = v;
                newBadValue(bad);
                break;
            }
        }
        
        if(bad == null){
            if(child!=null){
                child.NewRow(newRow);
            }
            return true;
        }
        
        if(removalType == removeTypes.removeErrorOnly){
            newRow.remove(bad);
            if(child!=null){
                child.NewRow(newRow);
            }
            return true;
        }
        
        if(child!=null){
            child.NewRow(removeBadVals(newRow,bad));
        }
        return true;
        
    }

    @Override
    public boolean NewValue(ValueObject newValue) {
        newVariableCheck(newValue);
        if(isFiltered(newValue) && isBadValue(newValue)){
            newBadValue(newValue);
        }else{
            //if not, then just don't send the value
            // and go down to true
            if(child != null)
                child.NewValue(newValue);            
        }
        return true;
    }
    
    private ArrayList<ValueObject> removeBadVals(
            ArrayList<ValueObject> vals, ValueObject firstBad){
        
        ArrayList<ValueObject> filtered = 
                new ArrayList<ValueObject>();
        
        for(ValueObject v:vals){
            if(removalType == removeTypes.removeAllBelow){
                 if(v.getVariable().compareToIgnoreCase(
                        firstBad.getVariable())==0 && 
                        v.getOffsetValue() > firstBad.getOffsetValue()){
                    newBadValue(v);
                    //this is a bad value. Do not add to final filtered array
                }else{
                    filtered.add(v);
                }
            }else if(removalType == removeTypes.removeAllSameVariable){
                if(v.getVariable().compareToIgnoreCase(
                        firstBad.getVariable()) == 0){
                    newBadValue(v);
                }else{
                    filtered.add(v);
                }
            }
        }
        return filtered;
    }
    
    private boolean isFiltered(ValueObject val){
        for(String v:variablesToFilter){
            if(v.compareToIgnoreCase(val.getVariable())==0)
                return true;
        }
        return false;
    }
    
    private boolean isBadValue(ValueObject val){
        if(valuesToFilter.length < 1)
            return false;
        
        for(int i=0;i<valuesToFilter.length;i++){
            if(Double.compare(val.getValue(),valuesToFilter[i])==0)
                return true;
        }
        return false;
    }
    

    /**
     * 
     * @return 
     */
    @Override
    public boolean Start() {
        isStarted = true;
        raiseEvent(events.started);
        if(child!=null){
            return child.Start() && isStarted;
        }else{
            return true;            
        }

    }

    @Override
    public boolean Stop() {
        isStarted = false;
        raiseEvent(events.stopped);
        if(child!=null){
            return child.Stop() && true;
        }else{
            return true;            
        }
    }

    @Override
    public void configure(Element e) throws Exception {
        if(e.getChild(DELETE_BELOW_TAG)!= null){
            removalType = removeTypes.removeAllBelow;
        }else if(e.getChild(DELETE_ALL_TAG)!= null){
            removalType = removeTypes.removeAllSameVariable;
        }
        
        List<Element> variables = 
                e.getChild(FILTERED_VARIABLES_TAG).getChildren(VARIABLE_TAG);
        List<Element> values = 
                e.getChild(FILTERED_VALUES_TAG).getChildren(VALUE_TAG);
        
        for(Element v:variables){
            variablesToFilter.add(v.getText());
        }
        double[] blah = new double[values.size()];
        for(int i=0;i<values.size();i++){
            blah[i] = Double.valueOf(values.get(i).getText());
        }
        valuesToFilter = blah;
        
        raiseEvent(events.reconfigured);
    }

    @Override
    public IDataRepository getChildRepository() {
        return child;
    }

    @Override
    public String getRepositoryDescription() {
        return "This filter can filter Apprise templine errors out of \n"+
                "the data stream.";
    }
    
    @Override
    public String getRepositoryShortname() {
        return "Apprise Error Filterer";
    }

    @Override
    public Element getSettingsXml() {
        Element toReturn = new Element(IDataRepository.DATA_REPOSITORY_TAG)
                .setAttribute("type",DataFilterApprise.class.getName());
        if(removalType == removeTypes.removeAllBelow){
            toReturn.addContent(new Element(DELETE_BELOW_TAG));
        }else if(removalType == removeTypes.removeAllSameVariable){
            toReturn.addContent(new Element(DELETE_ALL_TAG));
        }
        Element values = new Element(FILTERED_VALUES_TAG);
        
        for(double d:valuesToFilter){
            values.addContent(new Element(VALUE_TAG).setText(Double.toString(d)));
        }
        toReturn.addContent(values);
        Element vars = new Element(FILTERED_VARIABLES_TAG);
        
        for(String s:variablesToFilter){
            vars.addContent(new Element(VARIABLE_TAG).setText(s));
        }
        toReturn.addContent(vars);
        
        return toReturn;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
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
        if(statusPanel == null){
            logger.trace("Lazily initialize Settings JPanel");
            statusPanel = new JPanelFilterApprise(this);
        }
        return statusPanel;
    }
    
    @Override
    public String toString(){
        return getRepositoryShortname();
    }
    
    public void raiseEvent(events e){
        for(IEventHandler h:handlers)
            h.eventRaised(e);
    }
    
    public void addEventHandler(IEventHandler h){
        handlers.add(h);
    }
    
    public void removeEventHandler(IEventHandler r){
        handlers.remove(r);
    }
    
    public interface IEventHandler{
        public void eventRaised(events e);
    }

    @Override
    public void setNotificationProvider(INotificationProvider provider) {
        notifProvider = provider;
    }
}
