package com.company;

import java.util.ArrayList;
import java.util.Hashtable;

public class QuizDBRAM implements IQuizDB {
    private Hashtable<Long, Hashtable<Long, Long>> scoreTable;
    private Hashtable<Long, Long> stateTable;
    private Hashtable<Long, Long> questionIdTable;

    public QuizDBRAM()
    {
        scoreTable = new Hashtable<>();
        stateTable = new Hashtable<>();
        questionIdTable = new Hashtable<>();
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

        var result = new ArrayList<QuizScore>();
        for (var entry : table.entrySet())
            result.add(new QuizScore(chatId, entry.getKey(), entry.getValue()));

        return (QuizScore[]) result.toArray();
    }

    public long getState(long chatId) {
        if (stateTable.containsKey(chatId))
            return stateTable.get(chatId);
        return 0;
    }

    public void setState(long chatId, long state) {
        stateTable.put(chatId, state);
    }

    public long getQuestionId(long chatId) {
        if (questionIdTable.containsKey(chatId))
            return questionIdTable.get(chatId);
        return 0;
    }

    public void setQuestionId(long chatId, long questionId) {
        questionIdTable.put(chatId, questionId);
    }
}
