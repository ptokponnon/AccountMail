import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE;
import static net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel.RIL_WORD;
import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;

public class Connect {
    private static final String TESSDATA = "C:\\Program Files\\Tesseract-OCR\\tessdata";
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
    static int nbOpenTab;
    static String sexe = "";
    static boolean mustBeNew = false;
    static List<String> urls;
    static List<String> newAccounts;
    static boolean urlToOpen = false;
    static String newAccountOpencommand = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" -new-tab ";
    static String firefoxExec = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String extContainerName = "\"ext+container:name=";
             //creating a constructor of the Robot class  
    static Robot robot;   
    volatile static boolean paused = false;
    private static boolean isMobile = false;
    private static String currentFirstname = "";
    private static String currentLastname = "";
    private static String currentEmail = "";
    private static LocalDate currentBirthDay;
    private static String currentGender = "";
    private static String currentCountry = "";
    private static List<String> commands = new ArrayList<>();
    private static String currentProfileName = "";
    private static Tesseract tesseract;
    private static double screenWidth;
    private static double screenHeight;

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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.writeString(Paths.get("log.txt"), "Program is terminating. Next start: "+startIndex+ " Counter: "+counter, StandardOpenOption.APPEND);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }));

        try {
            robot = new Robot();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Thread inputThread = new Thread(new InputReader());
        inputThread.start();
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
                    nbOpenTab = 0;
                    startIndex = counter + 2;
                    index = startIndex - 2;
                    counter = startIndex - 2;
                    int counterLimit = counter + nbTotalAccount;
                    System.out.println("StartIndex: "+startIndex+" counter:"+counter);
                    while(index < startIndex + nbInvite && counter < counterLimit) {
                        checkInterrupt();
                        String account = accounts.get(counter%nbTotalAccount);
                        String[] values = account.split(",");
                        if(isPrintable(account, values)) {
                            currentProfileName = values[0];
                            String[] accountNames = currentProfileName.split(" ");
                            currentFirstname = accountNames[0];
                            currentLastname = accountNames[1];
                            currentEmail = values[2];
                            currentCountry = values[3];
                            currentBirthDay = LocalDate.of(Integer.parseInt(values[6]), Integer.parseInt(values[5]), Integer.parseInt(values[4]));
                            automate();
                            index++;
                        }
                        counter++;
                    }
                    System.out.println("\nnext start: " + (counter + 2)); // In accounts.txt, the first account start with index = 2
                    paused = true;
                    checkInterrupt();
                    // animateTab(nbOpenTab);
                    if(nbOpenTab >= nbInvite) {
                        closeTabs();
                    }
                }
            } else {
                String containerOpenString = extContainerName;
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
                            if(isPrintable(l, values)) {
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

    private static boolean isPrintable(String line, String[] values) {
        if(line.contains("Vérouillé")) {
            return false;
        }
        
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

    public static void select(int nbstep) {
        // // Simule un appui sur ALT + DOWN pour ouvrir la liste (fonctionne sur certains composants)
        // robot.keyPress(KeyEvent.VK_ALT);
        // robot.keyPress(KeyEvent.VK_DOWN);
        // robot.keyRelease(KeyEvent.VK_DOWN);
        // robot.keyRelease(KeyEvent.VK_ALT);

        // Attendre que la liste soit ouverte
        robot.delay(10*ThreadLocalRandom.current().nextInt(40, 60));

        int keyPress = 0;
        if(nbstep>0){
            keyPress = KeyEvent.VK_DOWN;
        } else {
            keyPress = KeyEvent.VK_UP;            
        }
        for (int i = 0; i < Math.abs(nbstep); i++) {
            robot.keyPress(keyPress);
            robot.keyRelease(keyPress);
            robot.delay(10*ThreadLocalRandom.current().nextInt(40, 60));
        }
        // Appuyer sur "Entrée" pour sélectionner l'élément
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public static void automate () {
        // if(!currentEmail.contains("allodfs.com"))
        //     return;
        nbOpenTab++;
        // launchDefaultUrl();

        // changeMailAdress();

        
        // executeConnect();
        
        createAccount();

        // configureAccount();
        robot.delay(5000);
        isMobile = false;
    }

    private static void launchDefaultUrl() {
        try {
            String containerOpenString = "";
            for(String url : urls) {
                if(ThreadLocalRandom.current().nextInt(10) < 5) {//launch in mobile form
                    containerOpenString = extContainerName + currentProfileName+"&url=https://google.com\"";
                    String[] command = {firefoxExec, containerOpenString};
                    Runtime.getRuntime().exec(command);
                    lauchMobile(url);
                } else {
                    containerOpenString = extContainerName + currentProfileName+"&url="+url+"\"";
                    String[] command = {firefoxExec, containerOpenString};
                    Runtime.getRuntime().exec(command);
                    
                }                            
                robot.delay(5000);
                // click(1047, 776); // agree cookies
                // tabControl(10);
                // clickActivatedButtonsOnPage();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void configureAccount() {
        lauchFirefox("https://www.facebook.com/privacy/unified_checkup");
        robot.delay(100*ThreadLocalRandom.current().nextInt(40, 60));
        // Qui peut voir ce que vous partagez
        click(580, 450);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // Continuer
        click(915, 802);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // Année : Moi uniquement 
        click(910, 520);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // Public : Tout le monde sur ou en dehors de Facebook
        click(903, 660);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // click on Enregistrer
        click(1100, 780);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // Click on Suivant 
        click(1100, 860);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        
        // Click on Qui peut voir vos futures publications ?
        click(900, 425);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        
        // Click Public : Tout le monde sur ou en dehors de Facebook 
        click(900, 460);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        
        // Click Enregistrer
        click(1100, 780);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        
        // Click Suivant
        click(1110, 720);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        
        // Click Examiner les identifications que d’autres personnes ajoutent à vos publications avant qu’elles n’apparaissent sur Facebook ?
        click(1150, 540);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // click Suivant 
        click(1110, 860);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter suivant 
        click(1110, 700);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // Click Review another topic
        click(920, 740);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // // validate 
        // robot.keyPress(KeyEvent.VK_ENTER);  
        // robot.keyRelease(KeyEvent.VK_ENTER);  
        // robot.delay(100*ThreadLocalRandom.current().nextInt(50, 70));
    }

    private static void lauchFirefox(String url) {
        try {
            String containerOpenString = extContainerName + currentProfileName+"&url="+url+"\"";
            String[] command = {firefoxExec, containerOpenString};
            Runtime.getRuntime().exec(command);
            // 2. Initialiser Tesseract
            tesseract = new Tesseract();
            tesseract.setDatapath(TESSDATA); // dossier contenant eng.traineddata
            tesseract.setLanguage("fra");
            tesseract.setVariable("user_defined_dpi", "110");
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenWidth = screenSize.getWidth();
            screenHeight = screenSize.getHeight();

            robot.delay(5000);  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createAccount() {
        lauchFirefox(defaultUurl);
        //click to create account
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        click(0, 0, 100, 100, "Créer un nouveau compte");
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // click to get focus 
        click(450, 145);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter firstname 
        click(850, 345);
        pasteText(currentFirstname);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter lastname 
        click(1000, 345);
        pasteText(currentLastname);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter day of birth 
        click(800, 415);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        LocalDate today = LocalDate.now();
        select(currentBirthDay.getDayOfMonth() - today.getDayOfMonth());
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter month of birth 
        click(930, 415);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        select(currentBirthDay.getMonthValue() - today.getMonthValue());
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter year of birth 
        click(1080, 415);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        select(today.getYear() - currentBirthDay.getYear()); // current year shall always be greater than currentbirthyear.
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter gender 
        click(820 + 100*(currentGender.contains("M") ? 1 : 0), 490);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter email 
        click(900, 535);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        pasteText(currentEmail);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        // enter  
        click(920, 770);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        pasteText("Goerte02");
        // validate 
        robot.keyPress(KeyEvent.VK_ENTER);  
        robot.keyRelease(KeyEvent.VK_ENTER);  
        robot.delay(100*ThreadLocalRandom.current().nextInt(50, 70));
        // save login
        click(620, 295); // save login and password
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));        
    }

    /**
     * 
     * @param x1 : upper-left corner x - coordinate
     * @param y1 : upper-left corner y - coordinate
     * @param x2 : lower-right corner x - coordinate
     * @param y2 : lower-right corner y - coordinate
     * @param labelText : the text to search for
     */
    private static void click(int x1, int y1, int x2, int y2, String labelText) {
        int width = Math.abs(x2 - x1), heigth = Math.abs(y2 - y1);
        Rectangle labelArea = findTextInScreenArea(labelText, new Rectangle(x1, y1, width, heigth));
        if (labelArea != null)
            click((int)labelArea.getCenterX(), (int)labelArea.getCenterY());
    }

    public static Rectangle findTextInScreenArea(String searchText, Rectangle area) {
        List<Word> words;
        while (area.height < screenHeight || area.width < screenWidth) {
                
            // 1. Capturer la zone de l'écran
            BufferedImage capture = robot.createScreenCapture(area);

            // File outputFile = new File("output.png");
            // try {
            //     ImageIO.write(capture, "png", outputFile);
            // } catch (IOException e) {
            //     // TODO Auto-generated catch block
            //     e.printStackTrace();
            // }
            try {
                String result = tesseract.doOCR(capture);
                System.out.println("Resultat : " + result + "\n"+result.toLowerCase().contains(searchText.toLowerCase()));
            } catch (TesseractException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(searchText.split(" ").length == 1) { // si le texte à rechercher est un mot
                words = tesseract.getWords(capture, RIL_WORD);
            } else { // si le texte à rechercher est un groupe de mot (il es nécessaire qu'il soit sur une ligne)
                words = tesseract.getWords(capture, RIL_TEXTLINE);
            }
            // 3. Obtenir tous les mots avec leur position
            // System.out.println("Text Found for "+searchText+" "+words);
            System.out.println("Area :"+area);
            if(words != null)
                for (Word word : words) {
                    String text = word.getText().trim();
                    if (text.equalsIgnoreCase(searchText)) {
                        Rectangle wordBox = word.getBoundingBox();
                        // Ajouter le décalage de la zone capturée
                        wordBox.translate(area.x, area.y);
                        return wordBox; // texte trouvé : on retourne sa position
                    }
                }
            area.grow(100, 100); // enlarge the search zone
            if(area.getX() < 0)
                area.setLocation(0, (int)area.getY());
            if(area.getY() < 0)
                area.setLocation((int)area.getX(), 0);
        }
        System.out.println("No corresponding text found for : "+searchText);
        return null; // Texte non trouvé
    }

    private static void changeMailAdress() {
        lauchFirefox("https://accountscenter.facebook.com/personal_info/contact_points");
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        click(840, 550);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        click(850, 660);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        click(950, 480);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        pasteText(currentEmail.replace(".com", ".win"));
        robot.delay(100*ThreadLocalRandom.current().nextInt(50, 70));
        click(1195, 675);
                
        robot.delay(100*ThreadLocalRandom.current().nextInt(10, 20));
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyRelease(KeyEvent.VK_TAB);
    
        robot.delay(100*ThreadLocalRandom.current().nextInt(10, 20));
        robot.keyPress(KeyEvent.VK_ENTER);  
        robot.delay(10*ThreadLocalRandom.current().nextInt(10, 30));
        robot.keyRelease(KeyEvent.VK_ENTER);  
        robot.delay(100*ThreadLocalRandom.current().nextInt(40, 50));
    }

    private static void executeConnect() {
        if(country.contains("belgique")){
            click(1047, 776); // agree cookies
            robot.delay(3000);    
        }
        lauchFirefox(defaultUurl);
        click(1120, 320); // address field
        robot.delay(3000);
        pasteText(currentEmail);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
        click(1200, 380);
        
        robot.delay(3000);
        robot.keyPress(KeyEvent.VK_ENTER);  
        robot.keyRelease(KeyEvent.VK_ENTER);  
        robot.delay(100*ThreadLocalRandom.current().nextInt(40, 50));

        // Accept to trust this device and save password 
        click(900, 670);
        robot.delay(100*ThreadLocalRandom.current().nextInt(20, 30));
        click(750, 415);
        robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50));
    }

    private static void clickActivatedButtonsOnPage() {
        // click on message button
        if(!isMobile){ // if ismobile, no need to click
            click(1700, 170);
        }
        robot.delay(100*ThreadLocalRandom.current().nextInt(20, 30));

        // click on notification button
        if(isMobile){
            click(1015, 270);          
        } else {
            robot.keyPress(KeyEvent.VK_F5);
            robot.keyRelease(KeyEvent.VK_F5);   
            robot.delay(100*ThreadLocalRandom.current().nextInt(30, 50)); 
            click(1750, 170);
        }
        robot.delay(100*ThreadLocalRandom.current().nextInt(50, 70));

    }

    private static void tabControl(int i) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 5));
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 2));
        robot.keyRelease(KeyEvent.VK_CONTROL);    
            
        robot.delay(i*1000);       

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 2));
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyRelease(KeyEvent.VK_CONTROL);  
    }

    private static void lauchMobile(String url) {
        robot.delay(5000);       
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_M);
        robot.keyRelease(KeyEvent.VK_M);
        robot.delay(500);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_CONTROL);        
        robot.delay(3000);
        robot.keyPress(KeyEvent.VK_F5);
        robot.keyRelease(KeyEvent.VK_F5);    
        
        robot.delay(5000);       
        click(610, 90);
        pasteText(url);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public static void pasteText (String text){
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);        
    }

    public static void animateTab(int nbTab) {
        for(int i=0; i<nbTab; i++) {
            checkInterrupt();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_CONTROL); 
            for(int j = 0; j < ThreadLocalRandom.current().nextInt(10, 15); j++){
                //scroll and wait a bit to give the impression of smooth scrolling
                robot.mouseWheel(1);
                robot.delay(1000*ThreadLocalRandom.current().nextInt(1, 5));
            }
        }
    }

    private static void checkInterrupt() {
        while (paused) {
            robot.delay(5000);
        }
    }

    public static void closeTabs() {
        for(int i=0; i<nbOpenTab; i++) {
            checkInterrupt();
            robot.delay(5000);
            robot.keyPress(KeyEvent.VK_CONTROL);   
            robot.keyPress(KeyEvent.VK_W); 
            robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
            robot.keyRelease(KeyEvent.VK_CONTROL);  
            robot.keyRelease(KeyEvent.VK_W);
            robot.delay(100*ThreadLocalRandom.current().nextInt(1, 3));
        }
        nbOpenTab = 0;
    }

    static public void pause() {
        paused = true;
    }

    static public void resume() {
        paused = false;
    }
}