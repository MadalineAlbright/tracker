package com.challengers.trackmyorder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.BreakIterator;

public class SignUp extends AppCompatActivity {
    private void registerUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        BreakIterator editTextEmail;
        String email = editTextEmail.getText().toString().trim();
        BreakIterator editTextPassword;
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Emails", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Your PASSWORDS", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User Statement..");
        progressDialog.show();


        //Firebase authentication (account save)
        Object firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        Task<AuthResult> users = ((FirebaseAuth) firebaseAuth).createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user already logged in
                            //start the activity
                            finish();
                            onAuthSuccess(task.getResult().getUser());
                            // startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Object user = null;
                            Object userId = null;
                            Firebase mDatabase = null;
                            mDatabase.child("users").child((String) userId).setValue(user);
                        }
                    }

                    private void onAuthSuccess(FirebaseUser user) {
                    }
    if(toggleBut.isChecked())

                    {
                        int toggle_val = 2;
                        //Toast.makeText(SignupActivity.this, "You're Driver", Toast.LENGTH_SHORT).show();
                    }
    else

                    {
                        int toggle_val = 1;
                        //Toast.makeText(SignupActivity.this, "You're Customer", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
