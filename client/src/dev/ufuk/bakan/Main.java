package dev.ufuk.bakan;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    private static Socket socket = new Socket();
    private static Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    public static void main(String[] args) {

        int port = 8080;
        try {
            System.out.println("Enter your username: ");
            String input =  scanner.nextLine();
            String username = input;
            // localhost'te çalışan servera bağlan :
            socket.connect(new InetSocketAddress("localhost", port));
            System.out.println("Connected to the server, type exit to quit or send a message");

            // input, output streamlerin set edilmesi:
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();

            // main thread kullanıcının klavye girdilerini servera yollarken
            // ikinci bir thread serverdan gelen mesajları ekrana yazdırmak için arkaplanda çalışacak :
            ChatRoomListener chatRoomListener = new ChatRoomListener(inputStream);
            chatRoomListener.start();

            // servera ilk mesaj olarak initUser nesnesini gönder;
            objectOutputStream.writeObject( new InitUser(username) );
            objectOutputStream.flush();

            // daha sonra klavyeden her girdiyi mesaj olarak gönder:
            while (!(input = scanner.nextLine()).equalsIgnoreCase("exit") ) {
                objectOutputStream.writeObject( new ClientMessage(username, input ) );
                objectOutputStream.flush();
            }
            // exit yazıldıysa soketi kapat ve oda dinleyicisini sonlandır :
            chatRoomListener.runListener = false;
            outputStream.close();
            inputStream.close();
            socket.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
