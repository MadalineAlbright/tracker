package com.challengers.trackmyorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.challengers.trackmyorder.util.Constants;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText userIdEditText,userPassEditText;
    String email,userPass, loginType;
    EditText mEmail;
    EditText mPassword;
    Button mLoginBtn,Gotosignupbtn;
    TextView mCreateBtn;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dboy);
        setTitle("Login");

        mLoginBtn = findViewById(R.id.loginbtn);
        Gotosignupbtn = findViewById(R.id.Gotosignupbtn);
        Gotosignupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (LoginActivity.this,SignUp.class));
            }
        });


        //7mCreateBtn = findViewById(R.id.createText);
        fAuth = FirebaseAuth.getInstance();
        userIdEditText = (EditText) findViewById(R.id.email);
        userPassEditText = (EditText) findViewById(R.id.userPass);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        //loginType = getIntent().getStringExtra(Constants.LOGINTYPE);
    }

    public void doLogin(){
        email = userIdEditText.getText().toString();
        userPass = userPassEditText.getText().toString();




                    //check if email field is empty
                if (TextUtils.isEmpty(email)){
        mEmail.setError("Email is required");
        return;
                }


    //check if password field is empty
                if (TextUtils.isEmpty(userPass)){
        mPassword.setError("Please enter your password");
        return;
    }
        fAuth.signInWithEmailAndPassword(email, userPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = fAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this,OrderDetailActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        }

    }



