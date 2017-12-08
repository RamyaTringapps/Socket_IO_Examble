package com.socket_io_example;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SocketAsyncTask {

    private ServerTaskListener listener;
    private JSONObject o;

    public SocketAsyncTask(ServerTaskListener listener) {
        this.listener = listener;
        SocketAsync task = new SocketAsync();
        task.execute();
    }

    private class SocketAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            URL url;
            HttpURLConnection urlConnection = null;
            String result = null;
            try {
                url = new URL("http://108.59.82.80:8080/_api/user/create/ramya.k@tringapps.com");

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);

                BufferedReader rd = new BufferedReader(isw);

                StringBuilder content = new StringBuilder();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    content.append(line);
                }

                o = new JSONObject(content.toString());

                Log.i("json value ", o.toString());
                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                listener.onTaskSuccess(o);
            } else {
                listener.onTaskFailure("Error");
            }
        }
    }
}
