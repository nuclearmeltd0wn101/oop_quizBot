package com.company;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.GetMe;

public class TelegramBotWrapper implements IChatBotWrapper {
    private TelegramBot m_bot;
    private final IChatBotLogic m_botLogic;
    private final String m_botToken;

    public TelegramBotWrapper(IChatBotLogic botLogic, String botToken) {
        m_botLogic = botLogic;
        this.m_botToken = botToken;
    }

    private String getName(User user) {
        if (user.username() != null)
            return user.username();

        if (user.lastName() == null)
            return user.firstName();

        return user.firstName() + " " + user.lastName();
    }

    public void run() {
        if (m_bot != null)
            throw new IllegalStateException();

        m_bot = new TelegramBot(m_botToken);

        var getMeResponse = m_bot.execute(new GetMe());
        var usernameMentionSubstring = "@" + getMeResponse.user().username();

        System.out.println(getMeResponse);

        m_bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                System.out.println(update);

                var source = update.message();
                if (source == null)
                    continue;
                var message = source.text();

                if (message != null) {
                    var senderId = source.from().id();
                    var chatId = source.chat().id();
                    var senderUsername = getName(source.from());

                    var event = new ChatBotEvent(senderId, senderUsername, message);

                    if (source.chat().type().toString().equals("group"))
                        event = event.toChatMessage(chatId, message.contains(usernameMentionSubstring));

                    var response = m_botLogic.handler(event);
                    if (response == null)
                        continue;

                    var request = new SendMessage(response.chatId, response.message);
                    var sendResponse = m_bot.execute(request);

                    if (!sendResponse.isOk())
                        System.out.print("Не могу ответить на сообщение :(");
                }

            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void stop()
    {
        if (m_bot == null)
            throw new IllegalStateException();

        m_bot.removeGetUpdatesListener();
        m_bot = null;
    }
}
