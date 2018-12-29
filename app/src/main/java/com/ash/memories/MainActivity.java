package com.ash.memories;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import memories.R;


public class MainActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private MyObserver instPhotoObs;
    private static final String TAG = null;
    //PhotosLibrarySettings settings;
    //private static final java.io.File DATA_STORE_DIR = new java.io.File(MainActivity.class.getResource("/").getPath(), "client_id.json");

    private static final List<String> REQUIRED_SCOPES =
            ImmutableList.of(
                    "https://www.googleapis.com/auth/photoslibrary.readonly",
                    "https://www.googleapis.com/auth/photoslibrary.appendonly");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Photos Library Client that interacts with the API

        String credentialsPath = "C:\\Users\\ash\\StudioProjects\\memories\\app\\src\\main\\java\\com\\ash\\memories\\client_id.json";



        try{
            //settings = PhotosLibrarySettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream((new FileInputStream(file))))).build();
            //PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings);
            PhotosLibraryClient client =
                    PhotosLibraryClientFactory.createClient(credentialsPath, REQUIRED_SCOPES);
            Log.d("MemoriesApp", "Photos Library Initialised");
        }catch (IOException e)
        {
            Log.d("MemoriesApp", "IOException PhotosLibraryClient: " + e);
        }catch (GeneralSecurityException e)
        {
            Log.d("MemoriesApp", "GeneralSecurityException PhotosLibraryClient: " + e);
        }

        //Create photo observer
        instPhotoObs = new MyObserver(this.getApplicationContext());
        Log.d("MemoriesApp", "onCreate Add ContentObserver");
        this.getApplicationContext().getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, instPhotoObs);

        Log.d("MemoriesApp", "onCreate finished");
    }

    @Override
    protected void onStart() {
        super.onStart();



        Intent takePictures = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

        /*fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name*/

        Log.d("MemoriesApp", "Pictures Started");
        // start the image capture Intent
        startActivity(takePictures);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.getApplicationContext().getContentResolver().unregisterContentObserver(instPhotoObs);

        Log.d("MemoriesApp", "onDestroy Remove ContentObserver");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}