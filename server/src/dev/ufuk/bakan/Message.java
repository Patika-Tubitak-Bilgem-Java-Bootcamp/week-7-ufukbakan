package dev.ufuk.bakan;

public class Message {
    public long id;
    public String content, sender;
    private static long messageId = Long.MIN_VALUE+1;
    public Message(String sender, String content){
        this.sender = sender;
        this.content = content;
        this.id = messageId++;
    }
    public Message(ClientMessage cm){
        this.content = cm.content;
        this.sender = cm.sender;
        this.id = messageId++;
    }
}
