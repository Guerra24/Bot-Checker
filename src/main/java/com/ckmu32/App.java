package com.ckmu32;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import com.ckmu32.config.ConfigVariables;
import com.ckmu32.service.TwitchChatService;

/**
 * Hello world!
 *
 */
public class App {
    private static final String BOT_CONFIG_PROPERTIES = "BotConfig.properties";
    
    public static void main(String[] args) {
        System.out.println("Starting bot");
        // Variables required for the process.
        var configVariables = loadConfiguration();

        // Check argumetns and if passed, set it on the properties.
        new TwitchChatService(
                configVariables.getUser(),
                configVariables.getToken(),
                configVariables.getClientID(),
                configVariables.getChattersToIgnore(),
                configVariables.getChannelsToJoin());
        while (true) {

        }
    }

    private static ConfigVariables loadConfiguration() {
        var configVariables = new ConfigVariables();

        // Check if we have the env varaible active to read from there.
        if ("Y".equals(System.getenv("BOT_CHECKER_ACTIVE"))) {
            System.out.println("Using env");
            var splittedChatters = System.getenv("CHATTERS_TO_IGNORE").split(",");
            if (splittedChatters != null)
                Arrays.stream(splittedChatters)
                        .forEach(chatter -> configVariables.getChattersToIgnore().add(chatter));

            var splittedChannels = System.getenv("CHANNELS_TO_JOIN").split(",");
            if (splittedChannels != null)
                Arrays.stream(splittedChannels)
                        .forEach(channel -> configVariables.getChannelsToJoin().add(channel));

            configVariables.setUser(System.getenv("BOT_USER"));
            configVariables.setToken(System.getenv("BOT_TOKEN"));
            configVariables.setClientID(System.getenv("BOT_CLIENT_ID"));
            return configVariables;
        }

        System.out.println("Using properties file");
        var properties = new Properties();

        try {
            properties.load(new FileInputStream(BOT_CONFIG_PROPERTIES));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return configVariables;
        }

        if (properties.isEmpty())
            return configVariables;

        var splittedChatters = properties.getProperty("chattersToIgnore").split(",");
        if (splittedChatters != null)
            Arrays.stream(splittedChatters)
                    .forEach(chatter -> configVariables.getChattersToIgnore().add(chatter));

        var splittedChannels = properties.getProperty("channelsToJoin").split(",");
        if (splittedChannels != null)
            Arrays.stream(splittedChannels)
                    .forEach(channel -> configVariables.getChannelsToJoin().add(channel));

        configVariables.setUser(properties.getProperty("user"));
        configVariables.setToken(properties.getProperty("token"));
        configVariables.setClientID(properties.getProperty("clientID"));
        return configVariables;
    }
}
