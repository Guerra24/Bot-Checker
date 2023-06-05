package com.ckmu32.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class TwitchAPI {
	private final static String USERS_URL = "https://api.twitch.tv/helix/users";
	private final static String CHATTERS_URL = "https://api.twitch.tv/helix/chat/chatters";
	private final static String BAN_URL = "https://api.twitch.tv/helix/moderation/bans";

	private String clientID;
	private String token;

	public TwitchAPI(String clientID, String token) {
		super();
		this.clientID = clientID;
		this.token = token;
	}

	public Optional<String> getUserID(String userName) {
		var client = HttpClient.newBuilder().build();
		try {
			var request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(USERS_URL + "?login=" + userName))
					.setHeader("Client-Id", clientID)
					.setHeader("Authorization", "Bearer " + token)
					.timeout(Duration.ofSeconds(10))
					.build();
			var response = client.send(request, BodyHandlers.ofString());

			var mapper = new ObjectMapper();
			var jsonNode = mapper.readTree(response.body());

			if (jsonNode == null || jsonNode.isEmpty())
				return Optional.empty();

			var dataNode = jsonNode.get("data");
			if (dataNode == null || dataNode.isEmpty())
				return Optional.empty();

			// Check if we have elements.
			if (!dataNode.elements().hasNext())
				return Optional.empty();

			// Get the first one, since we are requesting an specific user.
			var node = dataNode.elements().next().get("id");

			var broadcasterID = node.asText();
			if (broadcasterID == null || broadcasterID.trim().isEmpty())
				return Optional.empty();

			return Optional.of(broadcasterID);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	public List<String> getChatters(String broadcasterID, String moderatorID) {
		return getChatters(broadcasterID, moderatorID, null, null);
	}

	private List<String> getChatters(String broadcasterID, String moderatorID, List<String> chatterList,
			String cursor) {
		if (chatterList == null)
			chatterList = new ArrayList<String>();

		var client = HttpClient.newBuilder().build();
		try {
			var request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(CHATTERS_URL + "?broadcaster_id=" + broadcasterID +
							"&moderator_id=" + moderatorID +
							((cursor == null || cursor.trim().isEmpty()) ? "" : "&after=" + cursor)))
					.setHeader("Client-Id", clientID)
					.setHeader("Authorization", "Bearer " + token)
					.timeout(Duration.ofSeconds(10))
					.build();

			var response = client.send(request, BodyHandlers.ofString());

			if (response.statusCode() != 200)
				return chatterList;

			var mapper = new ObjectMapper();
			var jsonNode = mapper.readTree(response.body());

			if (jsonNode == null || jsonNode.isEmpty())
				return chatterList;

			var paginatioNode = jsonNode.get("pagination");
			var hasACursor = false;

			if (paginatioNode != null && paginatioNode.has("cursor")) {
				cursor = paginatioNode.get("cursor").asText();
				hasACursor = true;
			}

			var dataNode = jsonNode.get("data");

			if (dataNode == null || dataNode.isEmpty() || !dataNode.isArray())
				return chatterList;

			var chatters = (ArrayNode) dataNode;
			if (chatters == null || chatters.isEmpty())
				return chatterList;

			for (var chatter : chatters) {
				chatterList.add(chatter.get("user_login").asText());
			}

			if (hasACursor)
				return getChatters(broadcasterID, moderatorID, chatterList, cursor);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chatterList;
	}

	public boolean banChatter(String broadcasterID, String moderatorID, String chatterToBan, String reason) {
		var client = HttpClient.newBuilder().build();
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("user_id", chatterToBan);
			params.put("reason", (reason == null || reason.trim().isEmpty()) ? "" : reason);
			var bodyText = "{\"data\":" + new ObjectMapper().writeValueAsString(params) + "}";

			var postBody = BodyPublishers.ofString(bodyText);
			var request = HttpRequest.newBuilder()
					.POST(postBody)
					.uri(new URI(BAN_URL + "?broadcaster_id=" + broadcasterID +
							"&moderator_id=" + moderatorID))
					.setHeader("Client-Id", clientID)
					.setHeader("Authorization", "Bearer " + token)
					.setHeader("Content-Type", "application/json")
					.timeout(Duration.ofSeconds(10))
					.build();

			var response = client.send(request, BodyHandlers.ofString());

			System.out.println("Ban response code: " + response.statusCode());
			if (response.statusCode() != 200)
				return false;
			var mapper = new ObjectMapper();
			var jsonNode = mapper.readTree(response.body());

			if (jsonNode == null || jsonNode.isEmpty())
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
