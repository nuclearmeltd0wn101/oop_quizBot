package com.company;

public class ChatBotEvent implements IEvent{
    public final long chatId;
    public final long senderId;
    public final String senderUsername;
    public final String message;
    public final boolean isPrivateChat;
    public final boolean isMentioned;

    public ChatBotEvent(long chatId, long senderId, String senderUsername,
                        String message, boolean isPrivateChat, boolean isMentioned)
    {

        this.chatId = chatId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.message = message;
        this.isPrivateChat = isPrivateChat;
        this.isMentioned = isMentioned;

    }

    public ChatBotEvent(long senderId, String senderUsername, String message)
    {

        this.chatId = senderId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.message = message;
        this.isPrivateChat = true;
        this.isMentioned = false;

    }

    public ChatBotEvent toChatMessage(long chatId, boolean isMentioned)
    {
        return new ChatBotEvent(chatId, senderId, senderUsername, message,
                false, isMentioned);
    }
    public ChatBotResponse toResponse(String message)
    {
        return new ChatBotResponse(chatId, message);
    }
}
