package com.chyureu.playsquad.websocket;

import com.chyureu.playsquad.PlaySquad;
import com.chyureu.playsquad.config.SettingManager;
import com.chyureu.playsquad.events.PlaySquadDonationEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketEventClient {
    // Initialize SettingManager and obtain token
    private SettingManager settingManager = SettingManager.getInstance(PlaySquad.getPlugin());
    private String token = settingManager.getToken();
    private WebSocketClient webSocketClient;
    private final String WEBSOCKET_BASE_URI = "wss://playsquad.gg/ws/event/?token=";

    // Constructor to establish WebSocket connection
    public WebSocketEventClient() {
        try {
            // Create URI for WebSocket connection with token
            URI uri = new URI(WEBSOCKET_BASE_URI + token);

            // Create WebSocket client and define its behavior
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // WebSocket connection established
                    System.out.println("WebSocket connection established.");

                    // Set up a timer to send heartbeat messages periodically
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendHeartbeat();
                        }
                    }, 0, 59000); // Send heartbeat every 59 seconds
                }

                @Override
                public void onMessage(String message) {
                    // Handle incoming WebSocket message
                    System.out.println("Received message: " + message);
                    handleWebSocketMessage(message);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    // WebSocket connection closed
                    System.out.println("WebSocket connection closed.");
                }

                @Override
                public void onError(Exception e) {
                    // Handle WebSocket errors
                    e.printStackTrace();
                }
            };

            // Connect to the WebSocket server
            webSocketClient.connect();
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

    // Send heartbeat message to the server
    private void sendHeartbeat() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            // Prepare and send the heartbeat message
            String heartbeatMessage = "{\"eventType\":\"HEARTBEAT\",\"eventId\":\"" + token + "\"}";
            webSocketClient.send(heartbeatMessage);
            System.out.println("HeartBeatMessage: " + heartbeatMessage);
            return;
        }
        // Close the WebSocket connection if it's not open
        closeWebSocket();
    }

    // Close the WebSocket connection
    public void closeWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    // Handle incoming WebSocket messages
    private void handleWebSocketMessage(String message) {
        try {
            // Parse the JSON message
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(message);

            // Extract relevant fields from the message
            String eventType = jsonNode.get("eventType").asText();
            if (!eventType.equals("DONATE")) {
                // If eventType is not "DONATE", return without further processing
                return;
            }
            String amount = jsonNode.has("amount") ? jsonNode.get("amount").asText() : null;
            String eventId = jsonNode.has("eventId") ? jsonNode.get("eventId").asText() : null;
            String guestName = jsonNode.has("guestNameText") ? jsonNode.get("guestNameText").asText() : null;
            String squadName = jsonNode.has("squadNameText") ? jsonNode.get("squadNameText").asText() : null;
            String clientMessage = jsonNode.has("clientMessageText") ? jsonNode.get("clientMessageText").asText() : null;

            // Dispatch a custom event (PlaySquadDonationEvent) with the extracted data
            Bukkit.getScheduler().runTask(PlaySquad.getPlugin(), () -> {
                PlaySquad.getPlugin().getServer().getPluginManager().callEvent(new PlaySquadDonationEvent(amount, eventId, StringEscapeUtils.unescapeJava(guestName), StringEscapeUtils.unescapeJava(squadName), StringEscapeUtils.unescapeJava(clientMessage)));
            });
        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
        }
    }
}