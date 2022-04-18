package dev.ufuk.bakan;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ChatRoomListener extends Thread{
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    public boolean runListener = true;

    public ChatRoomListener(InputStream inputStream) throws IOException {
        // input output streamlerin set edilmesi :
        this.inputStream = inputStream;
        this.objectInputStream = new ObjectInputStream(inputStream);
    }

    @Override
    public void run(){
        while(runListener){
            try {
                if(inputStream.available() > 0){ // eğer sunucudan mesaj gelirse ;
                    Object o = objectInputStream.readObject(); // nesne olarak oku
                    if(o instanceof ClientMessage){
                        ClientMessage message = (ClientMessage) o; // nesneyi client mesaja dönüştür
                        String output = String.format("%s : %s", message.sender, message.content);
                        System.out.println(output); // mesaj formatında ekrana yaz
                    }
                }
            }
            catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
}
