package com.ckmu32.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigVariables {
    private String user;
    private String token;
    private String host;
    private String channel;
    private String clientID;
    private List<String> chattersToIgnore;
    
    public ConfigVariables() {
        this.chattersToIgnore = new ArrayList<>();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public List<String> getChattersToIgnore() {
        return chattersToIgnore;
    }

    public void setChattersToIgnore(List<String> chattersToIgnoreList) {
        this.chattersToIgnore = chattersToIgnoreList;
    }

}
