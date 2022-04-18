package dev.ufuk.bakan;

import java.io.Serializable;

public class InitUser implements Serializable {
    public String username;
    public InitUser(String username){
        this.username = username;
    }
}