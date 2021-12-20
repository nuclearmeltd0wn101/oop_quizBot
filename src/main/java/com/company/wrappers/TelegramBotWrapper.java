package com.company.wrappers;

import com.company.botBehavior.ChatBotEvent;
import com.company.botBehavior.ChatBotResponse;
import com.company.botBehavior.IChatBotLogic;
import com.company.botBehavior.SelfInducedEvent;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetMeResponse;

import java.util.List;


public class TelegramBotWrapper implements IChatBotWrapper {
    private final IChatBotLogic m_botLogic;
    private final String m_botToken;

    private TelegramBot m_bot;
    private GetMeResponse m_getMeResponse;
    private String m_usernameMentionSubstring;

    @Inject
    public TelegramBotWrapper(IChatBotLogic botLogic, @Named("botToken") String botToken) {
        if (botLogic == null) {
            throw new IllegalArgumentException("Bot logic not submitted");
        }

        if (botToken == null || "".equals(botToken)) {
            throw new IllegalArgumentException("Telegram Bot Token not submitted");
        }

        m_botLogic = botLogic;
        this.m_botToken = botToken;

        m_bot = new TelegramBot(m_botToken);

        m_getMeResponse = m_bot.execute(new GetMe());
        if (m_getMeResponse.user() == null)
            throw new IllegalStateException("Invalid bot token");

        System.out.println(m_getMeResponse);
        m_usernameMentionSubstring = "@" + m_getMeResponse.user().username().toLowerCase();
    }

    private String getName(User user) {
        if (user.username() != null)
            return user.username();

        if (user.lastName() == null)
            return user.firstName();

        return user.firstName() + " " + user.lastName();
    }

    private void sendResponse(ChatBotResponse response) {
        if (m_bot == null)
            throw new IllegalStateException();

        if (response == null)
            return;

        var request = new SendMessage(response.chatId, response.message);
        SendSticker sticker = null;
        if (response.telegramStickerId != null) {
            sticker = new SendSticker(response.chatId, response.telegramStickerId);
        }
        var sendResponse = m_bot.execute(request.parseMode(ParseMode.Markdown).replyMarkup(createKeyboard()));

        if (sticker != null) {
            m_bot.execute(sticker);
        }
        if (!sendResponse.isOk())
            System.err.printf("Не могу отправить ответ (ChatID: %d, Message: \"%s\")%n",
                    response.chatId,
                    response.message);
    }

    public void callSelfInduced() {
        if (m_bot == null)
            throw new IllegalStateException();

        var isEndReached = false;
        while (!isEndReached && m_bot != null) {
            var resp = m_botLogic.handle(new SelfInducedEvent());
            sendResponse(resp);
            isEndReached = resp == null || resp.isSelfInducedEnd;
        }
    }

    private void processUpdates(List<Update> updates) {
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

                if (m_getMeResponse.user().id().equals(senderId))
                    continue; // don't react to thine own messages in event

                var isMentioned = message.toLowerCase()
                        .contains(m_usernameMentionSubstring);
                if (isMentioned) {
                    message = message.replaceAll(
                                    String.format("\\s*?(?i)(%s)\\s*?",
                                            m_usernameMentionSubstring), "")
                            .strip();
                }

                var event = new ChatBotEvent(senderId, senderUsername, message);
                if (source.chat().type().toString().equals("group")) {
                    event = event.toChatMessage(chatId, isMentioned);
                }

                var response = m_botLogic.handle(event);

                sendResponse(response);
            }
        }
    }

    public void run() {
        m_bot.setUpdatesListener(updates -> {
            processUpdates(updates);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public ReplyKeyboardMarkup createKeyboard() {
        var a = new KeyboardButton("/start");
        var b = new ReplyKeyboardMarkup(a);
        return b.addRow("/score").addRow("/help").addRow("сдаюсь").resizeKeyboard(true);
    }

    public void stop() {
        if (m_bot == null)
            return;

        m_bot.removeGetUpdatesListener();
    }
}
