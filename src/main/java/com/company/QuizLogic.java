package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {
    enum State
    {
        Inactive,
        WaitingForTheAnswer
    }
    private static final int giveUpCountRequired = 2;
    private static final int wrongAnswersLimit = 9;
    private static final int firstHintThreshold = 2;
    private static final int secondHintThreshold = 5;

    private final ArrayList<QuizQuestion> questions;
    private final Random rand;
    private final IQuizDB db;
    public QuizLogic(ArrayList<QuizQuestion> questions, IQuizDB db)
    {
        this.questions = questions;
        rand = new Random();
        this.db = db;
    }

    public void setRemindPolicy(long remindDelaySeconds, int maxRemindAttempts) {
        db.setRemindPolicy(remindDelaySeconds, maxRemindAttempts);
    }
    private ChatBotResponse quizHandler(ChatBotEvent event, State state) {
        if (event.message.contains("/score"))
        {
            return new DisplayOfScore(db).display(event)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDVPlhmilWc7ZzcjRMtge8ij3llCTEQAACYwQAAs7Y6Asx61tywusibCIE");
        }

        if (event.message.contains("/help") || event.message.contains("/start") )
            return event.toResponse(
                    !event.isPrivateChat
                            ? String.valueOf(Constants.greetMessageChat)
                            : String.valueOf(Constants.greetMessagePM))
                    .AddTelegramSticker("CAACAgIAAxkBAAEDShFhkmhuE5lz_InXvOrrxZifKKaxYQACuwIAAqKK8QdcF8HD_GCZXyIE");
        if (event.message.contains("повтор"))
        {
            return event.toResponse(questions.get(db.getQuestionId(event.chatId)).question);
        }
        if (event.message.toLowerCase().contains("вопрос"))
        {
            if (state != State.Inactive)
                return event.toResponse(String.valueOf(Constants.questionAlreadyExistMessage))
                        .AddTelegramSticker("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE");

            var question = updateQuestion(event);
            return event.toResponse(question.question)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE");
        }

        if (state != State.Inactive) {
            var questionId= db.getQuestionId(event.chatId);
            var question = questions.get(questionId);

            if (question.validateAnswer(event.message.toLowerCase())) {
                db.setUserName(event.senderId, event.senderUsername);
                db.scoreIncrement(event.chatId, event.senderId);
                resetQuestion(event);
                return event.toResponse(String.valueOf(Constants.messageRightAnswer))
                        .AddTelegramSticker("CAACAgIAAxkBAAEDSjthkoEJoQKIsjn-1zi9UzVQFkI-jAAC4w0AArAsKUkmVocAAbI_aIAiBA");
            }

            if (event.message.toLowerCase().contains("сдаюсь"))
                return processGiveUpRequest(event);

            return processWrongAnswer(event, question);
        }

        return event.toResponse(String.valueOf(Constants.messageHelpHint));
    }


    public ChatBotResponse handler(ChatBotEvent event) {
        if (event.isSelfInduced)
            return new SelfInducedHandler(db, new ArrayList<>(Arrays.
                    asList("ты забыл обо мне?", "не хотите сыграть?", "готов задать вопрос", "Давай сыграем!"
                            , "проверим твою эрудицию?"))).induce();

        if (!event.isPrivateChat && !event.isMentioned) // ignore public chat w\o mention
            return null;

        db.updateChatLastActiveTimestamp(event.chatId);
        var state=db.getState(event.chatId)==0?State.Inactive:State.WaitingForTheAnswer;
        return quizHandler(event, state);
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
            return event.toResponse(String.valueOf(Constants.wrongAnswersLimitMessage));
        }

        var sb = new StringBuilder(
                getRemainingAnswersCountMessage(failureCount));

        if (failureCount >= firstHintThreshold)
            sb.append(String.format(String.valueOf(Constants.firstHintMessage),
                    question.answerHintFirstLetter()));

        if (failureCount >= secondHintThreshold)
            sb.append(String.format(String.valueOf(Constants.secondHintMessage),
                    question.answerHintLength()));

        db.wrongAnswersCountIncrement(event.chatId);
        return event.toResponse(sb.toString())
                .AddTelegramSticker("CAACAgIAAxkBAAEDSjNhkoAkb9KIVhJ0xTBLBn5HdDeE5QACrBIAAmCRIEnnz3aDncA0fCIE");
    }

    private String getRemainingAnswersCountMessage(int failureCount) {
        if (failureCount < 5)
            return String.format(String.valueOf(Constants.remainingAnswersCountMessageMany), wrongAnswersLimit- failureCount);
        else if (failureCount <8)
            return String.format(String.valueOf(Constants.remainingAnswersCountMessageFew), wrongAnswersLimit - failureCount);
        else
            return String.format(String.valueOf(Constants.remainingAnswersCountMessageLast), wrongAnswersLimit - failureCount);
    }

    private ChatBotResponse processGiveUpRequest(ChatBotEvent event) {
        var giveUpCount= db.getGiveUpRequestsCount(event.chatId);

        var response = "";
        if (giveUpCount < giveUpCountRequired ) {
            db.giveUpRequestsCountIncrement(event.chatId);
            response = String.format(giveUpCount==1?String.valueOf(Constants.giveUpMessage2):String.valueOf(Constants.giveUpMessage),
                    giveUpCountRequired - giveUpCount);
        }
        else
        {
            resetQuestion(event);
            var question = updateQuestion(event);
            response = String.valueOf(Constants.questionResetMessage)
                    + question.question;

        }
        return event.toResponse(response);
    }
}