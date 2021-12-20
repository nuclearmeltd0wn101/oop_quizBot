package com.company.database;

import com.company.botBehavior.InactiveChatInfo;
import com.company.botBehavior.RemindPolicy;

import java.sql.SQLException;

public class RemindRepositorySQLite implements IRemindRepository {
    private final IDatabaseCoreSQLite db;
    private final RemindPolicy remindPolicy;

    public RemindRepositorySQLite(IDatabaseCoreSQLite db, RemindPolicy remindPolicy) {
        this.db = db;
        this.remindPolicy = remindPolicy;
    }

    public InactiveChatInfo getChat() {
        var resultBoxed = db.Get(
                String.format(SQLRequestsTemplates.RemindRepo_GetRecord.value,
                        remindPolicy.delaySeconds,
                        remindPolicy.maxAttempts),
                response -> {
                    try {
                        return new InactiveChatInfo(
                                response.getLong("chatId"),
                                response.next()
                        );
                    } catch (SQLException e) { return null; }
                });

        if (resultBoxed == null || resultBoxed.size() < 1)
            return null;

        return resultBoxed.get(0);
    }

    public void updateLastActiveTimestamp(long chatId) {
        db.Save(new String[] {
                String.format(SQLRequestsTemplates.RemindRepo_CreateRecord.value,
                        chatId),
                String.format(SQLRequestsTemplates.RemindRepo_UpdateRecordTimestamp.value,
                        chatId)
        });
    }

    public void incrementRemindAttemptsCount(long chatId) {
        db.Save(new String[] {
                String.format(SQLRequestsTemplates.RemindRepo_IncrementAttemptsRecord.value,
                        chatId),
                String.format(SQLRequestsTemplates.RemindRepo_CleanUpRecords.value,
                remindPolicy.maxAttempts)
        });
    }
}
