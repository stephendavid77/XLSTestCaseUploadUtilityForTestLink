package com.macys;

/**
 * Created by m932317 on 11/13/18.
 */

import lombok.Data;

public @Data
class Keyword {
    private String keywordName;
    private String notes;

    public void setKeywordName(String keywordName) {
        this.keywordName = keywordName;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
