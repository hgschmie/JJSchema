package com.github.reinert.jjschema.inheritance;

import com.github.reinert.jjschema.annotations.JsonSchema;

public class Student {

    private String name;

    @JsonSchema(required = true, description = "student name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

