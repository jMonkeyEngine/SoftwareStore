package com.jayfella.website.http.request.message;

public class NewReplyRequest {

    private long messageId;
    private String content;

    public long getMessageId() { return messageId; }
    public void setMessageId(long messageId) { this.messageId = messageId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

}
