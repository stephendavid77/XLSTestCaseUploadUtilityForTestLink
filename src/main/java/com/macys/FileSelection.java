package com.macys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/*
    This is an existing implementation and has been re-used/retained
 */

public class FileSelection
        extends JFrame {

    final Logger logger = LogManager.getLogger(FileSelection.class.getName());

    JButton submitButton = new JButton("Submit");
    JPanel panel = new JPanel();
    private JTextField textField;
    private JTextField localBackupPathText;

    FileSelection() {
        super("File Selection");
        setTitle("Excel to Testlink compatible XML converter");
        setSize(471, 268);
        setLocation(500, 280);
        panel.setLayout(null);
        submitButton.setBounds(210, 117, 80, 20);
        panel.add(submitButton);
        JLabel lblUsername = new JLabel("Excel Path");
        lblUsername.setBounds(10, 33, 80, 14);
        panel.add(lblUsername);

        getContentPane().add(panel);
        localBackupPathText = new JTextField();
        localBackupPathText.setBounds(100, 28, 265, 25);
        panel.add(localBackupPathText);
        localBackupPathText.setColumns(10);
        JButton backupOpenButton = new JButton("Open");
        backupOpenButton.setBounds(375, 28, 65, 25);
        panel.add(backupOpenButton);
        backupOpenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel File", new String[]{"xls"});
                chooser.setFileFilter(filter);
                chooser.setFileSelectionMode(0);
                int option = chooser.showOpenDialog(getContentPane());
                if (option == 0) {
                    File sf = chooser.getSelectedFile();
                    String filelist = "nothing";
                    filelist = sf.getAbsolutePath();
                    localBackupPathText.setText(filelist);
                }
            }
        });
        JLabel lblPassword = new JLabel("XML Path");
        lblPassword.setBounds(10, 72, 80, 14);
        panel.add(lblPassword);

        textField = new JTextField();
        textField.setColumns(10);
        textField.setBounds(100, 69, 265, 25);
        panel.add(textField);

        JButton button = new JButton("Open");
        button.setBounds(375, 68, 65, 25);
        panel.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                chooser.setFileSelectionMode(2);
                int option = chooser.showOpenDialog(getContentPane());
                if (option == 0) {
                    File sf = chooser.getSelectedFile();
                    String filelist = "nothing";
                    filelist = sf.getAbsolutePath();
                    textField.setText(filelist);
                }
            }
        });
        JScrollPane scrollPaneForText = new JScrollPane();
        scrollPaneForText.setHorizontalScrollBarPolicy(31);
        scrollPaneForText.setBounds(10, 148, 430, 72);
        panel.add(scrollPaneForText);

        final JTextArea logTextArea = new JTextArea();
        scrollPaneForText.setViewportView(logTextArea);
        logTextArea.setLineWrap(true);
        logTextArea.setEnabled(true);
        logTextArea.setForeground(Color.DARK_GRAY);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!FileSelection.this.validateFeed()) {
                    return;
                }
                Object excel = localBackupPathText.getText();
                Object xml = textField.getText();
                XLSToXMLConverterAction xlsxmlConversion = new XLSToXMLConverterAction();
                logger.debug("Input Parameters to poiExample.convertFile ");
                logger.debug("XLS File name: " + excel.toString());
                logger.debug("XML File name: " + xml.toString());
                xlsxmlConversion.transformXLSToXML(excel.toString(), xml.toString());
                logTextArea.append("Xml file has been generated to the following location: " + xml.toString() + System.getProperty("line.separator"));
            }
        });
        setDefaultCloseOperation(3);
        setVisible(true);
    }

    public static void main(String[] args) {
        FileSelection frameTabel = new FileSelection();
    }

    private boolean validateFeed() {
        boolean valid = true;
        if (textField.getText().isEmpty()) {
            textField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            localBackupPathText.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            JOptionPane.showMessageDialog(getContentPane(), "This field should not be blank", "Error", 0);
            valid = false;
        }
        if (localBackupPathText.getText().isEmpty()) {
            localBackupPathText.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            JOptionPane.showMessageDialog(getContentPane(), "This field should not be blank", "Error", 0);
            valid = false;
        }
        if (!localBackupPathText.getText().isEmpty()) {
            localBackupPathText.setBorder(BorderFactory.createEmptyBorder());
        }
        if (!textField.getText().isEmpty()) {
            textField.setBorder(BorderFactory.createEmptyBorder());
        }
        if ((!localBackupPathText.getText().isEmpty()) && (!textField.getText().isEmpty())) {
            valid = true;
        }
        return valid;
    }
}