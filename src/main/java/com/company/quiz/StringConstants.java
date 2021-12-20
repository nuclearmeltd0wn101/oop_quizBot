package com.company.quiz;

public class StringConstants {
    public static String greetMessagePM = """
            Привет, я - Викторина-бот!
            Напиши "вопрос" и я задам тебе вопрос
            Напиши /score для получения таблицы счета""";
    public static String remainingAnswersCountMessageMany = "Вы не угадали. Осталось %s попыток";
    public static String remainingAnswersCountMessageFew = "Вы не угадали. Осталось %s попытки";
    public static String remainingAnswersCountMessageLast = "Вы не угадали. Осталась %s попытка";
    public static String wrongAnswersLimitMessage = "Слишком много неправильных попыток. Напишите \"вопрос\", чтобы получить новый вопрос."
            + "\nНапиши /score для получения таблицы счета";
    public static String greetMessageChat = "Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос";
    public static String messageHelpHint = "Напишите /help для получения справочного сообщения";
    public static String messageRightAnswer = "Вы угадали!";
    public static String firstHintMessage = "\n\nПодсказка №1: Ответ начинается с буквы \"%s\" ";
    public static String secondHintMessage = "\nПодсказка №2: Длина ответа - %s символов";
    public static String giveUpMessage = "Напишите \"сдаюсь\" ещё %s раза";
    public static String giveUpMessage2 = "Напишите \"сдаюсь\" ещё %s раз";
    public static String questionResetMessage = "Вопрос снят. Следующий вопрос:\n";
    public static String questionAlreadyExistMessage = "Вопрос уже был задан, ожидаю ответ";
}
