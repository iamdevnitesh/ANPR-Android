package com.iamdevnitesh.anpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iamdevnitesh.anpr.R;
import com.iamdevnitesh.anpr.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth ;
    DatabaseReference anprDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        // find todays date java
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = formatter.format(date);
        anprDB = FirebaseDatabase.getInstance().getReference(strDate);
        Log.d("Todays Date",strDate);

        Log.d("Firebase fetch",anprDB.child("-NH8upL_rqVnMc66ZnmH").child("date").getKey());


        binding.BtnGenerateReport.setOnClickListener(v -> {
            // generate report and show it on the screen
        });
    }

    public void addUser(MenuItem menuItem){
        // go to new activity
        startActivity(new Intent(MainActivity.this, LicenseListActivity.class));
        finish();
    }

    public void logOut(MenuItem menuItem){
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }
}