package com.company;

public interface IStatesRepository {
    long Get(long chatId);
    void Set(long chatId, long state);
}
