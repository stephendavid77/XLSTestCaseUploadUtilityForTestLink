package com.macys;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m932317 on 11/13/18.
 */
public @Data
class Steps {
    private List<Step> stepList;

    Steps() {
        stepList = new ArrayList<>();
    }

    public void addStep(Step step) {
        stepList.add(step);
    }

}
