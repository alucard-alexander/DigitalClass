package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PDFReadings extends AppCompatActivity {

    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private EditText editText;
    private PDFView pdfView;

    private static final int  MEGABYTE = 1024 * 1024;

    private Boolean playing;

    private String url123;


    private TextToSpeech textToSpeech;
    private TextView textView;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreadings);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        editText = findViewById(R.id.editText8);

        db = FirebaseFirestore.getInstance();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        playing = false;

    }




    public void fabClick(View view){
        if (!playing){
            Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();

            try {

                //PdfReader reader = new PdfReader(Uri.parse("https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1574359822709?alt=media&token=2ac31c56-589e-46c3-bc86-10ea3a6b9fb9"));
                /*int n = reader.getNumberOfPages();
                for (int i = 0; i < n;i++){
                    String s = PdfTextExtractor.getTextFromPage(reader,i).trim();
                    textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
                    break;
                }*/

                Toast.makeText(this, pdfView.getCurrentPage(), Toast.LENGTH_SHORT).show();
                //Log.d("errrr", pdfView.conte);


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("errrr", e.getMessage());
            }

            playing = true;


        }else{
            Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
            playing = false;
        }
    }




    public void downloadFile(View view) throws IOException {

        url123 = editText.getText().toString();
        if (url123.isEmpty()) {
            Toast.makeText(this, "Please enter file code inorder to download the file", Toast.LENGTH_SHORT).show();
            return;
        }

        final DocumentReference docRef = db.collection("filesURL").document(url123);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //url123 = document.getString("url");
                        pdfView = (PDFView) findViewById(R.id.pdfView123123);
                        //pdfView.fromUri(uri).load();
                        //pdfView.fromStream(inpu)
                        //pdfView.fromUri()
                        new RetrivePdfStream().execute(document.getString("url"));
                        //download(PDFReadings.this, "test", ".pdf", DIRECTORY_DOWNLOADS, );

                        Toast.makeText(PDFReadings.this, "SuccessFully downloaded", Toast.LENGTH_SHORT).show();
                    } else {
                        //Log.d(TAG, "No such document");
                        Toast.makeText(PDFReadings.this, "Enter Valid file code", Toast.LENGTH_SHORT).show();

                    }
                    progressDialog.hide();
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(PDFReadings.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading File...");
        progressDialog.show();
        /*File localFile = File.createTempFile("file", "pdf");
        mStorageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        ////URLLLLLLL


                        download(PDFReadings.this, "test", ".pdf", DIRECTORY_DOWNLOADS, url123);


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
        });*/

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

    /*private void download(Context context, String fileName, String fileExtendion, String desctinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, desctinationDirectory, fileName + fileExtendion);

        try {
            downloadManager.enqueue(request);

            //pdfView.fromAsset("test.pdf").load();

            downloadManager.openDownloadedFile();

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }*/


    class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL uri = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            pdfView.fromStream(inputStream).load();
        }
    }

}
