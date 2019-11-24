package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import java.util.regex.Pattern;

public class RegistrationPage extends AppCompatActivity {
    private EditText name, email, pass,rePass, mobile;
    private Intent loginPage;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.editText);
        email = findViewById(R.id.editText1);
        pass = findViewById(R.id.editText2);
        rePass = findViewById(R.id.editText3);
        mobile = findViewById(R.id.editText4);
        progressDialog = new ProgressDialog(this);
        loginPage = new Intent(this, LoginPage.class);
    }

    public boolean isValidateFields(){
        //Checking Empty

        String nameVar,emailVar,passVar,rePassVar,mobileVar;
        nameVar = name.getText().toString();
        emailVar = email.getText().toString();
        passVar = pass.getText().toString();
        rePassVar = rePass.getText().toString();
        mobileVar = mobile.getText().toString();

        if (nameVar.isEmpty()){
            name.setError("Name Field cannot be empty");
            return false;
        }
        if (emailVar.isEmpty()){
            email.setError("Email Field cannot be empty");
            return false;
        }
        if (passVar.isEmpty()){
            pass.setError("Password Field cannot be empty");
            return false;
        }
        if (rePassVar.isEmpty()){
            rePass.setError("Re-type the password");
            return false;
        }
        if (mobileVar.isEmpty()){
            mobile.setError("Mobile Number should be provided");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailVar).matches()){
            email.setError("Email is invalid");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(passVar).matches()){
            pass.setError("Password is not strong, Enter atleast 1 Upper, 1 Lower and 1 special character");
            return false;
        }

        if (!passVar.equals(rePassVar)){
            rePass.setError("Retype password is not same");
            return false;
        }

        if (mobile.length() < 10){
            mobile.setError("Mobile number should have 10 digits");
            return false;
        }



        return true;
    }

    public void registerUser(View view) {

        if (!isValidateFields()){
            return;
        }


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

        mAuth= FirebaseAuth.getInstance();
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
                                            mAuth.signOut();
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
