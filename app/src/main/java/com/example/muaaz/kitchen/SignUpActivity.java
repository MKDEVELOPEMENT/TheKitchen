package com.example.muaaz.kitchen;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.muaaz.kitchen.classes.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText mEmailEditText;
    EditText mNameEditText;
    EditText mSurnameEditText;
    EditText mPhoneNoEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.signup_email_et);
        mNameEditText = (EditText) findViewById(R.id.signup_name_et);
        mSurnameEditText = (EditText) findViewById(R.id.signup_surname_et);
        mPhoneNoEditText = (EditText) findViewById(R.id.signup_phone_et);
        mPasswordEditText = (EditText) findViewById(R.id.signup_password_et);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.signup_password_confirm_et);
        mSignUpButton = (Button) findViewById(R.id.signup_signup_button);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean allchecked = true;

                final String email;
                final String name;
                final String surname;
                final long phoneNo;
                final String password;


                if(mEmailEditText.getText().toString().trim().length() > 0 ){
                    email = mEmailEditText.getText().toString();
                }else {
                    email = "";
                    allchecked = false;
                }

                if(mNameEditText.getText().toString().trim().length() > 0 ){
                    name = mNameEditText.getText().toString();
                }else {
                    name = null;
                    allchecked = false;
                }

                if(mSurnameEditText.getText().toString().trim().length() > 0 ){
                    surname = mSurnameEditText.getText().toString();
                }else {
                    surname = null;
                    allchecked = false;
                }

                if(mPhoneNoEditText.getText().toString().trim().length() == 10){
                    phoneNo = Long.parseLong(mPhoneNoEditText.getText().toString());
                }else {
                    phoneNo = 0;
                    allchecked = false;
                }

                if(mPasswordEditText.getText().toString().trim().length() > 0 ){
                    password = mPasswordEditText.getText().toString();
                }else {
                    password = null;
                    allchecked = false;
                }
                final String newmail = email.replaceAll("[.]", "%");
                String pass = mPasswordEditText.getText().toString();
                String confpass = mConfirmPasswordEditText.getText().toString();
                if(allchecked && pass.equals(confpass)){
                    final DatabaseReference ref = database.getReference("Users");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(newmail)) {
                                Log.i("child", "has child");
                                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("this email is already associated with another account");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }else{
                                Log.i("child", "new child successfully added");
                                DatabaseReference newUser = database.getReference("Users").child(newmail);
                                User user = new User(newmail, name, surname, phoneNo, password);
                                newUser.setValue(user);

                                Intent intent  = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.putExtra("userEmail", email);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //old code, kept in case for no real reason
                    /*ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(newmail).exists()){
                                Log.i("child", "has child");
                                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("One or more fields are empty or have incorrect inputs");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }else{
                                Log.i("nochild", "onDataChange: no child");
                                User user = new User(newmail, name, surname, phoneNo, password);
                                ref.setValue(user);

                                Intent intent  = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.putExtra("userEmail", email);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });*/

                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("One or more fields are empty or have incorrect inputs");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        super.onBackPressed();
    }
}