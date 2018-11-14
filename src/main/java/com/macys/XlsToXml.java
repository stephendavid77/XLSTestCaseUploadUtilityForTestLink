package com.macys;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XlsToXml {
    protected DocumentBuilderFactory domFactory = null;
    protected javax.xml.parsers.DocumentBuilder domBuilder = null;

    public XlsToXml() {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();
        } catch (FactoryConfigurationError exp) {
            System.err.println(exp.toString());
        } catch (ParserConfigurationException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
    }

    public void createNode(List<String> headers, ArrayList<String> rowData, int num, Document newDoc, Element ele, Element ele2, String abc) {
        for (int col = num; col == num; col++) {
            String header = (String) headers.get(col);
            String value = null;

            if (col < rowData.size()) {
                value = (String) rowData.get(col);
            } else {
                value = "";
            }
            if ((col == 4) || (col == 5)) {
                ele.appendChild(ele2);
                Element curElement = newDoc.createElement(header);
                ele2.appendChild(curElement);
                String[] str = value.split("\n");
                String values1 = "";
                for (String xxx : str) {
                    values1 = values1 + "<p>" + xxx + "</p><br>";
                }
                curElement.appendChild(newDoc.createCDATASection(values1));

            } else if (abc.equalsIgnoreCase("n")) {
                value.equals("");


                ele.appendChild(ele2);
                Element curElement = newDoc.createElement(header);
                ele2.appendChild(curElement);
                curElement.appendChild(newDoc.createCDATASection(value));


            } else if (abc.equalsIgnoreCase("y")) {
                if (!value.equals("")) {


                    Element curElement = newDoc.createElement(header);
                    ele.appendChild(curElement);
                    curElement.setAttribute("name", value);
                }
            } else {
                Element curElement = newDoc.createElement(header);
                ele2.appendChild(curElement);
                curElement.appendChild(newDoc.createCDATASection(value));
            }
        }
    }


    public void convertFile(String xlsFileName, String xmlFileName) {
        try {
            Document newDoc = domBuilder.newDocument();
            Element rootElement = newDoc.createElement("testcases");
            newDoc.appendChild(rootElement);
            java.io.InputStream InputStream = new java.io.FileInputStream(new File(xlsFileName));
            HSSFWorkbook workBook = new HSSFWorkbook(InputStream);
            HSSFSheet sheet = workBook.getSheetAt(0);
            Iterator<?> rows = sheet.rowIterator();
            List<String> headers = new ArrayList();
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                int rowNumber = row.getRowNum();
                Iterator<?> cells = row.cellIterator();
                ArrayList<String> rowData = new ArrayList();
                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell) cells.next();
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            int i = (int) cell.getNumericCellValue();
                            rowData.add(String.valueOf(i));
                            break;
                        case BLANK:
                            rowData.add("");
                            break;
                        case STRING:
                            HSSFRichTextString richTextString = cell.getRichStringCellValue();
                            rowData.add(richTextString.getString());
                    }
                }

                if (rowNumber == 1) {
                    headers.addAll(rowData);
                } else {
                    String testCaseSerialNo = row.getCell(0).getRichStringCellValue().getString();
                    if (testCaseSerialNo != null && !testCaseSerialNo.isEmpty() && NumberUtils.isDigits(testCaseSerialNo)) {
                        Element rowElement = newDoc.createElement("testcase");
                        for (int col = 0; col < headers.size(); col++) {
                            String header = (String) headers.get(col);
                            String value = null;

                            if (col < rowData.size()) {
                                value = (String) rowData.get(col);
                            } else {
                                value = "";
                            }
                            if (col == 0) {
                                rowElement.setAttribute("name", value);
                            }
                        }


                        for (int col = 0; col < headers.size(); col++) {
                            String header = (String) headers.get(col);
                            String value = null;

                            if (col < rowData.size()) {
                                value = (String) rowData.get(col);
                            } else {
                                value = "";
                            }

                            if ((col != 0) && (col != 3) && (col != 4) && (col != 5) && (col != 6) && (col != 7) && (col != 8) && (col != 9) && (col != 10) && (col != 11) && (col != 12) && (col != 13)) {


                                Element curElement = newDoc.createElement(header);
                                curElement.appendChild(newDoc.createCDATASection(value));
                                rowElement.appendChild(curElement);
                            }
                        }


                        rootElement.appendChild(rowElement);
                        Element rowElement3_1 = newDoc.createElement("steps");
                        rowElement.appendChild(rowElement3_1);
                        Element rowElement3_2 = newDoc.createElement("step");
                        rowElement.appendChild(rowElement3_2);
                        createNode(headers, rowData, 3, newDoc, rowElement3_1, rowElement3_2, "n");
                        createNode(headers, rowData, 4, newDoc, rowElement3_1, rowElement3_2, "n");
                        createNode(headers, rowData, 5, newDoc, rowElement3_1, rowElement3_2, "n");
                        Element rowElement2 = newDoc.createElement("keywords");
                        rowElement.appendChild(rowElement2);
                        Element rowElement2_1 = newDoc.createElement("keyword");

                        createNode(headers, rowData, 6, newDoc, rowElement2, rowElement2_1, "y");

                        createNode(headers, rowData, 7, newDoc, rowElement2, rowElement2_1, "y");

                        Element rowElement1 = newDoc.createElement("custom_fields");
                        rowElement.appendChild(rowElement1);
                        Element rowElement4 = newDoc.createElement("custom_field");
                        createNode(headers, rowData, 8, newDoc, rowElement1, rowElement4, "n");
                        createNode(headers, rowData, 9, newDoc, rowElement1, rowElement4, "n");
                        Element rowElement5 = newDoc.createElement("custom_field");
                        createNode(headers, rowData, 10, newDoc, rowElement1, rowElement5, "n");
                        createNode(headers, rowData, 11, newDoc, rowElement1, rowElement5, "n");

                    } else {

                    }


                }
            }


            ByteArrayOutputStream baos = null;
            OutputStreamWriter osw = null;

            try {
                baos = new ByteArrayOutputStream();
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty("indent", "yes");
                aTransformer.setOutputProperty("encoding", "UTF-8");
                aTransformer.setOutputProperty("method", "xml");


                javax.xml.transform.Source src = new javax.xml.transform.dom.DOMSource(newDoc);
                javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new File(xmlFileName));
                aTransformer.transform(src, result);

                osw.flush();
                System.out.println(new String(baos.toByteArray()));
            } catch (Exception exp) {
                exp.printStackTrace();
                try {
                    osw.close();
                } catch (Exception localException1) {
                }
                try {
                    baos.close();
                } catch (Exception localException2) {
                }
            } finally {
                try {
                    osw.close();
                } catch (Exception localException3) {
                }
                try {
                    baos.close();
                } catch (Exception localException4) {
                }
            }
            try {
                baos.close();
            } catch (Exception localException6) {
            }


            return;
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }
    }
}
