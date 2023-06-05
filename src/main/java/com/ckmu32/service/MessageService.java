package com.ckmu32.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ckmu32.api.TwitchAPI;
import com.ckmu32.model.TwitchJoinMessage;
import com.ckmu32.model.TwitchMessage;
import com.ckmu32.utilities.Utilities;

public class MessageService implements Utilities, MessageServiceInterface {
	@Override
	public Optional<TwitchMessage> getMessageFromLine(String line) {
		try {

			var semiColons = line.chars().filter(c -> ':' == c).count();

			if (semiColons < 2)
				return Optional.empty();

			if (!line.contains(TwitchTags.PRIV_MSG.message))
				return Optional.empty();

			Map<String, String> messageInformation = Arrays.stream(line.split(";"))
					.map(detail -> detail.split("="))
					.collect(Collectors.toMap(detailPart -> detailPart[0], detailPart -> getValueForKey(detailPart)));

			// Get comment.
			var channelAndMessage = line.substring(line.indexOf(TwitchTags.PRIV_MSG.message) + 9);
			var channel = channelAndMessage.substring(0, channelAndMessage.indexOf(":")).trim();
			var comment = channelAndMessage.substring(channelAndMessage.indexOf(":") + 1).trim();

			// Check if the user has privileges.
			boolean isPrivileged = false;
			var badges = messageInformation.getOrDefault("badges", "");
			var isBroadcaster = badges.contains("broadcaster");
			var isModerator = badges.contains("moderator");
			if (isBroadcaster || isModerator)
				isPrivileged = true;

			return Optional.of(new TwitchMessage(
					messageInformation.getOrDefault("display-name", ""),
					comment.trim(),
					messageInformation.getOrDefault("id", ""),
					channel,
					isPrivileged,
					isBroadcaster));
		} catch (Exception e) {
			System.err.println("An exception occured while parsing message: " + e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public List<TwitchJoinMessage> getJoinMessages(String twitchMessage, TwitchAPI twitchAPI, Map<String, String> broadcasterIDMap, Map<String, String> userIDMap) {
		var messages = twitchMessage.split(System.lineSeparator());

		if (isInvalid(messages.length))
			return List.of();

		var joinMessages = new ArrayList<TwitchJoinMessage>();

		for (var message : messages) {
			var containsJoin = message.contains(TwitchTags.JOIN.message);
			if (!containsJoin)
				continue;

			var channel = message
					.substring(message.indexOf(TwitchTags.JOIN.message) + TwitchTags.JOIN.message.length());

			if (isInvalid(channel))
				continue;

			var broadcasterID = broadcasterIDMap.get(channel);
			if (isInvalid(broadcasterID))
				continue;

			var messageParts = message.split("@");

			for (var part : messageParts) {
				var domainPosition = part.indexOf(".tmi.twitch.tv");

				if (isInvalid(domainPosition))
					continue;

				var userName = part.substring(0, domainPosition);
				if (isInvalid(userName))
					continue;

				if(userIDMap.containsKey(userName.trim())){
					System.out.println("Using cache for user: " + userName.trim());
					joinMessages.add(new TwitchJoinMessage(channel.trim(), userName.trim(), broadcasterID.trim(),
					userIDMap.get(channel.trim())));
					continue;
				}

				var userToBanID = twitchAPI.getUserID(userName.trim());
				if (userToBanID.isEmpty())
					continue;

				userIDMap.put(channel.trim(), userToBanID.get());

				joinMessages.add(new TwitchJoinMessage(channel.trim(), userName.trim(), broadcasterID.trim(),
						userToBanID.get().trim()));
			}
		}
		// return new TwitchJoinMessage(channel, userName);
		return joinMessages;
	}

	private String getValueForKey(String[] part) {
		if (part.length == 1)
			return "";

		if (isInvalid(part[1]))
			return "";

		// We do not want the message as part of the normal keys.
		if (part[1].contains(TwitchTags.PRIV_MSG.message))
			return "";

		return part[1];
	}

}
