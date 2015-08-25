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

import com.wisc.csvParser.plugins.DataFilterGleonDist.events;
import org.apache.log4j.Logger;

/**
 *
 * @author  lawinslow
 */
public class JPanelGleonDist extends javax.swing.JPanel {
    
    private DataFilterGleonDist filter;
    private static Logger logger = Logger.getLogger(JPanelGleonDist.class.getName());
    
    /** Creates new form JPanelGleonDist
     * @param d Pass the underlying Data repository object
     */
    public JPanelGleonDist(DataFilterGleonDist d) {
        initComponents();
        
        logger.trace("Constructing JPanel Object");
        
        filter = d;
        filter.addEventHandler(new DataFilterGleonDist.IEventHandler() {

            @Override
            public void eventRaised(events e) {
                handleEvent(e);
            }
        });
        
        updateDisplay();
        startStopStateChange(d.isStarted());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        addressTB = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        usernameTB = new javax.swing.JTextField();
        passwordTB = new javax.swing.JPasswordField();
        saveButton = new javax.swing.JButton();
        uploadedCountLabel = new javax.swing.JLabel();
        cachedCountLabel = new javax.swing.JLabel();
        startedCB = new javax.swing.JCheckBox();
        uploadPathTB = new javax.swing.JTextField();
        changePathEnableCB = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setText("Address:");

        addressTB.setText("data.gleon.org");

        jLabel2.setText("Username:");

        jLabel3.setText("Password:");

        usernameTB.setText("gleonUser");

        passwordTB.setText("pass4gleon");

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        uploadedCountLabel.setText("Files Uploaded Count: 0");

        cachedCountLabel.setText("Files Cached Count: 0");

        startedCB.setText("Started");
        startedCB.setEnabled(false);

        uploadPathTB.setText("/gleonData/");
        uploadPathTB.setEnabled(false);

        changePathEnableCB.setText("Change Path (Expert Only!)");
        changePathEnableCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePathEnableCBActionPerformed(evt);
            }
        });

        jLabel4.setText("Upload Path:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(saveButton)
                    .addComponent(cachedCountLabel)
                    .addComponent(uploadedCountLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordTB, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                            .addComponent(usernameTB, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                            .addComponent(addressTB, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                            .addComponent(uploadPathTB, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addComponent(startedCB))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(changePathEnableCB)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(addressTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startedCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(passwordTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uploadPathTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changePathEnableCB)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addComponent(saveButton)
                .addGap(30, 30, 30)
                .addComponent(uploadedCountLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cachedCountLabel)
                .addContainerGap(93, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void handleEvent(DataFilterGleonDist.events e){
        if(e ==DataFilterGleonDist.events.reconfigured){//Update panel fields
            updateDisplay();
        }else if(e ==DataFilterGleonDist.events.newFile){
            cachedCountLabel.setText("Files Cached Count: " +
                    Integer.toString(filter.getCachedFilesCount()));
        }else if(e ==DataFilterGleonDist.events.fileUploaded){
            uploadedCountLabel.setText("Files Uploaded Count: "
                    + Integer.toString(filter.getUploadedFilesCount()));
            cachedCountLabel.setText("Files Cached Count: " +
                    Integer.toString(filter.getCachedFilesCount()));
        }else if(e ==DataFilterGleonDist.events.connectionStarted){
            
        }else if(e ==DataFilterGleonDist.events.connectionFailed){
            
        }else if(e==DataFilterGleonDist.events.started ||
                e==DataFilterGleonDist.events.stopped){
            startStopStateChange(filter.isStarted());
        }
            
    }
    
    private void startStopStateChange(boolean isStarted){
        startedCB.setSelected(isStarted);
        addressTB.setEnabled(!isStarted);
        usernameTB.setEnabled(!isStarted);
        passwordTB.setEnabled(!isStarted);
        if(isStarted){
            uploadPathTB.setEnabled(false);
        }else{
            uploadPathTB.setEnabled(changePathEnableCB.isSelected());
        }
        changePathEnableCB.setEnabled(!isStarted);
        saveButton.setEnabled(!isStarted);
    }
    
    private void updateDisplay(){
            logger.trace("Core Filter reconfigured, updating JPanel fields");
            addressTB.setText(filter.getFtpHost());
            passwordTB.setText(filter.getPassword());
            usernameTB.setText(filter.getUsername());
            uploadPathTB.setText(filter.getUploadPath());
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        filter.setFtpHost(addressTB.getText());
        filter.setPassword(String.valueOf(passwordTB.getPassword()));
        filter.setUsername(usernameTB.getText());
        filter.setUploadPath(uploadPathTB.getText());

    }//GEN-LAST:event_saveButtonActionPerformed

    private void changePathEnableCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePathEnableCBActionPerformed
        uploadPathTB.setEnabled(changePathEnableCB.isSelected());
    }//GEN-LAST:event_changePathEnableCBActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressTB;
    private javax.swing.JLabel cachedCountLabel;
    private javax.swing.JCheckBox changePathEnableCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField passwordTB;
    private javax.swing.JButton saveButton;
    private javax.swing.JCheckBox startedCB;
    private javax.swing.JTextField uploadPathTB;
    private javax.swing.JLabel uploadedCountLabel;
    private javax.swing.JTextField usernameTB;
    // End of variables declaration//GEN-END:variables
    
}
