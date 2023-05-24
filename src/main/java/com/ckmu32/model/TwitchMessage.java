package com.ckmu32.model;

public class TwitchMessage {
    private String user;
    private String message;
    private String messageID;
    private String channel;
    private boolean privileged;
    private boolean broadcaster;

    public TwitchMessage(String user, String message, String messageID, String channel, boolean privileged,
            boolean broadcaster) {
        this.user = user;
        this.message = message;
        this.messageID = messageID;
        this.channel = channel;
        this.privileged = privileged;
        this.broadcaster = broadcaster;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public boolean isBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(boolean broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
