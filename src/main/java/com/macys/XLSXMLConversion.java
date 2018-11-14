package com.macys;

import com.sun.tools.javac.util.Assert;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by m932317 on 11/12/18.
 */

public class XLSXMLConversion {

    ArrayList<TestCase> testCaseList = null; //An ArrayList holding unique Test case in each index
    HashMap<String, Integer> testCaseHeaderMap = null; //A Map holding Test Case Header & the respective column in xls

    private int getColumnIndexOfTestCaseHeader(String headerName) {
        if (testCaseHeaderMap != null && !testCaseHeaderMap.isEmpty()) {
            return testCaseHeaderMap.get(headerName);
        } else {
            return -1;
        }

    }

    public boolean generateXMLFile(TestCases testCaseList, String xmlFilePath) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        /*
        <testcases> tag
         */
        Element root = document.createElement("testcases");
        testCaseList.getTestCasesList().stream().forEach(testCaseObj -> {
            /*
            <testcase> tag
             */
            Element testCase = document.createElement("testcase");
            testCase.setAttribute("name", testCaseObj.getName());
            /*
            <summary> tag
             */
            Element summary = document.createElement("summary");
            summary.setNodeValue(testCaseObj.getSummary());
            /*
            <preconditions> tag
             */
            Element preconditions = document.createElement("preconditions");
            preconditions.setNodeValue(testCaseObj.getPreConditions());
            /*
            <execution_type> tag
             */
            Element executionType = document.createElement("execution_type");
            executionType.setNodeValue(testCaseObj.getExecutionType());
            /*
            <importance> tag
             */
            Element importance = document.createElement("importance");
            importance.setNodeValue(testCaseObj.getImportance());
             /*
            <steps> tag
             */
            Element steps = document.createElement("steps");
            testCaseObj.getSteps().getStepList().stream().forEach(stepObj -> {
                /*
                <step> tag
                 */
                Element step = document.createElement("step");
                /*
                <step_number> tag
                 */
                Element stepNumber = document.createElement("step_number");
                stepNumber.setNodeValue(stepObj.getStepNumber());

                /*
                <actions> tag
                 */
                Element actions = document.createElement("actions");
                actions.setNodeValue(stepObj.getActions());

                /*
                <expectedresults> tag
                 */
                Element expectedResults = document.createElement("expectedresults");
                actions.setNodeValue(stepObj.getExpectedResults());

                /*
                <expectedresults> tag
                 */
                Element step_execution_type = document.createElement("execution_type");
                actions.setNodeValue(stepObj.getExecutionType());
                step.appendChild(stepNumber);
                step.appendChild(actions);
                step.appendChild(expectedResults);
                step.appendChild(step_execution_type);
                steps.appendChild(step);
            });
            /*
            <keywords> tag
             */
            Element keywords = document.createElement("keywords");
            testCaseObj.getKeywords().getKeywordList().stream().forEach(keywordObj -> {
                /*
                <keyword> tag
                 */
                Element keyword = document.createElement("keyword");
                keyword.setAttribute("name", keywordObj.getKeywordName());
                keywords.appendChild(keyword);

            });
            /*
            <custom_fields> tag
             */
            Element customFields = document.createElement("custom_fields");
            testCaseObj.getCustomFields().getCustomFieldsList().stream().forEach(customFieldObj -> {
                /*
                <custom_field> tag
                 */
                Element customField = document.createElement("custom_field");
                /*
                <name> tag
                 */
                Element name = document.createElement("name");
                name.setNodeValue(customFieldObj.getName());
                customField.appendChild(name);
                /*
                <value> tag
                 */
                Element value = document.createElement("value");
                value.setNodeValue(customFieldObj.getValue());
                customField.appendChild(value);
                customFields.appendChild(customField);
            });

            testCase.appendChild(summary);
            testCase.appendChild(preconditions);
            testCase.appendChild(executionType);
            testCase.appendChild(importance);
            testCase.appendChild(steps);
            testCase.appendChild(keywords);
            testCase.appendChild(customFields);
            root.appendChild(testCase);
        });
        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(domSource, result);
            String xmlString = result.getWriter().toString();
            System.err.println("DOM Source: " + xmlString);
            //StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            //transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean transformXLSToXML(String xlsFileName, String xmlFileName) {

        try {
            return generateXMLFile(parseXLSFile(xlsFileName), xmlFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public TestCases parseXLSFile(String xlsFileName) throws Exception {
        InputStream inputStream = new FileInputStream(new File(xlsFileName));
        HSSFWorkbook workBook = new HSSFWorkbook(inputStream);
        TestCases testCasesList = null;
        TestCase currentTestCase = null;
        HSSFSheet sheet = workBook.getSheetAt(0);
        Iterator<?> rows = sheet.rowIterator();
        Map<String, Integer> headers = new HashMap<>();
        while (rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            Iterator<?> cells = row.cellIterator();
            ArrayList<String> rowData = new ArrayList(); //An ArrayList holding data in current row under iteration
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

            if (rowData != null && rowData.contains(TestCaseAttributes.TC_NAME)) {
                testCaseList = new ArrayList<>(); //Initializing ArrayList indicating the xls sheet has valid Headers
                testCaseHeaderMap = new HashMap<>(); //Initializing Header Map indicating the xls sheet has valid Headers
                IntStream.range(0, rowData.size())
                        .forEach(index -> {
                            System.err.println("Header name: " + rowData.get(index));
                            System.err.println("index: " + index);
                            testCaseHeaderMap.put(rowData.get(index), index);
                        });
            } else {

                if (rowData.size() < testCaseHeaderMap.size()) {
                    int sizeDiff = testCaseHeaderMap.size() - (testCaseHeaderMap.size() - rowData.size());
                    System.err.println("Size Diff: " + sizeDiff);
                    for (int var1 = sizeDiff; var1 < testCaseHeaderMap.size(); var1++) {
                        rowData.add("");
                    }
                    System.err.println("rowData Size: " + rowData.size());
                    System.err.println("testCaseHeaderMap Size: " + testCaseHeaderMap.size());
                    Assert.check(rowData.size() == testCaseHeaderMap.size());
                }

                boolean isNewTestCase = NumberUtils.isDigits(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SERIAL_NO)).trim());
                System.err.println("Serial No: " + rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SERIAL_NO)));
                if (isNewTestCase) {
                    if (currentTestCase != null) {
                        if (testCasesList == null) {
                            testCasesList = new TestCases();
                        }
                        testCasesList.addToTestCasesList(currentTestCase);
                        currentTestCase = null;
                    }
                    TestCase newTestCase = new TestCase();
                    System.err.println("getColumnIndexOfTestCaseHeader(TestCaseAttributes.TC_NAME): " + getColumnIndexOfTestCaseHeader(TestCaseAttributes.TC_NAME));
                    newTestCase.setName(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.TC_NAME)));
                    System.err.println("Test Case Name: " + newTestCase.getName());
                    newTestCase.setSummary(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SUMMARY)));
                    System.err.println("Summary: " + newTestCase.getSummary());
                    newTestCase.setPreConditions(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.PRE_CONDITIONS)));
                    newTestCase.setExecutionType(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.EXECUTION_TYPE)));
                    newTestCase.setImportance(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.IMPORTANCE)));
                    String stepNumber = rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_NUMBER));
                    newTestCase.setStepNumber(stepNumber);
                    newTestCase.setStepActions(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_ACTIONS)), stepNumber);
                    newTestCase.setExpectedResults(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.EXPECTED_RESULTS)), stepNumber);
                    newTestCase.setStepExecutionType(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_EXECUTION_TYPE)), stepNumber);
                    newTestCase.setKeyword(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.KEYWORD)));
                    newTestCase.setCustomField(TestCaseAttributes.SCRIPT_NAME, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SCRIPT_NAME)));
                    newTestCase.setCustomField(TestCaseAttributes.USER_STORY, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.USER_STORY)));
                    newTestCase.setCustomField(TestCaseAttributes.TENANT, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.TENANT)));
                    newTestCase.setCustomField(TestCaseAttributes.TEST_CASE_TYPE, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.TEST_CASE_TYPE)));
                    newTestCase.setCustomField(TestCaseAttributes.IS_AUTOMATION_FEASIBLE, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.IS_AUTOMATION_FEASIBLE)));
                    newTestCase.setCustomField(TestCaseAttributes.MANUAL_TC_REASON, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.MANUAL_TC_REASON)));
                    newTestCase.setCustomField(TestCaseAttributes.COMMENTS, rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.COMMENTS)));
                    currentTestCase = newTestCase;
                } else {
                    if (currentTestCase != null) {
                        String stepNumber = rowData.get(rowData.indexOf(TestCaseAttributes.STEP_NUMBER));
                        currentTestCase.setStepNumber(stepNumber);
                        currentTestCase.setStepActions(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_ACTIONS)), stepNumber);
                        currentTestCase.setExpectedResults(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.EXPECTED_RESULTS)), stepNumber);
                        currentTestCase.setStepExecutionType(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_EXECUTION_TYPE)), stepNumber);
                    }
                }

            }
        }
        return testCasesList;
    }
}












