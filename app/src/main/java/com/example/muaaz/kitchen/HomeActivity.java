package com.example.muaaz.kitchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    FirebaseRemoteConfig mRemoteConfig;
    LinearLayout topIm;
    LinearLayout midIm;
    LinearLayout botIm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);

        HashMap<String, Object> defaults = new HashMap<>();

        defaults.put("action_bar_color", R.color.colorPrimary);
        defaults.put("status_bar_color", R.color.colorPrimaryDark);
        mRemoteConfig.setDefaults(defaults);

        final SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("USER_EMAIL", "Error1");
        setTitle(username);

        String abColor = prefs.getString("action_bar_color", String.valueOf(R.color.colorPrimary));
        Log.i("colorprefs", "abColor:" + abColor);
        String statColor = prefs.getString("status_bar_color", String.valueOf(R.color.colorPrimaryDark));
        Log.i("colorprefs", "statColor:" + statColor);

        try {
            ActionBar ab = getSupportActionBar();
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(statColor));
            }
        }catch (IllegalArgumentException ie){
            ActionBar ab = getSupportActionBar();
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(String.valueOf(R.color.colorPrimary))));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(String.valueOf(R.color.colorPrimaryDark)));
            }
        }

        topIm = (LinearLayout) findViewById(R.id.top_ll);
        midIm = (LinearLayout) findViewById(R.id.mid_ll);
        botIm = (LinearLayout) findViewById(R.id.bot_ll);

        topIm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(HomeActivity.this, PlaceOrderActivity.class);
                startActivity(intent);
            }

        });

        midIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SweetActivity.class);
                startActivity(intent);
            }
        });

        botIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ViewOrdersActivity.class);
                startActivity(intent);
            }
        });

        /*Picasso.with(this)
                .load(R.drawable.jam_tarts_compressed)
                .into(topIm);

        Picasso.with(this)
                .load(R.drawable.jam_tarts_compressed)
                .into(midIm);

        Picasso.with(this)
                .load(R.drawable.jam_tarts_compressed)
                .into(botIm); */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home_sign_out:
                SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("LOGGED", "OUT");
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setActionColor(){
        ActionBar ab = getSupportActionBar();
        String abColor = mRemoteConfig.getString("action_bar_color");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));
    }


    private void setStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            String statusColor = mRemoteConfig.getString("status_bar_color");
            window.setStatusBarColor(Color.parseColor(statusColor));
        }
    }
}