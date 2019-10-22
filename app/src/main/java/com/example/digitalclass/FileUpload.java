package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

public class FileUpload extends AppCompatActivity {

    private Uri pdfUri = null;
    private FirebaseStorage storage;
    private EditText ed7;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private Intent intent;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        storage = FirebaseStorage.getInstance();
        ed7 = findViewById(R.id.editText7);
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        intent = new Intent(this, MainActivity.class);
    }

    public void uploadFile(View view) {
        if (pdfUri != null) {
            //String fileName = System.currentTimeMillis()+"";
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading File...");
            progressDialog.setProgress(0);
            progressDialog.show();

            Uri file = pdfUri;
            String fileName = System.currentTimeMillis() + "";
            StorageReference riversRef = mStorageRef.child("pdf/" + fileName);

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl;
                            Task<Uri> downloadUrl1 = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            downloadUrl1.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Map<String, Object> fileURL1 = new HashMap<>();
                                    fileURL1.put("url", downloadUrl);

                                    db.collection("filesURL")
                                            .document(ed7.getText().toString())
                                            .set(fileURL1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.hide();
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Log.w(TAG, "Error adding document", e);
                                                    Toast.makeText(FileUpload.this, "Failed on storing file in database", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });


                            //Uri downloadUri = taskSnapshot.getDownload



                            //End of URL storing to the storage
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setProgress(currentProgress);
                        }
                    })
            ;


        } else {
            Toast.makeText(this, "Please choose the file and then upload", Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseFile(View view) {
        if (ContextCompat.checkSelfPermission(FileUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectPDF();
        } else {
            ActivityCompat.requestPermissions(FileUpload.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPDF();
        } else {
            Toast.makeText(this, "Read Permission should be granted inorder to upload File", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            Toast.makeText(this, "File is choosen", Toast.LENGTH_SHORT).show();
            //25 Min
        } else {
            Toast.makeText(this, "Please select a File", Toast.LENGTH_SHORT).show();
        }
    }
}
