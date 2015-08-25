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


/**
 *
 * @author jwyuen
 */
public class DataColumn{

    public static final String COLUMN_TAG = "Column";
    public static final String IGNORE_COLUMN_TYPE = "ignore";
    public static final String DATA_COLUMN_TYPE = "data";
    public static final String COLUMN_TYPE_TAG = "ColType";

    private String type;
    private ValueObject templateValue = null;

    public DataColumn(){
        type = IGNORE_COLUMN_TYPE;
        templateValue = new ValueObject();
    }
    
    public DataColumn(Element xml){
        //These two things are required.
        //index = Integer.valueOf(xml.getAttributeValue("index"));
        type = xml.getChild(COLUMN_TYPE_TAG).getText().trim();

        if(type.trim().equals(DATA_COLUMN_TYPE)){
            templateValue  = new ValueObject(xml.getChild(ValueObject.METADATA_TAG));
        }
    }
    public Element getSettingsXml(){
        Element e = new Element(COLUMN_TAG);
        //e.setAttribute("index",Integer.toString(index));
        e.addContent(new Element(COLUMN_TYPE_TAG).setText(type));
        
        if(type.compareToIgnoreCase(IGNORE_COLUMN_TYPE)!=0){
            e.addContent(templateValue.getMetadataXml());
        }
        return e;
    }

    
    
    public ValueObject getNewValue(){
        if(this.ignoreColumn()){
            return null;
        }else{
            return templateValue.cloneValueless();          
        }
    }
    public boolean ignoreColumn(){
        return type.equals(IGNORE_COLUMN_TYPE);
    }
    @Override
    public String toString(){
        if(type.compareTo(DATA_COLUMN_TYPE)==0 && templateValue != null){
            return templateValue.toString();
        }else{
            return type;
        }
        
    }
    
    @Override
    public DataColumn clone(){
        DataColumn dolly = new DataColumn();
        
        dolly.setType(this.getType());
        dolly.setTemplateValue(this.getTemplateValue().clone());
        
        return dolly;
    }

    public ValueObject getTemplateValue() {
        return templateValue;
    }

    public void setTemplateValue(ValueObject templateValue) {
        this.templateValue = templateValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void displaySettingsDialog(){
        JDialogDataColumn dialog = new JDialogDataColumn(null,true,this);
        dialog.setVisible(true);
    }
    
    
}
