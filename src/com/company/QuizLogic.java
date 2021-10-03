package com.company;

import java.util.Random;

public class QuizLogic implements IChatBotLogic {
    private final QuestionBase m_questionBase;
    private boolean m_isQuestion_present;
    private int m_currentQuestionNumber;
    private final Random rand;

    public QuizLogic(QuestionBase questionBase)
    {
        m_questionBase = questionBase;
        rand = new Random();
    }

    public ChatBotResponse handler(ChatBotEvent event) {
        if (!event.isPrivateChat && !event.isMentioned) // ignore public chat w\o mention
            return null;

        if ("/help".equals(event.message))
            return event.toResponse(
                    (!event.isPrivateChat)
                            ? "Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос"
                            : "Привет, я - Викторина-бот!\nНапиши \"вопрос\" и я задам тебе вопрос");

        if (event.message.contains("вопрос")) {
            m_currentQuestionNumber = rand.nextInt(m_questionBase.size());

            var question = m_questionBase
                    .getQuestionById(m_currentQuestionNumber);

            var text = String.format(
                    "Вопрос: %s\nПодсказка к ответу: начинается с \"%s\", длина %d символов",
                    question.question,
                    question.answerHintFirstLetter(),
                    question.answerHintLength()
            );

            m_isQuestion_present = true;
            return event.toResponse(text);
        }
        if (m_isQuestion_present) {
            var question = m_questionBase
                    .getQuestionById(m_currentQuestionNumber);
            if (question.validateAnswer(event.message.toLowerCase())) {
                m_isQuestion_present = false;

                return event.toResponse("Вы угадали!");
            }
            else
                return event.toResponse("Вы не угадали!");
        }

        return event.toResponse("Напишите /help для получения справочного сообщения");
    }
}
