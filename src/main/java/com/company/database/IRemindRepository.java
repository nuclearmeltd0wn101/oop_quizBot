package com.company.database;

import com.company.botBehavior.InactiveChatInfo;

public interface IRemindRepository {
    InactiveChatInfo getChat();
    void updateLastActiveTimestamp(long chatId);
    void incrementRemindAttemptsCount(long chatId);
}
