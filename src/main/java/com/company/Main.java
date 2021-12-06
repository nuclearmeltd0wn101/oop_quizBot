package com.company;

import com.company.botBehavior.ChatBotResponse;
import com.company.botBehavior.IChatBotLogic;
import com.company.botBehavior.RemindPolicy;
import com.company.database.*;
import com.company.inject.BasicModule;
import com.company.quiz.QuestionsParser;
import com.company.quiz.QuizLogic;
import com.company.quiz.QuizQuestion;
import com.company.wrappers.ConsoleChatWrapper;
import com.company.wrappers.TelegramBotWrapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main {
    private static Injector injector;

    public static void main(String[] args) {
        try {
            compositionRootInitialize();
        } catch(Error e) {
            e.printStackTrace();
        }
        var env = System.getenv();
        var token = env.getOrDefault("tgBotToken", "");
        if ("".equals(token)) {
            System.err.println("Telegram Bot Token not submitted");
            return;
        }
       // var questions = QuestionsParser.fromTextFile("quiz_questions.txt", "\\*");
        var botLogic = injector.getInstance(IChatBotLogic.class);

        var bot = new TelegramBotWrapper(botLogic, token);
        var consoleBot = new ConsoleChatWrapper(botLogic);
        consoleBot.run();
        //  bot.run();

        var selfInducedTimer = new Timer();
        selfInducedTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                bot.callSelfInduced();
            }
        }, 0, 5000);
    }

    private static void compositionRootInitialize() {
        injector = Guice.createInjector(new BasicModule());

    }
}
