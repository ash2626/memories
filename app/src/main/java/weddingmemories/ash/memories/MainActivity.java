package weddingmemories.ash.memories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.NetworkUtils;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.security.GeneralSecurityException;


public class MainActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private MyObserver instPhotoObs;
    private static final String TAG = null;
    // This account must exists on the server side
    private String mServerUri;
    private String mUser;
    private String mPass;
    private OwnCloudClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instPhotoObs = new MyObserver(this.getApplicationContext());
        Log.d("MemoriesApp", "onCreate Add ContentObserver");
        this.getApplicationContext().getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, instPhotoObs);


        mServerUri = getString(R.string.server_base_url);
        mUser = getString(R.string.username);
        mPass = getString(R.string.password);

        Protocol pr = Protocol.getProtocol("https");
        if (pr == null || !(pr.getSocketFactory() instanceof SelfSignedConfidentSslSocketFactory)) {
            try {
                ProtocolSocketFactory psf = new SelfSignedConfidentSslSocketFactory();
                Protocol.registerProtocol(
                        "https",
                        new Protocol("https", psf, 443));

            } catch (GeneralSecurityException e) {
                Log.e(TAG, "Self-signed confident SSL context could not be loaded");
            }
        }

        mClient = new OwnCloudClient(Uri.parse(mServerUri), NetworkUtils.getMultiThreadedConnManager());
        mClient.setDefaultTimeouts(
                OwnCloudClientFactory.DEFAULT_DATA_TIMEOUT,
                OwnCloudClientFactory.DEFAULT_CONNECTION_TIMEOUT);
        mClient.setFollowRedirects(true);
        mClient.setCredentials(
                OwnCloudCredentialsFactory.newBasicCredentials(
                        mUser,
                        mPass
                )
        );
        mClient.setBaseUri(Uri.parse(mServerUri));

        Log.d("MemoriesApp", "onCreate finished, ownCloud setup complete");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.getApplicationContext().getContentResolver().unregisterContentObserver(instPhotoObs);

        Log.d("MemoriesApp", "onDestroy Remove ContentObserver");
    }

    public void takePictures(View view){
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

        /*fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name*/

        Log.d("MemoriesApp", "Pictures Started");
        // start the image capture Intent
        startActivity(intent);
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