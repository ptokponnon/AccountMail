import java.util.Scanner;

class InputReader implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input thread is waiting for your input:");

        while (true) {
            String userInput = scanner.nextLine();
            System.out.println("You entered: " + userInput);

            switch (userInput) {
                case "p":
                    Connect.pause();
                    break;

                case "r":
                    Connect.resume();
                    break;
                    
                default:
                    break;
            }

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Exiting input thread.");
                break;
            }
        }
        scanner.close();
    }
}