package com.company;

public class ChatBotResponse {
    public ChatBotResponse(long chatId, String message)
    {
        this.chatId = chatId;
        this.message = message;
    }

    public final long chatId;
    public final String message;
}
