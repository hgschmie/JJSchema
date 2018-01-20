package com.github.reinert.jjschema.inheritance;

import com.github.reinert.jjschema.annotations.JsonSchema;

public class CollegeStudent extends Student {

    private String major;

    @JsonSchema(required = true, description = "college major")
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}


