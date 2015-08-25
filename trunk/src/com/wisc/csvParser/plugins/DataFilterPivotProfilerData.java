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
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.lang.Math;




/**
 *
 * @author user
 */
public class DataFilterPivotProfilerData implements IDataRepository{


    private String panelID;
    private JPanel statusPane;
    private IDataRepository child;
    private boolean started;

    private String depthVariable = "depth"; //defaults to just 'depth'
    private HashSet<String> varsToPivot = new HashSet<String>();
    private int minDwellInMin = 5;

    private double depthAve;
    private LinkedList<ValueObject> depthHistory = new LinkedList<ValueObject>();

    private HashMap<String,double[]> runningAveTable
            = new HashMap<String,double[]>();
    private HashMap<String, Integer> runningAvePointer
            = new HashMap<String,Integer>();
    private int runAveNum = 4;

    
    public DataFilterPivotProfilerData(){
        panelID = getRepositoryShortname() + 
                Integer.toString((new java.util.Random()).nextInt());

        //Just hard-code for now. 
        varsToPivot.add("chlorophyll");
        varsToPivot.add("dissolved_oxygen_concentration");
        varsToPivot.add("dissolved_oxygen_saturation");
        varsToPivot.add("oxidation_reduction_potential");
        varsToPivot.add("ph");
        varsToPivot.add("specific_conductivity");
        varsToPivot.add("phycocyanin");
        varsToPivot.add("turbidity");


        
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

        if(depthHistory.size() > 0 &&
           (depthVal.getTimeStamp().getTime() - depthHistory.peek().getTimeStamp().getTime())
           > minDwellInMin*60*1000){
            //If the difference between the first and last in the list is greater
            // than the allotted average window, get rid of the last.
            depthHistory.pop();
        }

        //If we still have any depth history, calculate running ave
        if(depthHistory.size() > 0){
            int counter = 0;
            depthAve = 0;
            for(ValueObject v:depthHistory){
                depthAve = depthAve + v.getValue();
                counter = counter + 1;
            }
            depthAve = depthAve/(new Double(counter));
            
        }else{
            depthAve = -1;
        }

        //Add most recent depth value history after calculating average
        depthHistory.offer(depthVal);
        ArrayList<ValueObject> valsToSend = new ArrayList<ValueObject>();
        
        if(depthVal != null){
            ValueObject tmp;
            for(int i = 0;i<newRow.size();i++){
                tmp = internalNewValue(newRow.get(i),depthVal);
                if(tmp!= null){
                    valsToSend.add(tmp);
                }
            }
        }
        
        if(child!=null){
            return child.NewRow(valsToSend);
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
        if(statusPane == null)
            statusPane = new JPanelProfilerPivoter(this);
        
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
    


    
    
    public ValueObject findDepthVal(ArrayList<ValueObject> vals){
        ValueObject depthVal = null;
        for(ValueObject v:vals){
            if(v.getVariable().equalsIgnoreCase(depthVariable)){
                depthVal = v;
                break;
            }
        }
        return depthVal;
        
    }
    
    public ValueObject internalNewValue(ValueObject val,ValueObject depthVal){

        if(!varsToPivot.contains(val.getVariable().toLowerCase())){
            //Then this is not a variable that needs pivoting
            return null;
        }
        
        
        ValueObject potVal = null;

        if(java.lang.Math.round(depthAve*10)/10.0 ==
                java.lang.Math.round(depthVal.getValue()*10)/10.0){
            potVal = val.clone();
            potVal.setOffsetValue(java.lang.Math.round(depthVal.getValue()*10)/10.0);
            potVal.setOffsetType("DEPTH");
            potVal.setSite(potVal.getSite().replace(" Raw", ""));
            
            return potVal;
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
