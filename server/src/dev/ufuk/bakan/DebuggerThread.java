package dev.ufuk.bakan;

import java.util.Locale;
import java.util.Scanner;

public class DebuggerThread implements Runnable {

    private static Scanner scanner = new Scanner(System.in);
    private static boolean runDebugger = true;

    @Override
    public void run() {
        while(runDebugger){
            String input = scanner.nextLine();
            if(input.toLowerCase(Locale.ROOT).equals("t")){
                System.out.println("Active threads: "+ Thread.activeCount() + "/256");
            }
        }
    }
}
