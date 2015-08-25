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


/**
 *
 * @author  lawinslow
 */
public class JDialogDataColumn extends javax.swing.JDialog {
    
    ValueObjectJPanel valueSettingsJPanel;
    DataColumn colObj;
    
    /** Creates new form JDialogDataColumn
     * @param parent
     * @param modal 
     * @param col
     */
    public JDialogDataColumn(java.awt.Frame parent, boolean modal,DataColumn col) {
        super(parent, modal);
        initComponents();
        
        colTypeCB.addItem(DataColumn.DATA_COLUMN_TYPE);
        colTypeCB.addItem(DataColumn.IGNORE_COLUMN_TYPE);
        
        
        colObj = col;
        
        valueSettingsJPanel= new ValueObjectJPanel(col.getTemplateValue());


        valueSettings.add(valueSettingsJPanel,"valueSettings");
        update();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        templateValuePane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        colTypeCB = new javax.swing.JComboBox();
        valueSettings = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        templateValuePane.setLayout(new java.awt.CardLayout());

        jLabel1.setText("Column Type:");

        colTypeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colTypeCBActionPerformed(evt);
            }
        });

        valueSettings.setLayout(new java.awt.CardLayout());

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(templateValuePane, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(229, Short.MAX_VALUE))
            .addComponent(valueSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap(274, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(colTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(templateValuePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(valueSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void colTypeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colTypeCBActionPerformed
        if(evt.getActionCommand().compareToIgnoreCase("comboboxchanged")==0){
            if(((String)colTypeCB.getSelectedItem()).compareTo(DataColumn.IGNORE_COLUMN_TYPE)==0){
                valueSettings.setVisible(false);
            }else{
                valueSettings.setVisible(true);
            }
            if(colObj!=null){
                colObj.setType((String)colTypeCB.getSelectedItem());
            }
           
        }
    }//GEN-LAST:event_colTypeCBActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if(valueSettingsJPanel.checkAndSave()){
            this.setVisible(false);
            this.dispose();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void updateValuePane(){
        //valueSettings.add(settings,"valueSettings");
    }
    
    private void update(){
        colTypeCB.setSelectedItem(colObj.getType());
    }
    

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox colTypeCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel templateValuePane;
    private javax.swing.JPanel valueSettings;
    // End of variables declaration//GEN-END:variables
    
}
