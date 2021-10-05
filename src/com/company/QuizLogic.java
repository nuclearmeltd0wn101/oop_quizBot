package com.company;

import java.util.ArrayList;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {

    private static final String greetMessagePM
            = "Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос";
    private static final String greetMessageChat
            = "Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос";
    private static final String messageHelpHint
            = "Напишите /help для получения справочного сообщения";
    private static final String messageFormatQuestion
            = "Вопрос: %s\nПодсказка к ответу: начинается с \"%s\", длина %d символов";
    private static final String messageRightAnswer = "Вы угадали!";
    private static final String messageWrongAnswer = "Вы не угадали!";

    private final ArrayList<QuizQuestion> questions;
    private boolean isQuestion_present;
    private int currentQuestionNumber;
    private final Random rand;

    public QuizLogic(ArrayList<QuizQuestion> questions)
    {
        this.questions = questions;
        rand = new Random();
    }

    public ChatBotResponse handler(ChatBotEvent event) {
        if (!event.isPrivateChat && !event.isMentioned) // ignore public chat w\o mention
            return null;

        if ("/help".equals(event.message))
            return event.toResponse(
                    !event.isPrivateChat
                            ? greetMessageChat
                            : greetMessagePM);

        if (event.message.contains("вопрос")) {
            currentQuestionNumber = rand.nextInt(questions.size());

            var question = questions.get(currentQuestionNumber);

            var text = String.format(messageFormatQuestion,
                    question.question,
                    question.answerHintFirstLetter(),
                    question.answerHintLength()
            );

            isQuestion_present = true;
            return event.toResponse(text);
        }
        if (isQuestion_present) {
            var question = questions.get(currentQuestionNumber);
            if (question.validateAnswer(event.message.toLowerCase())) {
                isQuestion_present = false;
                return event.toResponse(messageRightAnswer);
            }
            else
                return event.toResponse(messageWrongAnswer);
        }

        return event.toResponse(messageHelpHint);
    }
}
