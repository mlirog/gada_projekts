package com.maris_skrivelis.gada_projekts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class course_list extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View root;
    private String course_json_url;
    private CourseObject[] all_courses;
    private NumberPicker grade_picker;
    private NumberPicker program_picker;
    private final ArrayList<String> course_list = new ArrayList<>();
    private ProgressDialog loading;

    public course_list() {
        // Required empty public constructor
    }

    public static course_list newInstance(String param1, String param2) {
        return new course_list();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //default link where to gather courses
        course_json_url = getString(R.string.url_course_json);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_course_list, container, false);

        grade_picker = root.findViewById(R.id.grade_picker);
        program_picker = root.findViewById(R.id.program_picker);
        Button save_course = root.findViewById(R.id.save_course);
        createCourseObjects(root, course_json_url);

        save_course.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            //save valid values
            if (grade_picker.getValue() > 0 && program_picker.getValue() >= 0){
                saveCourseCode(program_picker.getValue(), grade_picker.getValue());
            }
            }
        });
        return root;
    }


    @Override
    public void onAttach(@NonNull Context context) {
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void createCourseObjects(final View root, String url) {
        loading = new ProgressDialog(getContext());
                loading.setCancelable(false);
                loading.setMessage(getString(R.string.courses_loading_text));
                loading.show();

        //request data from server
        RequestQueue requestQueue= Volley.newRequestQueue(root.getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{
                    loading.setMessage(getString(R.string.courses_loading_text_recieveddata));
                    JSONObject jobject=new JSONObject(response);
                    JSONArray jsonArray = jobject.getJSONArray("result");

                    //assign length with size of recieved object count
                    all_courses = new CourseObject[jsonArray.length()];

                    //for each json object
                    for(int i=0;i<jsonArray.length();i++){

                        loading.setMessage(getString(R.string.courses_loading_text_creatingcourses));

                        String tmp = jsonArray.getString(i);

                        //find object
                        JSONObject jsonObject1 = new JSONObject(tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1));

                        //create course object
                        all_courses[i] = new CourseObject(jsonObject1.getInt("id"), jsonObject1.getString("abbreviation"), jsonObject1.getInt("course_from"), jsonObject1.getInt("course_to"));

                        //add to list with titles only
                        course_list.add(all_courses[i].title);
                    }

                    //fill the pickers
                    fillNumberPickers();

                }catch (JSONException e){
                    e.printStackTrace();

                    //error snackbar
                    Snackbar.make(root, getString(R.string.course_error_text), Snackbar.LENGTH_INDEFINITE).setBackgroundTint(R.color.design_default_color_error).show();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        //define retry policy for stringrequest
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void fillNumberPickers(){
        loading.setMessage(getString(R.string.courses_loading_text_displayingcourses));

        //-- course titles
        //from 0.element
        program_picker.setMinValue(0);
        //to last element (sizeof  courses titles-only array)
        program_picker.setMaxValue(course_list.size()-1);
        //display for each course matching name from titles-only array
        program_picker.setDisplayedValues(course_list.toArray(new String[course_list.size()]));

        //-- grade picker
        //finds lowest grade for selected course
        grade_picker.setMinValue(all_courses[0].getcourse_from());
        //finds highest grade for sleected course
        grade_picker.setMaxValue(all_courses[0].getcourse_to());
        //sets lowest value
        grade_picker.setValue(all_courses[0].getcourse_from());

        loading.setMessage(getString(R.string.courses_loading_text_finishing));

        //listens when selected course is changed
        program_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int was, int is) {
                //on change uodates grades
               grade_picker.setMinValue(all_courses[is].getcourse_from());
               grade_picker.setMaxValue(all_courses[is].getcourse_to());
               grade_picker.setValue(all_courses[is].getcourse_from());
            }
        });

        //closes loading dialog
        loading.dismiss();
    }


    private void saveCourseCode(int program_value, int grade_value){
        //checks for valid input
        if (program_value >= 0 && grade_value > 0){
            //save course to sharedprefs (local memory)
            SharedPreferences.Editor editor = requireActivity().getApplicationContext().getSharedPreferences("saved_program", MODE_PRIVATE).edit();
            editor.putString("program", all_courses[program_value].getTitle());
            editor.putInt("study_year", grade_value);
            editor.apply();

            Log.i("SharedPrefs", "course code saved into shared prefs.");

            //To recieve notifications form Firebase
            //unsubscribes from all topics
            try {

                FirebaseInstanceId.getInstance().deleteInstanceId();
                Log.i("Firebase", "unsubscribed from all channels.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //subscribes to topic related to selected course
            //Firebase CM topic can be only one word without spaces
            //so i removed spaces in string, because we have "Master Exchange" program too
            FirebaseMessaging.getInstance().subscribeToTopic(all_courses[program_value].getTitle().replaceAll("\\s+","")+grade_value);
            Log.w("Subscribed", "to topic: "+ all_courses[program_value].getTitle().replaceAll("\\s+","")+grade_value);

            //changes fragments
            getFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new lectures_graph())
                    .addToBackStack(null)
                    .commit();
            //changes selected item in navbar
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(1).setChecked(true);


        }else {
            Snackbar.make(root, "Izvēle nedrīkst saturēt tukšumus!", Snackbar.LENGTH_LONG).show();
        }
    }
}
