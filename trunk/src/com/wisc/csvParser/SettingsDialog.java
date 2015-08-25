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
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author  lawinslow
 */
public class SettingsDialog extends javax.swing.JDialog {
    
    /** Creates new form NewJDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        okbutton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        vocabProviderSettingsButton = new javax.swing.JButton();
        notificationProviderSettings = new javax.swing.JButton();
        updateVocabulary = new javax.swing.JButton();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        okbutton.setText("OK");
        okbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okbuttonActionPerformed(evt);
            }
        });

        cancelButton.setText("cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        vocabProviderSettingsButton.setText("Vocabulary Provider Settings");
        vocabProviderSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vocabProviderSettingsButtonActionPerformed(evt);
            }
        });

        notificationProviderSettings.setText("Event Notification Provider Settings");
        notificationProviderSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notificationProviderSettingsActionPerformed(evt);
            }
        });

        updateVocabulary.setText("Update Vocabulary");
        updateVocabulary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateVocabularyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(330, 330, 330)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okbutton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vocabProviderSettingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(275, 275, 275))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateVocabulary)
                .addContainerGap(323, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(notificationProviderSettings)
                .addContainerGap(243, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vocabProviderSettingsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateVocabulary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationProviderSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okbutton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //simply exit
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okbuttonActionPerformed

        GlobalProgramSettings.saveSettings();
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_okbuttonActionPerformed
    /**
     * Displays, if available, the settings dialog for the global vocab provider
     * 
     * @param evt
     */
    private void vocabProviderSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vocabProviderSettingsButtonActionPerformed
        javax.swing.JDialog frame = new javax.swing.JDialog(this);
        frame.setSize(256,256);
        frame.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(GlobalProgramSettings.vocabProvider.getSettingsJPanel());
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
}//GEN-LAST:event_vocabProviderSettingsButtonActionPerformed

    private void notificationProviderSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notificationProviderSettingsActionPerformed
        javax.swing.JDialog frame = new javax.swing.JDialog(this);
        frame.setSize(256,256);
        frame.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(GlobalProgramSettings.provider.getStatusJPanel());
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        
}//GEN-LAST:event_notificationProviderSettingsActionPerformed

    private void updateVocabularyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateVocabularyActionPerformed
        // TODO add your handling code here:
        GlobalProgramSettings.vocabProvider.updateLocalVocab();
        javax.swing.JOptionPane.showMessageDialog(this, "The local vocabulary " +
                "was updated.");
    }//GEN-LAST:event_updateVocabularyActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SettingsDialog dialog = new SettingsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton notificationProviderSettings;
    private javax.swing.JButton okbutton;
    private javax.swing.JButton updateVocabulary;
    private javax.swing.JButton vocabProviderSettingsButton;
    // End of variables declaration//GEN-END:variables
    
}