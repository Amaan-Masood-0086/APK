package com.example.p.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents a single client connection
 */
public class ClientConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;
    private String clientIp;
    private long connectedAt;
    private boolean isConnected;

    public ClientConnection(Socket socket, String clientId) throws IOException {
        this.socket = socket;
        this.clientId = clientId;
        this.clientIp = socket.getRemoteSocketAddress().toString();
        this.connectedAt = System.currentTimeMillis();
        this.isConnected = true;
        
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        if (out != null && isConnected) {
            out.println(message);
        }
    }

    public String readMessage() throws IOException {
        if (in != null && isConnected) {
            return in.readLine();
        }
        return null;
    }

    public void close() {
        try {
            isConnected = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected && socket != null && socket.isConnected() && !socket.isClosed();
    }

    // Getters
    public String getClientId() { return clientId; }
    public String getClientIp() { return clientIp; }
    public long getConnectedAt() { return connectedAt; }
    public Socket getSocket() { return socket; }
}
