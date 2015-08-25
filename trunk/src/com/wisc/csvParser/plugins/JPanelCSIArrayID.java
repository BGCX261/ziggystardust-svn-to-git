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

import com.wisc.configFileConverter.ConfigFile;
import com.wisc.csvParser.plugins.DataParserCSIArrayID.events;
import org.jdom.input.SAXBuilder;
import org.jdom.*;
import java.awt.FileDialog;
import javax.swing.*;
import java.io.File;
import com.wisc.csvParser.*;
import java.util.ArrayList;
import java.awt.Component;
import java.awt.FileDialog;


/**
 *
 * @author  lawinslow
 */
public class JPanelCSIArrayID extends javax.swing.JPanel {

    DataParserCSIArrayID parser;
    
    //A list of the components that need to have enable toggled when parsing
    private ArrayList<Component> components = new ArrayList<Component>();

    /** Creates new form JPanelCSIArrayID */
    public JPanelCSIArrayID(DataParserCSIArrayID p) {
        initComponents();
        parser = p;
        components.add(monitoredFileTB);
        components.add(removeArrayButton);
        components.add(addArrayButton);
        components.add(editArrayButton);
        components.add(changeMonitoredFileButton);
        components.add(startAtFileStartButton);
        
        parser.addEventHandler(new DataParserCSIArrayID.CSIArrayEventHandler() {

            @Override
            public void eventRaised(events v) {
                newEvent(v);
            }
        });

        updateDisplay();

    }
    
    public void showMessageDialog(String message){
        JOptionPane.showMessageDialog(
                (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this), message);
    }

    public void newEvent(DataParserCSIArrayID.events e){
        if(e == events.started){
            updateDisplay();
        }else if(e==events.stopped){
            updateDisplay();
        }else if(e==events.reconfigured){
            updateDisplay();
        }else if(e==events.newMalformedRow){
            malformedRowsLabel.setText("Malformed Rows: " +
                    Integer.toString(parser.getMalformedRows().size()));
        }else if(e==events.startDataParse){
            parsingPanel.setBackground(java.awt.Color.GREEN);
            
        }else if(e==events.stopDataParse){
            parsingPanel.setBackground(java.awt.Color.RED);
        }else if(e==events.rowParsed){
            rowsParsedLabel.setText("Rows Parsed: " +
                    Long.toString(parser.getRowsParsedTotal()));
        }
        
    }
    
    //I'm being lazy. Instead of having property change events
    //I'm just going to have updateDisplay which re-populates all
    //gui objects with current values
    public void updateDisplay() {
        monitoredFileTB.setText(parser.getFileMonitored());
        startedCheckBox.setState(parser.isStarted());
        ArrayIDList.removeAll();
        ArrayIDList.setListData(parser.getArrayIdCollection().values().toArray());
        //update enabled/disabled UI components
        startedCheckBox.setState(parser.isStarted());
        //Reenables all of the interactive components when the parser is stopped
        for (Component c : components) {
            c.setEnabled(!parser.isStarted());
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startedCheckBox = new java.awt.Checkbox();
        jLabel3 = new javax.swing.JLabel();
        monitoredFileTB = new javax.swing.JTextField();
        parsingPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        rowsParsedLabel = new javax.swing.JLabel();
        malformedRowsLabel = new javax.swing.JLabel();
        startAtFileStartButton = new javax.swing.JButton();
        editArrayButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ArrayIDList = new javax.swing.JList();
        addArrayButton = new javax.swing.JButton();
        removeArrayButton = new javax.swing.JButton();
        changeMonitoredFileButton = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel1.setText("CSIArrayID Parser");

        startedCheckBox.setEnabled(false);
        startedCheckBox.setLabel("Started?");

        jLabel3.setText("Monitored File:");

        javax.swing.GroupLayout parsingPanelLayout = new javax.swing.GroupLayout(parsingPanel);
        parsingPanel.setLayout(parsingPanelLayout);
        parsingPanelLayout.setHorizontalGroup(
            parsingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );
        parsingPanelLayout.setVerticalGroup(
            parsingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jLabel5.setText("Parsing...");

        rowsParsedLabel.setText("Rows Parsed: 0");

        malformedRowsLabel.setText("Malformed Rows: 0");

        startAtFileStartButton.setText("Start at line 0");
        startAtFileStartButton.setToolTipText("This resets the internal line counter to zero. Forces system to re-parse entire file.");
        startAtFileStartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startAtFileStartButtonActionPerformed(evt);
            }
        });

        editArrayButton.setText("Edit ArrayID");
        editArrayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editArrayButtonActionPerformed(evt);
            }
        });

        ArrayIDList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ArrayIDListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ArrayIDList);

        addArrayButton.setText("Add ArrayID");
        addArrayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addArrayButtonActionPerformed(evt);
            }
        });

        removeArrayButton.setText("Remove ArrayID");
        removeArrayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeArrayButtonActionPerformed(evt);
            }
        });

        changeMonitoredFileButton.setText("...");
        changeMonitoredFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeMonitoredFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(monitoredFileTB, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(malformedRowsLabel)
                                            .addComponent(rowsParsedLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                                        .addComponent(startAtFileStartButton))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(removeArrayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(editArrayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(addArrayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                            .addComponent(startedCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(changeMonitoredFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 224, Short.MAX_VALUE)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parsingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(parsingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(monitoredFileTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeMonitoredFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rowsParsedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(malformedRowsLabel))
                    .addComponent(startAtFileStartButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(editArrayButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeArrayButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addArrayButton)))
                .addGap(35, 35, 35)
                .addComponent(startedCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private boolean tryFileConversion(String path) {
        try {
            ConfigFile cfg = new ConfigFile(path);
            cfg.saveConfigFileNewFormat(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //I don't want this event here, but the computer won't let me delete it right now.
    private void startAtFileStartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startAtFileStartButtonActionPerformed
        parser.setRowPointer(0);
        parser.setLastModifiedPointer(0);
        javax.swing.JOptionPane.showMessageDialog(null, "Row Pointer reset to zero.");
}//GEN-LAST:event_startAtFileStartButtonActionPerformed

    private void editArrayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editArrayButtonActionPerformed
        if(ArrayIDList.getSelectedValue()!= null){
            
           DialogCSIArrayMetadata editDialog = new DialogCSIArrayMetadata(null, true,
                   (DataParserCSIArrayID.ArrayIDObject)ArrayIDList.getSelectedValue());

           editDialog.setLocationRelativeTo(this);
           editDialog.setVisible(true);
        }
}//GEN-LAST:event_editArrayButtonActionPerformed

    private void addArrayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addArrayButtonActionPerformed

        String newIDStr = JOptionPane.showInputDialog("What is the new array id?");
        if(newIDStr == null)
            return;
        
        Integer newID = null;
        try{
             newID = Integer.parseInt(newIDStr);
        }catch(Exception e){
            return;
        }
        
        DataParserCSIArrayID.ArrayIDObject newObj = parser.getNewArrayIDObject(newID);
        if(!parser.getArrayIdCollection().contains(newID)){
            parser.addArrayID(newID, newObj);
        }else{
            JOptionPane.showMessageDialog(null, "ID already exists!");
        }
}//GEN-LAST:event_addArrayButtonActionPerformed

    private void removeArrayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeArrayButtonActionPerformed
        if(ArrayIDList.getSelectedValue() != null){
            parser.removeArrayID(
                    ((DataParserCSIArrayID.ArrayIDObject)ArrayIDList.getSelectedValue()).id);
        }
        
}//GEN-LAST:event_removeArrayButtonActionPerformed

    private void ArrayIDListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ArrayIDListMouseClicked
        if(evt.getClickCount()==2 && !parser.isStarted()){
            if(ArrayIDList.getSelectedValue()!= null){

               DialogCSIArrayMetadata editDialog = new DialogCSIArrayMetadata(null, true,
                       (DataParserCSIArrayID.ArrayIDObject)ArrayIDList.getSelectedValue());

               editDialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_ArrayIDListMouseClicked

    private void changeMonitoredFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeMonitoredFileButtonActionPerformed
        FileDialog newConfig = new FileDialog(new java.awt.Frame());
        newConfig.setMode(FileDialog.LOAD);
        newConfig.setLocationRelativeTo(this);
        newConfig.setVisible(true);
        if(newConfig.getFile() == null){
            return;
        }
        parser.setFileMonitored(newConfig.getDirectory()+newConfig.getFile());
        
}//GEN-LAST:event_changeMonitoredFileButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList ArrayIDList;
    private javax.swing.JButton addArrayButton;
    private javax.swing.JButton changeMonitoredFileButton;
    private javax.swing.JButton editArrayButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel malformedRowsLabel;
    private javax.swing.JTextField monitoredFileTB;
    private javax.swing.JPanel parsingPanel;
    private javax.swing.JButton removeArrayButton;
    private javax.swing.JLabel rowsParsedLabel;
    private javax.swing.JButton startAtFileStartButton;
    private java.awt.Checkbox startedCheckBox;
    // End of variables declaration//GEN-END:variables
}
