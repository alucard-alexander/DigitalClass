package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    ArrayAdapter <String> adapter;
    List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.pdf_list,R.id.pdfInfo,list);
        db.collection("filesURL")
                .whereEqualTo("id",mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                //PdfContents pdfContents = new PdfContents(document.getString("name"),document.getString("description"),document.getString("url"));
                                list.add("File name: "+document.getString("name")+"\nFile Description: "+document.getString("description")+"\nURL: "+document.getString("url"));
                            }
                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2: {
                Toast.makeText(this, "Already in the same page", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.item3: {
                Intent i = new Intent(this, PDFReadings.class);
                startActivity(i);
                return true;
            }
            case R.id.item4: {

                mAuth.signOut();
                Intent i = new Intent(this, LoginPage.class);
                startActivity(i);
                return true;
            }
            case R.id.item5:{
                Intent i = new Intent(this,MainActivity.class);
                startActivity(i);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
