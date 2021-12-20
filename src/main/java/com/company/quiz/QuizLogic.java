package com.company.quiz;

import com.company.botBehavior.*;
import com.company.database.*;
import javax.inject.Inject;
import java.util.ArrayList;
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

    @Inject
    private Random rand = new Random();

    private final IQuestionIdRepository questionRepo;
    private final IRemindRepository remindRepo;
    private final IScoreRepository scoreRepo;
    private final IStatesRepository statesRepo;
    private final IUserNamesRepository userNameRepo;
    private final IWrongAnswersCountRepository wrongRepo;
    private final IGiveUpRequestsCountRepository giveUpRepo;

    @Inject
    public SelfInducedHandler selfInducedHandler;

    @Inject
    public DisplayOfScore displayOfScore;

    @Inject
    public QuizLogic(ArrayList<QuizQuestion> questions,
                     IQuestionIdRepository questionRepo,
                     IRemindRepository remindRepo,
                     IScoreRepository scoreRepo,
                     IStatesRepository statesRepo,
                     IUserNamesRepository userNameRepo,
                     IWrongAnswersCountRepository wrongRepo,
                     IGiveUpRequestsCountRepository giveUpRepo)
    {
        this.questions = questions;
        this.questionRepo = questionRepo;
        this.remindRepo = remindRepo;
        this.scoreRepo = scoreRepo;
        this.statesRepo = statesRepo;
        this.userNameRepo = userNameRepo;
        this.wrongRepo = wrongRepo;
        this.giveUpRepo = giveUpRepo;
    }

    private ChatBotResponse quizHandler(ChatBotEvent event, State state) {
        if (event.message.contains(UserCommands.Score.text)) {
            return displayOfScore.display(event)
                    .AddTelegramSticker(Stickers.Score.token);
        }

        if (event.message.contains(UserCommands.Help.text) || event.message.contains(UserCommands.Start.text))
            return event.toResponse(
                            !event.isPrivateChat
                                    ? StringConstants.greetMessageChat
                                    : StringConstants.greetMessagePM)
                    .AddTelegramSticker(Stickers.Greet.token);
        if (event.message.contains(UserCommands.Repeat.text)) {
            return event.toResponse(questions.get(questionRepo.Get(event.chatId)).question);
        }
        if (event.message.toLowerCase().contains(UserCommands.Question.text)) {
            if (state != State.Inactive)
                return event.toResponse(StringConstants.questionAlreadyExistMessage)
                        .AddTelegramSticker(Stickers.QuestionAlreadyExists.token);

            var question = updateQuestion(event);
            return event.toResponse(question.question)
                    .AddTelegramSticker(Stickers.Question.token);
        }

        if (state != State.Inactive) {
            var questionId = questionRepo.Get(event.chatId);
            var question = questions.get(questionId);

            if (question.validateAnswer(event.message.toLowerCase())) {
                userNameRepo.Set(event.senderId, event.senderUsername);
                scoreRepo.Increment(event.chatId, event.senderId);
                resetQuestion(event);
                return event.toResponse(StringConstants.messageRightAnswer)
                        .AddTelegramSticker(Stickers.RightAnswer.token);
            }

            if (event.message.toLowerCase().contains(UserCommands.ThrowUp.text))
                return processGiveUpRequest(event);

            return processWrongAnswer(event, question);
        }

        return event.toResponse(StringConstants.messageHelpHint);
    }


    public ChatBotResponse handle(IEvent event) {

        if (event instanceof SelfInducedEvent)
            return selfInducedHandler.induce();
        if (event instanceof ChatBotEvent chatBotEvent) {
            if (!chatBotEvent.isPrivateChat && !chatBotEvent.isMentioned) // ignore public chat w\o mention
                return null;

            remindRepo.updateLastActiveTimestamp(chatBotEvent.chatId);
            var state = statesRepo.Get(chatBotEvent.chatId) == 0 ? State.Inactive : State.WaitingForTheAnswer;
            return quizHandler(chatBotEvent, state);
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
                .AddTelegramSticker(Stickers.WrongAnswer.token);
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