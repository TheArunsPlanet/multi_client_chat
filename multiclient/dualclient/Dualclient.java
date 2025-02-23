package dualclient;

import java.io.*;
import java.net.*;

public class Dualclient {
    private static final String SERVER_ADDRESS = "localhost"; // or the IP address of the server
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            // Receive initial welcome message from server
            String initialMessage = reader.readLine();  // Rename to avoid conflict with lambda variable
            System.out.println(initialMessage);
            
            // Prompt user for their name
            System.out.print("Enter your name: ");
            String name = consoleReader.readLine();
            writer.println(name);  // Send the name to the server
            
            // Start a thread to listen for messages from the server
            Thread listenerThread = new Thread(() -> {
                try {
                    String messageFromServer;  // Use a different variable name
                    while ((messageFromServer = reader.readLine()) != null) {
                        System.out.println(messageFromServer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            listenerThread.start();
            
            // Allow the user to send messages
            String message;
            while ((message = consoleReader.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    writer.println(message);  // Send exit message to server
                    break;  // Break the loop to exit the program
                }
                writer.println(message);
            }
            
            // Close resources after exit
            socket.close();
            System.out.println("Disconnected from the server.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
