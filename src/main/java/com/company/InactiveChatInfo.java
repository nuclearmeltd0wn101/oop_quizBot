package com.company;

public class InactiveChatInfo {
    public final long chatId;
    public final boolean isAnyMore;

    public InactiveChatInfo(long chatId, boolean isAnyMore) {
        this.chatId = chatId;
        this.isAnyMore = isAnyMore;
    }
}
