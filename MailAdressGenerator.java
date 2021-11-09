
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import jgenderize.GenderizeIoAPI;
import jgenderize.client.Genderize;
import jgenderize.model.NameGender;

public class MailAdressGenerator {

    public static void main(String[] args) throws IOException {
        try (
                FileWriter mailAddressWriter = new FileWriter("address.txt");
        ) {
                Path namePath = Paths.get("names.txt");
                writeAccounts(mailAddressWriter, namePath);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private static void writeAccounts(FileWriter writer, Path path) {
        String[] domains = {"pegozone.com", "mediabenin.com", "allodfs.com"};
        String[] fbMonths = {"jan","fév","mar","avr","mai","jun","juil","août","sep","oct","nov","déc"}; 
        try (Stream<String> accountLines = Files.lines(path)) {
                accountLines.forEach(al -> {
                        String emailAddress = "";
                        List<String> profileName = Arrays.asList(al.split(" "));
                        int nameLength = profileName.size();
                        String completeFirstName = "";
                        for(int i=0; i<nameLength-1;i++) {
                            emailAddress += stripAccents(profileName.get(i)).toLowerCase().strip();
                            completeFirstName += profileName.get(i)+" "; // Don't care about the final " "
                        }
                        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
                        emailAddress += "."+stripAccents(profileName.get(nameLength-1)).toLowerCase()+"@"+domains[randomNum];
                        randomNum = ThreadLocalRandom.current().nextInt(0, 12);
                        String birthMonth = fbMonths[randomNum];
                        int birthdate = ThreadLocalRandom.current().nextInt(1, 29);// avoid beyond 28 for february
                        int birthyear = ThreadLocalRandom.current().nextInt(1996, 2005);
                        
                        Genderize api = GenderizeIoAPI.create();
                        NameGender gender = api.getGender(stripAccents(completeFirstName.strip()));
                        String genderToAppend = gender.isMale() ? ",M" : ",F";
                        try {
                            writer.append(al+","+emailAddress+",benin,"+birthdate+","+birthMonth+","+birthyear+genderToAppend+"\n");
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