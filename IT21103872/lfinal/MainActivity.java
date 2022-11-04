package com.example.lfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Privalent.Privalent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.loButton);
        registerButton = (Button) findViewById(R.id.rButton);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,dHome.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, register.class);
                startActivity(intent);
            }
        });

         String UserPhoneKey = Paper.book().read(Privalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Privalent.UserPassword);

         if(UserPhoneKey != "" && UserPasswordKey != "")
         {
             if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
             {
                 AllowAccess(UserPhoneKey,UserPasswordKey);

                 loadingBar.setTitle("Already Logged in");
                 loadingBar.setMessage("Please wait....");
                 loadingBar.setCanceledOnTouchOutside(false);
                 loadingBar.show();
             }
         }


    }

    private void AllowAccess(final String telephone,final String password)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(telephone).exists())
                {
                    Users userData = snapshot.child("Users").child(telephone).getValue(Users.class);

                    if(userData.getTelephone().equals(telephone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in successfuly...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, Uprofile.class);
                            startActivity(intent);


                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with this"+telephone+"number do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}