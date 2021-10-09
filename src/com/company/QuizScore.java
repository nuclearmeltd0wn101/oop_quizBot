package com.company;

public class QuizScore {
    public final long chatId;
    public final long userId;
    public final long score;

    public QuizScore(long chatId, long userId, long score)
    {
        this.chatId = chatId;
        this.userId = userId;
        this.score = score;
    }
}
