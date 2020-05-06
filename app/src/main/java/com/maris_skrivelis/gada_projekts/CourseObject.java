package com.maris_skrivelis.gada_projekts;

import android.util.Log;

public class CourseObject {
    int id;
    public String title;
    public int course_from;
    public int course_to;

    public CourseObject(int id, String title, int course_from, int course_to) {
        this.id = id;
        this.title = title;
        this.course_from = course_from;
        this.course_to = course_to;
        Log.i("COURSE_OBJECT_CREATED", "id:"+id+", title:"+title+", from:"+course_from+", to:"+course_to);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getcourse_from() {
        return course_from;
    }

    public int getcourse_to() {
        return course_to;
    }
}
