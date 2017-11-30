package com.example.muaaz.kitchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muaaz.kitchen.classes.KitchenItem;
import com.example.muaaz.kitchen.classes.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    FirebaseRemoteConfig mRemoteConfig;

    TextView orderItemTv;
    TextView orderCostTv;
    TextView orderItemPriceTv;
    DatePicker mDatePicker;
    Button mConfirmOrderButton;

    String orderString = "";
    String orderPriceString = "";

    static int orderNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        orderItemTv = (TextView) findViewById(R.id.co_order_read_tv);
        orderCostTv = (TextView) findViewById(R.id.co_order_price_tv);
        orderItemPriceTv = (TextView) findViewById(R.id.co_order_item_price_tv);
        mDatePicker = (DatePicker) findViewById(R.id.co_date_picker);
        mConfirmOrderButton = (Button) findViewById(R.id.co_confirm_order_button);

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
        final String username = prefs.getString("USER_EMAIL", "Error1");
        setTitle(username);

        String abColor = prefs.getString("action_bar_color", String.valueOf(R.color.colorPrimary));
        String statColor = prefs.getString("status_bar_color", String.valueOf(R.color.colorPrimaryDark));

        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statColor));
        }

        //TODO set up the layout and code for this activity
        //TODO print out all the ordered items from getting the intent
        Intent intent = getIntent();
        final ArrayList<KitchenItem> orderList = (ArrayList<KitchenItem>) intent.getSerializableExtra("orderList");
        final Double totalPrice = intent.getDoubleExtra("totalPrice", 0.00);

        Log.i("total price", String.valueOf(totalPrice));

        Log.i("valuestring", "before for loop:" + orderString);


        for (KitchenItem item : orderList){
            String newLine = System.getProperty("line.separator");

            orderString = orderString.concat(item.itemName + newLine);
            orderItemTv.setText(orderString);

            orderPriceString = orderPriceString.concat(String.valueOf("R" + item.itemPrice + newLine));
            orderItemPriceTv.setText(orderPriceString);

        }

        Log.i("valuestring", "after for loop:" + orderString);

        orderCostTv.setText("R" + String.valueOf(totalPrice));
        orderString = null;
        orderPriceString = null;


        //TODO print total price
        //TODO setup date picker
        mConfirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                startActivity(intent);

                final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference orderNoRef = mDatabase.getReference("Orders");
                orderNoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        orderNo = dataSnapshot.getValue(int.class);

                        Log.i("orderno", "after = datasnapshot:" + String.valueOf(orderNo));

                        if(orderNo == 0){

                            Toast.makeText(ConfirmOrderActivity.this, "Order Failed", Toast.LENGTH_SHORT).show();
                            Log.i("orderno", "failed = " + String.valueOf(orderNo));

                        }else{
                            Log.i("orderno", "works" + String.valueOf(orderNo));

                            DatabaseReference usersOrdersRef = mDatabase.getReference("Users/" + username + "/orders/" + String.valueOf(orderNo));

                            orderNoRef.setValue(orderNo + 1);

                            int day = mDatePicker.getDayOfMonth();
                            int month = mDatePicker.getMonth();
                            int year = mDatePicker.getYear();

                            Date pickUpDate = new Date(year, month, day);

                            Order thisOrder = new Order(orderNo, orderList, totalPrice, pickUpDate, false, username);
                            usersOrdersRef.setValue(thisOrder);

                            Toast.makeText(ConfirmOrderActivity.this, "Succesful order", Toast.LENGTH_SHORT).show();

                        }

                        /*DatabaseReference usersOrdersRef = mDatabase.getReference("Users/" + username + "/orders/" + orderNo);

                        orderNoRef.setValue(orderNo + 1);

                        int day = mDatePicker.getDayOfMonth();
                        int month = mDatePicker.getMonth() + 1;
                        int year = mDatePicker.getYear();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                        Date pickUpDate = new Date(year, month, day);

                        Order thisOrder = new Order(orderList, totalPrice, pickUpDate);
                        usersOrdersRef.setValue(thisOrder);

                        Toast.makeText(ConfirmOrderActivity.this, "Succesful order", Toast.LENGTH_SHORT).show(); */
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(ConfirmOrderActivity.this, PlaceOrderActivity.class);
                intent.putExtra("prevAct", "COA");
                startActivity(intent);
                orderString = null;
                orderPriceString = null;
                finish();
        }

        return true;
    }

    @Override
    public void onResume(){
        orderString = null;
        orderPriceString = null;
        Log.i("valuestring", "onResume" + orderString);
        super.onResume();
    }

    @Override
    public void onPause(){
        orderString = null;
        orderPriceString = null;
        Log.i("valuestring", "onPause" + orderString);
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.i("valuestring", "onStop" + orderString);
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(ConfirmOrderActivity.this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }
}
