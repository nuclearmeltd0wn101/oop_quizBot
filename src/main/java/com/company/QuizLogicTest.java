package com.company;

import com.company.botBehavior.ChatBotEvent;
import com.company.botBehavior.RemindPolicy;
import com.company.database.*;
import com.company.quiz.QuizLogic;
import com.company.quiz.QuizQuestion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class QuizLogicTest {
    ArrayList<QuizQuestion> questions;

    String testQuestion = "test question 1";
    String testAnswer = "testanswer";

    void initializeDatabaseRepos(IDatabaseCoreSQLite db) {
        db.Save(new String[] {
                "CREATE table if NOT EXISTS wrongAnswers (chatId INTEGER PRIMARY KEY, count INTEGER)",
                "CREATE table if NOT EXISTS giveUpRequests (chatId INTEGER PRIMARY KEY, count INTEGER)",
                "CREATE table if NOT EXISTS questionIds (chatId INTEGER PRIMARY KEY, questionId INTEGER)",
                "CREATE table if NOT EXISTS chatsInactive (chatId INTEGER PRIMARY KEY, "
                        + "lastActiveTimestampUnix INTEGER, remindAttemptsCount)",
                "CREATE table if NOT EXISTS score (chatId INTEGER,"
                        + "userId INTEGER, score INTEGER, PRIMARY KEY (chatId, userId))",
                "CREATE table if NOT EXISTS states (chatId INTEGER PRIMARY KEY, state INTEGER)",
                "CREATE table IF NOT EXISTS userNames (userId INTEGER PRIMARY KEY, name TEXT)"
        });
    }


    @Test
    void handler_randomMessage_helpHint() {
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        var dbCore = new DatabaseCoreSQLite(null);
        initializeDatabaseRepos(dbCore);
        var questionRepo=new QuestionIdRepositorySQLite(dbCore);
        var remindRepo=new RemindRepositorySQLite(dbCore,new RemindPolicy());
        var scoreRepo=new ScoreRepositorySQLite(dbCore);
        var statesRepo=new StatesRepositorySQLite(dbCore);
        var userNameRepo=new UserNamesRepositorySQLite(dbCore);
        var wrongRepo=new WrongAnswersCountRepositorySQLite(dbCore);
        var giveUpRepo=new GiveUpRequestsCountRepositorySQLite(dbCore);
        var botLogic = new QuizLogic(questions,questionRepo,remindRepo,scoreRepo,
                statesRepo,userNameRepo,wrongRepo,giveUpRepo);
        var eventRandomMessage= new ChatBotEvent(0,null,"ab");
        var response = botLogic.handler(eventRandomMessage);
        Assertions.assertEquals("Напишите /help для получения справочного сообщения", response.message);
    }
    @Test
    void handler_helpMessage_help()
    {
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        var dbCore = new DatabaseCoreSQLite(null);
        initializeDatabaseRepos(dbCore);
        var questionRepo=new QuestionIdRepositorySQLite(dbCore);
        var remindRepo=new RemindRepositorySQLite(dbCore,new RemindPolicy());
        var scoreRepo=new ScoreRepositorySQLite(dbCore);
        var statesRepo=new StatesRepositorySQLite(dbCore);
        var userNameRepo=new UserNamesRepositorySQLite(dbCore);
        var wrongRepo=new WrongAnswersCountRepositorySQLite(dbCore);
        var giveUpRepo=new GiveUpRequestsCountRepositorySQLite(dbCore);
        var botLogic = new QuizLogic(questions,questionRepo,remindRepo,scoreRepo,
                statesRepo,userNameRepo,wrongRepo,giveUpRepo);
        var eventRandomMessage= new ChatBotEvent(0,null,"/start");
        var response = botLogic.handler(eventRandomMessage);
        Assertions.assertEquals("Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос\nНапиши /score для получения таблицы счета",
                        response.message);
    }
    @Test
    void handler_quizWrongAnswer() {
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        var dbCore = new DatabaseCoreSQLite(null);
        initializeDatabaseRepos(dbCore);
        var questionRepo=new QuestionIdRepositorySQLite(dbCore);
        var remindRepo=new RemindRepositorySQLite(dbCore,new RemindPolicy());
        var scoreRepo=new ScoreRepositorySQLite(dbCore);
        var statesRepo=new StatesRepositorySQLite(dbCore);
        var userNameRepo=new UserNamesRepositorySQLite(dbCore);
        var wrongRepo=new WrongAnswersCountRepositorySQLite(dbCore);
        var giveUpRepo=new GiveUpRequestsCountRepositorySQLite(dbCore);
        var botLogic = new QuizLogic(questions,questionRepo,remindRepo,scoreRepo,
                statesRepo,userNameRepo,wrongRepo,giveUpRepo);
        var eventGiveQuestion = new ChatBotEvent(0,null,"вопрос");
        var eventAnswer = new ChatBotEvent(0,null, "$#@!");
        botLogic.handler(eventGiveQuestion);
        var response = botLogic.handler(eventAnswer);
        Assertions.assertEquals(response.message.split("\\.")[0],"Вы не угадали");
    }
    @Test
    void handler_quizRightAnswer(){
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        var dbCore = new DatabaseCoreSQLite(null);
        initializeDatabaseRepos(dbCore);
        var questionRepo=new QuestionIdRepositorySQLite(dbCore);
        var remindRepo=new RemindRepositorySQLite(dbCore,new RemindPolicy());
        var scoreRepo=new ScoreRepositorySQLite(dbCore);
        var statesRepo=new StatesRepositorySQLite(dbCore);
        var userNameRepo=new UserNamesRepositorySQLite(dbCore);
        var wrongRepo=new WrongAnswersCountRepositorySQLite(dbCore);
        var giveUpRepo=new GiveUpRequestsCountRepositorySQLite(dbCore);
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        var botLogic = new QuizLogic(questions,questionRepo,remindRepo,scoreRepo,
                statesRepo,userNameRepo,wrongRepo,giveUpRepo);
        var eventGiveQuestion = new ChatBotEvent(0,null,"вопрос");
        botLogic.handler(eventGiveQuestion);
        var eventAnswer=new ChatBotEvent(0,"aaa",testAnswer);
        var response = botLogic.handler(eventAnswer);
        Assertions.assertEquals(response.message,"Вы угадали!");
    }
    @Test
    void handler_score(){
        var dbCore = new DatabaseCoreSQLite(null);
        initializeDatabaseRepos(dbCore);
        var questionRepo=new QuestionIdRepositorySQLite(dbCore);
        var remindRepo=new RemindRepositorySQLite(dbCore,new RemindPolicy());
        var scoreRepo=new ScoreRepositorySQLite(dbCore);
        var statesRepo=new StatesRepositorySQLite(dbCore);
        var userNameRepo=new UserNamesRepositorySQLite(dbCore);
        var wrongRepo=new WrongAnswersCountRepositorySQLite(dbCore);
        var giveUpRepo=new GiveUpRequestsCountRepositorySQLite(dbCore);
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        var botLogic = new QuizLogic(questions,questionRepo,remindRepo,scoreRepo,
                statesRepo,userNameRepo,wrongRepo,giveUpRepo);
        var eventGiveQuestion = new ChatBotEvent(0,null,"вопрос");
        botLogic.handler(eventGiveQuestion);
        var eventAnswer=new ChatBotEvent(0,"bbb",testAnswer);
        var score=new ChatBotEvent(0,"bbb","/score");
         botLogic.handler(eventAnswer);
        var response=botLogic.handler(score);
        Assertions.assertEquals(response.message,"```\n" +
                "bbb | 1```");
    }

}