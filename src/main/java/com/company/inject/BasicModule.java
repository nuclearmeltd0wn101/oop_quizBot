package com.company.inject;

import com.company.botBehavior.IChatBotLogic;
import com.company.botBehavior.RemindPolicy;
import com.company.database.*;
import com.company.quiz.QuestionsParser;
import com.company.quiz.QuizLogic;
import com.company.quiz.QuizQuestion;
import com.company.wrappers.IChatBotWrapper;
import com.company.wrappers.TelegramBotWrapper;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.ArrayList;
import java.util.Arrays;


public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        var env = System.getenv();
        var token = env.getOrDefault("tgBotToken", "");
        var dbPath = env.getOrDefault("dbPath", "QuizBot.sqlite");

        var remindMessages = new ArrayList<>(
                Arrays.asList("ты забыл обо мне?",
                        "не хотите сыграть?",
                        "готов задать вопрос",
                        "Давай сыграем!",
                        "проверим твою эрудицию?"));

        bind(IChatBotLogic.class).to(QuizLogic.class);

        bind(RemindPolicy.class).toInstance(new RemindPolicy(5, 3 * 24 * 60 * 60));

        bind(new TypeLiteral<ArrayList<String>>(){})
                .annotatedWith(Names.named("remindMessages"))
                .toInstance(remindMessages);


        bind(new TypeLiteral<ArrayList<QuizQuestion>>() {
        })
                .toInstance(QuestionsParser.fromTextFile("quiz_questions.txt", "\\*"));

        bind(IChatBotWrapper.class).to(TelegramBotWrapper.class);
        bind(String.class).annotatedWith(Names.named("botToken")).toInstance(token);

        bind(IDatabaseCoreSQLite.class).to(DatabaseCoreSQLite.class);
        bind(String.class).annotatedWith(Names.named("dbPath")).toInstance(dbPath);

        bind(IQuestionIdRepository.class).to(QuestionIdRepositorySQLite.class);
        bind(IRemindRepository.class).to(RemindRepositorySQLite.class);
        bind(IScoreRepository.class).to(ScoreRepositorySQLite.class);
        bind(IStatesRepository.class).to(StatesRepositorySQLite.class);
        bind(IUserNamesRepository.class).to(UserNamesRepositorySQLite.class);
        bind(IWrongAnswersCountRepository.class).to(WrongAnswersCountRepositorySQLite.class);
        bind(IGiveUpRequestsCountRepository.class).to(GiveUpRequestsCountRepositorySQLite.class);

    }
}