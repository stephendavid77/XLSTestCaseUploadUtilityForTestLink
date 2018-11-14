package com.macys;

import lombok.Data;

import java.util.ArrayList;

/**
 * Created by m932317 on 11/13/18.
 */
public @Data
class CustomFields {
    private ArrayList<CustomField> customFieldsList;

    CustomFields() {
        customFieldsList = new ArrayList<>();
    }
}
