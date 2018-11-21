package com.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by m932317 on 11/12/18.
 */

public class XLSToXMLConverterAction {

    final Logger logger = LogManager.getLogger(XLSToXMLConverterAction.class.getName());
    private HashMap<String, Integer> testCaseHeaderMap = null; //A Map holding Test Case Header & respective column index from xls

    /*
        This method parses .xls file containing Test cases and converts them to a List of Test Case objects to be converted to a XML
        * @param  xlsFileName  an absolute URL giving the base location of the .xls file containing Test Case
        * @return TestCases    returns TestCases object which holds a list of Test Case objects parsed from the .xls
     */
    public boolean transformXLSToXML(String xlsFileName, String xmlFileName) throws Exception {
        return generateXMLFile(parseXLSFile(xlsFileName), xmlFileName);
    }

    /*
        This method parses .xls file containing Test cases and converts them to a List of Test Case objects to be converted to a XML
        * @param  xlsFileName  an absolute URL giving the base location of the .xls file containing Test Case
        * @return TestCases    returns TestCases object which holds a list of Test Case objects parsed from the .xls
     */
    private TestCases parseXLSFile(String xlsFileName) {
        if (!Objects.isNull(xlsFileName)) {
            InputStream inputStream = null;
            HSSFWorkbook workBook = null;
            try {
                inputStream = new FileInputStream(new File(xlsFileName));
                workBook = new HSSFWorkbook(inputStream);
            } catch (FileNotFoundException e) {
                logger.debug("File not found: " + xlsFileName);
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                logger.debug("Error occurred while processing file... " + xlsFileName);
                e.printStackTrace();
                return null;
            }
            TestCases testCasesList = null; //TestCases object holds the list of all Test Cases
            TestCase currentTestCase = null; //Pointer to the current Test Case being processed
            HSSFSheet sheet = workBook.getSheetAt(0);
            Iterator<?> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                ArrayList<String> rowData = new ArrayList(); //An ArrayList holding data of the current row
                int columnStart = Math.min(0, sheet.getFirstRowNum()); // Range of rows to process
                int columnEnd = Math.max(18, sheet.getLastRowNum());
                for (int columnNum = columnStart; columnNum < columnEnd; columnNum++) {
                    HSSFCell cell = (HSSFCell) row.getCell(columnNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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

                /*
                    This checks for and processes the Test Case Headers
                 */

                if (rowData != null && rowData.contains(TestCaseAttributes.TC_NAME)) {
                    testCaseHeaderMap = new HashMap<>(); //Initializing Header Map indicating the xls sheet has valid Headers
                    IntStream.range(0, rowData.size())
                            .forEach(index -> {
                                testCaseHeaderMap.put(rowData.get(index), index);
                            });
                } else {
                    /*
                        This processes the Test Case Data
                     */
                    String serialNumber = rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SERIAL_NO)).trim();
                    boolean isNewTestCase = StringUtils.isNotBlank(serialNumber) && NumberUtils.isDigits(serialNumber);
                    /*
                        Not all rows denote a new Test Case as steps can span across multiple rows
                        A new test case is identified by the Serial No column in .xls file
                        If a row exists and does not have a serial number, it denotes it is a continuation of a test case
                     */
                    if (isNewTestCase) {
                        if (currentTestCase != null) { //This implies that currentTestCase object exists that is yet to be added to TestCases Object
                            if (testCasesList == null) {
                                testCasesList = new TestCases();
                            }
                            testCasesList.addToTestCasesList(currentTestCase);
                            currentTestCase = null; //Once the current test case is added to list, it should be de referenced to hold new Test Case object
                        }
                        TestCase newTestCase = new TestCase();
                        newTestCase.setName(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.TC_NAME)));
                        newTestCase.setSummary(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SUMMARY)));
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
                        if (currentTestCase != null && StringUtils.isEmpty(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.SERIAL_NO))) && !StringUtils.isEmpty(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_NUMBER)))) {
                            String stepNumber = rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_NUMBER));
                            currentTestCase.setStepNumber(stepNumber);
                            currentTestCase.setStepActions(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_ACTIONS)), stepNumber);
                            currentTestCase.setExpectedResults(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.EXPECTED_RESULTS)), stepNumber);
                            currentTestCase.setStepExecutionType(rowData.get(getColumnIndexOfTestCaseHeader(TestCaseAttributes.STEP_EXECUTION_TYPE)), stepNumber);
                        }
                    }
                }
            }
            if (currentTestCase != null) {
                if (testCasesList == null) {
                    testCasesList = new TestCases();
                }
                testCasesList.addToTestCasesList(currentTestCase);
            }
            return testCasesList;
        }
        return null;
    }

    /*
        This method returns the corresponding index of a given Header in .xls file
        * @param  Test Case Header Name
        * @return corresponding index/position of the Header in .xls
     */
    private int getColumnIndexOfTestCaseHeader(String headerName) {
        if (testCaseHeaderMap != null && !testCaseHeaderMap.isEmpty()) {
            return testCaseHeaderMap.get(headerName);
        } else {
            return -1;
        }

    }

    /*
        This method generates the required xml file from TestCases object
        * @param  testCasesList Object contains a list of all test cases parsed from .xls file
        * @param  xmlFilePath Output XML file name & directory
        * @return corresponding index/position of the Header in .xls
     */
    public boolean generateXMLFile(TestCases testCaseList, String xmlFilePath) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        /*
        <testcases> tag
         */
        Element root = document.createElement("testcases");
        document.appendChild(root);
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
            summary.appendChild(document.createCDATASection(testCaseObj.getSummary()));
            /*
            <preconditions> tag
             */
            Element preconditions = document.createElement("preconditions");
            preconditions.appendChild(document.createCDATASection(testCaseObj.getPreConditions()));
            /*
            <execution_type> tag
             */
            Element executionType = document.createElement("execution_type");
            executionType.appendChild(document.createCDATASection(testCaseObj.getExecutionType()));
            /*
            <importance> tag
             */
            Element importance = document.createElement("importance");
            importance.appendChild(document.createCDATASection(testCaseObj.getImportance()));
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
                stepNumber.appendChild(document.createCDATASection(stepObj.getStepNumber()));

                /*
                <actions> tag
                 */
                Element actions = document.createElement("actions");
                actions.appendChild(document.createCDATASection(stepObj.getActions()));

                /*
                <expectedresults> tag
                 */
                Element expectedResults = document.createElement("expectedresults");
                expectedResults.appendChild(document.createCDATASection(stepObj.getExpectedResults()));

                /*
                <expectedresults> tag
                 */
                Element step_execution_type = document.createElement("execution_type");
                step_execution_type.appendChild(document.createCDATASection(stepObj.getExecutionType()));
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
                name.appendChild(document.createCDATASection(customFieldObj.getName()));
                customField.appendChild(name);
                /*
                <value> tag
                 */
                Element value = document.createElement("value");
                value.appendChild(document.createCDATASection(customFieldObj.getValue()));
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

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(domSource, result);
            logger.info("XML");
            logger.info(result.getWriter().toString());
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);
            logger.info("XML has been generated successfully at: " + xmlFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("XML generation failed");
        }
        return false;
    }
}