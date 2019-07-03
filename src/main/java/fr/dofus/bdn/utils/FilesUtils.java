package fr.dofus.bdn.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dofus.bdn.model.D2JsonModel;

public class FilesUtils {

    private static final Logger log = Logger.getLogger(FilesUtils.class);

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
    public static D2JsonModel useD2Json(final String pathToInvoker) throws IOException, InterruptedException {
        log.info("Using d2json.exe");
        File temp = File.createTempFile("d2json", ".tmp");
        temp.setExecutable(true);
        temp.setReadable(true);
        temp.setWritable(true);
        InputStream input = FilesUtils.class.getClassLoader().getResourceAsStream("d2json.exe");
        FileUtils.copyInputStreamToFile(input, temp);
        input.close();

        Process process = null;

        if (SystemUtils.IS_OS_LINUX){
            process = new ProcessBuilder("wine", temp.getPath(), pathToInvoker).start();
        } else {
            process = new ProcessBuilder(temp.getPath(), pathToInvoker).start();
        }

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
