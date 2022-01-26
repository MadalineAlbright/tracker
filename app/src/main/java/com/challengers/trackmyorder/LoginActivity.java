package com.challengers.trackmyorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class LoginActivity extends AppCompatActivity {
    protected EditText userIdEditText, userPassEditText;
    protected String email, userPass, loginType, userId;
    protected Button mLoginBtn, Gotosignupbtn;
    protected FirebaseAuth fAuth;
    protected ProgressDialog progBar;
    protected FirebaseFirestore firebaseFirestore;
    protected CollectionReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dboy);
        setTitle("Login");

        fAuth = FirebaseAuth.getInstance();
        loginType = getIntent().getStringExtra(Constants.LOGINTYPE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        if(fAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, OrderProductActivity.class);
            intent.putExtra(Constants.CURRENT_USER,userId);
        }
        mLoginBtn = findViewById(R.id.loginbtn);
        Gotosignupbtn = findViewById(R.id.Gotosignupbtn);
        userIdEditText = (EditText) findViewById(R.id.email);
        userPassEditText = (EditText) findViewById(R.id.userPass);

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
                progBar = new ProgressDialog(LoginActivity.this);
                progBar.setTitle("Authenticating.");
                progBar.setMessage("Please wait");
                progBar.setCancelable(false);
                progBar.show();
                doLogin();
            }
        });

    }

    public void doLogin() {
        email = userIdEditText.getText().toString().trim();
        userPass = userPassEditText.getText().toString().trim();


        //check if email field is empty
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(userPass)) {
            progBar.dismiss();
            Toast.makeText(LoginActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        }
        else{
            try{
                fAuth.signInWithEmailAndPassword(email, userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    userId = fAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = usersReference.document(userId);
                                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot value,FirebaseFirestoreException error) {
                                            progBar.dismiss();
                                            String userType = value.getString("type");
                                            if(userType == null){
                                                Toast.makeText(LoginActivity.this, "Error encountered", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                if (userType.equals(Constants.DELIVERY_BOY) && getIntent().getStringExtra(Constants.LOGINTYPE).equals(Constants.DELIVERY_BOY)) {
                                                    progBar.dismiss();
                                                    Intent driver = new Intent(LoginActivity.this, DeliveryBoyActivity.class);
                                                    driver.putExtra(Constants.CURRENT_DELBOY,userId);
                                                    driver.putExtra("email",fAuth.getCurrentUser().getEmail());
                                                    startActivity(driver);
                                                }
                                                else if (userType.equals(Constants.CUSTOMER) && getIntent().getStringExtra(Constants.LOGINTYPE).equals(Constants.CUSTOMER)) {
                                                    progBar.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, OrderProductActivity.class);
                                                    intent.putExtra(Constants.CURRENT_USER,userId);
                                                    startActivity(intent);
                                                }
                                                else if (userType.equals("admin")) {
                                                    progBar.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, ShowUserOrdersActivity.class);
                                                    startActivity(intent);
                                                }
                                                else {
                                                    progBar.dismiss();
                                                }
                                            }
                                        }
                                    });
                                }
                                else {
                                    progBar.dismiss();
                                    Exception exception = task.getException();
                                    Log.i("Login Error",exception.getMessage());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            catch (Exception e){
                Log.i("Login Error",e.getMessage());
            }
        }



    }
}