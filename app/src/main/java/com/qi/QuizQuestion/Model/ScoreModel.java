package com.qi.QuizQuestion.Model;

import java.util.ArrayList;

public class ScoreModel {
    String Score;
    String Question;
    ArrayList<String>QuestionList = new ArrayList<>();
    ArrayList<String>AnswersList = new ArrayList<>();
    ArrayList<String>UsersAnsList = new ArrayList<>();
    String QuizID;
    String StudentID;
    String ParentID;
    String Stars;

    public String getQuizID() {
        return QuizID;
    }

    public void setQuizID(String quizID) {
        QuizID = quizID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getStars() {
        return Stars;
    }

    public void setStars(String stars) {
        Stars = stars;
    }

    public ArrayList<String> getQuestionList() {
        return QuestionList;
    }

    public void setQuestionList(ArrayList<String> questionList) {
        QuestionList = questionList;
    }

    public ArrayList<String> getAnswersList() {
        return AnswersList;
    }

    public void setAnswersList(ArrayList<String> answersList) {
        AnswersList = answersList;
    }

    public ArrayList<String> getUsersAnsList() {
        return UsersAnsList;
    }

    public void setUsersAnsList(ArrayList<String> usersAnsList) {
        UsersAnsList = usersAnsList;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }
}
