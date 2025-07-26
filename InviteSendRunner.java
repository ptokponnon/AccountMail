import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.awt.AWTException;
import java.awt.Robot;   
import java.awt.event.KeyEvent;   


public class InviteSendRunner {
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
    static String firefoxExec = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String newAccountOpencommand = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ";
    static String extContainerName = "\"ext+container:name=";
    static String containerOpenString = "";
    private static final Logger log = Logger.getLogger(InviteSendRunner.class.getName());
    static Path  logFile;
    static String LOGHEADER = "Start Index, Invite Number, Tab Number, Next start, Duration (min), Vpn Server";
    static String[] vpns = {"local", "be", "ca", "de", "local", "fr", "hgkg", "local", "in", "sp", "us", "local"};
    static String lastVpn = "local";
    final static int VPNCHANGECOUNT = 6; // We choose 6 instead of 5 because we start counting from 1
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
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                                    .withLocale(Locale.FRANCE)
                                    .withZone(ZoneId.systemDefault());

        LocalDateTime ldt = LocalDateTime.now();
        String logFileString = "C:\\Users\\parfait\\Programming\\AccountMail\\logs"+formatter.format(ldt)
                .replace("/", "-").replace(":", "-")+".txt";
        logFile = Paths.get(logFileString);
        try {
            Files.writeString(logFile, LOGHEADER, 
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path accountPath = Paths.get("C:\\Users\\parfait\\Programming\\AccountMail\\accounts.txt");
        Path urlPath = Paths.get("C:\\Users\\parfait\\Programming\\AccountMail\\urls.txt");
        sendGenerator(accountPath, urlPath, urlToOpen);
    }

    private static void sendGenerator(Path accountPath, Path urlPath, boolean urlToOpen) {
        List<String> accounts, randomUrls;
        try (   
            Stream<String> lines0 = Files.lines(accountPath);
            Stream<String> lines1 = Files.lines(urlPath);
        ) {
            randomUrls = lines1.collect(Collectors.toList()); // list of accounts
            int nbTotalRandomUrl = randomUrls.size();
            
            accounts = lines0.collect(Collectors.toList()); // list of accounts
            accounts.remove(0); // remove first line of the file which is the title header
            int nbTotalAccount = accounts.size();

            // We redefine startIndex and nbInvite just to get the randomness
            startIndex = ThreadLocalRandom.current().nextInt(2, nbTotalAccount); 
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
                String[] disconnect = {"C:\\Program Files\\OpenVPN\\bin\\openvpn-gui.exe", "--command", "disconnect_all"};
                Runtime.getRuntime().exec(disconnect);
                while (true) {
                    Instant start = Instant.now();
                    changeVpn();
                    nbInvite = ThreadLocalRandom.current().nextInt(0, 4);
                    startIndex = counter + 2;
                    index = startIndex - 2;
                    counter = startIndex - 2;
                    int counterLimit = counter + nbTotalAccount;
                    int nbTab = 0;
                    containerOpenString = "";
                    while(index < startIndex + nbInvite && counter < counterLimit) {
                        String[] values = accounts.get(counter%nbTotalAccount).split(",");
                        if(isPrintable(values)) {
                            String containerName = values[0];
                            int randomNum = ThreadLocalRandom.current().nextInt(0, nbTotalRandomUrl);
                            String randomUrl = randomUrls.get(randomNum);
                            System.out.println("    randomUrl "+randomUrl+" lastVpn "+lastVpn);
                            if(randomUrl.contains("facebook.com")) {
                                if(lastVpn.equals("local")) {// we open a facebook page only if we are connecting from home country
                                    containerOpenString += extContainerName + containerName+"&url="+randomUrl+"\" ";
                                    for(String url : urls) {
                                        containerOpenString += extContainerName + containerName+"&url="+url+"\" ";
                                    }
                                } else {
                                    containerOpenString += extContainerName + containerName+"&url="+randomUrl+"\" ";
                                }
                            } else {
                                containerName = containerName+"#"+ThreadLocalRandom.current().nextInt(0, 1000);
                                containerOpenString += extContainerName + containerName+"&url="+randomUrl+"\" ";
                            }
                            index++;
                        }
                        counter++;
                    }
                    containerOpenString = containerOpenString.substring(0, containerOpenString.length()-1); // The last blanc (" ") must be deleted to avoid problem in the thread running 
                    System.out.println("    containerOpenString "+containerOpenString);
                    String[] commands = {firefoxExec, containerOpenString};
                    nbTab = containerOpenString.split("url", -1).length;
                    // CommandRunner commandRunner = new CommandRunner(commands, 2*nbUrl);
                    // Pass it to a Thread object
                    // Thread thread = new Thread(commandRunner);
                    // Start the thread
                    // thread.start();
                    Runtime.getRuntime().exec(commands);
                    try {
                        TimeUnit.MINUTES.sleep(ThreadLocalRandom.current().nextInt(2, 3)); // the next loop should last 30 seconds times nbTab, that's 1/2 minutes * nbTab

                         //creating a constructor of the Robot class  
                         Robot robot = new Robot();   
                         //pressing key by invoking the keyPress() method  
                         robot.keyPress(KeyEvent.VK_F5);   
                         Thread.sleep(200);
                         robot.keyRelease(KeyEvent.VK_F5);   
                         //delay of 5 miliseconds after each key press  
                         TimeUnit.SECONDS.sleep(10);
                         for(int i = 0; i < nbTab; i++) { 
                            robot.keyPress(KeyEvent.VK_CONTROL);   
                            robot.keyPress(KeyEvent.VK_TAB); 
                            Thread.sleep(200);
                            robot.keyRelease(KeyEvent.VK_CONTROL);  
                            robot.keyRelease(KeyEvent.VK_TAB);
                            TimeUnit.SECONDS.sleep(3);
                            robot.keyPress(KeyEvent.VK_F5);   
                            Thread.sleep(200);
                            robot.keyRelease(KeyEvent.VK_F5);   
                            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(7, 10));
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

                        // String[] killCommand = {"WMIC.exe", "process", "where", "name=\"firefox.exe\"", "call", "terminate"}; // consider changing this code with the use of CTRL-W
                        // Runtime.getRuntime().exec(killCommand);
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(20, 60));
                        Instant finish = Instant.now();
                        System.out.println("\nStart Index "+startIndex+" NbInvite "+ nbInvite+ " nbTab "+nbTab+ " next start: " + (counter + 2)
                                +" Duration "+ Duration.between(start, finish).toMinutes()+" Vpn : "+lastVpn+"\n"); // In accounts.txt, the first account start with index = 2
                        try {
                            Files.writeString(logFile, "\n"+startIndex + ", " + nbInvite + ", " + nbTab + ", " + (counter + 2) + ", " 
                                + Duration.between(start, finish).toMinutes()+", "+lastVpn, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException | AWTException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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

    private static void changeVpn() {
        int randomVpnNum = ThreadLocalRandom.current().nextInt(0, vpns.length);
        try {
            if(vpns[randomVpnNum].equals("local")) {
                if(!lastVpn.equals("local")) {
                    String[] disconnect = {"C:\\Program Files\\OpenVPN\\bin\\openvpn-gui.exe", "--command", "disconnect", lastVpn};
                    Runtime.getRuntime().exec(disconnect);
                    TimeUnit.SECONDS.sleep(5);
                }
                lastVpn = "local";
            } else {
                String vpnServer = vpns[randomVpnNum]+"-vpnserver";
                String[] connect = {"C:\\Program Files\\OpenVPN\\bin\\openvpn-gui.exe", "--command", "connect", vpnServer};
                if(lastVpn.contains("vpnserver")){
                    String[] disconnect = {"C:\\Program Files\\OpenVPN\\bin\\openvpn-gui.exe", "--command", "disconnect", lastVpn};
                    Runtime.getRuntime().exec(disconnect);
                    TimeUnit.SECONDS.sleep(5);
                }
                Runtime.getRuntime().exec(connect);            
                lastVpn = vpnServer;
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
}
