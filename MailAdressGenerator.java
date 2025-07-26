
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class MailAdressGenerator {

    public static void main(String[] args) throws IOException {
        try (
                FileWriter mailAddressWriter = new FileWriter("address.txt");
        ) {
                String firstname = "firstname.txt", lastname = "lastname.txt";
                writeAccounts(mailAddressWriter, firstname, lastname);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private static void writeAccounts(FileWriter writer, String firstnameFile, String lastnameFile) {
        Path firstnamePath = Paths.get(firstnameFile), lastnamePath = Paths.get(lastnameFile);
        String[] domains = {"mediabenin.com", "allodfs.win"};
        String[] fbMonths = {"jan","fév","mar","avr","mai","jun","juil","août","sep","oct","nov","déc"}; 
        List<String> firstNames, lastNames, fullNameWithGenders;
        try {
            firstNames = Files.lines(firstnamePath).toList();
            lastNames = Files.lines(lastnamePath).toList();
            fullNameWithGenders = randomAssociation(firstNames, lastNames);
        
            fullNameWithGenders.forEach(fng -> {
                String emailAddress = "";
                List<String> SplittedFng = Arrays.asList(fng.split(","));
                String profil = SplittedFng.get(0);
                String gender = SplittedFng.get(1);
                List<String> fullName = Arrays.asList(profil.split(" "));
                int nameLength = fullName.size();
                for(int i=0; i<nameLength-1;i++) {
                    emailAddress += stripAccents(fullName.get(i)).toLowerCase().strip();
                }
                int randomNum = ThreadLocalRandom.current().nextInt(0, domains.length);
                emailAddress += "."+stripAccents(fullName.get(nameLength-1)).toLowerCase()+"@"+domains[randomNum];
                randomNum = ThreadLocalRandom.current().nextInt(0, 12);
                String birthMonth = fbMonths[randomNum];
                int birthdate = ThreadLocalRandom.current().nextInt(1, 29);// avoid beyond 28 for february
                int birthyear = ThreadLocalRandom.current().nextInt(1996, 2005);
                
                System.out.println(profil+" "+emailAddress+" "+gender);
                try {
                    writer.append(profil+",xxxxxx,"+emailAddress+",benin,"+birthdate+","+birthMonth+","+birthyear+","+gender+"\n");
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

    
    public static List<String> randomAssociation(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();

        // Shuffle the input lists to randomize order
        List<String> shuffled1 = new ArrayList<>(list1);
        List<String> shuffled2 = new ArrayList<>(list2);
        Collections.shuffle(shuffled1);
        Collections.shuffle(shuffled2);

        // Get the minimum size to avoid IndexOutOfBounds
        int size = Math.min(shuffled1.size(), shuffled2.size());

        for (int i = 0; i < size; i++) {
            String[] firstNameGender = shuffled1.get(i).split(",");
            String firstname = firstNameGender[0];
            String gender = firstNameGender[1];
            String combined = firstname + " " + shuffled2.get(i)+","+gender;
            result.add(combined);
        }

        return result;
    }

}