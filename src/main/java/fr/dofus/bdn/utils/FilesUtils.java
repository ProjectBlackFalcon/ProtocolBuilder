package fr.dofus.bdn.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dofus.bdn.model.D2JsonModel;

public class FilesUtils {

    private static final Logger log = Logger.getLogger(FilesUtils.class);

    private static final String PATH_D2JSON_EXE = "src/main/resources/d2json.exe";

    private static String outputDirectory = "output/";

    /**
     * Private constructor.
     */
    private FilesUtils() {

    }

    /**
     * Get data out of D2JSON process
     *
     * @param pathToInvoker The dofus in voker from where we get the datas
     * @return The datas formatted as JSON
     * @throws IOException Exception if the process fails
     */
    public static D2JsonModel useD2Json(final String pathToInvoker) throws IOException {
        log.info("Starting d2json.exe");
        Process process = new ProcessBuilder(PATH_D2JSON_EXE, pathToInvoker).start();
        String result = IOUtils.toString(process.getInputStream(), "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, D2JsonModel.class);
    }


    /**
     * Write content in a file
     *
     * @param content The content to write in a file
     * @param path    The path to the file
     */
    public static void writeFile(String content, String path) throws IOException {
        File file = new File(Paths.get(outputDirectory + path).getParent().toString());
        if (!file.exists()) file.mkdirs();
        Files.write(Paths.get(outputDirectory + path), content.getBytes());

    }

    /**
     * Read file
     *
     * @param url Url of the file
     * @return File as a String
     */
    public static String readFile(URL url) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(url.toURI())));
    }

    public static void setOutputDirectory(String outputDirectory) {
        if (!outputDirectory.endsWith("/")) {
            outputDirectory += "/";
        }

        FilesUtils.outputDirectory = outputDirectory;
    }
}
