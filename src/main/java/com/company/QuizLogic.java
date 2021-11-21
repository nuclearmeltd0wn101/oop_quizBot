package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizLogic implements IChatBotLogic {

    private static final int giveUpCountRequired = 2;
    private static final int wrongAnswersLimit = 9;
    private static final int firstHintThreshold = 2;
    private static final int secondHintThreshold = 5;

    private static final String greetMessagePM
            = """
            Привет, я - Викторина-бот!
            Напиши "вопрос" и я задам тебе вопрос
            Напиши /score для получения таблицы счета""";
    private static final String remainingAnswersCountMessageMany
            = "Вы не угадали. Осталось %s попыток";
    private static final String remainingAnswersCountMessageFew
            = "Вы не угадали. Осталось %s попытки";
    private static final String remainingAnswersCountMessageLast
            = "Вы не угадали. Осталась %s попытка";
    private static final String wrongAnswersLimitMessage
            = "Слишком много неправильных попыток. Напишите \"вопрос\", чтобы получить новый вопрос."
            + "\nНапиши /score для получения таблицы счета";
    private static final String greetMessageChat
            = "Привет, я - Викторина-бот!\nНапиши @Quiz_bot_bot, затем любое сообщение и я задам тебе вопрос";
    private static final String messageHelpHint
            = "Напишите /help для получения справочного сообщения";
    private static final String messageRightAnswer = "Вы угадали!";
    private static final String firstHintMessage = "\n\nПодсказка №1: Ответ начинается с буквы \"%s\" ";
    private static final String secondHintMessage="\nПодсказка №2: Длина ответа - %s символов";
    private static final String giveUpMessage= "Напишите \"сдаюсь\" ещё %s раза";
    private static final String giveUpMessage2= "Напишите \"сдаюсь\" ещё %s раз";
    private static final String questionResetMessage= "Вопрос снят. Следующий вопрос:\n";
    private static final String questionAlreadyExistMessage="Вопрос уже был задан, ожидаю ответ";

    private final ArrayList<QuizQuestion> questions;
    private final Random rand;
    private final IQuizDB db;
    public QuizLogic(ArrayList<QuizQuestion> questions, IQuizDB db)
    {
        this.questions = questions;
        rand = new Random();
        this.db = db;
    }

    public void setRemindPolicy(long remindDelaySeconds, int maxRemindAttempts) {
        db.setRemindPolicy(remindDelaySeconds, maxRemindAttempts);
    }

    private ChatBotResponse selfInducedHandler() {
        var inactiveInfo = db.getInactiveChat();
        if (inactiveInfo == null)
            return null;
        ArrayList<String> messages = new ArrayList<String>();
        messages.add("не хотите сыграть?");
        messages.add("ты забыл обо мне?");
        messages.add("готов задать вопрос");
        messages.add("Давай сыграем!");
        messages.add("проверим твою эрудицию?");
        Collections.shuffle(messages);
        // todo бахнуть более информативное (и, желательно рофельное) напоминание (бахни массив всратых цитат
        //  душного артема из закрепов конфы и кидай их в начало сообщения, в конце же просто напоминай юзеру,
        //  что мб ему стоит снова поиграть с ботом (желательно тоже с закосом под душного артема, и ваще тексты
        //  сообщений лучше переделать так чтобы это был не викторина-бот а "Викторина Душных Вопросов от ТОП-1 КНа")
        var response = new ChatBotResponse(inactiveInfo.chatId, messages.get(0));

        return inactiveInfo.isAnyMore
                ? response.SelfInducedNotOverYet()
                : response;
    }

    private ChatBotResponse quizHandler(ChatBotEvent event, long state) {
        if (event.message.contains("/score"))
        {
            return new DisplayOfScore(db).display(event)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDVPlhmilWc7ZzcjRMtge8ij3llCTEQAACYwQAAs7Y6Asx61tywusibCIE");
        }

        if (event.message.contains("/help") || event.message.contains("/start") )
            return event.toResponse(
                    !event.isPrivateChat
                            ? greetMessageChat
                            : greetMessagePM)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDShFhkmhuE5lz_InXvOrrxZifKKaxYQACuwIAAqKK8QdcF8HD_GCZXyIE");
        if (event.message.contains("повтор"))
        {
            return event.toResponse(questions.get(db.getQuestionId(event.chatId)).question);
        }
        if (event.message.toLowerCase().contains("вопрос"))
        {
            if (state != 0)
                return event.toResponse(questionAlreadyExistMessage)
                        .AddTelegramSticker("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE");

            var question = updateQuestion(event);
            return event.toResponse(question.question)
                    .AddTelegramSticker("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE");
        }

        if (state != 0) {
            var questionId= db.getQuestionId(event.chatId);
            var question = questions.get(questionId);

            if (question.validateAnswer(event.message.toLowerCase())) {
                db.setUserName(event.senderId, event.senderUsername);
                db.scoreIncrement(event.chatId, event.senderId);
                resetQuestion(event);
                return event.toResponse(messageRightAnswer)
                        .AddTelegramSticker("CAACAgIAAxkBAAEDSjthkoEJoQKIsjn-1zi9UzVQFkI-jAAC4w0AArAsKUkmVocAAbI_aIAiBA");
            }

            if (event.message.toLowerCase().contains("сдаюсь"))
                return processGiveUpRequest(event);

            return processWrongAnswer(event, question);
        }

        return event.toResponse(messageHelpHint);
    }


    public ChatBotResponse handler(ChatBotEvent event) {
        if (event.isSelfInduced)
            return selfInducedHandler();

        if (!event.isPrivateChat && !event.isMentioned) // ignore public chat w\o mention
            return null;

        db.updateChatLastActiveTimestamp(event.chatId);

        var state = db.getState(event.chatId);
        return quizHandler(event, state);
    }

    private void resetQuestion(ChatBotEvent event) {
        db.giveUpRequestsCountReset(event.chatId);
        db.wrongAnswersCountReset(event.chatId);
        db.setState(event.chatId, 0);
    }

    private QuizQuestion updateQuestion(ChatBotEvent event) {
        var questionId = rand.nextInt(questions.size());
        db.setQuestionId(event.chatId, questionId);

        db.setState(event.chatId,1);
        return questions.get(questionId);
    }

    private ChatBotResponse processWrongAnswer(ChatBotEvent event, QuizQuestion question) {
        var failureCount= db.getWrongAnswersCount(event.chatId);
        if (failureCount == wrongAnswersLimit)
        {
            resetQuestion(event);
            return event.toResponse(wrongAnswersLimitMessage);
        }

        var sb = new StringBuilder(
                getRemainingAnswersCountMessage(failureCount));

        if (failureCount >= firstHintThreshold)
            sb.append(String.format(firstHintMessage,
                    question.answerHintFirstLetter()));

        if (failureCount >= secondHintThreshold)
            sb.append(String.format(secondHintMessage,
                    question.answerHintLength()));

        db.wrongAnswersCountIncrement(event.chatId);
        return event.toResponse(sb.toString())
                .AddTelegramSticker("CAACAgIAAxkBAAEDSjNhkoAkb9KIVhJ0xTBLBn5HdDeE5QACrBIAAmCRIEnnz3aDncA0fCIE");
    }

    private String getRemainingAnswersCountMessage(int failureCount) {
        if (failureCount < 5)
            return String.format(remainingAnswersCountMessageMany, wrongAnswersLimit- failureCount);
        else if (failureCount <8)
            return String.format(remainingAnswersCountMessageFew, wrongAnswersLimit - failureCount);
        else
            return String.format(remainingAnswersCountMessageLast, wrongAnswersLimit - failureCount);
    }

    private ChatBotResponse processGiveUpRequest(ChatBotEvent event) {
        var giveUpCount= db.getGiveUpRequestsCount(event.chatId);

        var response = "";
        if (giveUpCount < giveUpCountRequired ) {
            db.giveUpRequestsCountIncrement(event.chatId);
            response = String.format(giveUpCount==1?giveUpMessage2:giveUpMessage,
                    giveUpCountRequired - giveUpCount);
        }
        else
        {
            resetQuestion(event);
            var question = updateQuestion(event);
            response = questionResetMessage
                    + question.question;

        }
        return event.toResponse(response);
    }
}