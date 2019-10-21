package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Intent homePage;
    private EditText email,pass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editText5);
        pass = findViewById(R.id.editText6);
        homePage = new Intent(this,MainActivity.class);
        progressDialog = new ProgressDialog(this);
    }

    public void signUp(View view){
        Intent i = new Intent(this,RegistrationPage.class);
        startActivity(i);
    }

    public void logInUser(View view){
        progressDialog.setMessage("Registering");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()){
                            Toast.makeText(LoginPage.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            startActivity(homePage);
                        }else{
                            Toast.makeText(LoginPage.this, "Wrong Password or Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
