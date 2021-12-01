package com.company.quiz;

import com.company.botBehavior.*;
import com.company.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {
    enum State {
        Inactive,
        WaitingForTheAnswer
    }

    private static final int giveUpCountRequired = 2;
    private static final int wrongAnswersLimit = 9;
    private static final int firstHintThreshold = 2;
    private static final int secondHintThreshold = 5;

    private final ArrayList<QuizQuestion> questions;
    private final Random rand;
    private final IQuestionIdRepository questionRepo;
    private final IRemindRepository remindRepo;
    private final IScoreRepository scoreRepo;
    private final IStatesRepository statesRepo;
    private final IUserNamesRepository userNameRepo;
    private final IWrongAnswersCountRepository wrongRepo;
    private final IGiveUpRequestsCountRepository giveUpRepo;


    public QuizLogic(ArrayList<QuizQuestion> questions, IQuestionIdRepository questionRepo,
                     IRemindRepository remindRepo, IScoreRepository scoreRepo, IStatesRepository statesRepo,
                     IUserNamesRepository userNameRepo, IWrongAnswersCountRepository wrongRepo, IGiveUpRequestsCountRepository giveUpRepo) {
        this.questions = questions;
        rand = new Random();
        this.questionRepo = questionRepo;
        this.remindRepo = remindRepo;
        this.scoreRepo = scoreRepo;
        this.statesRepo = statesRepo;
        this.userNameRepo = userNameRepo;
        this.wrongRepo = wrongRepo;
        this.giveUpRepo = giveUpRepo;
    }

    private ChatBotResponse quizHandler(ChatBotEvent event, State state) {
        if (event.message.contains("/score")) {
            return new DisplayOfScore(scoreRepo, userNameRepo).display(event)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDVPlhmilWc7ZzcjRMtge8ij3llCTEQAACYwQAAs7Y6Asx61tywusibCIE");
        }

        if (event.message.contains("/help") || event.message.contains("/start"))
            return event.toResponse(
                            !event.isPrivateChat
                                    ? StringConstants.greetMessageChat
                                    : StringConstants.greetMessagePM)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDShFhkmhuE5lz_InXvOrrxZifKKaxYQACuwIAAqKK8QdcF8HD_GCZXyIE");
        if (event.message.contains("повтор")) {
            return event.toResponse(questions.get(questionRepo.Get(event.chatId)).question);
        }
        if (event.message.toLowerCase().contains("вопрос")) {
            if (state != State.Inactive)
                return event.toResponse(StringConstants.questionAlreadyExistMessage)
                        .AddTelegramSticker("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE");

            var question = updateQuestion(event);
            return event.toResponse(question.question)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE");
        }

        if (state != State.Inactive) {
            var questionId = questionRepo.Get(event.chatId);
            var question = questions.get(questionId);

            if (question.validateAnswer(event.message.toLowerCase())) {
                userNameRepo.Set(event.senderId, event.senderUsername);
                scoreRepo.Increment(event.chatId, event.senderId);
                resetQuestion(event);
                return event.toResponse(StringConstants.messageRightAnswer)
                        .AddTelegramSticker("CAACAgIAAxkBAAEDSjthkoEJoQKIsjn-1zi9UzVQFkI-jAAC4w0AArAsKUkmVocAAbI_aIAiBA");
            }

            if (event.message.toLowerCase().contains("сдаюсь"))
                return processGiveUpRequest(event);

            return processWrongAnswer(event, question);
        }

        return event.toResponse(StringConstants.messageHelpHint);
    }


    public ChatBotResponse handler(IEvent event) {

        if (event instanceof SelfInducedEvent)
            return new SelfInducedHandler(remindRepo, new ArrayList<>(Arrays.
                    asList("ты забыл обо мне?", "не хотите сыграть?", "готов задать вопрос", "Давай сыграем!"
                            , "проверим твою эрудицию?"))).induce();
        if (event instanceof ChatBotEvent cast) {
            if (!cast.isPrivateChat && !cast.isMentioned) // ignore public chat w\o mention
                return null;

            remindRepo.updateLastActiveTimestamp(cast.chatId);
            var state = statesRepo.Get(cast.chatId) == 0 ? State.Inactive : State.WaitingForTheAnswer;
            return quizHandler(cast, state);
        }
        throw new IllegalStateException();
    }

    private void resetQuestion(ChatBotEvent event) {
        giveUpRepo.Reset(event.chatId);
        wrongRepo.Reset(event.chatId);
        statesRepo.Set(event.chatId, 0);
    }

    private QuizQuestion updateQuestion(ChatBotEvent event) {
        var questionId = rand.nextInt(questions.size());
        questionRepo.Set(event.chatId, questionId);

        statesRepo.Set(event.chatId, 1);
        return questions.get(questionId);
    }

    private ChatBotResponse processWrongAnswer(ChatBotEvent event, QuizQuestion question) {
        var failureCount = wrongRepo.Get(event.chatId);
        if (failureCount == wrongAnswersLimit) {
            resetQuestion(event);
            return event.toResponse(StringConstants.wrongAnswersLimitMessage);
        }

        var sb = new StringBuilder(
                getRemainingAnswersCountMessage(failureCount));

        if (failureCount >= firstHintThreshold)
            sb.append(String.format(StringConstants.firstHintMessage,
                    question.answerHintFirstLetter()));

        if (failureCount >= secondHintThreshold)
            sb.append(String.format(StringConstants.secondHintMessage,
                    question.answerHintLength()));

        wrongRepo.Increment(event.chatId);
        return event.toResponse(sb.toString())
                .AddTelegramSticker("CAACAgIAAxkBAAEDSjNhkoAkb9KIVhJ0xTBLBn5HdDeE5QACrBIAAmCRIEnnz3aDncA0fCIE");
    }

    private String getRemainingAnswersCountMessage(int failureCount) {
        if (failureCount < 5)
            return String.format(StringConstants.remainingAnswersCountMessageMany, wrongAnswersLimit - failureCount);
        else if (failureCount < 8)
            return String.format(StringConstants.remainingAnswersCountMessageFew, wrongAnswersLimit - failureCount);
        else
            return String.format(StringConstants.remainingAnswersCountMessageLast, wrongAnswersLimit - failureCount);
    }

    private ChatBotResponse processGiveUpRequest(ChatBotEvent event) {
        var giveUpCount = giveUpRepo.Get(event.chatId);

        var response = "";
        if (giveUpCount < giveUpCountRequired) {
            giveUpRepo.Increment(event.chatId);
            response = String.format(giveUpCount == 1 ? StringConstants.giveUpMessage2 : StringConstants.giveUpMessage,
                    giveUpCountRequired - giveUpCount);
        } else {
            resetQuestion(event);
            var question = updateQuestion(event);
            response = StringConstants.questionResetMessage
                    + question.question;

        }
        return event.toResponse(response);
    }
}