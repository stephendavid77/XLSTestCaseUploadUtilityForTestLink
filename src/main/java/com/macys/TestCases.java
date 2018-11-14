package com.macys;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m932317 on 11/13/18.
 */
public @Data
class TestCases {
    private List<TestCase> testCasesList;

    TestCases() {
        testCasesList = new ArrayList<>();
    }

    public void addToTestCasesList(TestCase testCase) {
        testCasesList.add(testCase);
    }

}
