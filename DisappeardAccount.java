import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DisappeardAccount {
    public static void main(String[] args) throws IOException {
        Path accountPath0 = Paths.get("accounts.txt");
        Path accountPath1 = Paths.get("container.txt");
        List<String> account0lLines;
        List<String> account1lLines;
        try (
            Stream<String> lines0 = Files.lines(accountPath0);
            Stream<String> lines1 = Files.lines(accountPath1);
        ) {
            account0lLines = lines0.collect(Collectors.toList());
            account1lLines = lines1.collect(Collectors.toList());
            account0lLines.forEach(a0l -> {
                List<String> accountName = Arrays.asList(a0l.split(" |,")).subList(0,2);
                boolean found = account1lLines.stream().anyMatch(al -> 
                al.contains(accountName.get(0)) && al.contains(accountName.get(1)));
                if(!found) {
                    System.out.println(accountName);
                }
            });
        } catch (IOException e) {
                //TODO: handle exception
        }    
    }
}