package com.utility;

import com.github.rjeschke.txtmark.Processor;
import lombok.Data;

/**
 * Created by m932317 on 11/13/18.
 */
public @Data
class Step {
    private String stepNumber;
    private String actions;
    private String expectedResults;
    private String executionType;

    public void setExecutionType(String executionType) {
        this.executionType = executionType.equals("Automated") ? "2" : "1";
    }

    public String getActions() {
        return Processor.process(actions);
    }

    public String getExpectedResults() {
        return Processor.process(expectedResults);
    }
}

