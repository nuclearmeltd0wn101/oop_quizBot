package com.company.wrappers;

import com.company.botBehavior.ChatBotEvent;
import com.company.botBehavior.IChatBotLogic;

import java.util.Scanner;

public class ConsoleChatWrapper implements IChatBotWrapper {
    private final IChatBotLogic botLogic;
    private boolean isRunning;

    public ConsoleChatWrapper(IChatBotLogic botLogic) {
        this.botLogic = botLogic;
    }

    public void run() {
        var scan = new Scanner(System.in);
        isRunning = true;
        System.out.println("Console Chat initialized.");
        while (isRunning) {
            System.out.print("Message >> ");
            var message = scan.nextLine();
            var event = new ChatBotEvent(0, "", message);
            var response = botLogic.handler(event);
            if (response != null) {
                System.out.print("Bot response >> ");
                System.out.println(response.message);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void callSelfInduced() {
        // it has no sense in CLI
    }
}
