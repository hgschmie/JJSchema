package com.github.reinert.jjschema.inheritance;

import com.github.reinert.jjschema.annotations.JsonSchema;

public class CollegeStudent extends Student {

    @JsonSchema(required = true, description = "college major")
    private String major;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}


