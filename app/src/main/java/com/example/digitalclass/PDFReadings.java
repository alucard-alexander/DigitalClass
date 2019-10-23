package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PDFReadings extends AppCompatActivity {

    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;

    //private StorageReference storageReference,ref;
    //private FirebaseStorage firebaseStorage;
    //StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreadings);

        //firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1571709881911?alt=media&token=c8a0a4d8-3445-4fb0-884d-6620d8f605fc");

        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");

    }

    public void downloadFile(View view) throws IOException {
        //Git working
        //Testings
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading File...");
        progressDialog.show();
        File localFile = File.createTempFile("images", "jpg");
        mStorageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        ////URLLLLLLL



                        download(PDFReadings.this,"test",".pdf",DIRECTORY_DOWNLOADS,"https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1571807064110?alt=media&token=ce0f337a-372c-447d-b301-86e52f8e716d");


                        ///////enddd


                        progressDialog.hide();
                        Toast.makeText(PDFReadings.this, "Successfully downloaded", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });

        /*storageReference = firebaseStorage.getReference();
        ref = storageReference.child("mobile.pdf");

        ref.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        download(PDFReadings.this,"mobile",".pdf",DIRECTORY_DOWNLOADS,"https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1571803919606?alt=media&token=9178190d-9a8d-46e7-8be3-92f308787d45");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/

    }

    private void download(Context context,String fileName,String fileExtendion,String desctinationDirectory,String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,desctinationDirectory,fileName+fileExtendion);

        downloadManager.enqueue(request);
    }
}
