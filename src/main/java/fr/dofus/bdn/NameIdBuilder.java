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

    private static final String PATH = "resources/NameId.json";


    public static void createNameId(final D2JsonModel d2JsonModel) throws IOException {
        log.info("Generating NameId.json...");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        d2JsonModel.getMessages().forEach(message -> (
            (ObjectNode) rootNode).put(String.valueOf(message.getProtocolId()), message.getName())
        );

        d2JsonModel.getTypes().forEach(message -> (
            (ObjectNode) rootNode).put(String.valueOf(message.getProtocolId()), message.getName())
        );

        FilesUtils.writeFile(mapper.writeValueAsString(rootNode), PATH);

    }


}
