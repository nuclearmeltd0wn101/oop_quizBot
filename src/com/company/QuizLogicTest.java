package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class QuizLogicTest {
    QuestionBase questionBase = new QuestionBase("quiz_questions.txt", "\\*");
    QuizLogic botLogic = new QuizLogic(questionBase);
    @Test
    void randomMessage() {
        Assert.assertEquals
                ("Напишите /help для получения справочного сообщения",
                        botLogic.handler(new ChatBotEvent(0,null,"help")).message);
    }
    @Test
    void helper()
    {
        Assert.assertEquals
                ("Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос",
                        botLogic.handler(new ChatBotEvent(0,null,"/help")).message);
    }
    @Test
    void question()
    {
        Assert.assertEquals
                (true,botLogic.handler(new ChatBotEvent(0,null,"вопрос"))
                        .message.contains("Подсказка к ответу: начинается с"));
    }

    @Test
    void RightAnswer() throws NoSuchFieldException, IllegalAccessException {
        botLogic.handler(new ChatBotEvent(0,null,"вопрос"));
        Field numberOfquestion=QuizLogic.class.getDeclaredField("m_currentQuestionNumber");
        numberOfquestion.setAccessible(true);
        Integer number = (int)numberOfquestion.get(botLogic);
        Field answer=QuizQuestion.class.getDeclaredField("answer");
        answer.setAccessible(true);
        var question=questionBase.getQuestionById(number);
        String StringAnswer=(String)answer.get(question);
        Assert.assertEquals(botLogic.handler(new ChatBotEvent(0,null,StringAnswer)).message,"Вы угадали!");
    }
    @Test
    void NotRightAnswer() throws NoSuchFieldException, IllegalAccessException {
        botLogic.handler(new ChatBotEvent(0,null,"вопрос"));
        Field numberOfquestion=QuizLogic.class.getDeclaredField("m_currentQuestionNumber");
        numberOfquestion.setAccessible(true);
        Integer number = (int)numberOfquestion.get(botLogic);
        Field answer=QuizQuestion.class.getDeclaredField("answer");
        answer.setAccessible(true);
        var question=questionBase.getQuestionById(number+1);
        String StringAnswer=(String)answer.get(question);
        Assert.assertEquals(botLogic.handler(new ChatBotEvent(0,null,StringAnswer)).message,"Вы не угадали!");
    }

}