package com.company.database;

public interface ICountRepository {
    int Get(long chatId);
    void Increment(long chatId);
    void Reset(long chatId);
}
