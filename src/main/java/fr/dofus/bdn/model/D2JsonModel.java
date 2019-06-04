package fr.dofus.bdn.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public class D2JsonModel {

    @JsonProperty(value = "Messages")
    private List<MessageModel> messages;

    @JsonProperty(value = "Types")
    private List<MessageModel> types;

    @JsonProperty(value = "Enums")
    private List<EnumModel> enums;

    @JsonProperty(value = "Version")
    private VersionModel version;


    public List<MessageModel> getMessages() {
        return messages;
    }

    public List<MessageModel> getTypes() {
        return types;
    }

    public List<EnumModel> getEnums() {
        return enums;
    }

    public VersionModel getVersion() {
        return version;
    }

    public MessageModel findMessageByName(final String name) {
        Optional<MessageModel> model = messages.stream().filter(messageModel -> messageModel.getName().equals(name)).findFirst();

        if (model.isPresent()){
            return model.get();
        }

        model = types.stream().filter(messageModel -> messageModel.getName().equals(name)).findFirst();

        if (model.isPresent()){
            return model.get();
        }

        throw new Error("Cannot find message with name : " + name);
    }
}
