package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    public String userName;
    private String userColor;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_MAIN_COLOR = "\u001B[35m";

    private static final List<String> colors = Collections.synchronizedList(new ArrayList<>(Arrays.asList(
            "\u001B[31m", // Red
            "\u001B[32m", // Green
            "\u001B[33m", // Yellow
            "\u001B[34m", // Blue
            "\u001B[35m", // Purple
            "\u001B[36m" // Cyan
    )));

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            userName = in.readLine();
            userColor = assignColor();
            Server.broadcastMessage(userColor + userName + " a rejoint le chat" + ANSI_RESET, this);
            System.out.println("New connected user: " + userName);
            out.println(ANSI_MAIN_COLOR + "Bienvenue dans le hot chat " + userName + " !" + ANSI_RESET);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                Server.broadcastMessage(userColor + userName + ": " + message + ANSI_RESET, this);
                System.out.println(userName + " write: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Server.broadcastMessage(userColor + "L'utilisateur " + userName + " a quitté le chat" + ANSI_RESET, this);
                Server.removeClient(this);
                releaseColor(userColor);
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private String assignColor() {
        synchronized (colors) {
            if (colors.isEmpty()) {
                return "\u001B[37m"; // Default to white if no colors are available
            }
            return colors.remove(0);
        }
    }

    private void releaseColor(String color) {
        synchronized (colors) {
            colors.add(color);
        }
    }
}