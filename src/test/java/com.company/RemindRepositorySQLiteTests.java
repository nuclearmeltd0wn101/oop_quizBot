package com.company;

import com.company.botBehavior.InactiveChatInfo;
import com.company.botBehavior.RemindPolicy;
import com.company.database.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.function.Function;

public class RemindRepositorySQLiteTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

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