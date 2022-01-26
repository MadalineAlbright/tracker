package com.challengers.trackmyorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.challengers.trackmyorder.model.Customer;
import com.challengers.trackmyorder.model.DeliveryBoy;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUp extends AppCompatActivity{

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private String email, password, confirmPassword, userType;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Button SignUpBtn, Gotologinbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        String loginType = getIntent().getStringExtra(Constants.LOGINTYPE);
        if(loginType.equals(Constants.CUSTOMER)){
            setTitle("Customer Registration");
        }
        else if(loginType.equals(Constants.DELIVERY_BOY)){
            setTitle("Delivery Boy Registration");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userType = getIntent().getStringExtra(Constants.LOGINTYPE);
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(SignUp.this, OrderProductActivity.class);
            intent.putExtra(Constants.CURRENT_USER,firebaseAuth.getCurrentUser().getUid());
        }

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.userPass);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
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
                Intent login =new Intent(SignUp.this, LoginActivity.class);
                login.putExtra(Constants.LOGINTYPE,getIntent().getStringExtra(Constants.LOGINTYPE));
                startActivity(login);
            }
        });


    }

    private void registerUser() {
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        confirmPassword = confirmPasswordEditText.getText().toString().trim();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all values", Toast.LENGTH_SHORT).show();
        }
        else{
            if (!password.equals(confirmPassword)) {
                confirmPasswordEditText.setError("Password Mismatch");
            }
            else{
                if(password.length()>=6){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String userId = user.getUid();
                                String userEmail = firebaseAuth.getCurrentUser().getEmail();
                                addToFirestore(userId, userEmail);
                            }
                        }
                    });
                }
                else{
                    passwordEditText.setError("Weak Password");
                    confirmPasswordEditText.setError("Weak Password");
                }
            }
        }
    }

    private void addToFirestore(String userid, String email) {
        if (userType.equals(Constants.DELIVERY_BOY)) {
            DeliveryBoy deliveryBoy = new DeliveryBoy(userid,email);
            firebaseFirestore.collection("users")
                    .document(userid)
                    .set(deliveryBoy)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(SignUp.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                        Intent driver = new Intent(SignUp.this, DeliveryBoyActivity.class);
                        driver.putExtra(Constants.CURRENT_DELBOY,userid);
                        startActivity(driver);

                    })
                    .addOnFailureListener(e -> {
                        Log.i("Register Error", e.getMessage());
                        Toast.makeText(SignUp.this, "Something went wrong! Try again later", Toast.LENGTH_SHORT).show();
                    });
        }
        else if(userType.equals(Constants.CUSTOMER)){
            Customer customer = new Customer(userid,email);
            firebaseFirestore.collection("users")
                    .document(userid)
                    .set(customer)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(SignUp.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                        Intent order = new Intent(SignUp.this, OrderProductActivity.class);
                        order.putExtra(Constants.CURRENT_USER,userid);
                        startActivity(order);
                    })
                    .addOnFailureListener(e -> {
                        Log.i("Register Error", e.getMessage());
                        Toast.makeText(SignUp.this, "Something went wrong! Try again later", Toast.LENGTH_SHORT).show();
                    });
        }
        else if(userType.equals("admin")){
            //TODO add admin page
        }
    }
}
