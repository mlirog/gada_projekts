package com.maris_skrivelis.gada_projekts;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;




public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureViewHolder>{

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<LectureObject> lecturesList;

    //getting the context and lecture list with constructor
    public LectureAdapter(Context mCtx, List<LectureObject> productList) {
        this.mCtx = mCtx;
        this.lecturesList = productList;
    }

    @Override
    public LectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.lecture_card, null);
        return new LectureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LectureViewHolder holder, int position) {
        //getting the lecture of the specified position
        LectureObject lectureObject = lecturesList.get(position);

        //binding the data with the viewholder views
        //if recived param is empty, hide its label in card to save space
        //useful in case to show that day is empty

        if (lectureObject.getLectureTitle().trim().isEmpty()){
            holder.lecture_title.setVisibility(View.GONE);
        }else{
            holder.lecture_title.setText(lectureObject.getLectureTitle());
        }

        if (lectureObject.getClassroom().trim().isEmpty()){
            holder.classroom.setVisibility(View.GONE);
        }else{
            holder.classroom.setText(lectureObject.getClassroom());
        }

        if (lectureObject.getTime_from().trim().isEmpty() || lectureObject.getTime_to().trim().isEmpty()){
            holder.lecture_time.setVisibility(View.GONE);
        }else{
            holder.lecture_time.setText(lectureObject.getTime_from() + " - " + lectureObject.getTime_to());
        }

        if (lectureObject.getLecturer().trim().isEmpty()){
            holder.lecturer.setVisibility(View.GONE);
        }else{
            holder.lecturer.setText(lectureObject.getLecturer());
        }

        if (lectureObject.getCourseCode().trim().isEmpty()){
            holder.courses.setVisibility(View.GONE);
        }else{
            holder.courses.setText(lectureObject.getCourseCode());
        }

        if (lectureObject.getCard_color().trim().isEmpty()){
            holder.card.setBackgroundColor(Color.parseColor("#e8e8e8"));
        }else{
            holder.card.setBackgroundColor(Color.parseColor(lectureObject.getCard_color()));
        }
    }


    @Override
    public int getItemCount() {
        return lecturesList.size();
    }

    class LectureViewHolder extends RecyclerView.ViewHolder{

        TextView lecture_title, classroom, lecture_time, lecturer, courses;
        CardView card;

        public LectureViewHolder(View itemView){
            super(itemView);

            //find every label in cardview XML to use it above in onBindViewHolder
            lecture_title = itemView.findViewById(R.id.lecture_title);
            classroom = itemView.findViewById(R.id.classroom);
            lecture_time = itemView.findViewById(R.id.lecture_time);
            lecturer = itemView.findViewById(R.id.lecturer);
            courses = itemView.findViewById(R.id.courses);
            card = itemView.findViewById(R.id.card);
        }

    }
}
