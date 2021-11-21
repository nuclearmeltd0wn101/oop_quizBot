package com.company;

public class TestLogic implements IChatBotLogic {
    public ChatBotResponse handler(ChatBotEvent event) {
        if (!event.isPrivateChat && !event.isMentioned) // ignore public chat w\o mention
            return null;

        var sb = new StringBuilder();
        sb.append("Test response\n");

        sb.append("\nIs private chat: ");
        sb.append(event.isPrivateChat);
        sb.append("\nIs mentioned: ");
        sb.append(event.isMentioned);
        sb.append("\nChat ID: ");
        sb.append(event.chatId);

        sb.append("\nSender ID: ");
        sb.append(event.senderId);
        sb.append("\nSender Username: ");
        sb.append(event.senderUsername);
        sb.append("\n\nMessage: ");
        sb.append(event.message);

        return event.toResponse(sb.toString());
    }
}
