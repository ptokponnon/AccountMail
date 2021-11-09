import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class InviteSender {
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
        Path accountPath = Paths.get("/media/parfait/26040AFB040ACE2D/Images/ProfilsFB/resources/accounts.txt"),
            newAccountPath = Paths.get("/media/parfait/26040AFB040ACE2D/Images/ProfilsFB/resources/new_accounts.txt");
        sendGenerator(accountPath, newAccountPath, urlToOpen);
    }

    private static void sendGenerator(Path accountPath, Path newAccountPath, boolean urlToOpen) {
        System.out.print("\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ");
        List<String> accountlLines, newAccountLines;
        try (Stream<String> lines0 = Files.lines(accountPath);
            Stream<String>  lines1 = Files.lines(newAccountPath)
        ) {
            accountlLines = lines0.collect(Collectors.toList());
            newAccountLines = lines1.collect(Collectors.toList());
            index = 0; counter = 0;
            while(index < startIndex) {
                if(counter%accountlLines.size() != 0){
                    String[] values = accountlLines.get(counter%accountlLines.size()).split(",");
                    String accountCountry = values[3];
                    if(accountCountry.equals(country)) {
                        index++;
                    }
                }
                counter++;
            }
            if(newAccountLines.isEmpty() || urlToOpen) {
                while(index < startIndex + nbInvite) {
                    if(counter%accountlLines.size() != 0){
                        String[] values = accountlLines.get(counter%accountlLines.size()).split(",");
                        String accountCountry = values[3];
                        if(accountCountry.equals(country)) {
                            String containerName = values[0];
                            System.out.print("\"ext+container:name="+containerName+"&url="+url+"\" ");
                            index++;
                        }
                    }
                    counter++;
                }
            } else {
                newAccountLines.forEach(l -> {
                    String[] values = l.split(",");
                    String profilUrl = values[1];
                    newAccountOpencommand += "\"ext+container:name="+values[0]+"&url=facebook.com/friends\" ";
                    while(index < startIndex + nbInvite) {
                        if(counter%accountlLines.size() != 0){
                            values = accountlLines.get(counter%accountlLines.size()).split(",");
                            String accountCountry = values[3];
                            if(accountCountry.equals(country)) {
                                String containerName = values[0];
                                System.out.print("\"ext+container:name="+containerName+"&url="+profilUrl+"\" ");
                                index++;
                            }
                        }
                        counter++;
                    }
                    startIndex = index;            
                });
                System.out.println("\n\n"+newAccountOpencommand);
            }
        } catch (IOException e) {
                //TODO: handle exception
        }
    }      
}