package com.example.p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private static MessageListener messageListener;

    public static void setOnMessageReceivedListener(MessageListener listener) {
        messageListener = listener;
    }

    public interface ConnectionListener {
        void onConnected();
        void onError(Exception e);
    }

    public static void connect(String ip, int port, ConnectionListener listener) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if (listener != null) {
                    listener.onConnected();
                }

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (messageListener != null) {
                        messageListener.onMessageReceived(msg);
                    }
                }

            } catch (IOException e) {
                if (listener != null) {
                    listener.onError(e);
                }
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

    public static void close() {
        try {
            if (socket != null) socket.close();
            socket = null;
            out = null;
            in = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
