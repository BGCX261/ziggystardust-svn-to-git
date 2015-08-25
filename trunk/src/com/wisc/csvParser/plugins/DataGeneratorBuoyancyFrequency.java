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
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 *
 * @author lawinslow
 */
public class DataGeneratorBuoyancyFrequency implements IDataRepository{
    
    private String waterTempVocab = "WATER_TEMP";//Default water temp vocab
    private String buoyancyFrequencyVocab = "Buoyancy Frequency";
    private IDataRepository child;
    private String panelID;
    private JPanel statusJPanel;
    private boolean started;
    private ArrayList<IEventHandler> handlers = new ArrayList<IEventHandler>();
    private Logger logger = Logger.getLogger(DataGeneratorBuoyancyFrequency.class.getName());
    
    public enum events{
        start,
        stop,
        bfgenerated,
        reconfigured
    }
    
    public DataGeneratorBuoyancyFrequency(){
            panelID = this.getRepositoryShortname() + 
                Integer.toString((new java.util.Random()).nextInt());
    }
    

    @Override
    public String getRepositoryDescription() {
        return "Buoyancy Frequency Generator";
    }

    @Override
    public String getRepositoryShortname() {
        return "Buoyancy Frequency Generator";
    }

    @Override
    public void configure(Element e) throws Exception {
        
        
        raiseEvent(events.reconfigured);
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean Start() {
        if(child != null ){
            if(child.Start()){
                this.started = true;
                raiseEvent(events.start);
                return true;                
            }else{
                return false;
            }
        }else{
            this.started = true;
            raiseEvent(events.start);
            return true;
        }
    }

    @Override
    public boolean Stop() {
        if(child != null && child.Stop()){
            if(child.Stop()){
                started = false;
                raiseEvent(events.stop);
                return true;
            }else{
                return false;
            }
        }else{
            started = false;
            raiseEvent(events.stop);
            return true;
        }
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean NewRow(ArrayList<ValueObject> newRow) {
        
        ArrayList<ValueObject> waterTemps = new ArrayList<ValueObject>();

        for(int i = 0;i<newRow.size();i++){
            if(newRow.get(i).getVariable().compareToIgnoreCase(waterTempVocab) == 0){
                waterTemps.add(newRow.get(i));
            }
        }
        
        //If we don't have 2 or more water temps we can't calculate buoyancy
        // frequency, just pass it on        
        if(waterTemps.size() <= 2 ){
            //child being null will rarely if ever happen
            if(child != null){
                return child.NewRow(newRow);
            }else{
                return true;
            }
        }
        waterTemps.trimToSize();
        ValueObject[] tempsArray = new ValueObject[0];
        tempsArray = waterTemps.toArray(tempsArray);
        
        
        Comparator<ValueObject> depthComp = new Comparator<ValueObject>(){
            @Override
            public int compare(ValueObject val1,ValueObject val2){
                if(val1.getOffsetValue() == val1.getOffsetValue()){
                    return 0;
                }else if(val1.getOffsetValue() > val2.getOffsetValue()){
                    return 1;
                }else if(val1.getOffsetValue() < val2.getOffsetValue()){
                    return -1;
                }
                return 0;//if we don't know what is going on
            }
        };
        
        java.util.Arrays.sort(tempsArray, depthComp);
        
        double[] dwDensity = new double[tempsArray.length - 1];
        double[] dz = new double[tempsArray.length - 1];
        double topDensity = waterDensity(tempsArray[0].getValue());
        
        for(int i=1;i<tempsArray.length;i++){
            dwDensity[i-1] = waterDensity(tempsArray[i].getValue()) -
                    waterDensity(tempsArray[i-1].getValue());
        }
        
        for(int i=1;i<tempsArray.length;i++){
            dz[i-1]=tempsArray[i].getOffsetValue() - tempsArray[i-1].getOffsetValue();
        }
        
        ValueObject tmpVal;
        for(int i=0;i<dz.length;i++){
            tmpVal = tempsArray[i+1].cloneValueless();
            tmpVal.setSensorID(-1);
            tmpVal.setVariable(buoyancyFrequencyVocab);
            tmpVal.setUnit("none");
            tmpVal.setValue( 9.81 * topDensity*(dwDensity[i]/dz[i]));
            newRow.add(tmpVal);
        }
        
        if(child != null){
            return child.NewRow(newRow);
        }else{
            return true;
        }
    }

    @Override
    public boolean NewValue(ValueObject newValue) {
        if(child != null){
            return child.NewValue(newValue);
        }else{
            return true;
        }
    }

    @Override
    public IDataRepository getChildRepository() {
        return child;
    }

    @Override
    public void setChildRepository(IDataRepository child) {
        this.child = child;
    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(IDataRepository.DATA_REPOSITORY_TAG);
        e.setAttribute("type",DataGeneratorBuoyancyFrequency.class.getName());
        return e;
    }

    @Override
    public JPanel getStatusJPanel() {
        if(statusJPanel==null){

            statusJPanel = new JPanelBuoyancyFrequency(this);
        }
        return statusJPanel;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }
    
    @Override
    public String toString(){
        return this.getRepositoryShortname();
    }
    
    private double waterDensity(double t){
        //This equation is from Paul. May want to check.
        return 1000 * (1-(t+288.9414) / 
                (508929.2 * (t+68.12963)) * Math.pow(t-3.9863,2));
    }

    public String getWaterTempVocab() {
        return waterTempVocab;
    }

    public void setWaterTempVocab(String waterTempVocab) {
        this.waterTempVocab = waterTempVocab;
    }

    public String getBuoyancyFrequencyVocab() {
        return buoyancyFrequencyVocab;
    }

    public void setBuoyancyFrequencyVocab(String buoyancyFrequencyVocab) {
        this.buoyancyFrequencyVocab = buoyancyFrequencyVocab;
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
    
}
