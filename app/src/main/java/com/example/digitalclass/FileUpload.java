package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import java.security.Permission;

public class FileUpload extends AppCompatActivity {

    private Uri pdfUri;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
    }

    public void chooseFile(View view){
        if (ContextCompat.checkSelfPermission(FileUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            selectPDF();
        }else{
            ActivityCompat.requestPermissions(FileUpload.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPDF();
        }else{
            Toast.makeText(this, "Read Permission should be granted inorder to upload File", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null){
            pdfUri = data.getData();

            //25 Min
        }else{
            Toast.makeText(this, "Please select a File", Toast.LENGTH_SHORT).show();
        }
    }
}
