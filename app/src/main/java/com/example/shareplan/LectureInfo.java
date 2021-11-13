package com.example.shareplan;

public class LectureInfo {
    String Name;// 강의 이름
    String Professor;// 교수 이름
    String Division; // 분반
    String Day; // 강의 요일
    String Time; // 강의 시간;

    public LectureInfo() { }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfessor() {
        return Professor;
    }

    public void setProfessor(String professor) {
        Professor = professor;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        Division = division;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
