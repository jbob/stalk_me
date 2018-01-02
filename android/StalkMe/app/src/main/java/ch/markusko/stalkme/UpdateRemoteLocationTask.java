package ch.markusko.stalkme;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by markus on 12/30/17.
 */

public class UpdateRemoteLocationTask extends AsyncTask<UpdateRemoteLocationRequest, Void, Integer> {
    @Override
    protected Integer doInBackground(UpdateRemoteLocationRequest... updateRemoteLocationRequests) {
        URL url = null;
        UpdateRemoteLocationRequest request = updateRemoteLocationRequests[0];
        try {
            url = new URL(CreateSessionTask.API_URL);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Cookie", request.getCookie());
            String param = "lat=" + request.getLat() + "&lng=" + request.getLng();
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.close();

            Log.i("network", "Update complete with response code: " + urlConnection.getResponseCode());
            urlConnection.disconnect();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return 0;
    }
}
