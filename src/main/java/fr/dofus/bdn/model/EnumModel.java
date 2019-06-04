package fr.dofus.bdn.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumModel {

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "Values")
    private List<EnumValueModel> values;
}
