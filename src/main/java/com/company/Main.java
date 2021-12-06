package com.company;

import com.company.inject.BasicModule;
import com.company.wrappers.IChatBotWrapper;
import com.google.inject.Guice;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new BasicModule());

        var bot = injector.getInstance(IChatBotWrapper.class);
        bot.run();

        var selfInducedTimer = new Timer();
        selfInducedTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                bot.callSelfInduced();
            }
        }, 0, 5000);
    }
}
