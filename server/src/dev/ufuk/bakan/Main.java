package dev.ufuk.bakan;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static boolean runServer = true;

    public static void main(String[] args) throws IOException {
        new Thread(new DebuggerThread()).start();
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Listening to port "+ port);
        while(runServer){
            // kaç thread çalıştığını gözden geçirmek için:
            System.out.println("Active threads: "+ Thread.activeCount() + "/256");
            // soket bağlantısı onayla :
            Socket acceptedSocket = serverSocket.accept();
            while(Thread.activeCount() >= 255){
                // Thread sayısının 256yı geçmemesi için bekle ..
            }
            // her bağlanan kişi için yeni bir thread ata :
            Thread messageHandler = new MessageHandler(acceptedSocket);
            messageHandler.start();
        }

        System.out.println("Server is closed");
    }

    public void clearOldLogs(){
        // chat log üst sınıra ulaştıysa:
        if(MessageHandler.chatlog.size() > MessageHandler.LOG_SIZE){
            synchronized (MessageHandler.LOCK){
                // en eski logu sil (0. sıradaki)
                MessageHandler.chatlog.remove(0);
            }
        }
    }
}
