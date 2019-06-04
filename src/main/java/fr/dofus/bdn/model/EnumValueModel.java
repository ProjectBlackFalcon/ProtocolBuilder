package fr.dofus.bdn.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumValueModel {

        @JsonProperty(value = "Name")
        private String name;

        @JsonProperty(value = "Value")
        private int value;

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }