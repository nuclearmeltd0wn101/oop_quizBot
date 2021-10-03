package com.company;

import java.util.Scanner;

public class ConsoleChatWrapper implements IChatBotWrapper {
    private final IChatBotLogic m_botLogic;
    private boolean m_isRunning;

    public ConsoleChatWrapper(IChatBotLogic botLogic)
    {
        m_botLogic = botLogic;
    }

    public void run() {
        var scan = new Scanner(System.in);
        m_isRunning = true;
        System.out.println("Console Chat initialized.");
        while (m_isRunning) {
            System.out.print("Message >> ");
            var message = scan.nextLine();
            var event = new ChatBotEvent(0, "", message);
            var response = m_botLogic.handler(event);
            if (response != null) {
                System.out.print("Bot response >> ");
                System.out.println(response.message);
            }
        }
    }

    public void stop() {
        m_isRunning = false;
    }
}
