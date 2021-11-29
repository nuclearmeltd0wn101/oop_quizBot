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
        var remindPolicy=new RemindPolicy(5, 3);
        var dbCore = new DatabaseCoreSQLite(dbPath);
        var questionRepo=new QuestionIdRepositorySQLite(dbCore);
        var remindRepo=new RemindRepositorySQLite(dbCore,remindPolicy);
        var scoreRepo=new ScoreRepositorySQLite(dbCore);
        var statesRepo=new StatesRepositorySQLite(dbCore);
        var userNameRepo=new UserNamesRepositorySQLite(dbCore);
        var wrongRepo=new WrongAnswersCountRepositorySQLite(dbCore);
        var giveUpRepo=new GiveUpRequestsCountRepositorySQLite(dbCore);
        var botLogic = new QuizLogic(questions,questionRepo,remindRepo,scoreRepo,
                statesRepo,userNameRepo,wrongRepo,giveUpRepo);

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
