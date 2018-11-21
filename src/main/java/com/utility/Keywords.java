package com.utility;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m932317 on 11/13/18.
 */
public @Data
class Keywords {
    private List<Keyword> keywordList;

    Keywords() {
        this.keywordList = new ArrayList<>();
    }
}
