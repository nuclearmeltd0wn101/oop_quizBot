package com.company;

public interface IUserNamesRepository {
    String Get(long userId);
    void Set(long userId, String name);
}
