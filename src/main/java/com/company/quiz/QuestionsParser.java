package com.company.quiz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QuestionsParser {


    public static ArrayList<QuizQuestion> fromTextFile(String questionsFile, String separatorRegex) {
        var result = new ArrayList<QuizQuestion>();
        try (var file = new BufferedReader(new FileReader(questionsFile))) {
            String s;
            var id = 0;
            while ((s = file.readLine()) != null) {
                var splittedLine = s.split(separatorRegex, 2);
                if (splittedLine.length < 2)
                    continue;

                result.add(new QuizQuestion(id, splittedLine[0], splittedLine[1]));
                id++;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
