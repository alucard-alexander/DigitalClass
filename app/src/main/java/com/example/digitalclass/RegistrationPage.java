package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {
    private EditText name, email, pass, mobile;
    private Intent loginPage;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.editText);
        email = findViewById(R.id.editText1);
        pass = findViewById(R.id.editText2);
        mobile = findViewById(R.id.editText4);
        progressDialog = new ProgressDialog(this);
        loginPage = new Intent(this, LoginPage.class);
    }

    public void registerUser(View view) {

        progressDialog.setMessage("Registering");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadData(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    public void uploadData(final String userID) {

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> user1 = new HashMap<>();
                            user1.put("name", name.getText().toString());
                            user1.put("mobile", mobile.getText().toString());
                            RadioButton teacher,student;
                            teacher = findViewById(R.id.radioButton);
                            student = findViewById(R.id.radioButton2);
                            if (teacher.isChecked()){
                                user1.put("type","teacher");
                            }else if (student.isChecked()){
                                user1.put("type","student");
                            }


                            db.collection("person").document(userID).set(user1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(RegistrationPage.this, "SuccessFully Registered", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                            startActivity(loginPage);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistrationPage.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                });




    }

}
