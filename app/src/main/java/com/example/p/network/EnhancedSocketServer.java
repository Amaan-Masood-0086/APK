package com.example.p.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Enhanced Socket Server that supports multiple clients
 */
public class EnhancedSocketServer {
    private static final String TAG = "EnhancedSocketServer";
    private static ServerSocket serverSocket;
    private static ConcurrentMap<String, ClientConnection> clients = new ConcurrentHashMap<>();
    private static boolean isRunning = false;
    private static int port;
    private static MessageListener messageListener;
    private static ConnectionListener connectionListener;

    public interface MessageListener {
        void onMessageReceived(String clientId, String message);
    }

    public interface ConnectionListener {
        void onClientConnected(String clientId);
        void onClientDisconnected(String clientId);
    }

    public static void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public static void setConnectionListener(ConnectionListener listener) {
        connectionListener = listener;
    }

    /**
     * Start server on specified port
     */
    public static void startServer(int serverPort) {
        if (isRunning) {
            Log.w(TAG, "Server already running");
            return;
        }

        port = serverPort;
        new Thread(() -> {
            try {
                closeServer();
                serverSocket = new ServerSocket(port);
                isRunning = true;
                Log.d(TAG, "Server started on port " + port);

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String clientId = clientSocket.getRemoteSocketAddress().toString();
                        Log.d(TAG, "New client connected: " + clientId);

                        ClientConnection connection = new ClientConnection(clientSocket, clientId);
                        clients.put(clientId, connection);

                        if (connectionListener != null) {
                            connectionListener.onClientConnected(clientId);
                        }

                        // Handle messages from this client
                        handleClientMessages(clientId, connection);

                    } catch (IOException e) {
                        if (isRunning) {
                            Log.e(TAG, "Error accepting client", e);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error starting server", e);
                isRunning = false;
            }
        }).start();
    }

    /**
     * Handle messages from a specific client
     */
    private static void handleClientMessages(String clientId, ClientConnection connection) {
        new Thread(() -> {
            try {
                String message;
                while (connection.isConnected() && (message = connection.readMessage()) != null) {
                    if (messageListener != null) {
                        messageListener.onMessageReceived(clientId, message);
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "Client disconnected: " + clientId);
            } finally {
                // Remove client on disconnect
                clients.remove(clientId);
                connection.close();
                if (connectionListener != null) {
                    connectionListener.onClientDisconnected(clientId);
                }
            }
        }).start();
    }

    /**
     * Send message to specific client
     */
    public static void sendToClient(String clientId, String message) {
        ClientConnection connection = clients.get(clientId);
        if (connection != null && connection.isConnected()) {
            connection.sendMessage(message);
        } else {
            Log.w(TAG, "Client not found or disconnected: " + clientId);
        }
    }

    /**
     * Broadcast message to all connected clients
     */
    public static void broadcastMessage(String message) {
        List<String> disconnectedClients = new ArrayList<>();
        
        for (String clientId : clients.keySet()) {
            ClientConnection connection = clients.get(clientId);
            if (connection != null && connection.isConnected()) {
                connection.sendMessage(message);
            } else {
                disconnectedClients.add(clientId);
            }
        }

        // Clean up disconnected clients
        for (String clientId : disconnectedClients) {
            clients.remove(clientId);
        }
    }

    /**
     * Broadcast message to specific clients
     */
    public static void broadcastToClients(List<String> clientIds, String message) {
        for (String clientId : clientIds) {
            sendToClient(clientId, message);
        }
    }

    /**
     * Get list of connected client IDs
     */
    public static List<String> getConnectedClients() {
        return new ArrayList<>(clients.keySet());
    }

    /**
     * Check if server is running
     */
    public static boolean isRunning() {
        return isRunning;
    }

    /**
     * Check if client is connected
     */
    public static boolean isClientConnected(String clientId) {
        ClientConnection connection = clients.get(clientId);
        return connection != null && connection.isConnected();
    }

    /**
     * Close server and all connections
     */
    public static void closeServer() {
        isRunning = false;
        
        // Close all client connections
        for (ClientConnection connection : clients.values()) {
            connection.close();
        }
        clients.clear();

        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing server", e);
        }
        
        serverSocket = null;
        Log.d(TAG, "Server closed");
    }

    /**
     * Get number of connected clients
     */
    public static int getClientCount() {
        return clients.size();
    }
}
