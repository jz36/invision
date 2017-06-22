package com.example.jz36.invisionapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new ParseTask().execute();

    }

    private class ParseTask extends AsyncTask<Void, Void, String> {


        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;
        private String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://urdm.ru/media/img/ob-director.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d(LOG_TAG, strJson);
            JSONObject obDirector = null;

            try {
                obDirector = new JSONObject(strJson);
                JSONObject organization = obDirector.getJSONObject("organization");
                TextView organizationName = (TextView) findViewById(R.id.organizationName);
                organizationName.setText((String) organization.get("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
