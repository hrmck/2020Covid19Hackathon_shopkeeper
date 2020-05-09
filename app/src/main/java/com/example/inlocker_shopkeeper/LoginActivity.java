package com.example.inlocker_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button login;
    EditText editTextEmail, editTextPwd;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email_login_editText);
        editTextPwd = findViewById(R.id.pwd_login_editText);
        progressBar = findViewById(R.id.login_progressBar);

        login = findViewById(R.id.loginBtn_login);
        login.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String pwd = editTextPwd.getText().toString().trim();

        if (isValidLoginCred(email, pwd)){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        Intent toMenu = new Intent(LoginActivity.this, SellPageActivity.class);
                        getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(toMenu);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private Boolean isValidLoginCred(String email, String pwd){
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }
        if(pwd.isEmpty()){
            editTextPwd.setError("Password is required");
            editTextPwd.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginBtn_login){
            userLogin();
        }
    }
}
