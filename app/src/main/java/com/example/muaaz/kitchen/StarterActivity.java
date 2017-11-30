package com.example.muaaz.kitchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.muaaz.kitchen.LoginActivity;
import com.example.muaaz.kitchen.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class StarterActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseRemoteConfig mRemoteConfig;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Bundle params = new Bundle();
        params.putString("UserLoggedIn", "in");
        mFirebaseAnalytics.logEvent("MK_logged", params);

        prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        final String logged = prefs.getString("LOGGED", "OUT");

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);

        HashMap<String, Object> defaults = new HashMap<>();

        defaults.put("action_bar_color", R.color.colorPrimary);
        defaults.put("status_bar_color", R.color.colorPrimaryDark);
        mRemoteConfig.setDefaults(defaults);

        Toast.makeText(this, "setting up colors", Toast.LENGTH_SHORT).show();

        final Task<Void> fetch = mRemoteConfig.fetch(0);
        fetch.addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRemoteConfig.activateFetched();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("action_bar_color", mRemoteConfig.getString("action_bar_color"));
                Log.i("colorprefs", "abcolor SA:" + mRemoteConfig.getString("action_bar_color"));
                editor.putString("status_bar_color", mRemoteConfig.getString("status_bar_color"));
                editor.apply();

                Toast.makeText(StarterActivity.this, "colors set up", Toast.LENGTH_SHORT).show();

                String mail = prefs.getString("USER_EMAIL", "");
                if(logged.equals("IN")){
                    DatabaseReference ref = database.getReference("Users/" + mail);

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User signedInToApp;
                            try {
                                signedInToApp = dataSnapshot.getValue(User.class);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("LOGGED", "IN");
                                editor.putString("USER_EMAIL", signedInToApp.email);
                                editor.putString("USER_NAME", signedInToApp.name);
                                editor.putString("USER_SURNAME", signedInToApp.surname);
                                editor.putLong("USER_PHONE", signedInToApp.phoneNo);
                                editor.apply();
                                Intent goToHome = new Intent(StarterActivity.this, HomeActivity.class);
                                startActivity(goToHome);
                            }catch (NullPointerException e){
                                Intent goToLogin = new Intent(StarterActivity.this, LoginActivity.class);
                                startActivity(goToLogin);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error){

                        }
                    });
                }else{
                    Intent goToLogin = new Intent(StarterActivity.this, LoginActivity.class);
                    startActivity(goToLogin);
                }
            }
        });

    }
}
