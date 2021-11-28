package com.company;

public interface IQuestionIdRepository {
    int Get(long chatId);
    void Set(long chatId, int questionId);
}
