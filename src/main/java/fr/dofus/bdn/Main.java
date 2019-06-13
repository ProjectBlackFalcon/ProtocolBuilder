package fr.dofus.bdn;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import fr.dofus.bdn.model.D2JsonModel;
import fr.dofus.bdn.utils.FilesUtils;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        final Options options = configParameters();
        final CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            help(options);
        }

        String pathInvoker = cmd.getOptionValue("file", "f");

        if (cmd.hasOption("o")){
            FilesUtils.setOutputDirectory(cmd.getOptionValue("output", "o"));
        }

        D2JsonModel d2json = FilesUtils.useD2Json(pathInvoker);

        ProtocolBuilder builder = new ProtocolBuilder(d2json);
        builder.generateClasses();
    }


    /**
     * Configure the command line options
     *
     * @return Options
     */
    private static Options configParameters() {

        final Option fileOption = Option.builder("f")
            .longOpt("file")
            .desc("Full path to the DofusInvoker.swf")
            .hasArg(true)
            .argName("DofusInvoker.swf")
            .required(true)
            .build();

        final Option outputOption = Option.builder("o")
            .longOpt("output")
            .desc("Full path to the output directory")
            .hasArg(true)
            .argName("outputDir")
            .required(false)
            .build();

        final Options options = new Options();

        options.addOption(fileOption);
        options.addOption(outputOption);
        return options;
    }

    /**
     * Print cmd help options for the user
     *
     * @param options Command line options
     */
    private static void help(final Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("Main", options);
        System.exit(0);
    }

}
