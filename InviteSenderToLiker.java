import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    static String containerName = "Cosme Assiogbé";
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
        options.addOption("c", "container-name", true, "specify the container to be used" );
        options.addOption("u", "url", true, "Url to Open");
        CommandLine line;
        try {
            line = parser.parse(options, args );
            containerName = line.hasOption("c") ? line.getOptionValue("c") : containerName;
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
        Path liker = Path.of("liker.txt");
        sendGenerator(liker, urlToOpen);
    }

    private static void sendGenerator(Path filePath, boolean urlToOpen) {
        System.out.print("\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ");
        String content;
        try {
            content = Files.readString(filePath);
            int index = -1;
            Set<String> accountUrl = new HashSet<>();
            do {
                index = content.indexOf("https://www.facebook.com/", index+1);
                if(index < 0) break; // No account remains

                // Here we got an account so we check if there is a "Ajouter", 
                // sign this guy is not in our friends list 
                int nextIndex = content.indexOf("<div data-visualcompletion", index);
                if((nextIndex > 0) && !content.substring(index, nextIndex).contains("Ajouter"))
                    continue;
                if((nextIndex < 0) && !content.substring(index).contains("Ajouter"))
                    continue;
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
