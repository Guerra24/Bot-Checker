package com.ckmu32.service;

import java.util.List;
import java.util.Optional;

import com.ckmu32.api.TwitchAPI;
import com.ckmu32.model.TwitchJoinMessage;
import com.ckmu32.model.TwitchMessage;

public interface MessageServiceInterface {

    Optional<TwitchMessage> getMessageFromLine(String line);

    List<TwitchJoinMessage> getJoinMessages(String twitchMessage, TwitchAPI twitchAPI);

}