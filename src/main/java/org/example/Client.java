package org.example;

import java.io.*;
import java.net.*;

public class Client {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter out;
    private BufferedReader keyboard;
    private static boolean isConnected = false;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            isConnected = true;
            System.out.println(ANSI_PURPLE +
                    "    ____      __    __   _____     _    _                   _             _  \n" +
                    "   |___      / /   /_ | | ____|   | |  | |                 | |           | | \n" +
                    "     __) |  / /_    | | | |__     | |__| |   ___    _   _  | |   __ _    | | \n" +
                    "    |__ <  |  _     | | |___      |  __  |  / _    | | | | | |  / _` |   | | \n" +
                    "    ___) | | (_) |  | |  ___) |   | |  | | | (_) | | |_| | | | | (_| |   |_| \n" +
                    "   |____/    ___/   |_| |____/    |_|  |_|   ___/    __,_| |_|   __,_|   (_) \n");
            System.out.print("Entrez votre nom: " + ANSI_RESET);
            String userName = keyboard.readLine();
            out.println(userName);

            new Thread(new ReadMessage()).start();

            String message;
            while (true) {
                message = keyboard.readLine();
                out.println(message);
                if (message.equalsIgnoreCase("exit")) {
                    stopConnection();
                    System.out.println(ANSI_PURPLE + "Vous etes deconnecte du hot chat" + ANSI_RESET);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadMessage implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() throws IOException {
        if (input != null) input.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        isConnected = false;
    }

    public static void main(String[] args) {
        while (true) {
            if (!isConnected) {
                new Client("localhost", 1234);
            }
        }
    }
}
