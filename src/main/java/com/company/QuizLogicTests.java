package com.company;

import com.company.botBehavior.ChatBotEvent;
import com.company.botBehavior.ChatBotResponse;
import com.company.botBehavior.SelfInducedEvent;
import com.company.botBehavior.SelfInducedHandler;
import com.company.database.*;
import com.company.quiz.DisplayOfScore;
import com.company.quiz.QuizLogic;
import com.company.quiz.QuizQuestion;
import com.company.quiz.UserCommands;
import com.pengrad.telegrambot.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

public class QuizLogicTests {
    private QuizLogic sut;
    ArrayList<QuizQuestion> questions;
    IQuestionIdRepository questionRepo;
    IRemindRepository remindRepo;
    IScoreRepository scoreRepo;
    IStatesRepository statesRepo;
    IUserNamesRepository userNameRepo;
    IWrongAnswersCountRepository wrongRepo;
    IGiveUpRequestsCountRepository giveUpRepo;
    SelfInducedHandler selfInducedHandler;
    DisplayOfScore displayOfScore;
    ChatBotEvent defaultEvent;

    @BeforeEach
    public void SetUp() {
        questions = new ArrayList<>();
        questionRepo = Mockito.mock(QuestionIdRepositorySQLite.class);
        remindRepo = Mockito.mock(RemindRepositorySQLite.class);
        scoreRepo = Mockito.mock(ScoreRepositorySQLite.class);
        statesRepo = Mockito.mock(StatesRepositorySQLite.class);
        userNameRepo = Mockito.mock(UserNamesRepositorySQLite.class);
        wrongRepo = Mockito.mock(WrongAnswersCountRepositorySQLite.class);
        giveUpRepo = Mockito.mock(GiveUpRequestsCountRepositorySQLite.class);
        selfInducedHandler = Mockito.mock(SelfInducedHandler.class);
        displayOfScore = Mockito.mock(DisplayOfScore.class);
        sut = new QuizLogic(questions, questionRepo, remindRepo, scoreRepo, statesRepo, userNameRepo, wrongRepo, giveUpRepo);
        sut.displayOfScore = displayOfScore;
        sut.selfInducedHandler = selfInducedHandler;
        defaultEvent = new ChatBotEvent(0L, 0L, "", "", true, false);
    }

    @Test
    public void handle_ShouldReturnNull_IfIsNotMentionedAndNotPrivateChat() {
        var event = new ChatBotEvent(0L, 0L, "", "", false, false);

        var response = sut.handle(event);

        Assertions.assertNull(response);
    }

    @Test
    public void handle_ShouldCallSelfInducedHandler_IfSelfInducedEvent() {
        var event = new SelfInducedEvent();

        sut.handle(event);

        Mockito.verify(selfInducedHandler, Mockito.times(1)).induce();
    }

    @Test
    public void handle_ShouldUpdateLastActiveTimestamp() {
        sut.handle(defaultEvent);

        Mockito.verify(remindRepo, Mockito.times(1)).updateLastActiveTimestamp(defaultEvent.chatId);
    }

    @Test
    public void handle_ShouldGetStateOnce() {
        sut.handle(defaultEvent);

        Mockito.verify(statesRepo, Mockito.times(1)).Get(defaultEvent.chatId);
    }

    @Test
    public void handle_ShouldGetScore_IfMessageContainsScoreCommand() {
        var displayScoreEvent = new ChatBotEvent(0L, 0L, "userName", UserCommands.Score.text, true, false);
        Mockito.when(displayOfScore.display(displayScoreEvent)).thenReturn(new ChatBotResponse(0L, ""));
        sut.handle(displayScoreEvent);

        Mockito.verify(displayOfScore, Mockito.times(1)).display(displayScoreEvent);
    }

}
