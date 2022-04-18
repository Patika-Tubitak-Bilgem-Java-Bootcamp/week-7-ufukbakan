package dev.ufuk.bakan;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private static final long serialVersionUID = 0L;
    public String sender, content;
    public ClientMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
    public ClientMessage(Message m){
        this.sender = m.sender;
        this.content = m.content;
    }
}
