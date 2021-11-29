package com.company;
public class DisplayOfScore {
    private final IScoreRepository scoreRepository;
    private final IUserNamesRepository userNamesRepository;
    public DisplayOfScore(IScoreRepository scoreRepository,IUserNamesRepository userNamesRepository){
        this.scoreRepository=scoreRepository;
        this.userNamesRepository=userNamesRepository;
    }
    public ChatBotResponse display(ChatBotEvent event)
    {
        var table= scoreRepository.GetTable(event.chatId);
        if (table==null)
            return event.toResponse("В таблице счета пока нет записей");
        System.out.println();
        var sb = new StringBuilder();
        var maximumScore=0;
        var maximumName=" ";
        for (var item:
                table) {
            if (item.score>maximumScore)
            {
                maximumScore=Math.toIntExact(item.score);
            }
            var name=userNamesRepository.Get(item.userId);
            if (name.length()>maximumName.length())
            {
                maximumName= name;
            }
        }

        for (var item:table) {
            sb.append("\n");
            var userName=userNamesRepository.Get(item.userId);
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
