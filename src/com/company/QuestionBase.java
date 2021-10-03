package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QuestionBase {
    private final ArrayList<QuizQuestion> questions;
    
    public QuestionBase(String questionsFile, String separatorRegex) {
        questions = new ArrayList<>();
        try (var file = new BufferedReader(new FileReader(questionsFile)))
        {
            String s;
            var id = 0;
            while((s = file.readLine()) != null) {
                var splittedLine= s.split(separatorRegex, 2);
                if (splittedLine.length < 2)
                    continue;

                questions.add( new QuizQuestion(id, splittedLine[0], splittedLine[1]) );
                id++;
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int size()
    {
        return questions.size();
    }

    public QuizQuestion getQuestionById(int id)
    {
        if ((id < 0) || (id >= questions.size()))
            throw new IllegalArgumentException();
        return questions.get(id);
    }
}
