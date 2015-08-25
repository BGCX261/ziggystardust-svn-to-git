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

package com.wisc.csvParser.plugins.CrossbowMoteParser;

import com.wisc.csvParser.plugins.CrossbowMoteParser.DataSourceDNRMotes.Mote;
import com.wisc.csvParser.plugins.CrossbowMoteParser.DataSourceDNRMotes.events;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author  lawinslow
 */
public class JPanelDNRMotes extends javax.swing.JPanel implements DataSourceDNRMotes.IEventHandler {
    
    DataSourceDNRMotes parser;
    
    /** Creates new form JPanelDNRMotes
     * @param parent Underlying data source object.
     */
    public JPanelDNRMotes(DataSourceDNRMotes parent) {
        initComponents();
        
        parser = parent;
        
        parser.addEventHander(this);

        updateDisplay();
        setEnabledDisabled(!parser.isStarted());
    }

    private void updateDisplay(){
        motesList.removeAll();
        motesList.setListData(new Vector<Mote>(parser.getMotes()));

        hostTF.setText(parser.getDbHost());
        userTF.setText(parser.getDbUser());
        nameTF.setText(parser.getDbName());

    }


    @Override
    public void handleEvent(events e) {
        if(e.equals(DataSourceDNRMotes.events.newMessage)){
            messageLabel.setText(parser.getMessage());
        }else if(e.equals(DataSourceDNRMotes.events.started)){
            this.setEnabledDisabled(!parser.isStarted());
        }else if(e.equals(DataSourceDNRMotes.events.stopped)){
            this.setEnabledDisabled(!parser.isStarted());
        }
    }

    private void setEnabledDisabled(boolean b){
        addMoteButton.setEnabled(b);
        removeMoteButton.setEnabled(b);
        editMoteButton.setEnabled(b);
        hostTF.setEnabled(b);
        userTF.setEnabled(b);
        nameTF.setEnabled(b);
        passTF.setEnabled(b);
        saveButton.setEnabled(b);
        cancelButton.setEnabled(b);
        testButton.setEnabled(b);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        motesList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        addMoteButton = new javax.swing.JButton();
        removeMoteButton = new javax.swing.JButton();
        editMoteButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        hostTF = new javax.swing.JTextField();
        nameTF = new javax.swing.JTextField();
        userTF = new javax.swing.JTextField();
        passTF = new javax.swing.JPasswordField();
        testButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();

        motesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                motesListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(motesList);

        jLabel1.setText("Motes:");

        addMoteButton.setText("+");
        addMoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMoteButtonActionPerformed(evt);
            }
        });

        removeMoteButton.setText("-");
        removeMoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMoteButtonActionPerformed(evt);
            }
        });

        editMoteButton.setText("Edit");
        editMoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMoteButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Info"));

        jLabel2.setText("Host:");

        jLabel3.setText("DB Name:");

        jLabel4.setText("User:");

        jLabel5.setText("Pass:");

        passTF.setText("jPasswordField1");

        testButton.setText("Test DB");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(hostTF, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(userTF, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(nameTF, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(passTF))
                .addContainerGap(63, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(saveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testButton)
                    .addComponent(saveButton)
                    .addComponent(cancelButton)))
        );

        messageLabel.setText("Everything seems well.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addMoteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editMoteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeMoteButton))
                    .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLabel)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addMoteButton)
                    .addComponent(editMoteButton)
                    .addComponent(removeMoteButton)
                    .addComponent(messageLabel))
                .addContainerGap(67, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeMoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMoteButtonActionPerformed
        if(motesList.getSelectedIndex() >=0){
            Mote mote = parser.getMotes().get(motesList.getSelectedIndex());
            if(mote != null){
                parser.removeMote(mote);
            }
        updateDisplay();
        }
    }//GEN-LAST:event_removeMoteButtonActionPerformed

    private void editMoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMoteButtonActionPerformed
        displayMoteDialog(parser.getMotes().get(motesList.getSelectedIndex()));
    }//GEN-LAST:event_editMoteButtonActionPerformed

    private void displayMoteDialog(Mote m){
        if(m==null){
            return;
        }
        JDialogMoteConfig config = new JDialogMoteConfig(null,true,m);
        config.setLocationRelativeTo(this);
        config.setVisible(true);
    }

    private void addMoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMoteButtonActionPerformed
        parser.addMote(parser.getNewMote());
        updateDisplay();
    }//GEN-LAST:event_addMoteButtonActionPerformed

    private void motesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_motesListMouseClicked
        if(parser.isStarted())
            return;

        if(evt.getClickCount()==2){
            displayMoteDialog(parser.getMotes().get(motesList.getSelectedIndex()));
        }
    }//GEN-LAST:event_motesListMouseClicked

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        parser.setDbHost(hostTF.getText());
        parser.setDbName(nameTF.getText());
        parser.setDbPass(new String(passTF.getPassword()));
        parser.setDbUser(userTF.getText());

    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        hostTF.setText(parser.getDbHost());
        nameTF.setText(parser.getDbName());
        userTF.setText(parser.getDbUser());
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        if(parser.testDB()){
            JOptionPane.showMessageDialog(null, "Connection successful!");
        }else{
            JOptionPane.showMessageDialog(null, "Check DB Settings.");
        }
    }//GEN-LAST:event_testButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMoteButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton editMoteButton;
    private javax.swing.JTextField hostTF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JList motesList;
    private javax.swing.JTextField nameTF;
    private javax.swing.JPasswordField passTF;
    private javax.swing.JButton removeMoteButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton testButton;
    private javax.swing.JTextField userTF;
    // End of variables declaration//GEN-END:variables

    
}
