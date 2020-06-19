package org.kraskovsky.polls.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FieldType {
    SINGLE_TEXT, MULTI_TEXT, RADIO_BUTTON, CHECKBOX, COMBOBOX, DATE;

    @JsonCreator
    public static FieldType fromText(String text) {
        switch (text) {
            case "MULTI_TEXT":
                return FieldType.MULTI_TEXT;
            case "RADIO":
                return FieldType.RADIO_BUTTON;
            case "CHECKBOX":
                return FieldType.CHECKBOX;
            case "COMBOBOX":
                return FieldType.COMBOBOX;
            case "DATE":
                return FieldType.DATE;
            default:
                return FieldType.SINGLE_TEXT;
        }
    }
}
