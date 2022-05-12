package com.qi.LeaderBoard.model;

public class LeaderModel {
    String Name,Count,Score,TotalScore;

    public LeaderModel(String name, String count, String score, String totalScore) {
        Name = name;
        Count = count;
        Score = score;
        TotalScore = totalScore;
    }

    public String getTotalScore() {
        return TotalScore;
    }

    public void setTotalScore(String totalScore) {
        TotalScore = totalScore;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }
}
