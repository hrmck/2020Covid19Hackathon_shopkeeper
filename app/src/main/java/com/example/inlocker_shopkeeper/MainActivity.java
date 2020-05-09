package com.example.inlocker_shopkeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button toLogin, toSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toLogin = findViewById(R.id.loginBtn_main);
        toSignUp = findViewById(R.id.signUpBtn_main);

        toLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent toLoginPage = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(toLoginPage);
            }
        });

        toSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent toSignUpPage = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(toSignUpPage);
            }
        });

    }
}
