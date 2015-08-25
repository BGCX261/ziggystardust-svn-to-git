package com.wisc.csvParser.plugins;

//import java.awt.*;
import com.wisc.csvParser.*;
import com.wisc.csvParser.vocabProviders.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Collections;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JDialogManualUpload extends JDialog
{
    List<TableCellEditor> vareditors = new ArrayList<TableCellEditor>();
    List<TableCellEditor> uniteditors = new ArrayList<TableCellEditor>();
    IVocabProvider vocabProvider = GlobalProgramSettings.vocabProvider;
    DataParserManualUpload.TableBasedFile file = null;
    DataParserManualUpload thisparser = null;
    JPanelManualUpload parent;
    File fileOpened;
    JTable table;
    boolean newConfig;
    String origName;
    private JComboBox sourceCB = new JComboBox();
    private JLabel sourcejLabel = new JLabel("Source:");
    private JLabel sitejLabel = new JLabel("Site:");
    private JComboBox siteCB = new JComboBox();
    private JLabel depthjLabel = new JLabel("Depth:");
    private JTextField depthjTextField = new JTextField();
    private JButton dateTimeSettingsButton = new JButton("DateTime Settings");
    private JComboBox dtFormatOptions = new JComboBox();
    private JTextField jTextField1 = new JTextField();
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private JButton chooseFileButton = new JButton("...");
    private JLabel jLabel1 = new JLabel("Configuration Name: ");
    private JTextField configNameTB = new JTextField();
    private JScrollPane scrollPane;
    private JLabel headerjLabel = new JLabel("Header row count:");
    private JTextField headerRowCountTB = new JTextField();
    private JLabel delimiterjLabel = new JLabel("Delimiter (use \"\\t\" for tab):");
    private JTextField delimiterTB = new JTextField();
    javax.swing.GroupLayout layout;

    public JDialogManualUpload(JPanelManualUpload parent, DataParserManualUpload.TableBasedFile muf, String configName
            , DataParserManualUpload parser)
    {
        this.parent = parent;
        file = muf;
        thisparser = parser;
        origName = configName;
        if (file.getDateTimeParser() == null) {
            file.setDateTimeParser(new DateTimeParserTableBased());
        }
        chooseFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseFileButtonActionPerformed(evt);
            }
        });
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        dateTimeSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateTimeSettingsButtonActionPerformed(evt);
            }
        });
        dtFormatOptions.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "String Format", "year, month, date, time" }));
        dtFormatOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtFormatOptionsActionPerformed(evt);
            }
        });
        if(muf.getDateTimeParser() instanceof DateTimeParserTableBased){
            this.dtFormatOptions.setSelectedIndex(0);
        }else if(muf.getDateTimeParser() instanceof DateTimeParserTableBasedChoice){
            this.dtFormatOptions.setSelectedIndex(1);
        }
        populateSources();
        populateSites();
        if (!muf.getColumns().isEmpty()) {
            // Find a data field that has a source, site, and offset
            DataColumn col = null;
            for (DataColumn c:muf.getColumns()) {
                if (!c.getType().equalsIgnoreCase("Ignore")) {
                    col = c;
                    break;
                }
            }
            // if col is still null all fields were ignore, so just give it an
            // arbitrary value, and fill in defaults
            if (col == null) {
                col = muf.getColumns().get(0);
            }
            String site = col.getTemplateValue().getSite();
            String source = col.getTemplateValue().getSource();
            siteCB.setSelectedItem(site);
            sourceCB.setSelectedItem(source);
            depthjTextField.setText(String.valueOf(col.getTemplateValue().getOffsetValue()));
        }
        jTextField1.setEditable(false);
        configNameTB.setText(configName);
        layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        if (file.getDataFilePath().equals("No File Selected.")) {
            newConfig = true;
            initComponents();
        } else {
            newConfig = false;
            fileOpened = new File(file.getDataFilePath());
            jTextField1.setText(file.getDataFilePath());
            headerRowCountTB.setText(Integer.toString(file.getHeaderRows()));
            delimiterTB.setText(file.getDelimiter());
            setupTable();
        }

        DocumentListener l = new DocumentListener(){
            @Override
            public void changedUpdate(DocumentEvent e){
                parseHeaderTB();
            }
            @Override
            public void removeUpdate(DocumentEvent e){
                parseHeaderTB();
            }
            @Override
            public void insertUpdate(DocumentEvent e){
                parseHeaderTB();
            }
        };

        DocumentListener l2 = new DocumentListener(){
            @Override
            public void changedUpdate(DocumentEvent e){
                parseDelimiterTB();
            }
            @Override
            public void removeUpdate(DocumentEvent e){
                parseDelimiterTB();
            }
            @Override
            public void insertUpdate(DocumentEvent e){
                parseDelimiterTB();
            }
        };

        headerRowCountTB.getDocument().addDocumentListener(l);
        delimiterTB.getDocument().addDocumentListener(l2);
    }

    private void parseHeaderTB(){

        try{
            int i = Integer.parseInt(headerRowCountTB.getText());
            file.setHeaderRows(i);
        }catch(NumberFormatException nfe){
            //ignore this error.
        }
    }

    private void parseDelimiterTB() {
        file.setDelimiter(delimiterTB.getText());
    }

    private void initComponents() {
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //.addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 758, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(configNameTB, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerRowCountTB, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delimiterjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delimiterTB, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chooseFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourcejLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceCB, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sitejLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(siteCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(depthjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(depthjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateTimeSettingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dtFormatOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseFileButton)
                    .addComponent(sourcejLabel)
                    .addComponent(dtFormatOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateTimeSettingsButton)
                    .addComponent(depthjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(depthjLabel)
                    .addComponent(siteCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sitejLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                //.addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(configNameTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerjLabel)
                    .addComponent(headerRowCountTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delimiterjLabel)
                    .addComponent(delimiterTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(saveButton))
                .addContainerGap())
        );

        pack();
    }

    private void redoComponents() {
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 758, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(configNameTB, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerRowCountTB, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(delimiterjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delimiterTB, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chooseFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourcejLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceCB, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sitejLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(siteCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(depthjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(depthjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateTimeSettingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dtFormatOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseFileButton)
                    .addComponent(sourcejLabel)
                    .addComponent(dtFormatOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateTimeSettingsButton)
                    .addComponent(depthjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(depthjLabel)
                    .addComponent(siteCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sitejLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(configNameTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerjLabel)
                    .addComponent(headerRowCountTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delimiterjLabel)
                    .addComponent(delimiterTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(saveButton))
                .addContainerGap())
        );

        pack();
    }

    private void chooseFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        java.awt.FileDialog chooseFile = new java.awt.FileDialog(new java.awt.Frame());
        
        chooseFile.setVisible(true);
        
        if(chooseFile.getFile() != null){
            fileOpened = new File(chooseFile.getDirectory() + chooseFile.getFile());
            file.setDataFilePath(chooseFile.getDirectory() + chooseFile.getFile());
            jTextField1.setText(file.getDataFilePath());
            headerRowCountTB.setText(Integer.toString(file.getHeaderRows()));
            delimiterTB.setText(file.getDelimiter());
            if (newConfig) {
                setupTable();
            } else {
                setupData();
            }
        }
    }

    private void dateTimeSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        file.getDateTimeParser().getSettingsDialog().setLocationRelativeTo(this);
        file.getDateTimeParser().getSettingsDialog().setVisible(true);
    }

    private void dtFormatOptionsActionPerformed(java.awt.event.ActionEvent evt) {
        //I'm just going to arbitrarily say 0 indx is string format and index 1
        // is the column format.
        if(this.dtFormatOptions.getSelectedIndex()==0){
            if(!(file.getDateTimeParser() instanceof DateTimeParserTableBased))
                file.setDateTimeParser(new DateTimeParserTableBased());
        }else if(this.dtFormatOptions.getSelectedIndex()==1){
            if(!(file.getDateTimeParser() instanceof DateTimeParserTableBasedChoice))
                file.setDateTimeParser(new DateTimeParserTableBasedChoice());
        }
    }  

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // More of course, needs to be added here
        if (fileOpened != null) {
            if (!newConfig) {
                // Just redo the data columns as they are now
                file.getColumns().clear();
            }
            if (file.getDateTimeParser() == null) {
                if(this.dtFormatOptions.getSelectedIndex()==0){
                    if(!(file.getDateTimeParser() instanceof DateTimeParserTableBased))
                        file.setDateTimeParser(new DateTimeParserTableBased());
                }else if(this.dtFormatOptions.getSelectedIndex()==1){
                    if(!(file.getDateTimeParser() instanceof DateTimeParserTableBasedChoice))
                        file.setDateTimeParser(new DateTimeParserTableBasedChoice());
                }
            }
            file.setTable(this.table);
            for (int i = 0; i< table.getColumnCount(); i++) {
                DataColumn col = new DataColumn();
                ValueObject val = new ValueObject();
                if(table.getCellEditor(0,i).getCellEditorValue().toString().equalsIgnoreCase("Ignore")) {
                    col.setType(DataColumn.IGNORE_COLUMN_TYPE);
                } else {
                    col.setType(DataColumn.DATA_COLUMN_TYPE);
                    val.setVariable(table.getCellEditor(0,i).getCellEditorValue().toString());
                    val.setUnit(table.getCellEditor(1,i).getCellEditorValue().toString());
                }
                val.setSite(siteCB.getSelectedItem().toString());
                val.setSource(sourceCB.getSelectedItem().toString());
                // TODO set the rest of val's data here
                val.setOffsetType("DEPTH");
                if(depthjTextField.getText() == null || depthjTextField.getText().compareTo("") == 0){
                    val.setOffsetValue(Double.NaN);
                } else {
                    val.setOffsetValue(Double.parseDouble(depthjTextField.getText()));
                }
                val.setAggMethod(null);
                val.setAggSpan(null);
                col.setTemplateValue(val);
                file.addColumn(col);
            }
            if (newConfig) {
                thisparser.addMonitoredFile(file);
                if (configNameTB.getText() != null) {
                    file.setConfigName(configNameTB.getText());
                    thisparser.addConfigName(configNameTB.getText());
                } else {
                    file.setConfigName("Unnamed Config");
                    thisparser.addConfigName("Unnamed Config");
                }
            } else if (!origName.equals(configNameTB.getText())) {
                thisparser.getConfigNames().set(
                        thisparser.getConfigNames().indexOf(origName), configNameTB.getText());
                file.setConfigName(configNameTB.getText());
            }
        }
        this.parent.newEvent(DataParserManualUpload.events.reconfigured);
        this.setVisible(false);
        this.dispose();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    private void setupTable() {
        ArrayList<ArrayList<String>> dataExamples = new ArrayList<ArrayList<String>>();
        int headers2skip = 0;
        if (headerRowCountTB.getText() != null) {
            headers2skip = Integer.parseInt(headerRowCountTB.getText());
        }
        int maxTableCnt = 100;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(fileOpened));
            while (dis.available() > 0 && maxTableCnt > 0) {
                if (headers2skip > 0) {
                    // throw out line
                    String line = dis.readLine();
                    headers2skip--;
                    continue;
                }
                String line = dis.readLine();
                String[] linePieces = line.split(file.getDelimiter(),-1);
                ArrayList<String> csvPieces = new ArrayList<String>(linePieces.length);
                for (String piece:linePieces) {
                    csvPieces.add(piece);
                }
                // A check for malformed rows, arbitrarily check for a size of one
                if (csvPieces.size() > 1) {
                    if (!dataExamples.isEmpty() && dataExamples.get(0).size() != csvPieces.size()) {
                        // Then we've hit another odd row
                    } else {
                        dataExamples.add(csvPieces);
                    }
                }
                maxTableCnt--;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating the table!"
                        + "\nMake sure you've set the amount of header rows to skip and type of delimiter before selecting file.");
             e.printStackTrace();
        }
        int columns = 0;
        if (!dataExamples.isEmpty()) {
            columns = dataExamples.get(0).size();
        }
        // Get size to 5, by just filling blank strings if needed
        while (dataExamples.size() < 5) {
            dataExamples.add(new ArrayList<String>(columns));
        }
        
        for (int i = 0;i<columns;i++) {
            JComboBox vars = populateVars();
            if (!newConfig) {
                if (!file.getColumns().get(i).getType().equalsIgnoreCase("Ignore")) {
                    vars.setSelectedItem(file.getColumns().get(i).getTemplateValue().getVariable());
                } else {
                    vars.setSelectedItem("Ignore");
                }
            }
            DefaultCellEditor dce1 = new DefaultCellEditor( vars );
            vareditors.add( dce1 );
        }
        for (int i = 0;i<columns;i++) {
            JComboBox units = populateUnits();
            if (!newConfig) {
                if (!file.getColumns().get(i).getType().equalsIgnoreCase("Ignore")) {
                    units.setSelectedItem(file.getColumns().get(i).getTemplateValue().getUnit());
                } else {
                    units.setSelectedItem("None");
                }
            }
            DefaultCellEditor dce2 = new DefaultCellEditor( units );
            uniteditors.add( dce2 );
        }

        //  Create the table with default data
        // Give them all rows of data to look at, with 2 column headers
        Object[][] newData = new Object[dataExamples.size()+2][columns];
        for (int r=0;r<dataExamples.size()+2;r++) {
            for (int c=0;c<columns;c++) {
                if (r == 0) {
                    if (newConfig) {
                        newData[r][c] = "Variable";
                    } else {
                        if (!file.getColumns().get(c).getType().equalsIgnoreCase("Ignore")) {
                            newData[r][c] = file.getColumns().get(c).getTemplateValue().getVariable();
                        } else {
                            newData[r][c] = "Ignore";
                        }
                    }
                } else if (r == 1) {
                    if (newConfig) {
                        newData[r][c] = "Unit";
                    } else {
                        if (!file.getColumns().get(c).getType().equalsIgnoreCase("Ignore")) {
                            newData[r][c] = file.getColumns().get(c).getTemplateValue().getUnit();
                        } else {
                            newData[r][c] = "None";
                        }
                    }
                } else {
                    // Below is r-2 to take into account variable and unit headers above
                    try {
                        newData[r][c]= dataExamples.get(r-2).get(c);
                    } catch (IndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(this, "Error creating the table!"
                            + "\nTo avoid this, set up the number of header rows to skip (found below) first,"
                            + " as well as delimiter type, then re-select a file.");
                        return;
                    }
                }
            }
        }

        String[] columnNames = new String[columns];
        for (int i = 0;i<columnNames.length;i++) {
            columnNames[i] = "Column " + i;
        }
        DefaultTableModel model = new DefaultTableModel(newData, columnNames);
        table = new JTable(model)
        {
            //  Determine editor to be used by row
            public TableCellEditor getCellEditor(int row, int column)
            {
                //int modelColumn = convertColumnIndexToModel( column );

                if (row == 0)
                    return vareditors.get(column);
                else if (row == 1)
                    return uniteditors.get(column);
                else
                    return super.getCellEditor(row, column);
            }
            public boolean isCellEditable(int row, int column) {
                if (row < 2) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane = new JScrollPane( table );
        scrollPane.validate();

        redoComponents();
    }

    private void setupData() {
        ArrayList<ArrayList<String>> dataExamples = new ArrayList<ArrayList<String>>();
        int headers2skip = 0;
        if (headerRowCountTB.getText() != null) {
            headers2skip = Integer.parseInt(headerRowCountTB.getText());
        }
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(fileOpened));
            while (dis.available() > 0 && dataExamples.size() < table.getRowCount()-2) {
                if (headers2skip > 0) {
                    // throw out line
                    String line = dis.readLine();
                    headers2skip--;
                    continue;
                }
                String line = dis.readLine();
                String[] linePieces = line.split(file.getDelimiter(),-1);
                ArrayList<String> csvPieces = new ArrayList<String>(linePieces.length);
                for (String piece:linePieces) {
                    csvPieces.add(piece);
                }
                // A check for malformed rows, arbitrarily check for a size of one
                if (csvPieces.size() > 1) {
                    if (!dataExamples.isEmpty() && dataExamples.get(0).size() != csvPieces.size()) {
                        // Then we've hit another odd row
                    } else {
                        dataExamples.add(csvPieces);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating the table!"
                        + "\nMake sure you've set the amount of header rows to skip and type of delimiter before selecting file.");
             e.printStackTrace();
        }
        int columns = 0;
        if (!dataExamples.isEmpty()) {
            columns = dataExamples.get(0).size();
        }
        // Get size to 5, by just filling blank strings if needed
        while (dataExamples.size() < table.getRowCount()-2) {
            dataExamples.add(new ArrayList<String>(columns));
        }
        for (int r=0;r<dataExamples.size();r++) {
            for (int c=0;c<columns;c++) {
                table.getModel().setValueAt(dataExamples.get(r).get(c), r+2, c);
            }
        }
    }

    private JComboBox populateVars() {
        // Create the editors to be used for each row
        JComboBox varComboBox = new JComboBox();
        if(vocabProvider == null){
            vocabProvider = new GLEONVocabProvider();
            vocabProvider.updateLocalVocab();
        }
        try {
            ArrayList<String> vararr = new ArrayList<String>();
            for(String v:vocabProvider.getVocab("variables"))
                vararr.add(v);
            Collections.sort(vararr, new AllowCapsComparator());
            vararr.add(0,"Ignore");
            for (String v:vararr)
                varComboBox.addItem(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return varComboBox;
    }
    private JComboBox populateUnits() {
        // Create the editors to be used for each row
        JComboBox unitComboBox = new JComboBox();
        if(vocabProvider == null){
            vocabProvider = new GLEONVocabProvider();
            vocabProvider.updateLocalVocab();
        }
        try {
            ArrayList<String> unitarr = new ArrayList<String>();
            for(String v:vocabProvider.getVocab("units"))
                unitarr.add(v);
            Collections.sort(unitarr, new AllowCapsComparator());
            unitarr.add(0,"None");
            for (String v:unitarr)
                unitComboBox.addItem(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unitComboBox;
    }
    private void populateSources() {
        if(vocabProvider == null){
            vocabProvider = new GLEONVocabProvider();
            vocabProvider.updateLocalVocab();
        }
        try {
            ArrayList<String> sourcearr = new ArrayList<String>();
            for(String v:vocabProvider.getVocab("sources"))
                sourcearr.add(v);
            Collections.sort(sourcearr, new AllowCapsComparator());
            for (String v:sourcearr)
                sourceCB.addItem(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void populateSites() {
        if(vocabProvider == null){
            vocabProvider = new GLEONVocabProvider();
            vocabProvider.updateLocalVocab();
        }
        try {
            ArrayList<String> sitearr = new ArrayList<String>();
            for(String v:vocabProvider.getVocab("sites"))
                sitearr.add(v);
            Collections.sort(sitearr, new AllowCapsComparator());
            for (String v:sitearr)
                siteCB.addItem(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}