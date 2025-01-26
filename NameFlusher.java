import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class NameFlusher {
    static String country = "benin";
    static int startIndex = 0;
    static int nbInvite = 30;
    static int index;
    static int counter;
    static String url = "https://fr-fr.facebook.com/";
    static boolean urlToOpen = false;
    static String newAccountOpencommand = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ";
    public static void main(String[] args) throws IOException {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("c", "country", true, "specify a country" );
        options.addOption("i", "start-index", true, "index to start from" );
        options.addOption("n", "number-invite", true, "Number of invites to send by account");
        options.addOption("u", "url", true, "Url to Open");
        CommandLine line;
        try {
            line = parser.parse(options, args );
            country = line.hasOption("c") ? line.getOptionValue("c") : country;
            startIndex = line.hasOption("i") ? Integer.parseInt(line.getOptionValue("i")) : startIndex;
            nbInvite = line.hasOption("n") ? Integer.parseInt(line.getOptionValue("n")) : nbInvite;
            if(line.hasOption("u")) {
                url = line.getOptionValue("u");
                urlToOpen = true;
            }
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Path namePath = Paths.get("names.txt");
        nameGenerator(namePath);
    }

    private static void nameGenerator(Path namePath) {
        List<String> names;
        try (Stream<String> lines = Files.lines(namePath);
        ) {
            names = lines.collect(Collectors.toList()); // list of accounts
            index = 0; counter = 0;
            Random random = new Random();
            while(!names.isEmpty()){
                String name = names.get(0);
                names.remove(0);
                if(names.isEmpty()) {
                    System.out.print(name);
                    break;
                }
                String[] name1 = name.split(" ");
                int nb = names.size();
                index = random.nextInt(nb);
                String[] name2 = names.get(index).split(" ");
                System.out.print(name1[0] + " " + name2[1]+"\n" + name2[0] + " " + name1[1]+"\n");
                names.remove(index);
            }
        } catch (IOException e) {
                System.err.println(e);
        }
    }      
}