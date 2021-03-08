package com.pavelpotapov.messenger;

public class Message {
    private String name;
    private String text;
    private String sender;
    private String recipient;
    private String imageUrl;
    private Boolean isMy;

    public Message() {
    }

    public Message(String name, String text, String sender, String recipient, String imageUrl, Boolean isMy) {
        this.name = name;
        this.text = text;
        this.sender = sender;
        this.recipient = recipient;
        this.imageUrl = imageUrl;
        this.isMy = isMy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean isMy() {
        return isMy;
    }

    public void setMy(Boolean my) {
        isMy = my;
    }
}
