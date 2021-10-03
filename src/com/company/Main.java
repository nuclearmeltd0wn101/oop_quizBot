package com.company;

public class Main {
    public static void main(String[] args) {
        var env = System.getenv();
        var token = env.getOrDefault("tgBotToken", "");
        if ("".equals(token)) {
            System.err.println("Telegram Bot Token not submitted");
            return;
        }

        var botLogic = new TestLogic();
        var bot = new TelegramBotWrapper(botLogic, token);
        bot.run();
    }
}
