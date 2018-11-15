package com.macys;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Created by m932317 on 11/12/18.
 */
public @Data
class TestCase {

    final Logger logger = LogManager.getLogger(TestCase.class.getName());

    private String name;
    private String summary;
    private String preConditions;
    private String executionType;
    private String importance;
    private Steps steps;
    private Keywords keywords;
    private CustomFields customFields;

    TestCase() {
        this.steps = new Steps();
        this.keywords = new Keywords();
        this.customFields = new CustomFields();
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType.equals("Automated") ? "2" : "1";
    }

    public void setImportance(String importance) {
        this.importance = importance.equals("High") ? "1" : executionType.equals("Medium") ? "2" : "3";
    }

    public void setStepNumber(String stepNumber) {
        if (stepNumber != null && !stepNumber.isEmpty()) {
            logger.debug("Steps Object: " + steps);
            logger.debug("Step List Object: " + steps.getStepList());
            if (steps.getStepList().isEmpty() || steps.getStepList().stream().filter(step1 -> step1.getStepNumber().equals(stepNumber)).count() != 1) {
                Step newStep = new Step();
                newStep.setStepNumber(stepNumber);
                steps.addStep(newStep);
            } else {
                logger.debug("Step number is already defined");
            }
        }
    }

    public void setStepActions(String stepActions, String stepNumber) {
        boolean isStepActionSet = false;
        for(Step step : steps.getStepList()){
            if (step.getStepNumber() == stepNumber) {
                step.setActions(stepActions);
                isStepActionSet = true;
            }
        }
        if(!isStepActionSet) {
            Step newStep = new Step();
            newStep.setStepNumber(stepNumber);
            newStep.setActions(stepActions);
            steps.addStep(newStep);
        }
    }

    public void setExpectedResults(String expectedResults, String stepNumber) {
        boolean isExpectedResultsSet = false;
        for(Step step : steps.getStepList()){
            if (step.getStepNumber() == stepNumber) {
                step.setExpectedResults(expectedResults);
                isExpectedResultsSet = true;
            }
        }
        if(!isExpectedResultsSet) {
            Step newStep = new Step();
            newStep.setStepNumber(stepNumber);
            newStep.setExpectedResults(expectedResults);
            steps.addStep(newStep);
        }
    }

    public void setStepExecutionType(String executionType, String stepNumber) {
        boolean isExecutionTypeSet = false;
        for(Step step : steps.getStepList()){
            if (step.getStepNumber() == stepNumber) {
                step.setExecutionType(executionType);
                isExecutionTypeSet = true;
            }
        }
        if(!isExecutionTypeSet) {
            Step newStep = new Step();
            newStep.setStepNumber(stepNumber);
            newStep.setExecutionType(executionType);
            steps.addStep(newStep);
        }
    }

    public void setKeyword(String keyword) {
        if (keyword != null && !keyword.isEmpty() && keyword.contains(",")) {
            Arrays.asList(keyword.split(",")).stream().forEach(str -> {
                Keyword keyword1 = new Keyword();
                new Keyword().setKeywordName(str);
                keywords.getKeywordList().add(keyword1);
            });
        }
    }

    public void setCustomField(String name, String value) {
        CustomField customField = new CustomField();
        customField.setName(name);
        customField.setValue(value);
        customFields.getCustomFieldsList().add(customField);
    }
}
