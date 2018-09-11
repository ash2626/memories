import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.NetworkUtils;


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

    SharedPreferences prefs;

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


        mClient = new OwnCloudClient(Uri.parse(mServerUri), NetworkUtils.getMultiThreadedConnManager());
        mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(mUser, mPass));
        mClient.setBaseUri(Uri.parse(mServerUri));

        //move to onresume once signup works!!
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //remove once signup works!!
        SharedPreferences.Editor edit1 = prefs.edit();
        edit1.putBoolean(getString(R.string.pref_previously_started), Boolean.FALSE);
        edit1.apply();

        Log.d("MemoriesApp", "onCreate finished, ownCloud setup complete");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.getApplicationContext().getContentResolver().unregisterContentObserver(instPhotoObs);

        Log.d("MemoriesApp", "onDestroy Remove ContentObserver");
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {

            //shared preferences is now set to true meaning this activity will only run on first app use
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();

            //Call Signup Activity
            Intent start_activity = new Intent(getApplicationContext(), Signup.class);
            startActivityForResult(start_activity, 1);
        }

    }

    public void takePictures(View view){
        Intent takepictures = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

        /*fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name*/

        Log.d("MemoriesApp", "Pictures Started");
        // start the image capture Intent
        startActivity(takepictures);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            final String user = data.getStringExtra("userid");
            final String password = data.getStringExtra("password");
            String groupid = data.getStringExtra("groupid");
            String[][] tasks = new String[][]
            {
                    {
                        getString(R.string.server_base_url)+"/ocs/v1.php/cloud/users",
                        "userid="+data.getStringExtra("userid")+"&password="+data.getStringExtra("password"),
                        getString(R.string.username)+":"+getString(R.string.password)
                    },
                    {
                        getString(R.string.server_base_url)+"/ocs/v1.php/cloud/users/"+data.getStringExtra("userid")+"/groups",
                        "groupid="+data.getStringExtra("groupid"),
                        getString(R.string.username)+":"+getString(R.string.password)
                    }

            };

            //Add user to owncloud
            AccountManagement myAccountManagement = new AccountManagement();
            myAccountManagement.execute(tasks);

        }
    }
}