package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class QuizDBRAM implements IQuizDB {
    private final Hashtable<Long, Hashtable<Long, Long>> scoreTable;
    private final Hashtable<Long, Long> stateTable;
    private final Hashtable<Long, Integer> questionIdTable;
    private final Hashtable<Long, Integer> wrongAnswersCountTable;
    private final Hashtable<Long, Integer> giveUpRequestsCountTable;
    private final Hashtable<Long, String> userNamesTable;

    public QuizDBRAM()
    {
        scoreTable = new Hashtable<>();
        stateTable = new Hashtable<>();
        questionIdTable = new Hashtable<>();
        wrongAnswersCountTable = new Hashtable<>();
        giveUpRequestsCountTable = new Hashtable<>();
        userNamesTable = new Hashtable<>();
    }

    public void scoreIncrement(long chatId, long userId) {
        scoreTable.putIfAbsent(chatId, new Hashtable<>());
        var table = scoreTable.get(chatId);
        table.put(userId, 1 + table.getOrDefault(userId, 0L));
    }

    public QuizScore[] getScoreTable(long chatId) {
        if (!scoreTable.containsKey(chatId))
            return null;
        var table = scoreTable.get(chatId);

        var resultList = new ArrayList<QuizScore>();
        for (var entry : table.entrySet())
            resultList.add(new QuizScore(chatId, entry.getKey(), entry.getValue()));

        var result = new QuizScore[resultList.size()];
        resultList.toArray(result);
        Arrays.sort(result, (o1, o2) -> (int)Math.signum(o2.score - o1.score));

        return result;
    }

    public long getState(long chatId) {
        return stateTable.getOrDefault(chatId, 0L);
    }

    public void setState(long chatId, long state) {
        stateTable.put(chatId, state);
    }

    public int getQuestionId(long chatId) {
        return questionIdTable.getOrDefault(chatId, 0);
    }

    public void setQuestionId(long chatId, int questionId) {
        questionIdTable.put(chatId, questionId);
    }

    public int getWrongAnswersCount(long chatId) {
        return wrongAnswersCountTable.getOrDefault(chatId, 0);
    }

    public void wrongAnswersCountIncrement(long chatId) {
        wrongAnswersCountTable.put(chatId, 1
                + wrongAnswersCountTable.getOrDefault(chatId, 0));
    }

    public void wrongAnswersCountReset(long chatId) {
        wrongAnswersCountTable.put(chatId, 0);
    }

    public int getGiveUpRequestsCount(long chatId) {
        return giveUpRequestsCountTable.getOrDefault(chatId, 0);
    }

    public void giveUpRequestsCountIncrement(long chatId) {
        giveUpRequestsCountTable.put(chatId, 1
                + giveUpRequestsCountTable.getOrDefault(chatId, 0));
    }

    public void giveUpRequestsCountReset(long chatId) {
        giveUpRequestsCountTable.put(chatId, 0);
    }

    public String getUserName(long userId) {
        return userNamesTable.getOrDefault(userId, String.format("ID %d", userId));
    }

    public void setUserName(long userId, String name) {
        if (name == null)
            throw new IllegalArgumentException();

        userNamesTable.put(userId, name);
    }

    // I dunno should I actually implement all what I do into this dummy db implementation
    public void setRemindPolicy(long remindDelaySeconds, int maxRemindAttempts) {

    }

    public InactiveChatInfo getInactiveChat() {
        return null;
    }

    public void updateChatLastActiveTimestamp(long userId) {

    }
}
