
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ThreadLocalRandom;

public class MailAdressGenerator {

    public static void main(String[] args) throws IOException {
        try (
                FileWriter mailAddressWriter = new FileWriter("address.txt");
        ) {
                Path namePath = Paths.get("names.txt");
                writeAccounts(mailAddressWriter, namePath);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch(IOException e) {
                e.printStackTrace();
        }
    }

    private static void writeAccounts(FileWriter writer, Path path) {
        String[] domains = {"pegozone.com", "mediabenin.com", "allodfs.com"};
        String[] fbMonths = {"jan","fév","mar","avr","mai","jun","juil","aoû","sep","oct","nov","déc"}; 
        try (Stream<String> accountLines = Files.lines(path)) {
                accountLines.forEach(al -> {
                        String emailAddress = "";
                        List<String> profileName = Arrays.asList(al.split(" "));
                        int nameLength = profileName.size();
                        for(int i=0; i<nameLength-1;i++) {
                            emailAddress +=stripAccents(profileName.get(i)).toLowerCase();
                        }
                        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
                        emailAddress += "."+stripAccents(profileName.get(nameLength-1)).toLowerCase()+"@"+domains[randomNum];
                        randomNum = ThreadLocalRandom.current().nextInt(0, 12);
                        String birthMonth = fbMonths[randomNum];
                        int birthdate = ThreadLocalRandom.current().nextInt(1, 29);// avoid beyond 28 for february
                        int birthyear = ThreadLocalRandom.current().nextInt(1996, 2005);
                        try {
                            writer.append(al+","+emailAddress+",benin,"+birthdate+","+birthMonth+","+birthyear+"\n");
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }                    
                });
                
        } catch (IOException ex) {
                // do something or re-throw...
                ex.printStackTrace();
        }
    }

    private static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFKD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

}