package com.company.database;

import com.google.inject.Inject;

public class GiveUpRequestsCountRepositorySQLite implements IGiveUpRequestsCountRepository {
    private final CountRepositorySQLite countRepo;

    @Inject
    public GiveUpRequestsCountRepositorySQLite(IDatabaseCoreSQLite db) {
        countRepo = new CountRepositorySQLite(db, "giveUpRequests");
    }

    public int Get(long chatId) {
        return countRepo.Get(chatId);
    }

    public void Increment(long chatId) {
        countRepo.Increment(chatId);
    }

    public void Reset(long chatId) {
        countRepo.Reset(chatId);
    }
}