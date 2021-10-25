package com.company;

import java.util.ArrayList;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {

    private static final int giveUpCountRequired = 2;
    private static final int wrongAnswersLimit = 9;
    private static final int firstHintThreshold = 2;
    private static final int secondHintThreshold = 5;

    private static final String greetMessagePM
            = "Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос";
    private static final String remainingAnswersCountMessageMany
            = "Вы не угадали. Осталось %s попыток";
    private static final String remainingAnswersCountMessageFew
            = "Вы не угадали. Осталось %s попытки";
    private static final String remainingAnswersCountMessageLast
            = "Вы не угадали. Осталась %s попытка";
    private static final String wrongAnswersLimitMessage
            = "Слишком много неправильных попыток. Напишите \"вопрос\", чтобы получить новый вопрос.";
    private static final String greetMessageChat
            = "Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос";
    private static final String messageHelpHint
            = "Напишите /help для получения справочного сообщения";
    private static final String messageRightAnswer = "Вы угадали!";
    private static final String scoreTableEmptyMessage =  "В таблице счета пока нет записей";
    private static final String firstHintMessage = "\n\nПодсказка №1: Ответ начинается с буквы \"%s\" ";
    private static final String secondHintMessage="\nПодсказка №2: Длина ответа - %s символов";
    private static final String giveUpMessage= "Напишите \"сдаюсь\" ещё %s раза";
    private static final String giveUpMessage2= "Напишите \"сдаюсь\" ещё %s раз";
    private static final String questionResetMessage= "Вопрос снят. Следующий вопрос:\n";
    private static final String questionAlreadyExistMessage="Вопрос уже был задан, ожидаю ответ";

    private final ArrayList<QuizQuestion> questions;
    private final Random rand;
    private final IQuizDB db;

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
            return displayScoreTable(event);
        }

        if (event.message.contains("/help"))
            return event.toResponse(
                    !event.isPrivateChat
                        ? greetMessageChat
                        : greetMessagePM);

        if (event.message.toLowerCase().contains("вопрос"))
        {
            if (state != 0)
                return event.toResponse(questionAlreadyExistMessage);

            var question = updateQuestion(event);
            return event.toResponse(question.question);
        }

        if (state != 0) {
            var questionId= db.getQuestionId(event.chatId);
            var question = questions.get(questionId);

            if (question.validateAnswer(event.message.toLowerCase())) {
                db.setUserName(event.senderId, event.senderUsername);
                db.scoreIncrement(event.chatId, event.senderId);
                resetQuestion(event);
                return event.toResponse(messageRightAnswer);
            }

            if (event.message.toLowerCase().contains("сдаюсь"))
                return processGiveUpRequest(event);

            return processWrongAnswer(event, question);
        }

        return event.toResponse(messageHelpHint);
    }

    private ChatBotResponse displayScoreTable(ChatBotEvent event) {
        var table= db.getScoreTable(event.chatId);
        if (db.getScoreTable(event.chatId) == null)
            return event.toResponse(scoreTableEmptyMessage);
        System.out.println();
        var sb = new StringBuilder();
        var maximumScore=0;
        var maximumName=" ";
        for (QuizScore item:
             table) {
            if (item.score>maximumScore)
            {
                maximumScore=Math.toIntExact(item.score);
            }
            if (db.getUserName(item.userId).length()>maximumName.length())
            {
                maximumName= db.getUserName(item.userId);
            }
        }

        for (QuizScore item:table) {
            sb.append("\n");
            var userName=db.getUserName(item.userId);
            var lengthName = 60;
            if (maximumScore + maximumName.length() > 58)
                lengthName = 58 -maximumScore;
            if (userName.length()+String.valueOf(item.score).length()>57)
                sb.append(userName,0,lengthName);
            else
                sb.append(userName);
            var diff=maximumName.length()-userName.length();
            for (var a=0;a<diff+1;a++)
                sb.append(" ");
            sb.append("| ");
            sb.append(item.score);
        }
        return event.toResponse("```"+sb+"```");
    }

    private void resetQuestion(ChatBotEvent event) {
        db.giveUpRequestsCountReset(event.chatId);
        db.wrongAnswersCountReset(event.chatId);
        db.setState(event.chatId, 0);
    }

    private QuizQuestion updateQuestion(ChatBotEvent event) {
        var questionId = rand.nextInt(questions.size());
        db.setQuestionId(event.chatId, questionId);

        db.setState(event.chatId,1);
        return questions.get(questionId);
    }

    private ChatBotResponse processWrongAnswer(ChatBotEvent event, QuizQuestion question) {
        var failureCount= db.getWrongAnswersCount(event.chatId);
        if (failureCount == wrongAnswersLimit)
        {
            resetQuestion(event);
            event.toResponse(wrongAnswersLimitMessage);
        }

        var sb = new StringBuilder(
                getRemainingAnswersCountMessage(failureCount));

        if (failureCount >= firstHintThreshold)
            sb.append(String.format(firstHintMessage,
                    question.answerHintFirstLetter()));

        if (failureCount >= secondHintThreshold)
            sb.append(String.format(secondHintMessage,
                    question.answerHintLength()));

        db.wrongAnswersCountIncrement(event.chatId);
        return event.toResponse(sb.toString());
    }

    private String getRemainingAnswersCountMessage(int failureCount) {
        if (failureCount < 5)
            return String.format(remainingAnswersCountMessageMany, wrongAnswersLimit- failureCount);
        else if (failureCount <8)
            return String.format(remainingAnswersCountMessageFew, wrongAnswersLimit - failureCount);
        else
            return String.format(remainingAnswersCountMessageLast, wrongAnswersLimit - failureCount);
    }

    private ChatBotResponse processGiveUpRequest(ChatBotEvent event) {
        var giveUpCount= db.getGiveUpRequestsCount(event.chatId);

        var response = "";
        if (giveUpCount < giveUpCountRequired ) {
            db.giveUpRequestsCountIncrement(event.chatId);
            response = String.format(giveUpCount==1?giveUpMessage2:giveUpMessage,
                    giveUpCountRequired - giveUpCount);
        }
        else
        {
            resetQuestion(event);
            var question = updateQuestion(event);
            response = questionResetMessage
                    + question.question;

        }
        return event.toResponse(response);
    }
}