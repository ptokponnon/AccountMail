import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Connect {
    static String defaultCountry = "";
    static String defaultStartIndex = "0";
    static String defaultNbInvite = "20";
    static String defaultSexe = "";
    static String defaultUurl = "https://www.facebook.com";
    static String country;
    static int startIndex;
    static int nbInvite;
    static int index;
    static int counter;
    static String sexe = "";
    static boolean mustBeNew = false;
    static List<String> urls;
    static List<String> newAccounts;
    static boolean urlToOpen = false;
    static String newAccountOpencommand = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ";
    static String containerOpenString = "";
    static String firefoxExec = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String extContainerName = "\"ext+container:name=";
             //creating a constructor of the Robot class  
    static Robot robot;   

    public static void main(String[] args) throws IOException {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("c", "country", true, "specify a country" );
        options.addOption("i", "start-index", true, "index to start from" );
        options.addOption("n", "number-invite", true, "Number of invites to send by account");
        options.addOption("s", "sexe", true, "sexe of accounts to open");
        options.addOption("N", "must-be-new", false, "if account must be new");
        Option urlOption = new Option("u", "url", false, "Url to Open");
        urlOption.setArgs(Option.UNLIMITED_VALUES);
        urlOption.setValueSeparator(' ');
        options.addOption(urlOption);
        Option newAccountOption = new Option("a", "new-account", false, "new accounts to Open in friends page");
        newAccountOption.setArgs(Option.UNLIMITED_VALUES);
        newAccountOption.setValueSeparator(';');
        options.addOption(newAccountOption);

        CommandLine line;
        try {
            line = parser.parse(options, args );
            country = line.getOptionValue("c", defaultCountry);
            startIndex = Integer.parseInt(line.getOptionValue("i", defaultStartIndex));
            nbInvite = Integer.parseInt(line.getOptionValue("n", defaultNbInvite));
            sexe = line.getOptionValue("s", defaultSexe);
            if(line.hasOption("u")) {
                urls = Arrays.asList(line.getOptionValues("u"));
            }
            if(line.hasOption("a")) {
                newAccounts = Arrays.asList(line.getOptionValues("a"));
            }
            if(line.hasOption("N")) {
                mustBeNew = true;
            }
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Path accountPath = Paths.get("C:\\Users\\parfait\\Programming\\AccountMail\\accounts.txt");
        
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendGenerator(accountPath, urlToOpen);
    }

    private static void sendGenerator(Path accountPath, boolean urlToOpen) {
        List<String> accounts;
        try (Stream<String> lines0 = Files.lines(accountPath);
        ) {
            accounts = lines0.collect(Collectors.toList()); // list of accounts
            accounts.remove(0); // remove first line of the file which is the title header
            int nbTotalAccount = accounts.size();

            if (startIndex < 2)
                startIndex = 2; // In accounts.txt, the first account start with index = 2
            index = startIndex - 2; 
            counter = startIndex - 2;
            if(newAccounts == null) {
                if(urls == null) {
                    urls = new ArrayList<>();
                }
                if(urls.isEmpty()) {
                    urls.add(defaultUurl);
                }
                while (true) {
                    startIndex = counter + 2;
                    index = startIndex - 2;
                    counter = startIndex - 2;
                    containerOpenString = "";
                    int counterLimit = counter + nbTotalAccount;
                    while(index < startIndex + nbInvite && counter < counterLimit) {
                        String[] values = accounts.get(counter%nbTotalAccount).split(",");
                        if(isPrintable(values)) {
                            String containerName = values[0];
                            String containerEmail = values[2];
                            for(String url : urls) {
                                containerOpenString = extContainerName + containerName+"&url="+url+"\"";
                            }
                            String[] commands = {firefoxExec, containerOpenString};
                            automate(commands, containerEmail);
                            index++;
                        }
                        counter++;
                    }
                    
                    System.out.println("\nnext start: " + (counter + 2)); // In accounts.txt, the first account start with index = 2
                }
            } else {
                newAccounts.forEach(l -> {
                    String[] values = l.split(",");
                    String profilUrl = values[1];
                    newAccountOpencommand += containerOpenString+values[0]+"&url=facebook.com/friends\" ";
                    int counterLimit = counter + nbTotalAccount;
                    while(index < startIndex + nbInvite && counter < counterLimit) {
                        values = accounts.get(counter%accounts.size()).split(",");
                        if(values.length < 4) continue;
                        String accountCountry = values[3];
                        boolean isOld = true;
                        if(values.length > 8) {
                            isOld = !values[8].equals("new");
                        }
                        if(accountCountry.equals(country) && isOld) {
                            String containerName = values[0];
                            if(isPrintable(values)) {
                                System.out.print(containerOpenString+containerName+"&url="+profilUrl+"\" ");
                            }
                            index++;
                        }
                        counter++;
                    }
                    startIndex = index;            
                });
                System.out.println("\n\n"+newAccountOpencommand);
                System.out.println("\nnext start: "+ (counter + 2)); // In accounts.txt, the first account start with index = 2
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        static boolean isProcessRunning(String processName) {
        String[] command = new String[]{ "powershell", "ps", processName };
        try {
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                command = new String[]{ "ps", "-o", "comm" };
            }
            Process ps = new ProcessBuilder(command).start();
            return new BufferedReader(
                    new InputStreamReader(ps.getInputStream(), StandardCharsets.UTF_8)
            ).lines().anyMatch(line -> line.contains(processName));

        } catch (IOException e) {
            throw new RuntimeException(String.join(" ", command) + ": No such command on this OS.");
        }
    }

    private static boolean isPrintable(String[] values) {
        String accountCountry ="";
        String accountSexe ="";
        String isNew = "";
        if(!country.isEmpty()){
            if(values.length<4)
                return false;
            else {
                accountCountry = values[3];
                if(!accountCountry.equals(country))
                    return false;
            }
        }
        if(!sexe.isEmpty()){
            if(values.length<8)
                return false;
            else {
                accountSexe = values[7];
                if(!accountSexe.equals(sexe))
                    return false;
            }
        }
        if(mustBeNew){
            if(values.length<9)
                return false;
            else {
                isNew = values[8];
                if(isNew.equals("false")) {
                    return false;
                }
            }
        }
        return true;
    }      

    public static void click(int x, int y){
        robot.mouseMove(x, y);    
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void automate (String[] commands, String containerEmail) {
        try {
            Runtime.getRuntime().exec(commands);
            robot.delay(10000);       
            click(1047, 776); // agree cookies
            robot.delay(3000);       
            click(1100, 250); // address field
            robot.delay(3000);
            pasteText(containerEmail);
            robot.delay(10000);
            click(1200, 310);
            // robot.delay(1000);
            // click(1110, 340);
            robot.delay(3000);
            robot.keyPress(KeyEvent.VK_ENTER);  
            robot.keyRelease(KeyEvent.VK_ENTER);  
            robot.delay(5000);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void pasteText (String text){
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}