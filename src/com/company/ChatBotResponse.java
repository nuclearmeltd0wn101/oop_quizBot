package com.company;

public class ChatBotResponse {
    public final long chatId;
    public final String message;
    public final boolean isSelfInducedEnd;

    public ChatBotResponse(long chatId, String message, boolean isSelfInducedEnd)
    {
        this.chatId = chatId;
        this.message = message;
        this.isSelfInducedEnd = isSelfInducedEnd;
    }

    public ChatBotResponse(long chatId, String message)
    {
        this(chatId, message, true);
    }

    public ChatBotResponse SelfInducedNotOverYet()
    {
        return new ChatBotResponse(this.chatId, this.message, false);
    }
}
