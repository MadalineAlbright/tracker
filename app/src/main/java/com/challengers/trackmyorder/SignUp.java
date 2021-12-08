package com.challengers.trackmyorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText emailEditText, passwordEditText, ConfirmPasswordEditText;
    private String email, password, confirmPassword, userType;
    private String[] userTypes;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Button SignUpBtn, Gotologinbtn;

    @Override
    protected void onCreate(Bundle
                                    savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userTypes = new String[]{"Driver", "Customer"};
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                userTypes);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spin.setAdapter(ad);


        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.userPass);
        ConfirmPasswordEditText = findViewById(R.id.confirmPassword);
        SignUpBtn = findViewById(R.id.SignUpBtn);
        Gotologinbtn = findViewById(R.id.Gotologinbtn);
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        Gotologinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, LoginActivity.class));
            }
        });


    }

    private void registerUser() {
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("creating your account");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = ConfirmPasswordEditText.getText().toString();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all values", Toast.LENGTH_SHORT).show();
            return;

        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;


        }






        /*if (TextUtils.isEmpty(userType)) {
            Toast.makeText(this, "Select the usertype", Toast.LENGTH_SHORT).show();
            return;
        }*/


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userid = user.getUid();
                            addToFirestore(userid);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void addToFirestore(String userid) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("user_type", userType);
        firebaseFirestore.collection("users")
                .document(userid)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SignUp.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                    if (userType.equals("Driver")) {
                        startActivity(new Intent(SignUp.this, DboyActivity.class));
                    } else if (userType.equals("Customer")) {
                        startActivity(new Intent(SignUp.this, ShowUserOrdersActivity.class));


                    } else if (userType.equals("admin")) {
                        startActivity(new Intent(SignUp.this, ShowUserOrdersActivity.class));

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(SignUp.this, "Something went wrong! Try again later", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userType = userTypes[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
