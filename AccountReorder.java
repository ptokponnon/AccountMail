import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountReorder {
    public static void main(String[] args) throws IOException {
        try (
            FileWriter accountInOderWriter = new FileWriter("accounts-order.txt");
        ) {
            Path accountPath = Paths.get("accounts.txt");
            reorder(accountInOderWriter, accountPath);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch(IOException e) {
                e.printStackTrace();
        }
    }

    private static void reorder(FileWriter accountInOderWriter, Path accountPath) {
        List<String> accountlLines;
        try (Stream<String> lines = Files.lines(accountPath)) {
            accountlLines = lines.collect(Collectors.toList());
            List<String> sortedList = accountlLines.stream().sorted(new SortByMail())
                .collect(Collectors.toList());  
            sortedList.forEach(s -> {
                try {
                    accountInOderWriter.write(s+"\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
                //TODO: handle exception
        }
    }      
}

class SortByMail implements Comparator<String> {
    // Used for sorting in ascending order of
    List<String> order = Arrays.asList("gmail", "yahoo", "allodfs", "mediabenin", "pegolab", "pegozone");
    @Override
    public int compare(String o1, String o2) {
        // TODO Auto-generated method stub
        for (int i = 0; i<order.size(); i++) {
            if(o1.contains(order.get(i)))
                return -1;
            if(o2.contains(order.get(i)))
                return 1;

        }
        return 0;
    }
}