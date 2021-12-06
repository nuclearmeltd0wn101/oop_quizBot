package com.company.botBehavior;

import com.company.database.IRemindRepository;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.Random;

public class SelfInducedHandler {
    private final IRemindRepository remindRepo;
    private final ArrayList<String> messages;

    @Inject
    private Random rand;

    @Inject
    public SelfInducedHandler(IRemindRepository remindRepo,
                              @Named("remindMessages") ArrayList<String> messages) {
        this.remindRepo = remindRepo;
        this.messages = messages;
    }

    public ChatBotResponse induce() {
        var inactiveInfo = remindRepo.getChat();
        if (inactiveInfo == null)
            return null;
        remindRepo.incrementRemindAttemptsCount(inactiveInfo.chatId);
        var messageNum = rand.nextInt(messages.size());
        var response = new ChatBotResponse(inactiveInfo.chatId, messages.get(messageNum));

        return inactiveInfo.isAnyMore
                ? response.SelfInducedNotOverYet()
                : response;
    }
}
