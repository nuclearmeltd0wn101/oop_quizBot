package com.company.botBehavior;

import com.company.database.IRemindRepository;

import java.util.ArrayList;
import java.util.Collections;

public class SelfInducedHandler {
    private final IRemindRepository remindRepo;
    private final ArrayList<String> messages;
    public SelfInducedHandler(IRemindRepository remindRepo,ArrayList<String> messages)
    {
        this.remindRepo=remindRepo;
        this.messages=messages;
    }
    public ChatBotResponse induce() {
        var inactiveInfo = remindRepo.getChat();
        if (inactiveInfo == null)
            return null;
        remindRepo.incrementRemindAttemptsCount(inactiveInfo.chatId);
        Collections.shuffle(messages);
        var response = new ChatBotResponse(inactiveInfo.chatId, messages.get(0));

        return inactiveInfo.isAnyMore
                ? response.SelfInducedNotOverYet()
                : response;
    }
}
