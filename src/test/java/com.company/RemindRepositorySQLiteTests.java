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
    RemindPolicy remindPolicy;
    RemindRepositorySQLite sut;

    Long chatId = 1337L;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
        remindPolicy = new RemindPolicy();
        sut = new RemindRepositorySQLite(dbCore, remindPolicy);
    }

    @Test
    void getChat_noInactiveChatsInDB_returnsNull() {
        Mockito.when(
                        dbCore.Get(Mockito.anyString(),
                                Mockito.any(Function.class)))
                .thenReturn(new ArrayList<Long>());

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
    void getChat_thereIsInactiveChatsInDB_returnsItsId() {
        var inactiveChatInfo = new InactiveChatInfo(chatId, false);
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
    void updateLastActiveTimestamp_passesCorrectCoreRequest() {
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
    void incrementRemindAttemptsCount_passesCorrectCoreRequest() {
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