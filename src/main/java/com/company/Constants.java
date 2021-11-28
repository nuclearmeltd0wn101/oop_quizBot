package com.company;

public enum Constants {
    greetMessagePM("""
            Привет, я - Викторина-бот!
            Напиши "вопрос" и я задам тебе вопрос
            Напиши /score для получения таблицы счета"""),
    remainingAnswersCountMessageMany("Вы не угадали. Осталось %s попыток"),
    remainingAnswersCountMessageFew("Вы не угадали. Осталось %s попытки"),
    remainingAnswersCountMessageLast("Вы не угадали. Осталась %s попытка"),
    wrongAnswersLimitMessage("Слишком много неправильных попыток. Напишите \"вопрос\", чтобы получить новый вопрос."
            + "\nНапиши /score для получения таблицы счета"),
    greetMessageChat("Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос"),
    messageHelpHint("Напишите /help для получения справочного сообщения"),
    messageRightAnswer("Вы угадали!"),
    firstHintMessage("\n\nПодсказка №1: Ответ начинается с буквы \"%s\" "),
    secondHintMessage("\nПодсказка №2: Длина ответа - %s символов"),
    giveUpMessage("Напишите \"сдаюсь\" ещё %s раза"),
    giveUpMessage2("Напишите \"сдаюсь\" ещё %s раз"),
    questionResetMessage("Вопрос снят. Следующий вопрос:\n"),
    questionAlreadyExistMessage("Вопрос уже был задан, ожидаю ответ");
    private final String constants;

    Constants(String cons) {
        this.constants = cons;
    }
}
