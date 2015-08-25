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
import javax.swing.JPanel;
import org.jdom.Element;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import com.wisc.csvParser.notificationProviders.*;
import java.util.Collections;
import java.util.Arrays;



/**
 *
 * @author user
 */
public class DataFilterPivotProfilerData implements IDataRepository{


    private String panelID;
    private JPanel statusPane;
    private IDataRepository child;
    private boolean started;

    
    public DataFilterPivotProfilerData(){
        panelID = getRepositoryShortname() + 
                Integer.toString((new java.util.Random()).nextInt());
        
        statusPane = new JPanel();
        
        
        
    }
    
    @Override
    public String getRepositoryDescription() {
        return "Pivots autoprofiler data.";
    }

    @Override
    public String getRepositoryShortname() {
        return "Autoprof Pivoter";
    }

    @Override
    public void configure(Element e) throws Exception {
        
    }

    @Override
    public boolean Start() {
        if(child.Start()){
            started = true;
            return true;
        }else{
            return false;
        }
        
    }

    @Override
    public boolean Stop() {
        if(child.Stop()){
            started = false;
            return false;
        }else{
            return false;
        }
        
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean NewRow(ArrayList<ValueObject> newRow) {
        
        ValueObject depthVal = findDepthVal(newRow);
        if(depthVal != null){
            ArrayList<ValueObject> valsToAdd = new ArrayList<ValueObject>();
            ValueObject tmp;
            for(int i = 0;i<newRow.size();i++){
                tmp = internalNewValue(newRow.get(i),depthVal);
                if(tmp!= null){
                    valsToAdd.add(tmp);
                }
            }
            newRow.addAll(valsToAdd);
        }
        
        if(child!=null){
            return child.NewRow(newRow);
        }
        return true;
    }

    @Override
    public boolean NewValue(ValueObject newValue) {
        
        if(child != null){
            return child.NewValue(newValue);
        }
        return true;
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
        e.setAttribute("type",DataFilterPivotProfilerData.class.getName());
        return e;
    }

    @Override
    public JPanel getStatusJPanel() {
        return statusPane;
    }

    @Override
    public String getPanelID() {
        return panelID;
    }
    
    @Override
    public String toString(){
        return this.getRepositoryShortname();
    }
    

    private HashMap<String,double[]> runningAveTable
            = new HashMap<String,double[]>();
    private HashMap<String, Integer> runningAvePointer
            = new HashMap<String,Integer>();
    private int runAveNum = 4;
    
    
    public ValueObject findDepthVal(ArrayList<ValueObject> vals){
        ValueObject depthVal = null;
        for(ValueObject v:vals){
            if(v.getVariable().compareToIgnoreCase("sensor depth")==0){
                depthVal = v;
                break;
            }
        }
        return depthVal;
        
    }
    
    public ValueObject internalNewValue(ValueObject val,ValueObject depthVal){
        
        
        if(val.getSite().compareToIgnoreCase("nsb autoprofiler_raw")!=0){
            return null;
        }
        
        double[] runAve = runningAveTable.get(val.getUniqueStreamString());
        int pointer;
        if(runAve != null){
            pointer = runningAvePointer.get(val.getUniqueStreamString())+1;
            if(pointer > runAveNum-1){
                pointer = 0;
            }
        }else{
            pointer = 0;
            runAve = new double[runAveNum];
            java.util.Arrays.fill(runAve, -1);
            runningAveTable.put(val.getUniqueStreamString(),runAve);
        }
        double ave = 0;
        for(double d:runAve){
            ave += d;
        }
        ave = ave/runAveNum;
        
        runAve[pointer] = depthVal.getValue();
        runningAvePointer.put(val.getUniqueStreamString(), pointer);
        runningAveTable.put(val.getUniqueStreamString(), runAve);
        
        ValueObject potVal = null;
        ValueObject pivotedVal = null;

        if(java.lang.Math.round(ave*10)/10.0 == 
                java.lang.Math.round(depthVal.getValue()*10)/10.0){
            potVal = val.clone();
            potVal.setOffsetValue(java.lang.Math.round(depthVal.getValue()*10)/10.0);
            potVal.setOffsetType("DEPTH");
            potVal.setSite(potVal.getSite().replace("_raw", ""));
            
            return newValueCandidate(potVal);
        }
        return null;
        
        
    }
    
    private HashMap<String,ValueObject> lastCandidate
            = new HashMap<String,ValueObject>();
    
    public ValueObject newValueCandidate(ValueObject val){
        ValueObject candidate = lastCandidate.get(val.getUniqueStreamStringWithoutDepth());
        if(candidate != null){
            lastCandidate.put(val.getUniqueStreamStringWithoutDepth(),val);
            System.out.println(candidate.getOffsetValue() +" "+ val.getOffsetValue());
            System.out.println(candidate.getTimeStamp().toString() +" "+ val.getTimeStamp().toString());
            if(candidate.getOffsetValue() == val.getOffsetValue()){
                return null;
            }else{
                return candidate;
            }
        }else{
            lastCandidate.put(val.getUniqueStreamStringWithoutDepth(), val);
            return null;
        }
    }

}
