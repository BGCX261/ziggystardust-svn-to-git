/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wisc.ziggy.test;

import com.wisc.csvParser.IDataParser;
import com.wisc.csvParser.IDataRepository;
import com.wisc.csvParser.ValueObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import org.jdom.Element;

/**
 *
 * @author lawinslow
 */
public class DataRepositoryNull implements IDataRepository{

    private boolean isStarted = false;
    private String panelID;
    private SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public DataRepositoryNull(){
        panelID = getRepositoryShortname() + (new Random()).nextInt();
    }

    @Override
    public String getRepositoryDescription() {
        return "This repository does nothing with the data. " +
                "Should only be terminal repo though, it will *not*" +
                "pass on the data!!";
    }

    @Override
    public String getRepositoryShortname() {
        return "Null Data Repository";
    }

    @Override
    public void configure(Element e) throws Exception {
        //do nothing
    }

    @Override
    public boolean Start() {
        isStarted = true;
        return true;
    }

    @Override
    public boolean Stop() {
        isStarted = false;
        return true;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public boolean NewRow(ArrayList<ValueObject> newRow) {

        for(ValueObject v:newRow){
            System.out.print(format.format(v.getTimeStamp())+":");
            System.out.println(Double.toString(v.getValue()));

        }
        return true;
    }

    @Override
    public boolean NewValue(ValueObject newValue) {
        System.out.print(format.format(newValue.getTimeStamp())+":");
        System.out.println(Double.toString(newValue.getValue()));
        return true;
    }

    @Override
    public IDataRepository getChildRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setChildRepository(IDataRepository child) {
        //thanks. Do nothing
    }

    @Override
    public Element getSettingsXml() {
        return (new Element(IDataRepository.DATA_REPOSITORY_TAG))
                .setAttribute("type",
                DataRepositoryNull.class.getName());
    }

    @Override
    public JPanel getStatusJPanel() {
        return new JPanel();
    }

    @Override
    public String getPanelID() {
        return panelID;
    }
}
