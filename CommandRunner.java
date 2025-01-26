import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner implements Runnable {
    private String[] command;
    private int nbUrl = 0;

    // Constructor to accept an argument
    public CommandRunner(String[] command, int nbUrl) {
        this.command = command;
        this.nbUrl = nbUrl;
    }


    @Override
    public void run() {
        try {
            Runtime.getRuntime().exec(command);
            Thread.sleep(nbUrl * 10000);
            String[] killCommand = {"WMIC.exe", "process", "where", "name=\"firefox.exe\"", "call", "terminate"};
            Runtime.getRuntime().exec(killCommand);
            // String[] wmicCmd = {"WMIC.exe", "process", "get", "description,", "processid"};
            // List<String> results = output(wmicCmd, "firefox");
            // for(String result : results) {
            //     String[] r = result.split("\\s+");
            //     if(r.length != 2)
            //         System.out.print("WMIC output not equal to 2");;
            //     int pid = Integer.parseInt(r[1]);
            //     System.out.println("pid "+pid);
            //     String[] killCommand = {"taskkill", "/F", "/PID", ""+pid};
            //     //Runtime.getRuntime().exec(killCommand );
            // }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Program has finished execution in the separate thread.");
    }

    public static List<String> output(String[] command, String processName) {
        StringBuilder b = new StringBuilder();
        List<String> result = new ArrayList<>();
        try {

            // Initialize a process object to run 
            // command and its parameters inside a cmd window, return a list of <proces name, pid>
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if(s.contains(processName))
                    result.add(s);
            }
            // Read any errors from the attempted command
            if(stdError.ready()) {
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
                System.exit(1);
            }

        } catch (Exception ex) {
            b.append(ex.toString());
        }
        return result;
    }
}