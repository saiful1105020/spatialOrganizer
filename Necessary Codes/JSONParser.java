package com.underconstruction.underconstruction;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Shabab on 12/4/2015.
 *
 * This class can handle both POST/GET request with any number of query parameters
 */

public class JSONParser {

    //an inputstream object to receive the response sent by the server
    static InputStream is = null;
    //a jsonobject which will be sent to the calling method
    static JSONObject jObj = null;
    //the string that will be retured as response to the query
    static String jsonString = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject  makeHttpRequest(String urlParameter, String method,
                                       List<Pair> params) {

        final String BASE_URL = Utility.ip;

        Log.d("base url", BASE_URL);

        URL url;
//        List<Pair> paramaters = new ArrayList<Pair>();
        // Making HTTP request
        try{

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
//            System.setProperty("http.keepAlive", "false");

            if (method == "POST") {
                url = new URL(BASE_URL + urlParameter);
                //Log.d("Test: ", "1");
                urlConnection = (HttpURLConnection) url.openConnection();
                //Log.d("Test: ", "2");
                urlConnection.setRequestMethod(method);
                //Log.d("Test: ", "3");
                urlConnection.setDoOutput(true);

                //Log.d("Test: ", "4");

                if(params != null) {
                    //opens a stream to send the request to the server
                    OutputStream os = urlConnection.getOutputStream();
                    //Log.d("Test: ", "5");
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    //Log.d("Test: ", "6");
                    writer.write(getQuery(params));
                    Log.d("output params: ",getQuery(params));
                    writer.close();
                }
            }
            else if(method == "GET") {
                url = new URL(BASE_URL + urlParameter + "?" + getQuery(params));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setDoInput(true);
                urlConnection.connect();
//                urlConnection.setInstanceFollowRedirects(false);
            }

            //builds a stream to get the response sent by the server
            is = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (is == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            // Log.d("the buffer string: ", buffer.length()+"");
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            //convert the entire response into a string
            jsonString = buffer.toString();
            Log.d("Input Stream: ", jsonString);
            urlConnection.disconnect();

        } catch (IOException e) {
            Log.d("JSONParser", "IOException");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("JSONParser", "Unknown Exception");
        }

        // try to parse the string to a JSON object
        try {

            jObj = new JSONObject(jsonString);
            Log.d("JSONParser", "json object created successfully " + jObj.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            Log.d("JSON Parser inputstream", jsonString);

        }

        if(jObj != null) {
            Utility.CurrentUser.ipOK = true;
        }

        return jObj;

    }

    /**
     * Builds a query string from user given params
     * @param params A list of key-value pair provided by the user
     * @return a query string built from the query params
     * @throws Exception
     */
    private String getQuery(List<Pair> params) throws Exception
    {
        if (params == null)
            return "";

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first.toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second.toString(), "UTF-8"));
        }

        return result.toString();
    }
}
