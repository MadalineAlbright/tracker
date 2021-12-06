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
import com.google.firebase.firestore.FirebaseFirestore;

//import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText userIdEditText, userPassEditText;
    String email, userPass, loginType;
    EditText mEmail;
    EditText mPassword;
    Button mLoginBtn, Gotosignupbtn;
    TextView mCreateBtn;
    FirebaseAuth fAuth;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
                startActivity(new Intent(LoginActivity.this, SignUp.class));
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

    public void doLogin() {
        email = userIdEditText.getText().toString();
        userPass = userPassEditText.getText().toString();


        //check if email field is empty
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required");
            return;
        }


//check if password field is empty
        if (TextUtils.isEmpty(userPass)) {
            mPassword.setError("Please enter your password");
            return;
        }
        fAuth.signInWithEmailAndPassword(email, userPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_id = currentUser.getUid();
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(task1 -> {
                                String user_type = task1.getResult().getString("user_type");

                                if (user_type.equals("Driver")) {
                                    Intent intent
                                            = new Intent(LoginActivity.this,
                                            DboyActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);


                                } else if (user_type.equals("Customer")) {
                                    Intent intent = new Intent(LoginActivity.this, OrderDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);


                                } else if (user_type.equals("admin")) {
                                    Intent intent = new Intent(LoginActivity.this, OrderDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();

                                    // hide the progress dialog

                                }
                            });

                        }

                        // Sign in success, update UI with the signed-in user's information
                        /*FirebaseUser user = fAuth.getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this,OrderDetailActivity.class);
                        startActivity(intent);*/
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }
}