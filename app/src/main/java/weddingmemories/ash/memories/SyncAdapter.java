package weddingmemories.ash.memories;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by joannewebster3 on 01/10/2016.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        context = mContext;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        context = mContext;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(mContext, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(mContext, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
        }

        /*
        While the actual implementation of onPerformSync() is specific to your app's data synchronization requirements and server connection protocols, there are a few general tasks your implementation should perform:

    Connecting to a server
        Although you can assume that the network is available when your data transfer starts, the sync adapter framework doesn't automatically connect to a server.
    Downloading and uploading data
        A sync adapter doesn't automate any data transfer tasks. If you want to download data from a server and store it in a content provider, you have to provide the code that requests the data, downloads it, and inserts it in the provider. Similarly, if you want to send data to a server, you have to read it from a file, database, or provider, and send the necessary upload request. You also have to handle network errors that occur while your data transfer is running.
    Handling data conflicts or determining how current the data is
        A sync adapter doesn't automatically handle conflicts between data on the server and data on the device. Also, it doesn't automatically detect if the data on the server is newer than the data on the device, or vice versa. Instead, you have to provide your own algorithms for handling this situation.
    Clean up.
        Always close connections to a server and clean up temp files and caches at the end of your data transfer.

         */


    }
}
