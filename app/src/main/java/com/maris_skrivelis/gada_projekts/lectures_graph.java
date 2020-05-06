package com.maris_skrivelis.gada_projekts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class lectures_graph extends Fragment {

    private View root;
    private OnFragmentInteractionListener mListener;
    private LectureObject[] all_lectures;
    private String lectures_url;
    private AlertDialog loading;
    private Calendar calendar;


    public lectures_graph() {
        // Required empty public constructor
    }

    public static lectures_graph newInstance(String param1, String param2) {
        lectures_graph fragment = new lectures_graph();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_lectures_graph, container, false);

        calendar = Calendar.getInstance();

        FloatingActionButton calendar_button = root.findViewById(R.id.fbutton_pick_date);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = sdf.format(calendar.getTime());

                loadDayLectures(dateString);
            }

        };

        calendar_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //sets swipe listener to cardviews too
        root.findViewById(R.id.lectures_view).setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                goToPreviousDay();
            }

            public void onSwipeLeft() {
                goToNextDay();
            }
        });


        //loads today lectures on load
        loadDayLectures(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


        // Inflate the layout for this fragment
        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //reload lecture cards on changed layout

        //create date for calendar picker and change it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(calendar.getTime());
        loadDayLectures(dateString);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private String getSavedCourse() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("saved_program", Context.MODE_PRIVATE);
        String course_code = prefs.getString("program", null);
        int study_year = prefs.getInt("study_year", 0);

        //if no course saved in SharedPrefs
        //redirect to course selection
        if (course_code == null || study_year == 0) {
            getFragmentManager().beginTransaction()
                    .remove(lectures_graph.this)
                    .replace(R.id.nav_host_fragment, new course_list())
                    .addToBackStack(null)
                    .commit();
            //changes selected item in navbar
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        if (course_code == null && study_year == 0){
            return null;
        }else{
            return course_code + study_year;
        }
    }

    private void loadDayLectures(String date) {
        showLoadingScreen();

        //build GET request
        lectures_url = getString(R.string.url_lectures_json) + "?date=" + date + "&program=" + getSavedCourse() + "&lang=" + Locale.getDefault().getLanguage();
        RequestQueue requestQueue = Volley.newRequestQueue(root.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, lectures_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //shows loading screen
                showLoadingScreen();
                try {
                    JSONObject jobject = new JSONObject(response);
                    JSONArray jsonArray = jobject.getJSONArray("result");
                    all_lectures = new LectureObject[jsonArray.length()];

                    //for each json object found
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String tmp = jsonArray.getString(i);
                        JSONObject jsonObject1 = new JSONObject(tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1));

                        //create lecture object
                        all_lectures[i] = new LectureObject(jsonObject1.getString("nodala"), jsonObject1.getString("kurss"), jsonObject1.getString("lektors"), jsonObject1.getString("sakums"), jsonObject1.getString("beigas"), jsonObject1.getString("nosaukums"), jsonObject1.getString("iela"));
                    }

                    //display lectures
                    createLectureCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hide loading screen
                hideLoadingScreen();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        requestQueue.add(stringRequest);

        //shows course info in actionbar
        if(getSavedCourse() != null && getSavedCourse() != ""){
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("(" + getSavedCourse() + ")" + " | " + calendar.getTime().toString().substring(4, 10));
        }else{
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(getString(R.string.title_lectures_graph));
        }
    }

    private void createLectureCards() {
        //getting the recyclerview from xml
        RecyclerView recyclerView = root.findViewById(R.id.lectures_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        //initializing the lectureList
        ArrayList<LectureObject> lecturesList = new ArrayList<>(Arrays.asList(all_lectures));

        //creating recyclerview adapter
        LectureAdapter adapter = new LectureAdapter(root.getContext(), lecturesList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    private void hideLoadingScreen() {
        //closes splashscreen
        root.findViewById(R.id.via_splash_screen).setVisibility(View.GONE);
        root.findViewById(R.id.splash_screen_progress).setVisibility(View.GONE);
    }

    private void showLoadingScreen() {
        //opens splashscreen
        root.findViewById(R.id.via_splash_screen).setVisibility(View.VISIBLE);
        root.findViewById(R.id.splash_screen_progress).setVisibility(View.VISIBLE);
    }

    private void goToNextDay() {
        //change calendar day ( add 1 day forward)
        calendar.add(Calendar.DATE, 1);

        //create date for calendar picker and change it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(calendar.getTime());

        //request lectures for selected day
        loadDayLectures(dateString);
    }

    private void goToPreviousDay() {
        //change calendar day ( add 1 day backward)
        calendar.add(Calendar.DATE, -1);

        //create date for calendar picker and change it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(calendar.getTime());

        //request lectures for selected day
        loadDayLectures(dateString);
    }
}

