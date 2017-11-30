package com.example.muaaz.kitchen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muaaz.kitchen.classes.KitchenItem;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SweetActivity extends AppCompatActivity {

    ListView sweets;

    FirebaseRemoteConfig mRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweet);

        sweets = (ListView) findViewById(R.id.sweet_lv);

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
        String statColor = prefs.getString("status_bar_color", String.valueOf(R.color.colorPrimaryDark));

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statColor));
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sweets");

        ListAdapter mAdaper = new FirebaseListAdapter<KitchenItem>(this, KitchenItem.class, R.layout.menu_item_view_only_layout, ref){
        @Override
        protected void populateView(View view, final KitchenItem kitchenItem, int position) {
            TextView nameTv = (TextView) view.findViewById(R.id.item_name);
            TextView priceTv = (TextView) view.findViewById(R.id.item_price);

            TextView itemTotalPrice = (TextView) view.findViewById(R.id.semi_total_text);

            nameTv.setText(kitchenItem.itemName);
            priceTv.setText(String.valueOf(kitchenItem.itemPrice));

            view.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(SweetActivity.this).create();
                alertDialog.setTitle(kitchenItem.itemName);
                alertDialog.setMessage(kitchenItem.description);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                        new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                          }
                        });
                alertDialog.show();
              }
            });

        }

        };
        sweets.setAdapter(mAdaper);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SweetActivity.this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }

}

