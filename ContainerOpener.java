import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ContainerOpener {
    static String country = "benin";
    static int startIndex = 0;
    static int nbInvite = 30;
    static int index;
    static int counter;
    public static void main(String[] args) throws IOException {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("c", "country", true, "specify a country" );
        options.addOption("i", "start-index", true, "index to start from" );
        options.addOption("n", "number-invite", true, "Number of invites to send by account");
        CommandLine line;
        try {
            line = parser.parse(options, args );
            country = line.hasOption("c") ? line.getOptionValue("c") : country;
            startIndex = line.hasOption("i") ? Integer.parseInt(line.getOptionValue("i")) : startIndex;
            nbInvite = line.hasOption("n") ? Integer.parseInt(line.getOptionValue("n")) : nbInvite;
            index = startIndex; counter = startIndex;
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Path linksToOpen = Paths.get("/media/parfait/26040AFB040ACE2D/Images/ProfilsFB/resources/links.txt"),
            containersToOpen = Paths.get("/media/parfait/26040AFB040ACE2D/Images/ProfilsFB/resources/containersToOpen.txt");
        commandGenerator(containersToOpen, linksToOpen);
    }

    private static void commandGenerator(Path containersPath, Path linksPath) {
        System.out.print("\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ");
        List<String> containers, links;
        try (Stream<String> lines0 = Files.lines(containersPath);
            Stream<String>  lines1 = Files.lines(linksPath)
        ) {
            containers = lines0.collect(Collectors.toList());
            links = lines1.collect(Collectors.toList());
            links.forEach(l -> {
                containers.forEach(c-> {
                    System.out.print("\"ext+container:name="+c+"&url="+l+"\" ");
                });
            });
        } catch (IOException e) {
                //TODO: handle exception
        }
    }      
}