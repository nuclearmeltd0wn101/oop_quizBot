package com.company;

public interface IQuizDB {
    public void scoreIncrement(long chatId, long userId);
    public QuizScore[] getScoreTable(long chatId);
    public long getState(long chatId);
    public void setState(long chatId, long state);
    public long getQuestionId(long chatId);
    public void setQuestionId(long chatId, long questionId);
}
