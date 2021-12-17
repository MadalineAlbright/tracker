package com.challengers.trackmyorder;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.challengers.trackmyorder.util.Constants;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

//import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText userIdEditText, userPassEditText;
    String email, userPass, loginType, userId;
    Button mLoginBtn, Gotosignupbtn;
    FirebaseAuth fAuth;
    ProgressBar spinnerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dboy);
        setTitle("Login");


        fAuth = FirebaseAuth.getInstance();
        loginType = getIntent().getStringExtra(Constants.LOGINTYPE);

        mLoginBtn = findViewById(R.id.loginbtn);
        Gotosignupbtn = findViewById(R.id.Gotosignupbtn);
        userIdEditText = (EditText) findViewById(R.id.email);
        userPassEditText = (EditText) findViewById(R.id.userPass);
        spinnerLogin = findViewById(R.id.spinnerLogin);

        Gotosignupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(LoginActivity.this, SignUp.class);
                signUp.putExtra(Constants.LOGINTYPE,loginType);
                startActivity(signUp);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

    }

    public void doLogin() {
        spinnerLogin.setVisibility(View.VISIBLE);
        email = userIdEditText.getText().toString().trim();
        userPass = userPassEditText.getText().toString().trim();


        //check if email field is empty
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(userPass)) {
            Toast.makeText(LoginActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        }
        else{
            fAuth.signInWithEmailAndPassword(email, userPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(task1 -> { String user_type = task1.getResult().getString("user_type");

                                userId = fAuth.getCurrentUser().getUid();
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        String userType = value.getString("user_type");

                                        Toast.makeText(LoginActivity.this, "User exists "+ userType, Toast.LENGTH_SHORT).show();
                                        if (userType.equals("Driver") && getIntent().getStringExtra(Constants.LOGINTYPE).equals("Driver")) {
                                            Intent intent = new Intent(LoginActivity.this, DboyActivity.class);
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                            startActivity(intent);


                                        } else if (userType.equals("Customer") && getIntent().getStringExtra(Constants.LOGINTYPE).equals("Customer")) {
                                            Intent intent = new Intent(LoginActivity.this, OrderProductActivity.class);
                                            intent.putExtra(Constants.CURRENT_USER,userId);
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else if (userType.equals("admin")) {
                                            Intent intent = new Intent(LoginActivity.this, ShowUserOrdersActivity.class);
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else {
                                            spinnerLogin.setVisibility(View.INVISIBLE);
                                            Toast.makeText(LoginActivity.this, "Account does not exist. ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            // Sign in success, update UI with the signed-in user's information/
                            else {
                                spinnerLogin.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }



    }
}