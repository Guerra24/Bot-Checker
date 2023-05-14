package com.ckmu32.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ckmu32.api.OnlineBotsAPI;
import com.ckmu32.api.TwitchAPI;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@ClientEndpoint
public class TwitchChatService implements MessageService {
    private static final String BOTS_COMMAND = "!bots";

    private Session session;
    private ExecutorService executor;
    private TwitchAPI twitchAPI;
    private OnlineBotsAPI botsAPI;

    private String user;
    private String token;
    private String host;
    private String channel;
    private String clientID;
    private List<String> chattersToIgnoreList;
    private int port;
    private boolean isJoined;

    public TwitchChatService(String user, String token, String channel, String clientID,
            List<String> chattersToIgnoreList) {
        // Fixed values per Twitch's IRC documentation.
        this.host = "ws://irc-ws.chat.twitch.tv";
        this.port = 80;
        this.user = user;
        this.token = token;
        this.channel = channel;
        this.clientID = clientID;
        this.chattersToIgnoreList = chattersToIgnoreList;
        this.twitchAPI = new TwitchAPI(this.clientID, this.token);
        this.botsAPI = new OnlineBotsAPI();

        try {
            var container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(host + ":" + port));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        try {
            System.out.println("Sending credentials.");
            sendMessage("PASS " + "oauth:" + token);
            sendMessage("NICK " + user.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String messageFromTwitch) {
        processMessage(messageFromTwitch);
    }

    private void processMessage(String message) {
        if (isInvalid(message))
            return;

        if (!isJoined) {
            processJoinLogic(message);
            return;
        }

        if (message.contains(TwitchTags.PING.message)) {// Twitch handshake.
            sendMessage(TwitchTags.PONG.message);
            return;
        }

        if (message.contains("JOIN #" + channel)) {
            processUserJoin(message);
            return;
        }

        // Process user messages on thread.
        executor.execute(() -> {
            var parsedMessage = getMessageFromLine(message);
            if (parsedMessage.isEmpty())
                return;

            // If the message does not start with an ! we return. Since we only process
            // commands for the time being.
            if (!parsedMessage.get().getMessage().startsWith("!"))
                return;

            System.out.println("Command: " + parsedMessage.get().getMessage());

            if (parsedMessage.get().getMessage().startsWith(BOTS_COMMAND)) {
                if (!parsedMessage.get().isPrivileged())
                    return;

                var broadcasterID = twitchAPI.getUserID(channel);
                if (broadcasterID.isEmpty())
                    return;

                var moderatorID = twitchAPI.getUserID(user);
                if (moderatorID.isEmpty())
                    return;

                var botList = botsAPI.getOnlineBots();
                var chattersList = twitchAPI.getChatters(broadcasterID.get(), moderatorID.get());

                var botsBanned = 0;
                var botsDetected = 0;
                var botsBannedUsernames = new StringJoiner(", ");
                var botsDetectedUsernames = new StringJoiner(", ");
                // var botUsernames = new StringJoiner(",");

                for (var chatter : chattersList) {
                    if (!botList.contains(chatter))
                        continue;

                    if (chattersToIgnoreList.contains(chatter))
                        continue;

                    var userToBanID = twitchAPI.getUserID(chatter);
                    if (userToBanID.isEmpty())
                        continue;

                    if (twitchAPI.banChatter(broadcasterID.get(), moderatorID.get(), userToBanID.get(), "Bot")) {
                        botsBanned++;
                        botsBannedUsernames.add(chatter);
                    }

                    botsDetectedUsernames.add(chatter);
                    botsDetected++;
                }
                // In case a bot was not banned.
                if (botsBanned != botsDetected)
                    sendMessageToChat("Bots that were detected: " + botsDetectedUsernames);

                if (botsBanned > 0)
                    sendMessageToChat("Bots that were banned: " + botsBannedUsernames);

                if (isInvalid(botsDetected) && isInvalid(botsBanned))
                    sendMessageToChat("No bots were found, nor banned, everything is clean.");
            }
        });
    }

    private void processJoinLogic(String messageFromTwitch) {
        // Split the message since twitch sends all the messages pre-join in one go.
        var messages = messageFromTwitch.split(System.lineSeparator());
        for (var message : messages) {
            System.out.println("Message for join logic: " + message);

            if (message.contains(TwitchTags.SUCCESSFUL_LOGIN.message)) {
                System.out.println("Connected and sending REQS and MEMBERSHIP.");
                sendMessage(TwitchTags.REQ_TAGS.message);
                sendMessage(TwitchTags.MEMBERSHIP_TAGS.message);
                continue;
            }

            if (message.contains(TwitchTags.READY_TO_JOIN.message)) {
                System.out.println("Sending JOIN.");
                sendMessage("JOIN #" + channel.toLowerCase());
                isJoined = true;
                // Initialize the exuctor to process users commands.
                executor = Executors.newFixedThreadPool(5);
                continue;
            }
        }
    }

    private void processUserJoin(String messageFromTwitch) {
        try {
            var botsBannedUsernames = new StringJoiner(", ");
            var botList = botsAPI.getOnlineBots();
            var broadcasterID = twitchAPI.getUserID(channel);

            if (broadcasterID.isEmpty())
                return;

            var moderatorID = twitchAPI.getUserID(user);
            if (moderatorID.isEmpty())
                return;

            var messages = messageFromTwitch.split(System.lineSeparator());
            for (var message : messages) {

                if (!message.contains("JOIN #" + channel))
                    continue;

                var messageParts = message.split("@");

                for (var part : messageParts) {
                    var domainPosition = part.indexOf(".tmi.twitch.tv");

                    if (isInvalid(domainPosition))
                        continue;

                    var chatter = part.substring(0, domainPosition);

                    if (isInvalid(chatter))
                        continue;

                    if (!botList.contains(chatter))
                        continue;

                    if (chattersToIgnoreList.contains(chatter))
                        continue;

                    var userToBanID = twitchAPI.getUserID(chatter);
                    if (userToBanID.isEmpty())
                        continue;

                    if (twitchAPI.banChatter(broadcasterID.get(), moderatorID.get(), userToBanID.get(), "Bot"))
                        botsBannedUsernames.add(chatter);
                }
            }
            if(!isInvalid(botsBannedUsernames.length()))
                sendMessageToChat("Banned due to being a bot: " + botsBannedUsernames);
        } catch (Exception e) {
            System.out.println("Exception while processing user join: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessageToChat(String message) {
        try {
            session.getBasicRemote().sendText("PRIVMSG #" + channel + " :" + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
