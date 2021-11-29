package com.company;

public interface IScoreRepository {
    void Increment(long chatId, long userId);
    QuizScore[] GetTable(long chatId);
}
