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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muaaz.kitchen.classes.KitchenItem;
import com.example.muaaz.kitchen.classes.Order;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ViewOrdersActivity extends AppCompatActivity {

    ListView ordersLv;

    FirebaseRemoteConfig mRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        ordersLv = (ListView) findViewById(R.id.vo_lv);

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + username + "/orders");

        ListAdapter mAdapter = new FirebaseListAdapter<Order>(this, Order.class, R.layout.view_orders_item_layout, ref) {

            @Override
            protected void populateView(View view, final Order order, int position) {

                LinearLayout parentLL = (LinearLayout) view.findViewById(R.id.mivo_ll);

                TextView orderNoTv = (TextView) view.findViewById(R.id.item_name);
                TextView amountItemsTv = (TextView) view.findViewById(R.id.item_price);
                TextView PUDateTv = (TextView) view.findViewById(R.id.voi_order_pickup_date_tv);

                final String orderNoText = "#" + order.OrderNumber;
                final String amountIemsText = String.valueOf(order.Items.size()) + " items";

                Date pickUpDate = order.PickUpDate;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
                String strDate = dateFormat.format(pickUpDate);

                orderNoTv.setText(orderNoText);
                amountItemsTv.setText(amountIemsText);
                PUDateTv.setText(strDate);

                Boolean confirmed = order.Confirmed;

                if (confirmed){
                    parentLL.setBackgroundColor(getResources().getColor(R.color.orderConfirmed));
                }else{
                    parentLL.setBackgroundColor(getResources().getColor(R.color.orderCancelled));
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog alertDialog = new AlertDialog.Builder(ViewOrdersActivity.this).create();
                        alertDialog.setTitle(orderNoText);
                        alertDialog.setMessage(amountIemsText);
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "View Order",
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(ViewOrdersActivity.this, ViewIndivOrderActivity.class);
                                intent.putExtra("orderNumber", order.OrderNumber);
                                intent.putExtra("itemsInList", order.Items);
                                intent.putExtra("totalPrice", order.TotalPrice);
                                intent.putExtra("pickUpDate", order.PickUpDate);
                                startActivity(intent);
                            }
                        });
                        alertDialog.show();

                    }
                });
            }
        };

        ordersLv.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(ViewOrdersActivity.this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }

}
