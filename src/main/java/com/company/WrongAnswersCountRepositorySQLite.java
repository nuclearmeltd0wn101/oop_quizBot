package com.company;

public class WrongAnswersCountRepositorySQLite implements IWrongAnswersCountRepository {
    private final CountRepositorySQLite countRepo;

    public WrongAnswersCountRepositorySQLite(IDatabaseCoreSQLite db) {
        countRepo = new CountRepositorySQLite(db, "wrongAnswers");
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