package com.company.database;

public interface IQuestionIdRepository {
    int Get(long chatId);
    void Set(long chatId, int questionId);
}
