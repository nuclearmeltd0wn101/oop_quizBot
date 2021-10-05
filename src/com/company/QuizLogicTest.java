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
        questions = new ArrayList<>();
        questions.add(new QuizQuestion(0, testQuestion, testAnswer));
        botLogic = new QuizLogic(questions);
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
        Assert.assertEquals("Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос",
                        response.message);
    }
    @Test
    void handler_questionMessage_question() {
        var eventRandomMessage= new ChatBotEvent(0,null,"вопрос");
        var response = botLogic.handler(eventRandomMessage);
        Assert.assertEquals(true, response.message.contains("Подсказка к ответу: начинается с"));
    }

    @Test
    void handler_quizCorrectAnswer() {
        var eventGiveQuestion = new ChatBotEvent(0,null,"вопрос");
        var eventAnswer = new ChatBotEvent(0,null, testAnswer);

        botLogic.handler(eventGiveQuestion);
        var response = botLogic.handler(eventAnswer);
        Assert.assertEquals(response.message,"Вы угадали!");
    }
    @Test
    void handler_quizWrongAnswer() {
        var eventGiveQuestion = new ChatBotEvent(0,null,"вопрос");
        var eventAnswer = new ChatBotEvent(0,null, "$#@!");

        botLogic.handler(eventGiveQuestion);
        var response = botLogic.handler(eventAnswer);
        Assert.assertEquals(response.message,"Вы не угадали!");
    }

}