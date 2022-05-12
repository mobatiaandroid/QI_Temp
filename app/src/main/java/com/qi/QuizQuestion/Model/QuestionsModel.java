package com.qi.QuizQuestion.Model;

import java.util.ArrayList;

public class QuestionsModel {
    String Question;
    String Answer;
    String Explanation;
    Integer QuestionType;
    ArrayList<String> options = new ArrayList<>();

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getExplanation() {
        return Explanation;
    }

    public void setExplanation(String explanation) {
        Explanation = explanation;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
    public Integer getQuestionType() {
        return QuestionType;
    }

    public void setQuestionType(Integer questionType) {
        QuestionType = questionType;
    }
}
