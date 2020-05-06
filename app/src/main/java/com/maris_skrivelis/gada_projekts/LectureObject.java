package com.maris_skrivelis.gada_projekts;

public class LectureObject {
    private String courseCode;
    private String lectureTitle;
    private String lecturer;
    private String time_from;
    private String time_to;
    private String classroom;
    private String card_color;


    public LectureObject(String courseCode, String lectureTitle, String lecturer, String time_from, String time_to, String classroom, String card_color) {
        this.courseCode = courseCode;
        this.lectureTitle = lectureTitle;
        this.lecturer = lecturer;
        this.time_from = time_from;
        this.time_to = time_to;
        this.classroom = classroom;
        this.card_color = card_color;
    }

    public String getCard_color() {
        return card_color;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getTime_from() {
        return time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public String getClassroom() {
        return classroom;
    }
}
