package com.ckmu32.service;

public enum TwitchTags {
    
    BOTS_COMMAND("!bots"),
    SUCCESSFUL_LOGIN("Welcome, GLHF!"),
    REQ_TAGS("CAP REQ :twitch.tv/tags"),
    MEMBERSHIP_TAGS("CAP REQ :twitch.tv/membership"),
    PRIV_MSG("PRIVMSG #"),
    JOIN("JOIN #"),
    PONG("PONG :tmi.twitch.tv"),
    PING("PING :tmi.twitch.tv"),
    READY_TO_JOIN(":You are in a maze of twisty passages, all alike.");

    public final String message;

    private TwitchTags(String message) {
        this.message = message;
    }
    
}
