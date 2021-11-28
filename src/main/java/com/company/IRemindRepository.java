package com.company;

public interface IRemindRepository {
    InactiveChatInfo getChat();
    void updateLastActiveTimestamp(long chatId);
    void incrementRemindAttemptsCount(long chatId);
}
