package com.company;

import java.util.ArrayList;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {

    private static final int giveUpCountRequired = 3;

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
    private final Random rand;
    private IQuizDB db;
    public QuizLogic(ArrayList<QuizQuestion> questions, IQuizDB db)
    {
        this.questions = questions;
        rand = new Random();
        this.db = db;
    }

    public ChatBotResponse handler(ChatBotEvent event) {
        if (!event.isPrivateChat && !event.isMentioned) // ignore public chat w\o mention
            return null;

        var state = db.getState(event.chatId);

        if (event.message.contains("/score"))
        {
            var table= db.getScoreTable(event.chatId);
            if (db.getScoreTable(event.chatId) == null)
                return event.toResponse("the score table is empty");

            var sb = new StringBuilder();
            for (QuizScore item:table)
            {
                sb.append(item);
                sb.append("\n");
            }
            return event.toResponse(sb.toString());

        }

        if (event.message.contains("/help"))
            return event.toResponse(
                    !event.isPrivateChat
                            ? greetMessageChat
                            : greetMessagePM);

        if (event.message.contains("вопрос")) {
            if (state != 0)
                return event.toResponse("Вопрос уже был задан,ожидаю ответ");

            var questionId = rand.nextInt(questions.size());
            db.setQuestionId(event.chatId, questionId);

            db.setState(event.chatId,1);
            var question = questions.get(questionId);
            var text = question.question;

            return event.toResponse(text);
        }

        if (state != 0) {
            var questionId= Math.toIntExact(db.getQuestionId(event.chatId));
            var question = questions.get(questionId);

            if (question.validateAnswer(event.message.toLowerCase())) {
                db.setState(event.chatId,0);
                db.scoreIncrement(event.chatId,event.senderId);
                return event.toResponse(messageRightAnswer);
            }

            var failureCount = state % 7 - 1;
            var giveUpCount = state / 7;
            var response = "";

            if (event.message.contains("сдаюсь")) {
                if (giveUpCount < giveUpCountRequired - 1) {
                    giveUpCount++;
                    response = String.format("Напишите \"сдаюсь\" ещё %s раз(-а)",
                            giveUpCountRequired - giveUpCount);
                }
                else
                {
                    failureCount = giveUpCount = 0;
                    response = "Вопрос снят. Напишите \"вопрос\", чтобы получить новый вопрос.";
                }

            }
            else {
                var sb = new StringBuilder("Вы не угадали.");
                if (failureCount < 5)
                    failureCount++;

                if (failureCount >= 3)
                    sb.append(String.format("\n\nПодсказка №1: Ответ начинается с буквы \"%s\"",
                            question.answerHintFirstLetter()));

                if (failureCount == 5)
                    sb.append(String.format("\nПодсказка №2: Длина ответа - %s символов",
                            question.answerHintLength()));

                response = sb.toString();
            }
            var newState = 1 + failureCount + giveUpCount * 6;
            db.setState(event.chatId, newState);

            return event.toResponse(response);
        }

        return event.toResponse(messageHelpHint);
    }
}