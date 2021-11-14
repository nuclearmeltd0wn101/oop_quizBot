package com.company;

public class ChatBotEvent {
    public final long chatId;
    public final long senderId;
    public final String senderUsername;
    public final String message;
    public final boolean isPrivateChat;
    public final boolean isMentioned;
    public final boolean isSelfInduced;

    public ChatBotEvent()
    {
        this.isSelfInduced = true;
        this.chatId = 0;
        this.senderId = 0;
        this.senderUsername = null;
        this.isMentioned = false;
        this.message = null;
        this.isPrivateChat = false;
    }

    public ChatBotEvent(long chatId, long senderId, String senderUsername,
                        String message, boolean isPrivateChat, boolean isMentioned)
    {
        this.isSelfInduced = false;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.message = message;
        this.isPrivateChat = isPrivateChat;
        this.isMentioned = isMentioned;
    }

    public ChatBotEvent(long senderId, String senderUsername, String message)
    {
        this.isSelfInduced = false;
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
        if (isSelfInduced) // because in this case you have to manually define where to send response
            throw new IllegalStateException();

        return new ChatBotResponse(chatId, message);
    }
}
