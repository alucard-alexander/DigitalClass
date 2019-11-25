package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        //homePage = new Intent(this,MainActivity.class);
        progressDialog = new ProgressDialog(this);


    }

    public void signUp(View view){
        Intent i = new Intent(this,RegistrationPage.class);
        startActivity(i);
    }

    public void logInUser(View view){
        progressDialog.setMessage("Signing Innnnn");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()){
                            try {

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("person")
                                        .document(mAuth.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    //Toast.makeText(LoginPage.this, "Successful", Toast.LENGTH_SHORT).show();

                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d("dataaaaa", "DocumentSnapshot data: " + document.getString("type"));
                                                        Toast.makeText(LoginPage.this, document.getString("type"), Toast.LENGTH_SHORT).show();
                                                        if (document.getString("type").equals("teacher")){
                                                            homePage = new Intent(LoginPage.this,FileUpload.class);
                                                            startActivity(homePage);
                                                        }
                                                        if (document.getString("type").equals("student")){
                                                            homePage = new Intent(LoginPage.this,PDFReadings.class);
                                                            startActivity(homePage);
                                                        }

                                                    } else {
                                                        Log.d("dattaaa", "Couldn't get data");
                                                    }

                                                }
                                            }
                                        });
                            }catch (Exception e){
                                progressDialog.hide();
                                Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("errrrrr", e.getMessage());
                            }

                            //Toast.makeText(LoginPage.this, "Logged In", Toast.LENGTH_SHORT).show();
                            try {
                                startActivity(homePage);
                            }catch (Exception e){
                                Log.d("errrrr", e.getMessage());
                            }



                        }else{
                            Toast.makeText(LoginPage.this, "Wrong Password or Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
