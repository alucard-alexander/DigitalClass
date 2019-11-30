package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class forgotPassword extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void sendPasswordResetLink(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking login credential");

        final EditText editText = findViewById(R.id.editText12);
        final String email = editText.getText().toString();
        if (email.isEmpty()){
            Toast.makeText(this, "Please Enter the email and proceed", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (!task.getResult().getSignInMethods().isEmpty()){
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    progressDialog.hide();
                                                    Toast.makeText(forgotPassword.this, "email is send Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(forgotPassword.this,LoginPage.class);
                                                    startActivity(i);
                                                }
                                                else{
                                                    progressDialog.hide();
                                                    Toast.makeText(forgotPassword.this, "Email does not exists, Need to register", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                            }
                        }
                    });
        }
    }
}
