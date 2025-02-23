package multiclient;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 5000;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is waiting for clients...");

            while (true) { // Keep accepting clients
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client trying to connect...");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                synchronized (clients) {
                    clients.add(clientHandler);
                }
                clientHandler.start(); // Start a new thread for this client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all connected clients
    public static void sendToAll(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) { // Don't send the message back to the sender
                    client.sendMessage(message);
                }
            }
        }
    }

    // Remove client when they disconnect
    public static void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
        }
    }
}

// Handles each client in a separate thread
class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask for client name
            out.println("Enter your name: ");
            clientName = in.readLine();

            if (clientName == null || clientName.trim().isEmpty()) {
                clientName = "Anonymous";
            }

            System.out.println(clientName + " connected!");
            Server.sendToAll(clientName + " joined the chat!", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println(clientName + " disconnected.");
                    Server.sendToAll(clientName + " left the chat.", this);
                    break;
                }
                // Remove or comment out this line if you don't want the server to print messages:
                // System.out.println(clientName + ": " + message);  
            
                Server.sendToAll(clientName + ": " + message, this);
            }      
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClient(this);
        }
    }

    // Send a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }
}
