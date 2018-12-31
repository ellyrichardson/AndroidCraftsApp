package com.example.android.androidcraftsappprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;


// I forgot what WS stands for, but this class serves as an adapter for JSON and Online stuff
// I think it stands for With-Server Adapter
public class WSAdapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    static public class SendAPIRequests extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.e("TAG", params[0]);
            Log.e("TAG", params[1]);
            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                // Sets up connection to the URL (params[0] from .execute in "login")
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();

                // Sets the request method for the URL
                httpURLConnection.setRequestMethod("POST");

                // Tells the URL that I am sending a POST request body
                httpURLConnection.setDoOutput(true);

                // To write primitive Java data types to an output stream in a portable way
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                // Writes out a byte to the underlying output stream of the data posted from .execute function
                wr.writeBytes("postData=" + params[1]);
                // Flushes the postData to the output stream
                wr.flush();
                wr.close();

                // Representing the input stream
                InputStream in = httpURLConnection.getInputStream();

                // Preparing input stream bytes to be decoded to charset
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                StringBuilder dataBuffer = new StringBuilder();

                // Translates input stream bytes to charset
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    // concatenates data characters from input stream
                    dataBuffer.append(current);
                }
                data = dataBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Disconnects socket after using
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            Log.e("TAG", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // expecting a response code fro my server upon receiving the POST data
            Log.e("TAG", result);
        }
    }


}