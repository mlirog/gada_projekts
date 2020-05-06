package com.maris_skrivelis.gada_projekts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements course_list.OnFragmentInteractionListener, lectures_graph.OnFragmentInteractionListener{

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        loadThemeChangeListener(navigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_course_list, R.id.nav_lectures_greph)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //adds listener to switch button in navigation

    private void loadThemeChangeListener(NavigationView nav) {
        MenuItem menuItem = nav.getMenu().findItem(R.id.switchToggleButton); // This is the menu item that contains your switch
        Switch drawerSwitch = (Switch) menuItem.getActionView().findViewById(R.id.drawer_switch);
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);

        Log.e("pref", prefs.getInt("darkTheme", 0) + "");
        if(prefs.getInt("darkTheme", 0) == 1){
//            setTheme(R.style.DarkTheme);
            drawerSwitch.setChecked(true);
        }else{
            setTheme(R.style.AppTheme);
            drawerSwitch.setChecked(false);
        }

        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "Switch turned on", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Switch turned off", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences.Editor editor = getSharedPreferences("app", MODE_PRIVATE).edit();
                editor.putInt("darkTheme", isChecked ? 1 : 0);
                editor.apply();

                //reloads app to apply theme
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }
}
