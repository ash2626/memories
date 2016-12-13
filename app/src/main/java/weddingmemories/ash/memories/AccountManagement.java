package weddingmemories.ash.memories;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.util.Base64.URL_SAFE;
import static weddingmemories.ash.memories.R.string.username;

/**
 * Created by joannewebster3 on 02/10/2016.
 */

public class AccountManagement extends AsyncTask<String[][],Void,Void> {

    List mylist;

    public AccountManagement() {

    }

    @Override
    protected  Void doInBackground(String[][]... strings) {

        String[][] passed = strings[0];

        for (int i = 0; i < passed.length; i++) {
            String dataUrl = passed[i][0].toString();
            String dataUrlParameters = passed[i][1].toString();
            String basicAuth = "Basic " + Base64.encodeToString(passed[i][2].toString().getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

            Log.d("MemoriesApp", dataUrl + dataUrlParameters);

            URL url;
            HttpURLConnection connection = null;
            try {
                // Create connection
                url = new URL(dataUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", basicAuth);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                // Send request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(dataUrlParameters);
                wr.flush();
                wr.close();
                // Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                //XMLparser myparse = new XMLparser();
                //mylist =  myparse.parse(is);

                String responseStr = response.toString();
                Log.d("MemoriesApp", responseStr);

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        return null;

    }

}
