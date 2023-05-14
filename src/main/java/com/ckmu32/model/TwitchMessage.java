package com.ckmu32.model;

public class TwitchMessage {
    private String user;
    private String message;
    private String messageID;
    private boolean privileged;
    private boolean broadcaster;

    public TwitchMessage(String user, String message, String messageID, boolean privileged, boolean broadcaster) {
        this.user = user;
        this.message = message;
        this.messageID = messageID;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((messageID == null) ? 0 : messageID.hashCode());
        result = prime * result + (privileged ? 1231 : 1237);
        result = prime * result + (broadcaster ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TwitchMessage other = (TwitchMessage) obj;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (messageID == null) {
            if (other.messageID != null)
                return false;
        } else if (!messageID.equals(other.messageID))
            return false;
        if (privileged != other.privileged)
            return false;
        if (broadcaster != other.broadcaster)
            return false;
        return true;
    }

}
