package com.example.android.androidcraftsappprototype;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

import static com.example.android.androidcraftsappprototype.WSAdapter.SendAPIRequests.MyPREFERENCES;


// I forgot what WS stands for, but this class serves as an adapter for JSON and Online stuff
// I think it stands for With-Server Adapter
public class WSAdapter {
    static public class SendAPIRequests extends AsyncTask<String, String, String> {
        // Add a pre-execute thing

        SharedPreferences ShPreference;
        SharedPreferences.Editor PrefEditor;
        static String MyPREFERENCES = "API Authentication";
        String accessToken = "Access Token";

        private WeakReference<Context> mLoginReference;

        // constructor
        public SendAPIRequests(Context context){
            mLoginReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Log.e("TAG", params[0]);
            Log.e("TAG", params[1]);
            //String data = "";

            StringBuilder result = new StringBuilder();

            HttpURLConnection httpURLConnection = null;
            try {

                // Sets up connection to the URL (params[0] from .execute in "login")
                httpURLConnection = (HttpURLConnection) new URL(params[2]).openConnection();

                // Sets the request method for the URL
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                httpURLConnection.setRequestProperty("Accept","application/json");

                // Tells the URL that I am sending a POST request body
                httpURLConnection.setDoOutput(true);
                // Tells the URL that I want to read the response data
                httpURLConnection.setDoInput(true);

                // JSON object for the REST API
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("client_id", "mYIHBd321Et3sgn7DqB8urnyrMDwzDeIJxd8eCCE");
                jsonParam.put("client_secret", "qkFYdlvikU4kfhSMBoLNsGleS2HNVHcPqaspCDR0Wdrdex5dHyiFHPXctedNjugnoTq8Ayx7D3v1C1pHeqyPh1BjRlBTQiJYSuH6pi9EVeuyjovxacauGVeGdsBOkHI3");
                jsonParam.put("username", params[0]);
                jsonParam.put("password", params[1]);
                jsonParam.put("grant_type", "password");

                Log.i("JSON", jsonParam.toString());

                // To write primitive Java data types to an output stream in a portable way
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                // Writes out a byte to the underlying output stream of the data posted from .execute function
                wr.writeBytes(jsonParam.toString());
                // Flushes the jsonParam to the output stream
                wr.flush();
                wr.close();

                // // Representing the input stream
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                // reading the input stream / response from the url
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Disconnects socket after using
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            Log.e("TAG", result.toString());
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            // expecting a response code fro my server upon receiving the POST data
            Log.e("TAG", result);

            // retrieves the context passed
            Context context = mLoginReference.get();

            ShPreference = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            // edits shared preferences for authentication and authorization
            PrefEditor = ShPreference.edit();

            // to save the Access Token from the API
            try {
                JSONObject pJObject = new JSONObject(result);

                PrefEditor.putString(accessToken, pJObject.getString("access_token"));
                PrefEditor.apply();
                // algorithm for parsing the JSONArray from the Django REST API
                /*for (int i = 0; i < pJObjArray.length(); i++) {
                    // puts the current iterated JSON object from the array to another temporary object
                    JSONObject pJObj_data = pJObjArray.getJSONObject(i);
                    PrefEditor.putString(accessToken, pJObj_data.getString("access_token"));
                    PrefEditor.apply();
                }*/

            } catch (JSONException e) {
                //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                Log.d("Json","Exception = "+e.toString());
            }
        }
    }

    public class SendPostsRequest extends AsyncTask<String, String, String> {
        TextView postsSect;
        // Add a pre-execute thing
        HttpURLConnection urlConnection;

        // gets the activity context
        private WeakReference<Context> mPostReference;
        // to be able to access activity resources
        Activity activity;

        SharedPreferences ShPreference;
        SharedPreferences.Editor PrefEditor;
        String accessToken = "Access Token";

        // constructor
        public SendPostsRequest(Context context, Activity activity){
            mPostReference = new WeakReference<>(context);
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {

            StringBuilder result = new StringBuilder();

            // retrieves the context passed
            Context context = mPostReference.get();

            ShPreference = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            String APIAuthentication = "Bearer " + ShPreference.getString(accessToken, "");

            try {
                // Sets up connection to the URL (params[0] from .execute in "login")
                urlConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty ("Authorization", APIAuthentication);
                urlConnection.connect();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            // expecting a response code fro my server upon receiving the POST data
            Log.e("TAG", result);

            // gets the JSON files stored in the posts details class from Posts Activity
            Posts.PostsDetails postsHelper = new Posts().new PostsDetails();

            // retrieves the context passed
            Context context = mPostReference.get();

            // For posts
            try {
                JSONArray pJObjArray = new JSONArray(result);

                // algorithm for parsing the JSONArray from the Django REST API
                for (int i = 0; i < pJObjArray.length(); i++) {
                    // puts the current iterated JSON object from the array to another temporary object
                    JSONObject pJObj_data = pJObjArray.getJSONObject(i);
                    // inputs necesarry elements to the ListPosts function
                    postsHelper.setPost(pJObj_data.getInt("id"), pJObj_data.getString("post_title"), pJObj_data.getString("post_content"));
                }

            } catch (JSONException e) {
                //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                Log.d("Json","Exception = "+e.toString());
            }

            // checks if context is not null before updating posts page
            if (context != null){
                postsSect = (TextView) activity.findViewById(R.id.PostsSection);


                int lastFrJSONArray = postsHelper.getPostID().size() - 1;

                // outputs the id of the very first post, something to put to the textview
                postsSect.setText("id: " + postsHelper.getPostID().get(lastFrJSONArray - 2) + "\n");
                for (int i = lastFrJSONArray; i >= 0; i--)
                {
                    // appending the titles and contents of the current post
                    postsSect.append("title: " + postsHelper.getPostTitle().get(i) + "\n");
                    postsSect.append("content: " + postsHelper.getPostContent().get(i) + "\n");

                    // if this is the last post, then don't need to append id for the next post.
                    if (i != 0) {
                        postsSect.append("id: " + postsHelper.getPostID().get(i) + "\n");
                    }
                }
            }

        }

    }
}
