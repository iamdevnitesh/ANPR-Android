package com.iamdevnitesh.anpr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.iamdevnitesh.anpr.R;
import com.iamdevnitesh.anpr.databinding.ActivityAddUserBinding;

public class AddUserActivity extends AppCompatActivity {
    ActivityAddUserBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Register User");
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.ActionBar));
        ab.setBackgroundDrawable(colorDrawable);


        mAuth = FirebaseAuth.getInstance();

        binding.BtnRegister.setOnClickListener(v -> {
            // register user
            registerUser();
        });
    }

    private void registerUser() {
        String email = binding.EdtTxtUserName.getEditableText().toString().trim();
        String password = binding.EdtTxtPassword.getEditableText().toString().trim();
        Toast.makeText(this, email + " " + password, Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(binding.EdtTxtUserName.toString())) {
            binding.EdtTxtUserName.setError("Email is required");
            binding.EdtTxtUserName.requestFocus();
        } else if (TextUtils.isEmpty(binding.EdtTxtPassword.toString())) {
            binding.EdtTxtPassword.setError("Password is required");
            binding.EdtTxtPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                Toast.makeText(AddUserActivity.this, "Registeration Complete", Toast.LENGTH_SHORT).show();
                                // start activity after 2 seconds of delay
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(AddUserActivity.this, MainActivity.class));
                                    }
                                }, 2000);
                            }
                            else {
                                //Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage()); //ADD THIS
                                Toast.makeText(AddUserActivity.this, "Registeration Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}