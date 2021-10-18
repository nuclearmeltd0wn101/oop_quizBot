package com.company;

import java.util.ArrayList;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {

    private static final int giveUpCountRequired = 2;

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
            System.out.println();
            var sb = new StringBuilder();
            for (QuizScore item:table)
            {
                sb.append(db.getUserName(item.userId));
                sb.append(" | ");
                sb.append(item.score);
                sb.append("\n");
            }
            return event.toResponse(sb.toString());

        }

        if (event.message.contains("/help"))
            return event.toResponse(
                    !event.isPrivateChat
                            ? greetMessageChat
                            : greetMessagePM);

        if (event.message.contains("вопрос")|event.message.contains("Вопрос"))
        {
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
                db.setUserName(event.senderId, event.senderUsername);
                db.scoreIncrement(event.chatId, event.senderId);
                db.giveUpRequestsCountReset(event.chatId);
                db.wrongAnswersCountReset(event.chatId);
                return event.toResponse(messageRightAnswer);
            }


            var response = "";
            var failureCount=db.getWrongAnswersCount(event.chatId);
            var giveUpCount=db.getGiveUpRequestsCount(event.chatId);
            if (event.message.contains("сдаюсь")|(event.message.contains("Сдаюсь"))) {
                if (giveUpCount < giveUpCountRequired ) {
                    db.giveUpRequestsCountIncrement(event.chatId);
                    response = String.format("Напишите \"сдаюсь\" ещё %s раз(-а)",
                            giveUpCountRequired - giveUpCount);
                }
                else
                {
                    db.giveUpRequestsCountReset(event.chatId);
                    db.wrongAnswersCountReset(event.chatId);
                    db.setState(event.chatId,0);
                    response = "Вопрос снят.\nСледующий вопрос:";
                    var nextQuestionId = rand.nextInt(questions.size());
                    db.setQuestionId(event.chatId, nextQuestionId);

                    db.setState(event.chatId,1);
                    var NextQuestion = questions.get(Math.toIntExact(db.getQuestionId(event.chatId)));
                    var text = NextQuestion.question;

                    return event.toResponse(response+text);

                }

            }
            else {
                var sb = new StringBuilder(
                        failureCount<5?String.format("Вы не угадали.Осталось %s попыток",9-failureCount):failureCount<8?String.format("Вы не угадали.Осталось %s попытки",9-failureCount):String.format("Вы не угадали.Осталось %s попытка",9-failureCount));
                db.wrongAnswersCountIncrement(event.chatId);

                if (failureCount >= 2)
                    sb.append(String.format("\n\nПодсказка №1: Ответ начинается с буквы \"%s\" ",
                            question.answerHintFirstLetter()));

                if (failureCount >= 5)
                    sb.append(String.format("\nПодсказка №2: Длина ответа - %s символов",
                            question.answerHintLength()));

                response = sb.toString();
                if (failureCount == 9)
                {
                    db.giveUpRequestsCountReset(event.chatId);
                    db.wrongAnswersCountReset(event.chatId);
                    db.setState(event.chatId,0);
                    response = "Слишком много неправильных попыток. Напишите \"вопрос\", чтобы получить новый вопрос.";
                }
            }
            return event.toResponse(response);
        }

        return event.toResponse(messageHelpHint);
    }
}