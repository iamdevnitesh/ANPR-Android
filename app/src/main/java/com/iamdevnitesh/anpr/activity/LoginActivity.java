package com.iamdevnitesh.anpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.iamdevnitesh.anpr.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.BtnLogin.setOnClickListener(v -> {
            loginUser();
        });

    }

    private void loginUser() {
        String email = binding.EdtTxtUserName.getEditableText().toString().trim();
        String password = binding.EdtTxtPassword.getEditableText().toString().trim();
        if (TextUtils.isEmpty(binding.EdtTxtUserName.toString())) {
            binding.EdtTxtUserName.setError("Email is required");
            binding.EdtTxtUserName.requestFocus();
        } else if (TextUtils.isEmpty(binding.EdtTxtPassword.toString())) {
            binding.EdtTxtPassword.setError("Password is required");
            binding.EdtTxtPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /*
    private void registerUser() {
        String email = binding.EdtTxtUserName.getEditableText().toString().trim();
        String password = binding.EdtTxtPassword.getEditableText().toString().trim();
        Log.d(TAG, "registerUser: " + email + " " + password);
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
                            //Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(LoginActivity.this, "Registeration Complete", Toast.LENGTH_SHORT).show();

                            if (!task.isSuccessful()) {
                                //Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage()); //ADD THIS
                                Toast.makeText(LoginActivity.this, "Registeration Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
     */
}