package ch.markusko.stalkme;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by markus on 12/30/17.
 */

public class CreateSessionTask extends AsyncTask<Void, Void, CreateSessionResult> {

    public static final String API_URL = "https://stalkme.markusko.ch/api/share";


    @Override
    protected CreateSessionResult doInBackground(Void... voids) {
        URL url = null;
        try {
            url = new URL(API_URL);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                InputStream responseBody = inputStream;
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(responseBodyReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                String share_url = jsonObject.getString("url");
                String id = jsonObject.getString("id");
                String cookie = urlConnection.getHeaderField("Set-Cookie");
                bufferedReader.close();
                urlConnection.disconnect();
                return new CreateSessionResult(share_url, cookie, id);
            }
            throw new IllegalStateException("Response code " + urlConnection.getResponseCode());
        } catch (JSONException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}