package fr.dofus.bdn;

import java.io.FileNotFoundException;

import org.apache.log4j.BasicConfigurator;

import fr.dofus.bdn.model.D2JsonModel;
import fr.dofus.bdn.utils.FilesUtils;

public class Main {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        if (args.length != 1) {
            throw new FileNotFoundException("Please specify the path of the DofusInvoker.swf");
        }

        String pathInvoker = args[0];

        D2JsonModel d2json = FilesUtils.useD2Json(pathInvoker);
        ProtocolBuilder builder = new ProtocolBuilder(d2json);
        builder.generateClasses();
    }

}
