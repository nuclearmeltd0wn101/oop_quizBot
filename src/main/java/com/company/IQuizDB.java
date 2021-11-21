package com.company;

public interface IQuizDB {
    public void scoreIncrement(long chatId, long userId);
    public QuizScore[] getScoreTable(long chatId);

    public long getState(long chatId);
    public void setState(long chatId, long state);

    public int getQuestionId(long chatId);
    public void setQuestionId(long chatId, int questionId);

    public int getWrongAnswersCount(long chatId);
    public void wrongAnswersCountIncrement(long chatId);
    public void wrongAnswersCountReset(long chatId);

    public int getGiveUpRequestsCount(long chatId);
    public void giveUpRequestsCountIncrement(long chatId);
    public void giveUpRequestsCountReset(long chatId);

    public String getUserName(long userId);
    public void setUserName(long userId, String name);

    public void setRemindPolicy(long remindDelaySeconds, int maxRemindAttempts);
    public InactiveChatInfo getInactiveChat();
    public void updateChatLastActiveTimestamp(long chatId);
}
