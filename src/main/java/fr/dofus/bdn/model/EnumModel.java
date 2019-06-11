package fr.dofus.bdn.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumModel {

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "Values")
    private List<EnumValueModel> values;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<EnumValueModel> getValues() {
        return values;
    }

    public void setValues(final List<EnumValueModel> values) {
        this.values = values;
    }
}
