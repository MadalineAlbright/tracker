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
import com.google.firebase.auth.FirebaseAuth;

//import android.support.v7.app.AppCompatActivity;

public class LoginActivity<password, email> extends AppCompatActivity {
    EditText userIdEditText,userPassEditText;
    String userName,userPass, loginType;
    EditText mEmail;
    EditText mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dboy);
        setTitle("Login");
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginbtn);
        mCreateBtn = findViewById(R.id.createText);
        fAuth = FirebaseAuth.getInstance();
        userIdEditText = (EditText) findViewById(R.id.userName);
        userPassEditText = (EditText) findViewById(R.id.userPass);

        loginType = getIntent().getStringExtra(Constants.LOGINTYPE);
    }

    public void doLogin(View v){
        userName = userIdEditText.getText().toString();
        userPass = userPassEditText.getText().toString();
        if(loginType.equals("D")) {




        }

    });

            if (
                    //check if email field is empty
                if (TextUtils.isEmpty(email)){
        mEmail.setError("Email is required.");
        return;
    }

    //check if password field is empty
                if (TextUtils.isEmpty(password)){
        mPassword.setError("Please enter your password");
        return;
    }

                if (password.length() (6){
        mPassword.setError("Password must have more than six characters");
        return;
    }) {
                Intent intent = new Intent(this, DboyActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
            }
        } else {

            if (
                    //check if email field is empty
        if (TextUtils.isEmpty(email)){
        mEmail.setError("Email is required.");
        return;
        }

        //check if password field is empty
        if (TextUtils.isEmpty(password)){
        mPassword.setError("Please enter your password");
        return;
        }

        if (password.length() < 6){
        mPassword.setError("Password must have more than six characters");
        return;
        }) {
                Intent intent = new Intent(this, ShowUserOrdersActivity.class
                startActivity(intent); //correct
            } else {
                Toast.makeText(this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
            }
        }

    }
                    //authenticating user
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
        //checks whether task was successful
        if (task.isSuccessful()){
        Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else {
        Toast.makeText(LogIn.this, "Error:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
        Toast.LENGTH_SHORT).show();
        }
        }
        });

        }

        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), SignUp.class));
        }
        });


        }
        }
        }
        });

}
