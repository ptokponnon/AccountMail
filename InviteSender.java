import java.awt.AWTException;
import java.awt.Robot;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class InviteSender {
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
        sendGenerator(accountPath, urlToOpen);
    }

    private static void sendGenerator(Path accountPath, boolean urlToOpen) {
        System.out.print("\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ");
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
                    int nbTab = 0;
                    while(index < startIndex + nbInvite && counter < counterLimit) {
                        String[] values = accounts.get(counter%nbTotalAccount).split(",");
                        if(isPrintable(values)) {
                            String containerName = values[0];
                            for(String url : urls) {
                                containerOpenString += extContainerName + containerName+"&url="+url+"\" ";
                            }
                            index++;
                        }
                        counter++;
                    }
                    containerOpenString = containerOpenString.substring(0, containerOpenString.length()-1); // The last blanc (" ") must be deleted to avoid problem in the thread running 
                    String[] commands = {firefoxExec, containerOpenString};
                    nbTab = containerOpenString.split("url", -1).length;
                    System.out.println("    containerOpenString "+containerOpenString);
                    // CommandRunner commandRunner = new CommandRunner(commands, 2*nbUrl);
                    // Pass it to a Thread object
                    // Thread thread = new Thread(commandRunner);
                    // Start the thread
                    // thread.start();
                    Runtime.getRuntime().exec(commands);
                    try {
                        TimeUnit.MINUTES.sleep(ThreadLocalRandom.current().nextInt(1, 2)); // the next loop should last 30 seconds times nbTab, that's 1/2 minutes * nbTab

                         //creating a constructor of the Robot class  
                         Robot robot = new Robot();   
                         //pressing key by invoking the keyPress() method  
                        //  robot.keyPress(KeyEvent.VK_F5);   
                        //  Thread.sleep(200);
                        //  robot.keyRelease(KeyEvent.VK_F5);   
                         //delay of 5 miliseconds after each key press  
                        //  TimeUnit.SECONDS.sleep(10);
                         for(int i = 0; i < nbTab; i++) { 
                            click(1047, 776);
                            Thread.sleep(2000);
                            robot.keyPress(KeyEvent.VK_CONTROL);   
                            robot.keyPress(KeyEvent.VK_TAB); 
                            Thread.sleep(200);
                            robot.keyRelease(KeyEvent.VK_CONTROL);  
                            robot.keyRelease(KeyEvent.VK_TAB);
                            TimeUnit.SECONDS.sleep(3);
                            // robot.keyPress(KeyEvent.VK_F5);   
                            // Thread.sleep(200);
                            // robot.keyRelease(KeyEvent.VK_F5);   
                            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 3));
                        }

                        while (isProcessRunning("firefox")) { 
                            robot.keyPress(KeyEvent.VK_CONTROL);   
                            robot.keyPress(KeyEvent.VK_W); 
                            Thread.sleep(200);
                            robot.keyRelease(KeyEvent.VK_CONTROL);  
                            robot.keyRelease(KeyEvent.VK_W);
                            TimeUnit.SECONDS.sleep(3);
                            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 3));
                        }
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 20));
                    } catch (InterruptedException | AWTException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
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
                //TODO: handle exception
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

    public static void click(int x, int y) throws AWTException{
        Robot bot = new Robot();
        bot.mouseMove(x, y);    
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}