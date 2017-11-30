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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muaaz.kitchen.classes.KitchenItem;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity {

    LinearLayout mParentLL;

    ListView sweets;

    private static Double GT;

    Menu m;

    FirebaseRemoteConfig mRemoteConfig;

    static ArrayList<KitchenItem> orderedItems = new ArrayList<KitchenItem>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        m = menu;
        getMenuInflater().inflate(R.menu.place_order_activity_menu, menu);
        return true;
    }

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

        ListAdapter mAdaper = new FirebaseListAdapter<KitchenItem>(this, KitchenItem.class, R.layout.menu_item_layout, ref){
            @Override
            protected void populateView(View view, final KitchenItem kitchenItem, int position) {

                TextView nameTv = (TextView) view.findViewById(R.id.item_name);
                TextView priceTv = (TextView) view.findViewById(R.id.item_price);

                final TextView itemTotalPrice = (TextView) view.findViewById(R.id.semi_total_text);

                Button addItemBtn = (Button) view.findViewById(R.id.add_item_button);
                final TextView amountOfItems = (TextView) view.findViewById(R.id.item_amount_text);
                Button removeItemBtn = (Button) view.findViewById(R.id.remove_item_button);

                nameTv.setText(kitchenItem.itemName);
                priceTv.setText(String.valueOf(kitchenItem.itemPrice));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(PlaceOrderActivity.this).create();
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

                addItemBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int amountOfIndivItem = Integer.parseInt(String.valueOf(amountOfItems.getText()));
                        amountOfIndivItem += 1;
                        amountOfItems.setText(String.valueOf(amountOfIndivItem));

                        Double indivItemTotalPrice = kitchenItem.itemPrice * amountOfIndivItem;
                        itemTotalPrice.setText("R" + String.valueOf(indivItemTotalPrice));

                        GT += kitchenItem.itemPrice;

                        MenuItem menuPrice = m.findItem(R.id.po_menu_total_amount);
                        menuPrice.setTitle("R" + String.valueOf(GT));

                        orderedItems.add(kitchenItem);
                    }
                });

                removeItemBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        if (Integer.parseInt(String.valueOf(amountOfItems.getText())) > 0){

                            int amountOfIndivItem = Integer.parseInt(String.valueOf(amountOfItems.getText()));
                            amountOfIndivItem -= 1;
                            amountOfItems.setText(String.valueOf(amountOfIndivItem));

                            Double indivItemTotalPrice = kitchenItem.itemPrice * amountOfIndivItem;
                            itemTotalPrice.setText("R" + String.valueOf(indivItemTotalPrice));

                            GT -= kitchenItem.itemPrice;

                            MenuItem menuPrice = m.findItem(R.id.po_menu_total_amount);
                            menuPrice.setTitle("R" + String.valueOf(GT));

                            orderedItems.remove(kitchenItem);

                        }

                    }
                });


            }

        };
        sweets.setAdapter(mAdaper);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        GT = 0.00;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        super.onOptionsItemSelected(menuItem);

        switch (menuItem.getItemId()){

            case R.id.po_menu_send_order:
                if (orderedItems.size() == 0){

                    final AlertDialog alertDialog = new AlertDialog.Builder(PlaceOrderActivity.this).create();
                    alertDialog.setTitle("Oops");
                    alertDialog.setMessage("It appears you do not have any items selected for purchase. Please add items to proceed to check-out.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Add Items",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }else{
                    Intent intent = new Intent(PlaceOrderActivity.this, ConfirmOrderActivity.class);
                    intent.putExtra("orderList", orderedItems);
                    intent.putExtra("totalPrice", GT);
                    startActivity(intent);
                    orderedItems.clear();
                    finish();
                    break;
                }

        }
        return true;
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(PlaceOrderActivity.this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }

}
