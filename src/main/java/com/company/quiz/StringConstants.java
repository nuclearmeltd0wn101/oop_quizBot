package com.company.quiz;

public class StringConstants {
    static String greetMessagePM = """
            Привет, я - Викторина-бот!
            Напиши "вопрос" и я задам тебе вопрос
            Напиши /score для получения таблицы счета""";
    static String remainingAnswersCountMessageMany = "Вы не угадали. Осталось %s попыток";
    static String remainingAnswersCountMessageFew = "Вы не угадали. Осталось %s попытки";
    static String remainingAnswersCountMessageLast = "Вы не угадали. Осталась %s попытка";
    static String wrongAnswersLimitMessage = "Слишком много неправильных попыток. Напишите \"вопрос\", чтобы получить новый вопрос."
            + "\nНапиши /score для получения таблицы счета";
    static String greetMessageChat = "Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос";
    static String messageHelpHint = "Напишите /help для получения справочного сообщения";
    static String messageRightAnswer = "Вы угадали!";
    static String firstHintMessage = "\n\nПодсказка №1: Ответ начинается с буквы \"%s\" ";
    static String secondHintMessage = "\nПодсказка №2: Длина ответа - %s символов";
    static String giveUpMessage = "Напишите \"сдаюсь\" ещё %s раза";
    static String giveUpMessage2 = "Напишите \"сдаюсь\" ещё %s раз";
    static String questionResetMessage = "Вопрос снят. Следующий вопрос:\n";
    static String questionAlreadyExistMessage = "Вопрос уже был задан, ожидаю ответ";
}
