package com.company;

public class Main {
    public static void main(String[] args) {
//         var env = System.getenv();
//         var token = env.getOrDefault("tgBotToken", "");
//         if ("".equals(token)) {
//             System.err.println("Telegram Bot Token not submitted");
//             return;
//         }
// 
//         var bot = new TelegramBotWrapper(botLogic, token);

        var questions = QuestionsParser.fromTextFile("quiz_questions.txt", "\\*");
        var botLogic = new QuizLogic(questions);
        var bot = new ConsoleChatWrapper(botLogic);

        bot.run();
    }
}
