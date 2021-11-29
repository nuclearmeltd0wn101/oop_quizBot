package com.company.database;

import com.company.quiz.QuizScore;

public interface IScoreRepository {
    void Increment(long chatId, long userId);
    QuizScore[] GetTable(long chatId);
}
