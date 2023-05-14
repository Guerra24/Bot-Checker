package com.ckmu32.service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ckmu32.model.TwitchMessage;
import com.ckmu32.utilities.Utilities;

public interface MessageService extends Utilities{

    public default Optional<TwitchMessage> getMessageFromLine(String line) {
		try {
			
			var semiColons = line.chars().filter(c -> ':' == c).count();
			
			if(semiColons < 2)
				return Optional.empty();

			Map<String, String> messageInformation = Arrays.stream(line.split(";"))
					.map(l -> l.split("="))
					.collect(Collectors.toMap(a -> a[0], a -> getValueForKey(a)));

			//Get comment.
			var comment = line.substring(line.indexOf(":", line.indexOf(TwitchTags.PRIV_MSG.message) + 9) + 1);

			//Check if the user has privileges.
			boolean isPrivileged = false;
			var badges = messageInformation.getOrDefault("badges", "");
			var isBroadcaster =  badges.contains("broadcaster");
			var isModerator = badges.contains("moderator");
			if(isBroadcaster || isModerator)
				isPrivileged = true;

			return Optional.of(new TwitchMessage(
					messageInformation.getOrDefault("display-name", ""),
					comment.trim(), 
					messageInformation.getOrDefault("id", ""), 
					isPrivileged,
					isBroadcaster));
		}catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	private String getValueForKey(String[] part) {
		if(part.length == 1)
			return "";

		if(isInvalid(part[1]))
			return "";

		//We do not want the message as part of the normal keys.
		if(part[1].contains(TwitchTags.PRIV_MSG.message))
			return "";

		return part[1];
	}

}
