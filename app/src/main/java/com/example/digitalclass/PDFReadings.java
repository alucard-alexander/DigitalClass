package com.example.digitalclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.digitalclass.Constants.ConstantsVariables;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PDFReadings extends AppCompatActivity {

    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private EditText editText;
    private PDFView pdfView = null;

    private InputStream inputStream = null;


    private Boolean playing;


    private String fileName, urlVar;

    private PdfReader pdfReader;


    private List<String> data = new ArrayList<String>();


    private TextToSpeech textToSpeech;


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
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    Toast.makeText(PDFReadings.this, "OK", Toast.LENGTH_SHORT).show();
                    //say
                } else {
                    Toast.makeText(PDFReadings.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }


            }
        });

        playing = false;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.item2: {
                Toast.makeText(this, "Already in the same page", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.item3:{
                Intent i = new Intent(this,PDFReadings.class);
                startActivity(i);
            }
            case R.id.item4:{
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent i = new Intent(this,LoginPage.class);
                startActivity(i);

            }

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();;

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
                            try {
                                if (document.exists()) {
                                    Log.d("dataaaaa", "DocumentSnapshot data: " + document.getString("type"));
                                    //Toast.makeText(LoginPage.this, document.getString("type"), Toast.LENGTH_SHORT).show();
                                    if (document.getString("type").equals("teacher")) {
                                        MenuInflater inflater = getMenuInflater();
                                        inflater.inflate(R.menu.menu, menu);

                                    }
                                    if (document.getString("type").equals("student")) {
                                    /*homePage = new Intent(LoginPage.this,PDFReadings.class);
                                    startActivity(homePage);*/
                                        MenuInflater inflater = getMenuInflater();
                                        inflater.inflate(R.menu.childmenu, menu);
                                    }

                                } else {
                                    Log.d("dattaaa", "Couldn't get data");
                                }
                            }catch (Exception e){
                                Toast.makeText(PDFReadings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("errrrroooooorrrrrr", e.getMessage());
                            }

                        }
                    }
                });

        return true;



    }

    public void fabClick(View view) {
        if (urlVar != null) {
            if (!playing) {
                EditText ed = findViewById(R.id.editText8);
                if (fileName != ed.getText().toString()) {

                    try {
                        textToSpeech.speak("Starting Text to Speech", TextToSpeech.QUEUE_FLUSH, null, null);
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, ConstantsVariables.PERMISSION_STORAGE_CODE);

                        } else {
                            progressDialog = new ProgressDialog(this);
                            progressDialog.setTitle("In order to play audio, file needs to be get downloaded");
                            progressDialog.show();
                            startDownloading(urlVar);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("errrr", e.getMessage());
                    }
                } else {

                }
                playing = true;


            } else {
                textToSpeech.stop();
                Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
                playing = false;
            }
        } else {
            Toast.makeText(this, "Please choose the file", Toast.LENGTH_SHORT).show();

        }
    }

    public void startDownloading(String url) {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/DigiClass", "test.pdf");
        if (file.exists()) {
            file.delete();
        }


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download");
        request.setDescription("Downloading File... to play audio");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, "DigiClass/test.pdf");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                // your code

                try {
                    pdfReader = new PdfReader(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/DigiClass/test.pdf");

                    int pageCount = pdfReader.getNumberOfPages();
                    Log.d("countingggg", String.valueOf(pageCount));
                    /*String strdd = null;
                    strdd = PdfTextExtractor.getTextFromPage(pdfReader,1);*/

                    for (int i = 1; i <= pageCount; i++) {
                        //strdd = PdfTextExtractor.getTextFromPage(pdfReader,i);
                        data.add(PdfTextExtractor.getTextFromPage(pdfReader, i));
                        //break;
                    }
                    //Log.d("data", data.get(0));
                    //Toast.makeText(ctxt, "", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ctxt, pageCount, Toast.LENGTH_SHORT).show();
                    /*for (int i = 0;i<pageCount;i++){
                        data[i] = PdfTextExtractor.getTextFromPage(pdfReader,i).trim();
                    }*/

                    for (int i = 0; i < pageCount; i++) {
                        if (i == 0) {
                            textToSpeech.speak(data.get(i), TextToSpeech.QUEUE_FLUSH, null, null);
                            //break;
                        } else {
                            textToSpeech.speak(data.get(i), TextToSpeech.QUEUE_ADD, null, null);
                        }

                    }

                    progressDialog.hide();
                    //Toast.makeText(this, std, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("errrrr", e.getMessage());
                }

            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //Toast.makeText(this, Environment.getExternalStoragePublicDirectory(
        //       Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/DigiClass/test.pdf", Toast.LENGTH_SHORT).show();
        //registerReceiver(manager, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //manager.getUriForDownloadedFile();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantsVariables.PERMISSION_STORAGE_CODE
                && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //startDownload();
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("In order to play audio, file needs to be get downloaded");
            progressDialog.show();
            startDownloading(urlVar);
        }


    }

    public void downloadFile(View view) throws IOException {

        fileName = editText.getText().toString();
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Please enter file code inorder to download the file", Toast.LENGTH_SHORT).show();
            return;
        }

        final DocumentReference docRef = db.collection("filesURL").document(fileName);
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
                        urlVar = document.getString("url");
                        new RetrivePdfStream().execute(urlVar);
                        //download(PDFReadings.this, "test", ".pdf", DIRECTORY_DOWNLOADS, );

                        //Toast.makeText(PDFReadings.this, "SuccessFully downloaded", Toast.LENGTH_SHORT).show();
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
        progressDialog.setTitle("Fetching File...");
        progressDialog.show();


    }


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
