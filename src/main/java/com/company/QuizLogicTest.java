package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class QuizLogicTest {
    ArrayList<QuizQuestion> questions;
    QuizLogic botLogic;

    String testQuestion = "test question 1";
    String testAnswer = "testanswer";

    public QuizLogicTest() {
        var db = new QuizDBRAM();
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        //botLogic = new QuizLogic(questions, db);
    }

    @Test
    void handler_randomMessage_helpHint() {
        var eventRandomMessage= new ChatBotEvent(0,null,"help");
        var response = botLogic.handler(eventRandomMessage);
        Assert.assertEquals("Напишите /help для получения справочного сообщения", response.message);
    }
    @Test
    void handler_helpMessage_help()
    {
        var eventRandomMessage= new ChatBotEvent(0,null,"/help");
        var response = botLogic.handler(eventRandomMessage);
        Assert.assertEquals("Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос\nНапиши /score для получения таблицы счета",
                        response.message);
    }
    @Test
    void handler_quizWrongAnswer() {
        var eventGiveQuestion = new ChatBotEvent(0,null,"вопрос");
        var eventAnswer = new ChatBotEvent(0,null, "$#@!");

        botLogic.handler(eventGiveQuestion);
        var response = botLogic.handler(eventAnswer);
        Assert.assertEquals(response.message.split("\\.")[0],"Вы не угадали");
    }

}