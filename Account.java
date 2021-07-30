
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

public class Account {

    public static void main(String[] args) throws IOException {
        try (
                FileWriter accountMailWriter = new FileWriter("accounts.txt");
                FileWriter errorAccountWriter = new FileWriter("error.txt");
        ) {
                Path accountPath = Paths.get("account.txt");
                Path mailPath = Paths.get("mail.txt");
                writeAccounts(accountMailWriter, errorAccountWriter, accountPath, mailPath);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch(IOException e) {
                e.printStackTrace();
        }
    }

    private static void writeAccounts(FileWriter accountWriter, FileWriter errorAccountWriter, 
    Path accountPath, Path mailPath) {
        try (Stream<String> accountLines = Files.lines(accountPath)) {
                accountLines.forEach(al -> {
                        List<String> accountName = Arrays.asList(al.split(" |,")).subList(0,2);

                        List<String> mailLines;
                        try (Stream<String> lines = Files.lines(mailPath)) {
                                mailLines = lines.collect(Collectors.toList());
                                Optional<String> ml = mailLines.stream().filter(mail -> 
                                mail.contains(stripAccents(accountName.get(0)).toLowerCase()) && 
                                mail.contains(stripAccents(accountName.get(1)).toLowerCase())).findFirst();
                                if(ml.isPresent()) {
                                        accountWriter.write(al+","+ml.get()+"\n");
                                } else {
                                        errorAccountWriter.write(al+" "+ accountName+"\n");
                                }
                        } catch (IOException e) {
                                //TODO: handle exception
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