package com.company.botBehavior;

public class ChatBotResponse {
    public final long chatId;
    public final String message;
    public final boolean isSelfInducedEnd;
    public final String telegramStickerId;
    public ChatBotResponse(long chatId, String message, boolean isSelfInducedEnd,String telegramStickerId)
    {
        this.chatId = chatId;
        this.message = message;
        this.isSelfInducedEnd = isSelfInducedEnd;
        this.telegramStickerId=telegramStickerId;
    }
    public ChatBotResponse AddTelegramSticker(String telegramStickerId)
    {
        return  new ChatBotResponse(this.chatId,this.message,this.isSelfInducedEnd,telegramStickerId);
    }
    public ChatBotResponse(long chatId, String message)
    {
        this(chatId, message, true,null);
    }
    public ChatBotResponse SelfInducedNotOverYet()
    {
        return new ChatBotResponse(this.chatId, this.message, false,null);
    }
}
