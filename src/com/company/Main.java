package com.company;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        var env = System.getenv();
        var token = env.getOrDefault("tgBotToken", "");
        var dbPath = env.getOrDefault("dbPath", "QuizBot.sqlite");
        if ("".equals(token)) {
            System.err.println("Telegram Bot Token not submitted");
            return;
        }

        var questions = QuestionsParser.fromTextFile("quiz_questions.txt", "\\*");
        var db = new QuizDBSQLite(dbPath);
        var botLogic = new QuizLogic(questions, db);
        botLogic.setRemindPolicy(5, 5);

        var bot = new TelegramBotWrapper(botLogic, token);
        bot.run();

        var selfInducedTimer = new Timer();
        selfInducedTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                bot.callSelfInduced();
            }
        },0, 5000);
    }
}
