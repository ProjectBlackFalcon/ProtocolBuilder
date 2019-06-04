package fr.dofus.bdn.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageModel {

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "Parent")
    private String parents;

    @JsonProperty(value = "ProtocolID")
	private long protocolId;

    @JsonProperty(value = "Fields")
	private List<FieldModel> fieldModels;

    @JsonProperty(value = "Namespace")
	private String namespace;

    @JsonProperty(value = "UseHashFunc")
	private boolean useHashFunc;

	public String getName() {
		return name;
	}

	public String getParents() {
		return parents;
	}

	public long getProtocolId() {
		return protocolId;
	}

	public List<FieldModel> getFieldModels() {
		return fieldModels;
	}

	public String getNamespace() {
		return namespace;
	}

	public boolean isUseHashFunc() {
		return useHashFunc;
	}
}
