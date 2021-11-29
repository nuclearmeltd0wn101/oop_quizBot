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

        db.Save(new String[] {
                "CREATE table if NOT EXISTS chatsInactive (chatId INTEGER PRIMARY KEY, "
                    + "lastActiveTimestampUnix INTEGER, remindAttemptsCount)"
        });
    }

    public InactiveChatInfo getChat() {
        var resultBoxed = db.Get(
                String.format("SELECT chatId FROM chatsInactive WHERE "
                                + "(abs(strftime('%%s', 'now') - lastActiveTimestampUnix) >= %d)"
                                + " AND (remindAttemptsCount < %d)",
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
        db.Save(new String[] { // increment count assuming this method is called for remind formation
                String.format("INSERT OR IGNORE INTO "
                                + "chatsInactive(chatId, lastActiveTimestampUnix, remindAttemptsCount) "
                                + "VALUES (%d, strftime('%%s', 'now'), 0)",
                        chatId),
                String.format("UPDATE chatsInactive"
                            + " SET lastActiveTimestampUnix = strftime('%%s', 'now') WHERE chatId = %d",
                        chatId)
        });
    }

    public void incrementRemindAttemptsCount(long chatId) {
        db.Save(new String[] {
                String.format("UPDATE chatsInactive"
                                + " SET remindAttemptsCount = remindAttemptsCount + 1 WHERE chatId = %s",
                        chatId),
                String.format("DELETE FROM chatsInactive WHERE remindAttemptsCount >= %d",
                remindPolicy.maxAttempts)
        });
    }
}
