package multiclient;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Ask for the client’s name
            System.out.print("Enter your name: ");
            String clientName = userInput.readLine();
            out.println(clientName); // Send name to the server
            in.readLine();
            // Start a thread to listen for incoming messages
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (!message.startsWith(clientName + ":")) {  // Don't show own messages
                            System.out.println("\r" + message);  // Use "\r" to overwrite "you: "
                            System.out.print("you: "); // Reprint "you:" for user input
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            receiveThread.start();

            // Sending messages
            while (true) {
                System.out.print("you: ");  // Show "you:" before typing
                String message = userInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    break;
                }
                out.println(clientName + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("Could not connect to the server.");
        }
    }
}
