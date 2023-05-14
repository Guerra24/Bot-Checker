package com.ckmu32.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class OnlineBotsAPI {
    private final static String ONLINE_BOTS_URL = "https://api.twitchinsights.net/v1/bots/online";

    public List<String> getOnlineBots() {
        var botList = new ArrayList<String>();
        var client = HttpClient.newBuilder().build();
        try {
            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(ONLINE_BOTS_URL))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            var response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200)
                return botList;

            var mapper = new ObjectMapper();
            var jsonNode = mapper.readTree(response.body());

            if (jsonNode == null || jsonNode.isEmpty())
                return botList;

            var botNode = jsonNode.get("bots");

            if (botNode == null || botNode.isEmpty() || !botNode.isArray())
                return botList;

            var bots = (ArrayNode) botNode;
            if (bots == null || bots.isEmpty())
                return botList;

            bots.forEach(bot -> botList.add(bot.get(0).asText()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return botList;
    }
}
