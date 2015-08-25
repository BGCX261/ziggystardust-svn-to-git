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

import com.wisc.csvParser.plugins.DataParserTableBased.events;
import org.jdom.input.SAXBuilder;
import org.jdom.*;
import java.awt.FileDialog;
import javax.swing.*;
import com.wisc.csvParser.*;
import com.wisc.configFileConverter.ConfigFile;
import java.util.ArrayList;
import java.awt.Component;

/**
 *
 * @author  lawinslow
 */
public class JPanelTableBased extends javax.swing.JPanel {

    DataParserTableBased parser;
    //A list of the components that need to have enable toggled when parsing
    private ArrayList<Component> components = new ArrayList<Component>();

    /** Creates new form JPanelTableBased
     * This panel holds the feedback and configuration controls for the user to
     * setup, monitor, and administer a single TableBased parser instance.
     * 
     * @param p Underlying DataParserTableBased object.
     */
    public JPanelTableBased(DataParserTableBased p) {
        initComponents();
        parser = p;
        components.add(monitoredFileList);
        components.add(monitoredFilesList);
        components.add(removeFileButton);
        components.add(editFileButton);
        components.add(addFileButton);
        

        parser.addEventHandler(new DataParserTableBased.TableBasedEventHandler() {

            @Override
            public void eventRaised(events v) {
                newEvent(v);
            }
        });

        this.updateDisplay();
    }
    
    /** Creates new form JPanelTableBased
     * Shows an alert message to the user. Used by the underlying object
     * to notify the user of important exceptions.
     * 
     * @param message Message to be show to the user by this swing object.
     */
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(
                (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this), message);
    }
    /**
     * Updates the panel display. Call when settings or general state changes.
     * 
     */
    private void updateDisplay() {
        monitoredFileList.setListData(parser.getMonitoredFiles());

        //updates 'started' checkbox
        startedCheckBox.setState(parser.isStarted());
        //enables and disables components depending on state.
        for (Component c:components) {
            c.setEnabled(!parser.isStarted());
        }
    }
    /**
     * Handles different events raised by underlying parser object.
     * Mostly pertaining to update of GUI.
     * @param e
     */
    public void newEvent(DataParserTableBased.events e) {
        if (e == DataParserTableBased.events.stopped ||
                e == DataParserTableBased.events.started) {
            updateDisplay();
        } else if (e == DataParserTableBased.events.reconfigured) {
            updateDisplay();
        } else if (e == DataParserTableBased.events.newMalformedRow) {
            malformedRowsLabel.setText("Malformed Rows: " +
                    Integer.toString(parser.getMalformedRows().size()));
        } else if (e == DataParserTableBased.events.rowParsed) {
            rowsParsedLabel.setText("Rows Parsed: " +
                    Long.toString(parser.getRowsParsedTotal()));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        startedCheckBox = new java.awt.Checkbox();
        monitoredFilesList = new javax.swing.JScrollPane();
        monitoredFileList = new javax.swing.JList();
        rowsParsedLabel = new javax.swing.JLabel();
        malformedRowsLabel = new javax.swing.JLabel();
        addFileButton = new javax.swing.JButton();
        removeFileButton = new javax.swing.JButton();
        editFileButton = new javax.swing.JButton();

        jLabel1.setText("TableBased Parser");

        jLabel3.setText("Monitored File(s):");

        startedCheckBox.setEnabled(false);
        startedCheckBox.setLabel("Started?");

        monitoredFileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                monitoredFileListMouseClicked(evt);
            }
        });
        monitoredFilesList.setViewportView(monitoredFileList);

        rowsParsedLabel.setText("Rows Parsed: 0");

        malformedRowsLabel.setText("Malformed Rows: 0");

        addFileButton.setText("Add File");
        addFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileButtonActionPerformed(evt);
            }
        });

        removeFileButton.setText("Remove File");
        removeFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileButtonActionPerformed(evt);
            }
        });

        editFileButton.setText("Edit File");
        editFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(startedCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(monitoredFilesList, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(removeFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editFileButton))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(malformedRowsLabel)
                        .addComponent(rowsParsedLabel)))
                .addContainerGap(193, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monitoredFilesList, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeFileButton)
                    .addComponent(addFileButton)
                    .addComponent(editFileButton))
                .addGap(95, 95, 95)
                .addComponent(rowsParsedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(malformedRowsLabel)
                .addGap(10, 10, 10)
                .addComponent(startedCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Stop button action listener.
     * @param evt
     */    /**
     * Start button action listener.
     * @param evt
     */
    /**
     * Load Config button action listener. Dispays file dialog box 
     * to allow user to select configuration file.
     * @param evt
     */
    private void removeFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileButtonActionPerformed
        if(monitoredFileList.getSelectedIndex() != -1){
            if(JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to remove this monitored file?")!=JOptionPane.OK_OPTION)
                return;
            
            parser.removeMonitoredFile(monitoredFileList.getSelectedIndex());
            updateDisplay();
        }
            
}//GEN-LAST:event_removeFileButtonActionPerformed

    private void monitoredFileListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_monitoredFileListMouseClicked
        if(evt.getClickCount()==2 && !parser.isStarted()){
            JDialogTableBased editDialog = 
                    new JDialogTableBased(null,true,
                    parser.getMonitoredFile(monitoredFileList.getSelectedIndex()));
                    
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        }
    }//GEN-LAST:event_monitoredFileListMouseClicked

    private void addFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileButtonActionPerformed
        DataParserTableBased.TableBasedFile file = parser.getNewFileObject();
        parser.addMonitoredFile(file);
        // Below 7 lines add the ability to choose a file directly from here
        java.awt.FileDialog chooseFile = new java.awt.FileDialog(new java.awt.Frame());

        chooseFile.setVisible(true);

        if(chooseFile.getFile() != null){
            file.setDataFilePath(chooseFile.getDirectory() + chooseFile.getFile());
        }

        updateDisplay();
}//GEN-LAST:event_addFileButtonActionPerformed

    private void editFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editFileButtonActionPerformed
        if(!parser.isStarted() && monitoredFileList.getSelectedIndex() != -1){
            JDialogTableBased editDialog =
                    new JDialogTableBased(null,true,
                    parser.getMonitoredFile(monitoredFileList.getSelectedIndex()));

            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        }
    }//GEN-LAST:event_editFileButtonActionPerformed
    /**
     * Converts config file from the older, non xml format 
     * to the new xml based format
     * 
     * @param path Path to the config file to try converting
     * @return true if file conversion was successful, otherwise false
     */
    private boolean tryFileConversion(String path) {
        try {
            ConfigFile cfg = new ConfigFile(path);
            cfg.saveConfigFileNewFormat(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFileButton;
    private javax.swing.JButton editFileButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel malformedRowsLabel;
    private javax.swing.JList monitoredFileList;
    private javax.swing.JScrollPane monitoredFilesList;
    private javax.swing.JButton removeFileButton;
    private javax.swing.JLabel rowsParsedLabel;
    private java.awt.Checkbox startedCheckBox;
    // End of variables declaration//GEN-END:variables

}