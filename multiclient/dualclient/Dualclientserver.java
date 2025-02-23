package dualclient;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Dualclientserver {
    private static final int PORT = 12345;   
    private static final int NUM_THREADS = 2; 
    
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS); 
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            List<ClientHandler> clients = new ArrayList<>();
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A client connected.");
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                executorService.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private List<ClientHandler> clients;
        private String clientName;
        
        public ClientHandler(Socket socket, List<ClientHandler> clients) {
            this.clientSocket = socket;
            this.clients = clients;
        }
        
        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                writer.println("Welcome! Please enter your name:");
                clientName = reader.readLine();
                writer.println("Hello " + clientName + "! You are now connected.");
                
                synchronized (clients) {
                    clients.add(this);
                }
                
                String message;
                while ((message = reader.readLine()) != null) {
                    //System.out.println(clientName + " says: " + message);
                    // Broadcast the message to other clients
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    synchronized (clients) {
                        clients.remove(this);
                    }
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client != this) {
                        client.sendMessage(message);
                    }
                }
            }
        }
        
        // Send a message to this client
        private void sendMessage(String message) {
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
