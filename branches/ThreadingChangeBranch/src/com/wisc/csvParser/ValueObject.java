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

import java.util.Date;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import org.jdom.Element;


/**
 *
 * @author lawinslow
 */
public class ValueObject {
    
    /***************************************************************************
     * Required Tags
     **************************************************************************/
    
    /**
     * XML tag labelthat stores the metadata in XML Serialized
     * <code>ValueObject</code>. Ultimately holds all tags excluding
     * <code>VALUE_OBJECT_TAG</code> which is the object root tag
     * and <code>VALUE_TAG</code>, <code>TIMESTAMP_TAG</code>, and
     * <code>FLAG_TAG</code> because they are value specific tags.
     */
    public static final String METADATA_TAG = "MetaData";
    /**
     * XML tag for the root Element.
     */
    public static final String VALUE_OBJECT_TAG = "ValueObject";
    /**
     * XML tag for the double floating point value of the <code>ValueObject</code>.
     */
    public static final String VALUE_TAG = "Value";
    /**
     * XML tag label of the value specific timestamp.
     */
    public static final String TIMESTAMP_TAG = "TimeStamp";
    /**
     * XML tag label of the value's flag (if any).
     */
    public static final String FLAG_TAG = "Flag";
    /**
     * XML tag label for UTC offset metadata.
     */
    public static final String UTCOFFSET_TAG = "UTCOffset";
    /**
     * XML tag label for the site metadata.
     */
    public static final String SITE_TAG = "Site";
    /**
     * XML tag label for variable metadata.
     */
    public static final String VARIABLE_TAG = "Variable";
    /**
     * XML tag label for the source metadata.
     */
    public static final String SOURCE_TAG = "Source";
    /**
     * XML tag label for the Aggregation Method (e.g., Mean, Inst, Min, Max).
     */
    public static final String AGGREGATION_METHOD_TAG = "AggMeth";
    /**
     * XML tag label for the aggregation span.
     */
    public static final String AGGREGATION_SPAN_TAG = "AggSpan";
    /**
     * XML tag label for the unit metadata.
     */
    public static final String UNIT_TAG = "Unit";
    /**
     * Default timestamp format used when saving this object to a text version.
     */
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * Default date/time formatter for outputting and parsing date/time as text.
     */
    public static final SimpleDateFormat dSimp =
            new SimpleDateFormat(TIMESTAMP_FORMAT);
    /***************************************************************************
     * Optional Tags
     **************************************************************************/
    
    /**
     * XML tag label for the sensor identification metadata.
     *
     * @deprecated Sensor metadata is being removed as first class metadata
     * in the vega system
     */
    @Deprecated
    public static final String SENSOR_TAG = "Sensor";
    /**
     * XML tag label for the offset value metadata.
     */
    public static final String OFFSET_TAG = "Offset";
    /**
     * XML tag label for the offset type metadata (depth, height, etc).
     */
    public static final String OFFSET_TYPE_TAG = "OffsetType";
    /**
     * XML tag label for the sample repetition number. 
     */
    public static final String SAMPLE_REP_ID_TAG = "SampleRep";

    /***************************************************************************
     *Required metadata and data fields
     **************************************************************************/
    
    /**
     * The floating point value of this observation.
     */
    private double value = Double.NaN;
    /**
     * The local timestamp of this observation. Not the UTC timestamp.
     */
    private Date timestamp = null;
    /**
     * The UTC offset of this observation.
     */
    private double utcOffset = Double.NaN;
    /**
     * The site name attribute of this observation.
     */
    private String site = "";
    /**
     * The variable name attribute of this observation.
     */
    private String variable = null;
    /**
     * The source name attribute of this observation.
     */
    private String source = "";
    /**
     * The unit attribute of this observation.
     */
    private String unit = "";
    /**
     * The aggregation method attirubte of this observation
     */
    private String aggMethod = "";
    /**
     * The aggregation span attribute. Needs to be formatted as hh:MM:ss
     */
    private String aggSpanString;

    /***************************************************************************
     * Optional data and metadata fields.
     **************************************************************************/

    /**
     * The floating point offset value attribute of this observation.
     */
    private double offsetValue = Double.NaN;
    /**
     * The offset type name of this observation. For example, depth or height.
     */
    private String offsetType = "";
    /**
     * The sensor id of the sensor used to measure this observation. Only
     * integer sensor id's are allowed. This matches the gleonid version of
     * sensor management.
     *
     * @deprecated SensorID is no longer first class metadata. It is added
     * after the fact by the technician
     */
    @Deprecated
    private int sensorID = -1;
    /**
     * The replicate id tag allows replicate measurements to be taken. ID's
     * should monotonically increase from 1. Default is 1.
     */
    private int duplicateID = 1;
    /**
     * The flag attribute. Cannot be over 3 characters.
     * Null if this object has no flag
     */
    private String flag = null;
    /**
     * The local cache of the metadata XML element.
     */
    private Element metadataElementCache;
    
    
    /**
     * Constructor for ValueObject.
     */
    public ValueObject() {
        
    }
    /**
     * Constructor for ValueObject that takes XML serialized
     * ValueObject.
     * @param metadataXml
     */
    public ValueObject(Element metadataXml){
        this();

        //These are all required metadata.
        utcOffset = Double.valueOf(metadataXml.getChildText(UTCOFFSET_TAG));
        site = metadataXml.getChildText(SITE_TAG);
        variable = metadataXml.getChildText(VARIABLE_TAG);
        source = metadataXml.getChildText(SOURCE_TAG);
        unit = metadataXml.getChildText(UNIT_TAG);
        aggMethod = metadataXml.getChildText(AGGREGATION_METHOD_TAG);
        aggSpanString = metadataXml.getChildText(AGGREGATION_SPAN_TAG);
        
        //These metadata are optional
        if(metadataXml.getChild(SENSOR_TAG) != null && 
                !metadataXml.getChildText(SENSOR_TAG).equalsIgnoreCase("")){
            try{
            sensorID = Integer.valueOf(metadataXml.getChildText(SENSOR_TAG));
            }catch(NumberFormatException nfe){
                //do nothing, probably a relic
            }
        }
        if(metadataXml.getChild(OFFSET_TAG) != null && 
                !metadataXml.getChildText(OFFSET_TAG).equalsIgnoreCase(""))
            offsetValue = Double.valueOf(metadataXml.getChildText(OFFSET_TAG));
        
        if(metadataXml.getChild(OFFSET_TYPE_TAG) != null &&
                !metadataXml.getChildText(OFFSET_TYPE_TAG).equalsIgnoreCase(""))
            offsetType = metadataXml.getChildText(OFFSET_TYPE_TAG);
        
        if(metadataXml.getChild(SAMPLE_REP_ID_TAG) != null && 
                !metadataXml.getChildText(SAMPLE_REP_ID_TAG).equalsIgnoreCase(""))
            duplicateID = Integer.valueOf(metadataXml.getChildText(SAMPLE_REP_ID_TAG));
        
    }
    /**
     * Returns an XML Element object representation of this object's metadata.
     * To get full XML representation, see {@link #getAsXml()}. 
     *
     * @return metadata formatted in XML 
     */
    public Element getMetadataXml(){
        
        //First, all required elements
        Element e = new Element(METADATA_TAG);
        e.addContent(new Element(UTCOFFSET_TAG)
                .setText(Double.toString(utcOffset)));
        e.addContent(new Element(SITE_TAG).setText(site));
        e.addContent(new Element(VARIABLE_TAG).setText(variable));
        e.addContent(new Element(SOURCE_TAG).setText(source));
        e.addContent(new Element(UNIT_TAG).setText(unit));
        e.addContent(new Element(AGGREGATION_METHOD_TAG).setText(aggMethod));
        e.addContent(new Element(AGGREGATION_SPAN_TAG)
                .setText(aggSpanString));
        e.addContent(new Element(SAMPLE_REP_ID_TAG)
                .setText(Integer.toString(duplicateID)));
        
        //now the optional ones
        if(sensorID > 0){
            e.addContent(new Element(SENSOR_TAG)
                    .setText(Integer.toString(sensorID)));
        }
        if(offsetValue != Double.NaN){
            e.addContent(new Element(OFFSET_TAG)
                    .setText(Double.toString(offsetValue)));
        }
        if(offsetType.compareToIgnoreCase("") != 0){
            e.addContent(new Element(OFFSET_TYPE_TAG).setText(offsetType));
        }
        metadataElementCache = e;
        return e;
        
    }   
    /**
     * Returns this object's value attribute. NaN if object has no value.
     * @return
     */
    public double getValue(){
        return value;
    }
    /**
     *
     * @return
     */
    public Date getTimeStamp(){
        return timestamp;
    }
    /**
     *
     * @return
     */
    public double getUtcOffset(){
        return utcOffset;
    }
    /**
     *
     * @return
     */
    public String getSite(){
        return site;
    }
    /**
     *
     * @return
     */
    public String getSource(){
        return source;
    }
    /**
     *
     * @return
     */
    public String getUnit(){
        return unit;
    }
    /**
     *
     * @return
     */
    public double getOffsetValue(){
        return offsetValue;
    }
    /**
     *
     * @return
     */
    public String getOffsetType(){
        return offsetType;
    }
    /**
     *
     * @return
     */
    public int getSensorID(){
        return sensorID;
    }
    /**
     *
     * @return
     */
    public String getAggMethod(){
        return aggMethod;
    }
    /**
     *
     * @return
     */
    public String getAggSpan(){
        return aggSpanString;
    }
    /**
     * Returns
     * @return
     */
    public int getDuplicateID(){
        return duplicateID;
    }
    /**
     * Returns variable name attribute for this object.
     * @return Variable name as string. Null if no variable name given.
     */
    public String getVariable(){
        return variable;
    }
    /**
     * Gets the flag attribute. If null if it has no flag.
     *
     * @return flag string for this value object
     */
    public String getFlag(){
        return flag;
    }
    
    /***************************************************************************
    * Setter methods below
    ***************************************************************************/
    /**
     *
     * @param var
     * @return the value object on which the method was called
     */
    public ValueObject setVariable(String var){
        variable = var;
        return this;
    }
    /**
     * Sets the offset value attribute. The offset value represents the magnitude
     * of the offset defined by the offset type.
     *
     * @param off the new value for the offset attribute
     * @return the value object on which the method was called
     */
    public ValueObject setOffsetValue(double off){
        offsetValue = off;
        return this;
    }
    /**
     *
     * @param val
     * @return the value object on which the method was called
     */
    public ValueObject setValue(double val){
        value = val;
        return this;
    }
    /**
     *
     * @param ts
     * @return the value object on which the method was called
     */
    public ValueObject setTimeStamp(Date ts){
        timestamp = ts;
        return this;
    }
    /**
     *
     * @param s
     * @return the value object on which the method was called
     */
    public ValueObject setSource(String s){
        source = s;
        return this;
    }
    /**
     *
     * @param utcOff
     * @return the value object on which the method was called
     */
    public ValueObject setUtcOffset(double utcOff){
        utcOffset = utcOff;
        return this;
    }
    /**
     *
     * @param s
     * @return the value object on which the method was called
     */
    public ValueObject setSite(String s){
        site = s;
        return this;
    }
    /**
     *
     * @param agg
     * @return the value object on which the method was called
     */
    public ValueObject setAggMethod(String agg){
        aggMethod = agg;
        return this;
    }
    /**
     *
     * @param u
     * @return the value object on which the method was called
     */
    public ValueObject setUnit(String u){
        unit = u;
        return this;
    }
    /**
     *
     * @param type
     * @return the value object on which the method was called
     */
    public ValueObject setOffsetType(String type){
        offsetType = type;
        return this;
    }
    /**
     *
     * @param agg
     * @return the value object on which the method was called
     */
    public ValueObject setAggSpan(String agg){
        aggSpanString = agg;
        return this;
    }
    /**
     *
     * @param dup
     * @return the value object on which the method was called
     */
    public ValueObject setDuplicateID(int dup){
        duplicateID = dup;
        return this;
    }
    /**
     *
     * @param sens
     * @return the value object on which the method was called
     * @deprecated
     */
    @Deprecated
    public ValueObject setSensorID(int sens){
        sensorID = sens;
        return this;
    }
    /**
     * Sets the flag attribute. Flag cannot be longer than 3 characters.
     * 
     * @param flag string of flag characters to set
     * @return the value object on which the method was called
     */    
    public ValueObject setFlag(String flag){
        if(flag.length() > 3){
            throw new IllegalArgumentException("Flag can only be a max of 3 characters.");
        }
        this.flag = flag;
        return this;
    }
    /**
     * Returns a cloned ValueObject without any value specific attributes.
     * This does not copy value, timestamp, or flag.
     * 
     * @return cloned ValueObject instance
     */
    public ValueObject cloneValueless(){
        ValueObject myClone = new ValueObject();
        myClone.setAggMethod(this.getAggMethod());
        myClone.setAggSpan(this.getAggSpan());
        myClone.setDuplicateID(this.getDuplicateID());
        myClone.setOffsetType(this.getOffsetType());
        myClone.setOffsetValue(this.getOffsetValue());
        myClone.setSensorID(this.getSensorID());
        myClone.setSite(this.getSite());
        myClone.setSource(this.getSource());
        myClone.setTimeStamp(this.getTimeStamp());
        myClone.setUnit(this.getUnit());
        myClone.setUtcOffset(this.getUtcOffset());
        myClone.setValue(this.getValue());
        myClone.setVariable(this.getVariable());
        return myClone;
    }
    /*
     * This returns an identical copy of this ValueObject. This includes all
     * value specific attributes.
     * 
     * @return completely cloned ValueObject copy of this value
     */
    @Override
    public ValueObject clone(){
        ValueObject cln = this.cloneValueless();
        cln.setTimeStamp(this.getTimeStamp());
        cln.setValue(this.getValue());
        return cln;
    }
    /**
     * Returns XML serialized version of this ValueObject.
     * 
     * @return XML form of this object in the form of a jdom.Element object
     */
    public Element getAsXml(){
        Element metadata = getMetadataXml();
        Element toReturn = new Element(VALUE_OBJECT_TAG);
        toReturn.addContent(metadata);
        toReturn.addContent(new Element(VALUE_TAG).setText(Double.toString(value)));

        //2010-04-12 law: Just changed this to the new format. 
        toReturn.addContent(new Element(TIMESTAMP_TAG)
                .setText(dSimp.format(timestamp)));
        
        return toReturn;
    }
    /**
     * Configures this object with the attribute values defined in the 
     * provided XML element object. 
     *
     * @param e The XML formatted value object
     * @throws java.text.ParseException
     */
    public void configure(Element e)throws java.text.ParseException{
        
        this.setValue(Double.parseDouble(e.getChildText(VALUE_TAG)));

        //We could have two different formats. Try the updated version first,
        // if that doesn't work, switch to the old version.
        // TODO: It would be great if we didn't have to check both and could just
        // use the newer version. Need way to update various GLEON sites!!!!
        try{
            this.setTimeStamp(dSimp.parse(e.getChildText(TIMESTAMP_TAG)));
        }catch(java.text.ParseException ex){
            DateFormat d = DateFormat.getDateTimeInstance();
            this.setTimeStamp(d.parse(e.getChildText(TIMESTAMP_TAG)));
        }
        Element metadataXml = e.getChild(METADATA_TAG);
        //These are all required.
        this.setUtcOffset(Double.valueOf(metadataXml.getChildText(UTCOFFSET_TAG)));
        this.setSite(metadataXml.getChildText(SITE_TAG));
        this.setVariable(metadataXml.getChildText(VARIABLE_TAG));
        this.setSource(metadataXml.getChildText(SOURCE_TAG));
        this.setUnit(metadataXml.getChildText(UNIT_TAG));
        this.setAggMethod(metadataXml.getChildText(AGGREGATION_METHOD_TAG));
        this.setAggSpan(metadataXml.getChildText(AGGREGATION_SPAN_TAG));
        
        //These are optional
        if(metadataXml.getChild(SENSOR_TAG) != null && 
                !metadataXml.getChildText(SENSOR_TAG).equalsIgnoreCase("")){
            try{
            sensorID = Integer.valueOf(metadataXml.getChildText(SENSOR_TAG));
            }catch(NumberFormatException nfe){
                //do nothing, probably a relic
            }
        }
        if(metadataXml.getChild(OFFSET_TAG) != null && 
                !metadataXml.getChildText(OFFSET_TAG).equalsIgnoreCase(""))
            this.setOffsetValue(
                    Double.valueOf(metadataXml.getChildText(OFFSET_TAG)));
        
        if(metadataXml.getChild(OFFSET_TYPE_TAG) != null &&
                !metadataXml.getChildText(OFFSET_TYPE_TAG).equalsIgnoreCase(""))
            this.setOffsetType(metadataXml.getChildText(OFFSET_TYPE_TAG));
        
        if(metadataXml.getChild(SAMPLE_REP_ID_TAG) != null && 
                !metadataXml.getChildText(SAMPLE_REP_ID_TAG).equalsIgnoreCase(""))
            this.setDuplicateID(
                    Integer.valueOf(metadataXml.getChildText(SAMPLE_REP_ID_TAG)));
    }
    @Override
    public String toString(){
        if(offsetValue != Double.NaN){
            return variable + "(" + offsetValue + ")" + site;
        }else{
            return variable + "." + site;
        }
    }
    /**
     * Returns a concatenated string that will uniquely identify
     * @return
     */
    public String getUniqueStreamString(){
        return (getSite()+getVariable()+getSource()+getUnit()+
                getOffsetValue()+getOffsetType()+getAggSpan().toString()+
                getAggMethod()+getDuplicateID()).toLowerCase();
    }

    /**
     *
     * @return
     */
    public String getUniqueStreamStringWithoutDepth(){
        return (getSite()+getVariable()+getSource()+getUnit()+
                getAggSpan().toString()+
                getAggMethod()+getDuplicateID()).toLowerCase();
    }

}
