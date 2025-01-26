import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class InviteSenderToLiker {
    static String country = "benin";
    static String containerName = "Cosme Assiogbé";
    static int startIndex = 0;
    static int nbAccount = 30;
    static int index;
    static int counter;
    static String url = "https://www.facebook.com/groups/00entrenous00";
    static boolean urlToOpen = false;
    static String file = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ";
    static String newAccountOpencommand = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ";
    public static void main(String[] args) throws IOException {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("c", "country", true, "specify a country" );
        options.addOption("u", "url", true, "Url to Open");
        options.addOption("n", "number-account", true, "Number of invites to send by account");
        options.addOption("i", "start-index", true, "index to start from" );
        options.addOption("f", "file", true, "file to read" );
        CommandLine line;
        try {
            line = parser.parse(options, args );
            country = line.hasOption("c") ? line.getOptionValue("c") : country;
            startIndex = line.hasOption("i") ? Integer.parseInt(line.getOptionValue("i")) : startIndex;
            nbAccount = line.hasOption("n") ? Integer.parseInt(line.getOptionValue("n")) : nbAccount;
            file = line.getOptionValue("f", "liker.txt");
            
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Path liker = Path.of(file);
        Path accountPath = Paths.get("/media/parfait/26040AFB040ACE2D/Images/ProfilsFB/resources/accounts.txt");
        sendGenerator(accountPath, liker, urlToOpen);
    }

    private static void sendGenerator(Path accountPath, Path filePath, boolean urlToOpen) {
        System.out.print("\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ");
        List<String> accounts;
        String content;
        try (Stream<String> lines0 = Files.lines(accountPath)
        ){
            accounts = lines0.collect(Collectors.toList()); // list of accounts
            accounts.remove(0); // remove first line of the file which is the title header
            
            if (startIndex < 2)
                startIndex = 2; // In accounts.txt, the first account start with index = 2
            index = startIndex - 2; counter = startIndex - 2;
           
            List<String> containers = new ArrayList<>();
            while(index < startIndex + nbAccount) {
                String[] values = accounts.get(counter%accounts.size()).split(",");
                if(values.length < 4) continue;
                String accountCountry = values[3];
                boolean isOld = true;
                if(values.length > 8) {
                    isOld = !values[8].equals("new");
                }
                if(accountCountry.equals(country) && isOld) {
                    containers.add(values[0]);
                    index++;
                }
                counter++;
            }
            startIndex = index;     

            content = Files.readString(filePath);
            String accountIdentifier = "https://www.facebook.com/";
            index = -1;
            Set<String> accountUrl = new HashSet<>();
            do {
                index = content.indexOf(accountIdentifier, index+1);
                if(index < 0) break; // No account remains
                containerName = containers.get(index % nbAccount);
                
                // Here we got an account so we check if there is a "Ajouter", 
                // sign this guy is not in our friends list 
                int nextIndex = content.indexOf("<div data-visualcompletion", index);
                // if((nextIndex > 0) && !content.substring(index, nextIndex).contains("Ajouter"))
                //     continue;
                // if((nextIndex < 0) && !content.substring(index).contains("Ajouter"))
                //     continue;
                // If it is a new profile, the url will contains "profile.php?id=" and ends 
                // with "&amp;", else it will contains the name and ends with "?__"
                String url="";
                if(content.substring(index, content.indexOf("\"", index)).contains("?__"))
                    url = content.substring(index, content.indexOf("?__", index));
                if(content.substring(index, content.indexOf("\"", index)).contains("profile.php"))
                    url = content.substring(index, content.indexOf("&amp;", index));
                if(!url.equals("") && accountUrl.add(url))
                    System.out.print("\"ext+container:name="+containerName+"&url="+url+"\" ");
            } while(index>0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }      
}
