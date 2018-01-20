package de.softwareforge.jsonschema.inheritance;

import de.softwareforge.jsonschema.annotations.JsonSchema;

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


