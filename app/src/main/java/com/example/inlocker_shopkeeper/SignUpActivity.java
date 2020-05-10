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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    Button signUp;
    EditText editTextEmail, editTextPwd, editTextPwdC;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email_signUp_editText);
        editTextPwd = findViewById(R.id.pwd_signUp_editText);
        editTextPwdC = findViewById(R.id.pwd_c_signUp_editText);

        progressBar = findViewById(R.id.signUp_progressBar);

        signUp  = findViewById(R.id.signUpBtn_signUp);
        signUp.setOnClickListener(this);
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String pwd = editTextPwd.getText().toString().trim();
        String pwdC = editTextPwdC.getText().toString().trim();

       if (isValidSignUpCred(email, pwd, pwdC)){
           progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Intent toMain = new Intent(SignUpActivity.this, MainActivity.class);
                        getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(toMain);
                    }
                    else{
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(),
                                    "You're already registered", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
       }
    }

    private Boolean isValidSignUpCred(String email, String pwd, String pwdC){
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
        if(pwdC.isEmpty()){
            editTextPwdC.setError("Confirm password is required");
            editTextPwdC.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }
        if(pwd.length() < 6){
            editTextPwd.setError("Minimum length of password should be 6");
            editTextPwd.requestFocus();
            return false;
        }
        if (!pwd.equals(pwdC)){
            //not same password, error
            editTextPwdC.setError("Both passwords are not the same");
            editTextPwdC.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signUpBtn_signUp){
            registerUser();
        }
    }
}
