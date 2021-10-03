package com.company;

public class Main {
    public static void main(String[] args) {
        var botLogic = new TestLogic();
        var bot = new ConsoleChatWrapper(botLogic);
        bot.run();
    }
}
