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

import com.itextpdf.kernel.pdf.PdfReader;

import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PDFReadings extends AppCompatActivity {

    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private EditText editText;
    private PDFView pdfView;

    private InputStream inputStream = null;

    private static final int  MEGABYTE = 1024 * 1024;

    private Boolean playing;

    private String url123;


    private TextToSpeech textToSpeech;

    private String line = null;


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
                if (status == TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.US);
                    Toast.makeText(PDFReadings.this, "OK", Toast.LENGTH_SHORT).show();
                    //say
                }else{
                    Toast.makeText(PDFReadings.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }


            }
        });

        playing = false;

    }




    public void fabClick(View view){
        if (!playing){
            //Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();

            try {
                //PdfReader reader = new PdfReader("");
                //PdfReader reader = new PdfReader(Uri.parse("https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1574359822709?alt=media&token=2ac31c56-589e-46c3-bc86-10ea3a6b9fb9"));
                /*int n = reader.getNumberOfPages();
                for (int i = 0; i < n;i++){
                    String s = PdfTextExtractor.getTextFromPage(reader,i).trim();

                    break;
                }*/

                //FileLoa

                textToSpeech.speak("Dilip Kumar M",TextToSpeech.QUEUE_FLUSH,null,null);

                FileLoader.with(this)
                        .load("https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1574359822709?alt=media&token=2ac31c56-589e-46c3-bc86-10ea3a6b9fb9")
                        .asFile(new FileRequestListener<File>() {
                            @Override
                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                File file = response.getBody();
                                try {
                                    PdfReader reader123 = new PdfReader(file);
                                    //String textExtractorString = PdfTextExtractor.getTextFromPage(reader123,1).trim();
                                    //String text123 = PdfTextExtractor.getTextFromPage(1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(FileLoadRequest request, Throwable t) {

                            }
                        });

                /*FileLoader.with(this)
                        .load("https://firebasestorage.googleapis.com/v0/b/digitalclass-4ca87.appspot.com/o/pdf%2F1574359822709?alt=media&token=2ac31c56-589e-46c3-bc86-10ea3a6b9fb9")
                        .fromDirectory("test4",FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(new FileRequestListener<File>() {
                            @Override
                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                File loadFile = response.getBody();
                                /*String str123= loadFile.;
                                Toast.makeText(PDFReadings.this, str123, Toast.LENGTH_SHORT).show();*/

                                /*StringBuffer text = new StringBuffer();

                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(loadFile));
                                    String line;
                                    while((line = br.readLine()) != null){
                                            //textToSpeech.speak(line,TextToSpeech.QUEUE_FLUSH,null,null);
                                        Toast.makeText(PDFReadings.this, line, Toast.LENGTH_SHORT).show();
                                        text.append(line);
                                        text.append("\n");
                                    }
                                    br.close();
                                    Toast.makeText(PDFReadings.this, "Double OK", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(FileLoadRequest request, Throwable t) {

                            }
                        });*/

                //textToSpeech.speak(convertStreamToString(inputStream),TextToSpeech.QUEUE_FLUSH,null,null);

                //Toast.makeText(this, pdfView.getCurrentPage(), Toast.LENGTH_SHORT).show();
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
            //text = convertStreamToString(inputStream);
        }
    }



}
