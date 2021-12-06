package com.company.inject;

import com.company.botBehavior.IChatBotLogic;
import com.company.botBehavior.RemindPolicy;
import com.company.database.*;
import com.company.quiz.QuestionsParser;
import com.company.quiz.QuizLogic;
import com.company.quiz.QuizQuestion;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import java.util.List;


public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        var dbPath = System.getenv().getOrDefault("dbPath", "QuizBot.sqlite");
        var dbCore = new DatabaseCoreSQLite(dbPath);

        bind(RemindPolicy.class).toInstance(new RemindPolicy(5, 3 * 24 * 60 * 60));
        bind(IDatabaseCoreSQLite.class).toInstance(dbCore);
        bind(IQuestionIdRepository.class).to(QuestionIdRepositorySQLite.class);
        bind(IRemindRepository.class).to(RemindRepositorySQLite.class);
        bind(IScoreRepository.class).to(ScoreRepositorySQLite.class);
        bind(IStatesRepository.class).to(StatesRepositorySQLite.class);
        bind(IUserNamesRepository.class).to(UserNamesRepositorySQLite.class);
        bind(IWrongAnswersCountRepository.class).to(WrongAnswersCountRepositorySQLite.class);
        bind(IGiveUpRequestsCountRepository.class).to(GiveUpRequestsCountRepositorySQLite.class);
        bind(new TypeLiteral<List<QuizQuestion>>() {})
                .toInstance(QuestionsParser.fromTextFile("quiz_questions.txt", "\\*"));
        bind(IChatBotLogic.class).to(QuizLogic.class);
    }
}