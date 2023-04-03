package com.poc;

import java.io.Serializable;


public class TestData implements Serializable {
    private String key;
    private String value1;

    public TestData() {
    }

    public TestData(String key, String value_1) {
        this.key = key;
        this.value1 = value_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }
}
