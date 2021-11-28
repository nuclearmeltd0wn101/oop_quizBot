package com.company;

import java.util.ArrayList;
import java.util.Collections;

public class SelfInducedHandler {
    private final IQuizDB db;
    private ArrayList<String> messages;
    public SelfInducedHandler(IQuizDB db,ArrayList<String> messages)
    {
        this.db=db;
        this.messages=messages;
    }
    public ChatBotResponse induce() {
        var inactiveInfo = db.getInactiveChat();
        if (inactiveInfo == null)
            return null;
        Collections.shuffle(messages);
        var response = new ChatBotResponse(inactiveInfo.chatId, messages.get(0));

        return inactiveInfo.isAnyMore
                ? response.SelfInducedNotOverYet()
                : response;
    }
}
