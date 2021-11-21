package com.company;
public class DisplayOfScore {
    private final IQuizDB db;
    public DisplayOfScore(IQuizDB db){
        this.db=db;
    }
    public ChatBotResponse display(ChatBotEvent event)
    {
        var table= db.getScoreTable(event.chatId);
        if (db.getScoreTable(event.chatId) == null)
            return event.toResponse("В таблице счета пока нет записей");
        System.out.println();
        var sb = new StringBuilder();
        var maximumScore=0;
        var maximumName=" ";
        for (QuizScore item:
                table) {
            if (item.score>maximumScore)
            {
                maximumScore=Math.toIntExact(item.score);
            }
            if (db.getUserName(item.userId).length()>maximumName.length())
            {
                maximumName= db.getUserName(item.userId);
            }
        }

        for (QuizScore item:table) {
            sb.append("\n");
            var userName=db.getUserName(item.userId);
            var lengthName = 60;
            if (maximumScore + maximumName.length() > 58)
                lengthName = 58 - maximumScore;
            if (userName.length() + String.valueOf(item.score).length()>57)
                sb.append(userName,0,lengthName);
            else
                sb.append(userName);
            var diff = maximumName.length() - userName.length();
            sb.append(" ".repeat(Math.max(0, diff + 1)));
            sb.append("| ");
            sb.append(item.score);
        }
        return event.toResponse("```"+sb+"```");
    }
}
