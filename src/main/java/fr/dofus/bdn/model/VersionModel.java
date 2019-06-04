package fr.dofus.bdn.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VersionModel {

    @JsonProperty(value = "Major")
    private int major;

    @JsonProperty(value = "Minor")
    private int minor;

    @JsonProperty(value = "Release")
    private int release;

    @JsonProperty(value = "Revision")
    private long revision;

    @JsonProperty(value = "Patch")
    private int patch;

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRelease() {
        return release;
    }

    public long getRevision() {
        return revision;
    }

    public int getPatch() {
        return patch;
    }
}
