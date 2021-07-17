package com.ash.memories

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ash.memories.MyObserver
import com.google.common.collect.ImmutableList
import memories.R

class MainActivity : AppCompatActivity() {
    private var instPhotoObs: MyObserver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the Photos Library Client that interacts with the API
        val credentialsPath =
            "C:\\Users\\ash\\StudioProjects\\memories\\app\\src\\main\\java\\com\\ash\\memories\\client_id.json"

        //Create photo observer
        instPhotoObs = MyObserver(this.applicationContext)
        Log.d("MemoriesApp", "onCreate Add ContentObserver")
        this.applicationContext.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            false,
            instPhotoObs
        )
        Log.d("MemoriesApp", "onCreate finished")
    }

    override fun onStart() {
        super.onStart()
        val takePictures = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)

        /*fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name*/
        Log.d("MemoriesApp", "Pictures Started")
        // start the image capture Intent
        startActivity(takePictures)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.applicationContext.contentResolver.unregisterContentObserver(instPhotoObs)
        Log.d("MemoriesApp", "onDestroy Remove ContentObserver")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100
        const val MEDIA_TYPE_IMAGE = 1
        const val MEDIA_TYPE_VIDEO = 2
        private val TAG: String? = null

        //PhotosLibrarySettings settings;
        //private static final java.io.File DATA_STORE_DIR = new java.io.File(MainActivity.class.getResource("/").getPath(), "client_id.json");
        private val REQUIRED_SCOPES: List<String> = ImmutableList.of(
            "https://www.googleapis.com/auth/photoslibrary.readonly",
            "https://www.googleapis.com/auth/photoslibrary.appendonly"
        )
    }
}