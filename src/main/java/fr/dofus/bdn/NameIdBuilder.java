package fr.dofus.bdn;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.dofus.bdn.model.D2JsonModel;
import fr.dofus.bdn.utils.FilesUtils;

public class NameIdBuilder {

    private static final Logger log = Logger.getLogger(NameIdBuilder.class);

    private static final String PATH_MESSAGES = "resources/MessageNameId.json";
    private static final String PATH_TYPES = "resources/TypeNameId.json";


    public static void createNameId(final D2JsonModel d2JsonModel) throws IOException {
        log.info("Generating MessageNameId.json...");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        // Generate NameId for the messages
        d2JsonModel.getMessages().forEach(message -> (
            (ObjectNode) rootNode).put(String.valueOf(message.getProtocolId()), message.getNamespace() + "." + message.getName())
        );
        FilesUtils.writeFile(mapper.writeValueAsString(rootNode), PATH_MESSAGES);

        // Generate NameId for the types
        log.info("Generating TypeNameId.json...");

        ((ObjectNode) rootNode).removeAll();

        d2JsonModel.getTypes().forEach(message -> (
            (ObjectNode) rootNode).put(String.valueOf(message.getProtocolId()), message.getNamespace() + "." + message.getName())
        );
        FilesUtils.writeFile(mapper.writeValueAsString(rootNode), PATH_TYPES);
    }


}
