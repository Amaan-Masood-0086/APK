package com.example.p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private static MessageListener messageListener;

    public static void setOnMessageReceivedListener(MessageListener listener) {
        messageListener = listener;
    }

    public static void startServer(int port, ClientConnectedListener listener) {
        new Thread(() -> {
            try {
                closeServer(); // Clean up any existing connection
                serverSocket = new ServerSocket(port);
                clientSocket = serverSocket.accept();

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                if (listener != null) {
                    listener.onClientConnected();
                }

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (messageListener != null) {
                        messageListener.onMessageReceived(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendMessage(String msg) {
        new Thread(() -> {
            if (out != null) {
                out.println(msg);
            }
        }).start();
    }

    public static void closeServer() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            out = null;
            in = null;
            clientSocket = null;
            serverSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public interface ClientConnectedListener {
        void onClientConnected();
    }
}
