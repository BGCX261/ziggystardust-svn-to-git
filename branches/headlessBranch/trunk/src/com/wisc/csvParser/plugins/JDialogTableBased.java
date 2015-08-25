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

/**
 *
 * @author  lawinslow
 */
public class JDialogTableBased extends javax.swing.JDialog {
    
    DataParserTableBased.TableBasedFile file;
    
    /** Creates new form JDialogTableBased */
    public JDialogTableBased(java.awt.Frame parent, boolean modal,
            DataParserTableBased.TableBasedFile tbf) {
        super(parent, modal);
        initComponents();
        
        file = tbf;
        filePathTB.setText(file.getDataFilePath());
        update();
        
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filePathTB = new javax.swing.JTextField();
        selectFileButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        columnList = new javax.swing.JList();
        dateTimeSettingsButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        filePathTB.setEnabled(false);

        selectFileButton.setText("...");
        selectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFileButtonActionPerformed(evt);
            }
        });

        columnList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                columnListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(columnList);

        dateTimeSettingsButton.setText("DateTime Settings");
        dateTimeSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateTimeSettingsButtonActionPerformed(evt);
            }
        });

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        upButton.setText("Up");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText("Down");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filePathTB, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(selectFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(dateTimeSettingsButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton)
                        .addGap(51, 51, 51)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filePathTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFileButton)
                    .addComponent(dateTimeSettingsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(upButton)
                    .addComponent(downButton)
                    .addComponent(addButton)
                    .addComponent(removeButton)
                    .addComponent(editButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFileButtonActionPerformed
        java.awt.FileDialog chooseFile = new java.awt.FileDialog(new java.awt.Frame());
        
        chooseFile.setVisible(true);
        
        if(chooseFile.getFile() != null){
            file.setDataFilePath(chooseFile.getDirectory() + chooseFile.getFile());
            filePathTB.setText(file.getDataFilePath());
        }
        
    }//GEN-LAST:event_selectFileButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int index = columnList.getSelectedIndex();
        if(index <=0){
            //do nothing, we are at top or nothing is selected
        }else{
            DataColumn above = file.getColumns().get(index-1);
            DataColumn curr = file.getColumns().get(index);
            file.getColumns().set(index, above);
            file.getColumns().set(index-1,curr);
            update();
            columnList.setSelectedIndex(index-1);
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int index = columnList.getSelectedIndex();
        if(index < 0 || index >= file.getColumns().size()-1){
            //do nothing, nothing is selected or we are at the bottom
        }else{
            DataColumn curr = file.getColumns().get(index);
            DataColumn below = file.getColumns().get(index+1);
            file.getColumns().set(index, below);
            file.getColumns().set(index+1,curr);
            update();
            columnList.setSelectedIndex(index+1);
        }
    }//GEN-LAST:event_downButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int index = columnList.getSelectedIndex();
        if(index >=0){
            file.removeColumn(index);
            update();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        if(columnList.getSelectedIndex() >=0){
            file.addColumn(file.getColumns().get(columnList.getSelectedIndex()).clone());
        }else{
            file.addColumn(new DataColumn());
        }
        update();
    }//GEN-LAST:event_addButtonActionPerformed

    private void columnListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_columnListMouseClicked
        if(evt.getClickCount() ==2 && columnList.getSelectedIndex() >=0){
            file.getColumns().get(columnList.getSelectedIndex()).displaySettingsDialog();
        }
    }//GEN-LAST:event_columnListMouseClicked

    private void dateTimeSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTimeSettingsButtonActionPerformed
        file.getDateTimeParser().getSettingsDialog().setVisible(true);
    }//GEN-LAST:event_dateTimeSettingsButtonActionPerformed

    private void update(){
        columnList.setListData(file.getColumns().toArray(new DataColumn[0]));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList columnList;
    private javax.swing.JButton dateTimeSettingsButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton editButton;
    private javax.swing.JTextField filePathTB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton selectFileButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
    
}
