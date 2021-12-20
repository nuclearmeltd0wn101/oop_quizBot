package com.company;

import com.company.botBehavior.InactiveChatInfo;
import com.company.botBehavior.RemindPolicy;
import com.company.database.*;
import com.company.quiz.QuizScore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.function.Function;

public class DatabaseTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

    // Score Repository tests

    @Test
    void ScoreRepository_GetTable_NoChatIdRecord_returnsNull() {
        Mockito.when(
                dbCore.Get(Mockito.anyString(),
                        Mockito.any(Function.class)))
                .thenReturn(new ArrayList<QuizScore>());

        var sut = new ScoreRepositorySQLite(dbCore);

        var scoreTable = sut.GetTable(1337);

        Assertions.assertNull(scoreTable);
        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                                1337)
                            ),
                    Mockito.any(Function.class));
    }

    @Test
    void ScoreRepository_GetTable_sortedByScoreDescending() {
        var chatId = 1337;
        var dbCoreGetMockResponse = new ArrayList<QuizScore>();
        dbCoreGetMockResponse.add(new QuizScore(1337, 4, 172));
        dbCoreGetMockResponse.add(new QuizScore(1337, 1, 16));
        dbCoreGetMockResponse.add(new QuizScore(1337, 3, 8));
        dbCoreGetMockResponse.add(new QuizScore(1337, 2, 3));

        Mockito.when(dbCore.Get(Mockito.anyString(),
                                Mockito.any(Function.class)))
                .thenReturn(dbCoreGetMockResponse);

        var scoreRepo = new ScoreRepositorySQLite(dbCore);
        var response = scoreRepo.GetTable(chatId);


        for (var i = 0; i < response.length - 1; i++)
            Assertions.assertTrue((response[i + 1]).score <= (response[i]).score);

        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                    SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                                    chatId)
                    ), Mockito.any(Function.class));
    }

    @Test
    void ScoreRepository_Increment_passesCorrectCoreRequest() {
        var chatId = 1337;
        var userId = 127001;

        var sut = new ScoreRepositorySQLite(dbCore);

        sut.Increment(chatId, userId);

        Mockito.verify(dbCore,
                Mockito.times(1))
                .Save(
                    Mockito.eq(
                            new String[] {
                                    String.format(
                                            SQLRequestsTemplates.ScoreRepo_InsertRecord.value,
                                            chatId, userId),
                                    String.format(
                                            SQLRequestsTemplates.ScoreRepo_Increment.value,
                                            chatId, userId)
                            }
                    )
        );
    }

    // States Repository tests

    @Test
    void StatesRepository_Get_NonexistentChatId_ReturnsZero() {
        var chatIdNonexistent = 321;
        var defaultValue = 0L;

        Mockito.when(dbCore.Get(
                Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new StatesRepositorySQLite(dbCore);

        var responseNonexistent = sut.Get(chatIdNonexistent);

        Assertions.assertEquals(responseNonexistent, defaultValue);
        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                    SQLRequestsTemplates.StatesRepo_GetRecord.value,
                                    chatIdNonexistent)),
                    Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                    Mockito.eq(defaultValue));
    }

    @Test
    void StatesRepository_Get_ExistentChatId_GeneralCorrectness() {
        var chatIdExistent = 123569;
        var defaultValue = 0L;
        var existentValue = 3L;
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.StatesRepo_GetRecord.value,
                chatIdExistent);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(existentValue);

        var sut = new StatesRepositorySQLite(dbCore);

        var responseExistent = sut.Get(chatIdExistent);

        Assertions.assertEquals(responseExistent, existentValue);
        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(correctSqlRequest),
                    Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                    Mockito.eq(defaultValue));
    }

    @Test
    void StatesRepository_Set_GeneralCorrectness() {
        var chatId = 123569;
        var stateToSet = 3;

        var sut = new StatesRepositorySQLite(dbCore);

        sut.Set(chatId, stateToSet);

        Mockito.verify(dbCore,
                Mockito.times(1))
                .Save(
                    Mockito.eq(
                        new String[] {
                            String.format(SQLRequestsTemplates.StatesRepo_InsertRecord.value,
                                    stateToSet, chatId),
                            String.format(SQLRequestsTemplates.StatesRepo_UpdateRecord.value,
                                    stateToSet, chatId)
                        }));
    }

    // QuestionId Repository tests

    @Test
    void QuestionIdRepository_Get_noQuestionIdForChat_returnsZero() {
        var defaultValue = 0L;
        var chatId = 123569;

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new QuestionIdRepositorySQLite(dbCore);

        var response = sut.Get(chatId);

        Assertions.assertEquals(defaultValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                    SQLRequestsTemplates.QuestionIdRepo_GetRecord.value,
                                        chatId)),
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)
                );
    }

    @Test
    void QuestionIdRepository_Get_questionIdForChatIsSet_GeneralCorrectness() {
        var defaultValue = 0L;
        var chatId = 123569;
        var questionId = 3L;
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.QuestionIdRepo_GetRecord.value,
                chatId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(questionId);

        var sut = new QuestionIdRepositorySQLite(dbCore);

        var response = sut.Get(chatId);

        Assertions.assertEquals(questionId, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)
                );
    }

    @Test
    void QuestionIdRepository_Set_GeneralCorrectness() {
        var chatId = 123569;
        var questionId = 3;

        var sut = new QuestionIdRepositorySQLite(dbCore);

        sut.Set(chatId, questionId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_InsertRecord.value,
                                                questionId, chatId),
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_UpdateRecord.value,
                                                questionId, chatId)
                                }));
    }

    // Count Repository tests (covers BOTH WrongAnswersCount and GiveUpRequestsCount Repos!)

    @Test
    void CountRepository_Get_noRecordForChatId_returnsZero()
    {
        var defaultValue = 0L;
        var chatId = 123569;
        var countName = "test";

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new CountRepositorySQLite(dbCore, countName);

        var response = sut.Get(chatId);

        Assertions.assertEquals(defaultValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.CountRepo_GetRecord.value,
                                        countName, chatId)),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)
                );
    }

    @Test
    void CountRepository_Get_ExistentChatId_GeneralCorrectness() {
        var chatIdExistent = 123569;
        var defaultValue = 0L;
        var existentValue = 3L;
        var countName = "test";
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.CountRepo_GetRecord.value,
                countName, chatIdExistent);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(existentValue);

        var sut = new CountRepositorySQLite(dbCore, countName);

        var responseExistent = sut.Get(chatIdExistent);

        Assertions.assertEquals(responseExistent, existentValue);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void CountRepository_Increment_passesCorrectCoreRequest() {
        var chatId = 1337;
        var countName = "test";

        var sut = new CountRepositorySQLite(dbCore, countName);

        sut.Increment(chatId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(
                                                SQLRequestsTemplates.CountRepo_InsertRecord.value,
                                                countName, chatId),
                                        String.format(
                                                SQLRequestsTemplates.CountRepo_Increment.value,
                                                countName, chatId)
                                }));
    }

    @Test
    void CountRepository_Reset_passesCorrectCoreRequest() {
        var chatId = 1337;
        var countName = "test";

        var sut = new CountRepositorySQLite(dbCore, countName);

        sut.Reset(chatId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(
                                                SQLRequestsTemplates.CountRepo_Reset.value,
                                                countName, chatId)
                                }));
    }

    // UserNames Repository tests

    @Test
    void UserNamesRepository_Get_UnknownUserId_ReturnsCorrectDefaultString() {
        var unknownUserId = 321;
        var defaultValue = String.format("ID %d", unknownUserId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new UserNamesRepositorySQLite(dbCore);

        var responseNonexistent = sut.Get(unknownUserId);

        Assertions.assertEquals(responseNonexistent, defaultValue);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.UserNamesRepo_GetRecord.value,
                                        unknownUserId)),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void UserNamesRepository_Get_KnownUserId_GeneralCorrectness() {
        var knownUserId = 123569;
        var defaultValue = String.format("ID %d", knownUserId);
        var knownValue = "aboba";
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.UserNamesRepo_GetRecord.value,
                knownUserId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(knownValue);

        var sut = new UserNamesRepositorySQLite(dbCore);

        var response = sut.Get(knownUserId);

        Assertions.assertEquals(knownValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void UserNamesRepository_Set_passesCorrectCoreRequest() {
        var userId = 1337;
        var userName = "herbal bebra";
        var escapedName = userName.replace("'", "''");

        var sut = new UserNamesRepositorySQLite(dbCore);

        sut.Set(userId, userName);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(
                                                SQLRequestsTemplates.UserNamesRepo_CreateRecord.value,
                                                escapedName, userId),
                                        String.format(
                                                SQLRequestsTemplates.UserNamesRepo_UpdateRecord.value,
                                                escapedName, userId)
                                }));
    }

    // Remind Repository tests

    // aw sheet here we go again

    @Test
    void RemindRepository_getChat_noInactiveChatsInDB_returnsNull() {
        var remindPolicy = new RemindPolicy();

        Mockito.when(
                        dbCore.Get(Mockito.anyString(),
                                Mockito.any(Function.class)))
                .thenReturn(new ArrayList<Long>());

        var sut = new RemindRepositorySQLite(dbCore, remindPolicy);

        var response = sut.getChat();

        Assertions.assertNull(response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(SQLRequestsTemplates.RemindRepo_GetRecord.value,
                                        remindPolicy.delaySeconds,
                                        remindPolicy.maxAttempts)
                        ),
                        Mockito.any(Function.class));
    }

    @Test
    void RemindRepository_getChat_thereIsInactiveChatsInDB_returnsItsId() {
        var remindPolicy = new RemindPolicy();
        var inactiveChatInfo = new InactiveChatInfo(1337L, false);
        var coreCallResponse = new ArrayList<InactiveChatInfo>();
        coreCallResponse.add(inactiveChatInfo);

        var correctSqlRequest = String.format(
                SQLRequestsTemplates.RemindRepo_GetRecord.value,
                remindPolicy.delaySeconds,
                remindPolicy.maxAttempts);

        Mockito.when(dbCore.Get(
                    Mockito.eq(correctSqlRequest),
                    Mockito.any(Function.class)))
                .thenReturn(coreCallResponse);

        var sut = new RemindRepositorySQLite(dbCore, remindPolicy);

        var response = sut.getChat();

        Assertions.assertEquals(inactiveChatInfo, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.any(Function.class));
    }

    @Test
    void RemindRepository_updateLastActiveTimestamp_passesCorrectCoreRequest() {
        var remindPolicy = new RemindPolicy();
        var chatId = 1337;
        var sut = new RemindRepositorySQLite(dbCore, remindPolicy);

        sut.updateLastActiveTimestamp(chatId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(SQLRequestsTemplates.RemindRepo_CreateRecord.value,
                                                chatId),
                                        String.format(SQLRequestsTemplates.RemindRepo_UpdateRecordTimestamp.value,
                                                chatId)
                                }));
    }

    @Test
    void RemindRepository_incrementRemindAttemptsCount_passesCorrectCoreRequest() {
        var remindPolicy = new RemindPolicy();
        var chatId = 1337;
        var sut = new RemindRepositorySQLite(dbCore, remindPolicy);

        sut.incrementRemindAttemptsCount(chatId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(SQLRequestsTemplates.RemindRepo_IncrementAttemptsRecord.value,
                                                chatId),
                                        String.format(SQLRequestsTemplates.RemindRepo_CleanUpRecords.value,
                                                remindPolicy.maxAttempts)
                                }));
    }
}