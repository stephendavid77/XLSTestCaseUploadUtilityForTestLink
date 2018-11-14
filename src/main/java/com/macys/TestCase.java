package com.macys;

import lombok.Data;

import java.util.Arrays;

/**
 * Created by m932317 on 11/12/18.
 */
public @Data
class TestCase {
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
            System.err.println("Steps Object: " + steps);
            System.err.println("Step List Object: " + steps.getStepList());
            if (steps.getStepList().isEmpty() || steps.getStepList().stream().filter(step1 -> step1.getStepNumber().equals(stepNumber)).count() != 1) {
                Step newStep = new Step();
                newStep.setStepNumber(stepNumber);
            } else {
                System.out.println("Step number is already defined");
            }
        }
    }

    public void setStepActions(String stepActions, String stepNumber) {
        if (stepActions != null && !stepActions.isEmpty()) {
            steps.getStepList().stream().forEach(step1 -> {
                if (step1.getStepNumber() == stepNumber) {
                    step1.setActions(stepActions);

                } else {
                    Step newStep = new Step();
                    newStep.setStepNumber(stepNumber);
                    newStep.setActions(stepActions);
                    steps.addStep(newStep);
                }
            });
        }
    }

    public void setExpectedResults(String expectedResults, String stepNumber) {
        if (expectedResults != null && !expectedResults.isEmpty()) {
            steps.getStepList().stream().forEach(step1 -> {
                if (step1.getStepNumber() == stepNumber) {
                    step1.setExpectedResults(expectedResults);
                } else {
                    Step newStep = new Step();
                    newStep.setStepNumber(stepNumber);
                    newStep.setExpectedResults(expectedResults);
                    steps.addStep(newStep);
                }
            });
        }
    }

    public void setStepExecutionType(String executionType, String stepNumber) {
        if (executionType != null && !executionType.isEmpty()) {
            steps.getStepList().stream().forEach(step1 -> {
                if (step1.getStepNumber() == stepNumber) {
                    step1.setExecutionType(executionType);
                } else {
                    Step newStep = new Step();
                    newStep.setStepNumber(stepNumber);
                    newStep.setExecutionType(executionType);
                    steps.addStep(newStep);
                }
            });
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
