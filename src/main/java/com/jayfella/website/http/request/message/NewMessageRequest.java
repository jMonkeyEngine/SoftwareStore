package com.jayfella.website.http.request.message;

public class NewMessageRequest {

    private String recipient;

    private String title;
    private String content;

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

}
