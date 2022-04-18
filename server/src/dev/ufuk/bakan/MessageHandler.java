package dev.ufuk.bakan;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageHandler extends Thread{
    public static List<Message> chatlog = new ArrayList<>(); // mesaj geçmişi
    public static int LOG_SIZE = 10; // max kaç mesajın sunucuda saklanacağı ve yeni katılan kullanıcılara gösterileceği
    private long lastRead = Long.MIN_VALUE; // kullanıcının en son kaçıncı mesajı okuduğu
    public static Object LOCK = new Object(); // semafor
    private String user = "unknown"; // kullanıcı adı, init mesajıyle set edilecek

    private Socket acceptedSocket;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;

    public MessageHandler(Socket acceptedSocket){
        this.acceptedSocket = acceptedSocket;
        try {
            // input, output streamlerin set edilmesi :
            inputStream = acceptedSocket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            outputStream = acceptedSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(acceptedSocket.isConnected()){ // soket bağlı olduğu sürece threadi ayakta tut
                if(inputStream.available() > 0){ // input geldiyse ;
                    Object o = objectInputStream.readObject(); // nesne olarak oku
                    if(o instanceof  InitUser){ // init user mesajı geldiyse ;
                        InitUser initUser = (InitUser) o;
                        this.user = initUser.username; // kullanıcı adını set et
                        synchronized (LOCK){
                            // chat loga kullanıcının odaya katıldığı mesajını ekle
                            chatlog.add( new Message(user, "joined the room") );
                        }
                        // serverda print et
                        System.out.println(user + " joined the room");
                    }
                    else if(o instanceof ClientMessage){ // init mesajı değil de normal mesaj geldiyse ;
                        //System.out.println("Client message geldi");
                        Message m = new Message((ClientMessage) o); // client mesajı, id'si olan bir mesaja dönüştürür
                        synchronized (LOCK){
                            // mesajı chat loga ekle
                            chatlog.add(m);
                        }
                    }
                    else{
                        // ne client mesaj ne de init mesajı geldiyse ignorela
                        //System.out.println("object geldi");
                    }
                }
                // clienta yazılması gereken mesajlar (başkalarının ve kendi yazdığı chatlog) :
                List<Message> needRead = chatlog.stream().filter(message -> message.id > lastRead).collect(Collectors.toList());
                if(needRead.size() > 0){
                    //System.out.println(user + " tarafından okunmayan mesajlar var");
                    for(Message m: needRead){
                        // her bir mesajı output streame yaz;
                        objectOutputStream.writeObject(new ClientMessage(m));
                        objectOutputStream.flush();
                    }
                    // id'si en büyük olanı (son okunanı) lastRead olarak ata ve
                    // bir sonraki iterasyonda okunmamış mesajların filtrelenmesinde kullan
                    lastRead = needRead.stream().reduce((p,n)-> p.id > n.id ? p : n ).get().id;
                }
            }
        }
        catch (Exception ex){
            if(!(ex instanceof SocketException)){
                ex.printStackTrace();
            }
        }
        finally {
            try {
                acceptedSocket.close();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            // soket kapatıldığında chat loga ve servera ayrılan kullanıcıyı yaz :
            System.out.println(user + " left the room.");
            chatlog.add( new Message(user, "left the room") );
        }
    }
}
