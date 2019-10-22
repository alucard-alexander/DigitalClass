package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PDFReadings extends AppCompatActivity {

    private StorageReference mStorageRef;
    //StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreadings);
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1571709881911?alt=media&token=c8a0a4d8-3445-4fb0-884d-6620d8f605fc");
    }

    public void downloadFile(View view) throws IOException {
        File localFile = File.createTempFile("test123","pdf");
        //File localFile = new File(Environment.DIRECTORY_DOWNLOADS +"/test.pdf");
        mStorageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(PDFReadings.this, "Downloaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PDFReadings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
