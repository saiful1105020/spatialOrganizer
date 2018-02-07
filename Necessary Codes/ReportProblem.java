package com.underconstruction.underconstruction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportProblem extends AppCompatActivity implements Utility.UploadDecision, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Custom listview to show the dynamic category
    ListView list;
    //
    TextView txtCateDesc;

    //variable to open another activity to capture an image
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //variable to open another activity to suggest a user similar post
    static final int REQUEST_POST_SUGGESTION = 2;
    //variable to control the operation of camera
    static int camera = 0;
    //a button to catch the addition of report
    Button btnAddReport;
    //an arraylist to store all the attributes of a location
    private ArrayList<String> locationAtrributes = new ArrayList<String>();
    //a bitmap variable to store the captured image
    Bitmap imageBitmap;
    //a byte array version of the previously mentioned bitmap.
    byte[] imageByteArray;
    //the imageview to show the image capturedd to user
    ImageView mImageView;
    //GoogleApiCilent variable to use google api to get latitude and longitude
    GoogleApiClient mGoogleApiClient;
    //the last location returned form the Google Locations API
    Location mLastLocation;
    //this variable is used to retrieve address(locality, street no) from a location(latitude, longitude)
    private AddressResultReceiver mResultReceiver;
    //an string representation of the address retrieved from Google API
    private String resultOutput;
    //the problem category selected by the user, by default the first category is selected
    private int categorySelected;
    //A TAG for debugging
    private String TAG = getClass().getSimpleName().toString();

    TextView lblMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);


        //camera is opened if it is already not opened, otherwise finish the activity
        if (camera == 0) {
            dispatchTakePictureIntent();
            camera++;
        } else
            finish();

        //all the necessary variables are instantiated. The meaning of the variables are described at the beginning of the class
        mResultReceiver = new AddressResultReceiver(new Handler());
        mImageView = (ImageView) findViewById(R.id.addReportImageImageView);
        btnAddReport = (Button) (findViewById(R.id.addReportNewReportButton));
        list = (ListView) findViewById(R.id.listView);
        txtCateDesc = (TextView) findViewById(R.id.txtCategoryDesc);
        lblMore = (TextView) findViewById(R.id.lblReportMore);
        lblMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rg = (RadioGroup) findViewById(R.id.layoutReportMore);
                rg.setVisibility(View.VISIBLE);
                lblMore.setVisibility(View.GONE);
            }
        });

        //String[] values = new String[]{"Broken Road", "Manhole", "Risky Intersection", "Crime prone area", "Others"};
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, Utility.CategoryList.getCategoryList());
        list.setAdapter(adapt);
        list.setItemChecked(Utility.CategoryList.getCategoryList().size(), true);
        Log.d("Category Selected", categorySelected + "");

        final ArrayList<Integer> getCategoryIds = new ArrayList<Integer>();
        getCategoryIds.addAll(Utility.CategoryList.getCategoryIds());
        list.setItemChecked(Utility.CategoryList.getCategoryList().size() - 1, true);
        list.smoothScrollToPosition(Utility.CategoryList.getCategoryList().size() - 1);
        categorySelected = Utility.CategoryList.get("Others");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //to select the user defined category
                categorySelected = getCategoryIds.get(position);
                Log.d("Category Selected", categorySelected + "");


                //the code inside if-else is not functional now.They may be used in future
                /*
                if (list.getItemAtPosition(position).equals("Others")) {
                    txtCateDesc.setVisibility(View.VISIBLE);
                    txtCateDesc.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtCateDesc, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    txtCateDesc.setVisibility(View.GONE);
                }

                */
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_problem, menu);
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

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //  The task of capturing image has completed successfully
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //extract data and set the bitmap appropriately
            imageBitmap = (Bitmap) extras.get("data");
            //set the imageview to show image to the user
            mImageView.setImageBitmap(imageBitmap);

            //so the image has been captured.And we will open the activity again. And we will close the camera and
            //populate the category list so that the user can choose the appropriate category
            Intent intent = new Intent(this, ReportProblem.class);
            startActivity(intent);
            //and now we can report a problem

            btnAddReport.setEnabled(true);

        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            Intent intent = new Intent(this, ReportProblem.class);
            startActivity(intent);
            //activity opened again

            Toast.makeText(getApplicationContext(), "Please add a photo", Toast.LENGTH_LONG).show();

            btnAddReport.setEnabled(false);

        }
        //THe user was showed some posts from the database which were very similar to the report he posted.
        //He can upload/ discard the report
        else if (requestCode == REQUEST_POST_SUGGESTION && resultCode == RESULT_OK) {
            int chosenOption = data.getIntExtra("uploadDecision", -1);
            Log.d(TAG, chosenOption + "");
            //THe user chose to upload the report
            if (chosenOption == UPLOAD_REPORT) {
                //We will format the report and upload it in the main database
                initiateTaskForPopulatingTheMainDB();
                Log.d(TAG, "upload");
            } else if (chosenOption == DONT_UPLOAD_REPORT) {
                //We will go to the default home activity
                Log.d(TAG, "dont upload");

                goToHomeActivity();
            }
        }
    }

    /**
     * THis method will be called when the user wants to upload the report
     * @param v the view associated with the current context
     */
    public void onUploadNowButtonClick(View v) {
        //We will fetch lat-long of the current location in a background thread.
        btnAddReport.setEnabled(false);

        //converting the bitmap into byte array for easily saving in sqlite
        imageByteArray=convertBitmapIntoByteArray(imageBitmap);

        new FetchLocation().execute();


    }

    /**
     * THis background class will be used to determine the latitude and longitude of the current location
     */
    class FetchLocation extends AsyncTask<Report, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(Report... args) {

            buildGoogleApiClient();
            return null;
        }

        protected void onPostExecute(String a) {
        }
    }

    /**
     * This method will open a connection with the google LocationServices to bring the current location
     */

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //This ia a asynchronous operation. It will call the onConnected method below when the location
        //information return
        mGoogleApiClient.connect();
    }

    /**
     * This method will be called when mGoogleClient.connect() function returns
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        //Extract the last known location form Google LocationServices
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }catch(SecurityException e){
            e.printStackTrace();
        }

        //We have got a null location
        if (mLastLocation == null) {
            //Toast.makeText(this, "Google client has returned null", Toast.LENGTH_LONG).show();
        }//We have got a non-null location
        else if (mLastLocation != null) {
            // Toast.makeText(this,"Google client has returned",Toast.LENGTH_LONG).show();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            //Toast.makeText(this, "Google client has returned not null", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();

            //Now we will send the report to appropriate database
            sendDataToAppropriateDatabase();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void sendDataToAppropriateDatabase() {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
//            Toast.makeText(this,"before starting the intent service",Toast.LENGTH_LONG).show();
            //This post has to be inserted in the main database
            if(Utility.isOnline(getApplicationContext())) {
                //We will give the user a list of very similar posts from the main database
                new PostSuggestionTask().execute();
            }
            //This post will be saved in the internal database.
            else {
                Toast.makeText(this, "Your report will be automatically uploaded when internet is available", Toast.LENGTH_LONG).show();
                formatDataForSavingInTheInternalDB();
                Log.d("Internet Connection", "absent");

            }
        }
        else {
            Toast.makeText(this, "Obtaining location failed. Please try after sometime.", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * Adds the report to the main database
     */
    class AddReportTask extends AsyncTask<String, Void, String> {

        //this object holds the json response form the database
        private JSONObject jsonAddReport;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();

            //building up all the parameters
            for (int i = 0; i < locationAtrributes.size(); i++) {

                String tagAndValueString = locationAtrributes.get(i);

                //time has be handles differently as there are multiple :
                String tag = tagAndValueString.split(":")[0];
                Log.d("timest: ", tagAndValueString);
                String value;
                if (!tag.equals("time"))
                    if (tagAndValueString.split(":").length == 1) {
                        value = "";
                    } else value = tagAndValueString.split(":")[1];
                else {
                    value = tagAndValueString.substring(tagAndValueString.indexOf(":") + 1);
                }

                //some tag names are changes to maintain uniformity with the main database
                if (tag.equals("street_number"))
                    tag = "streetNo";
                else if (tag.equals("sublocality_level_1"))
                    tag = "sublocality";
                params.add(new Pair(tag, value));

                Log.d("string_test", tag + " " + value);
            }
            //the image byte array was encoded in Base64 for efficiency
            String encodedString = Base64.encodeToString(imageByteArray, 0);
            params.add(new Pair("image", encodedString));

            //teh ide of the user
            params.add(new Pair("userId", Utility.CurrentUser.getUserId()));


            // getting JSON string from URL
            jsonAddReport = jParser.makeHttpRequest("/insertPost", "POST", params);

            return null;

        }

        protected void onPostExecute(String a) {


            if (jsonAddReport == null)
                Log.d("report_database", " null");
            else Log.d("report_database", jsonAddReport.toString());


        }
    }


    /**
     * Starts a service for bringing in the address of a location
     */

    protected void startIntentServiceForReverseGeoTagging() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        /*
        This is a asynchronous service.It will call the method onReceieveResult in the class AddressResultReceiver when it completes.
         */
        this.startService(intent);


    }


    /**
     * This class will receive the result returned by the FetchAddressIntentService
     */

    class AddressResultReceiver extends ResultReceiver {


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            resultOutput= resultData.getString(Constants.RESULT_DATA_KEY);
            Log.d("returned to destination","true");

            // Show a toast message if an address was found.
            Log.d("result code",resultCode+"");

            //the address was received successfully
            if (resultCode == Constants.SUCCESS_RESULT) {

                Log.d("address location", resultOutput);
                //format the data and set it to main db
                formatAndSendDataToMainDB();
            }

        }
    }


    /**
     * Formats data to send to the main db
     */

    private void formatAndSendDataToMainDB() {
        Log.d("result_output",resultOutput);

        //The resultOutput has the attributes separated by a ~
        String[] locationPairs=resultOutput.split("~" +
                "");

        //add all the parts of location like localiy,sublocality
        for(int i=0;i<locationPairs.length;i++){
            locationAtrributes.add(locationPairs[i]);
        }


        //adding all the attributes.The meanings are very similar to the formatDataForSavingInTheInternalDB()

        locationAtrributes.add("latitude:"+mLastLocation.getLatitude()+"");
        locationAtrributes.add("longitude:" + mLastLocation.getLongitude());

        locationAtrributes.add("category:"+categorySelected);
        locationAtrributes.add("time:" + getCurrentTimestamp());


        String informalLocation=((EditText)findViewById(R.id.addInformalLocationEditText)).getText().toString();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=((EditText)findViewById(R.id.addInformalDescEditText)).getText().toString();
        locationAtrributes.add("problemDescription:" + informalDescription);



        //now add the report to the main database
        new AddReportTask().execute();

    }


    /**
     * Formats data for saving in the sqlite db
     */
    private void formatDataForSavingInTheInternalDB(){

        locationAtrributes.clear();
        //adding lat,lon attributes from mLocation
        locationAtrributes.add("latitude:"+mLastLocation.getLatitude()+"");
        locationAtrributes.add("longitude:" + mLastLocation.getLongitude());
        //adding the selected category
        locationAtrributes.add("category:"+categorySelected);
        //adding the current time in a suitable format
        locationAtrributes.add("time:" + getCurrentTimestamp());

        //adding informal location and informal description from user
        String informalLocation=((EditText)findViewById(R.id.addInformalLocationEditText)).getText().toString();
        locationAtrributes.add("informalLocation:" + informalLocation);
        String informalDescription=((EditText)findViewById(R.id.addInformalDescEditText)).getText().toString();
        locationAtrributes.add("problemDescription:" + informalDescription);



        //saving the report in the sqlite
        saveTheReportInDatabase(imageByteArray);
    }



    private byte[] convertBitmapIntoByteArray(Bitmap imageBitmap){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        return bArray;

    }

    /**
     *
     * @return the current time in string format
     */
    private String getCurrentTimestamp(){
        boolean isBangla = false;

        if(Utility.Settings.get_language(Utility.initialContext).equals("bn")) {
            isBangla = true;
            Utility.Settings.set_app_language("en", Utility.initialContext);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

        Date date = new Date();


        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Date newDate = cal.getTime();
        String timestamp = dateFormat.format(newDate);
        Log.d("timestamp of report: ", "" + timestamp);

        if(isBangla == true) {
            Utility.Settings.set_app_language("bn", Utility.initialContext);
            //isBangla = false;
        }

        return timestamp;
    }

    /**
     * Saves the report in the sqlite internal db
     * @param imageByteArray the image captured by the user in byte array format
     */
    private void saveTheReportInDatabase(byte[] imageByteArray) {

        DBHelper help=new DBHelper(this);
        Log.d("before new insertion : ",help.getAllRecords().toString());

        //inserting the report in sqlite database
        help.insertRecord(locationAtrributes, imageByteArray);
        help.close();

        Log.d("after new insertion : ", help.getAllRecords().toString());

        //after inserting, go to the hoem activity
        goToHomeActivity();

    }

    /**
     * The class will bring all the similar posts form the main db
     */
    class PostSuggestionTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonPostSuggestion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("lat",mLastLocation.getLatitude()+""));
            params.add(new Pair("lon",mLastLocation.getLongitude() + ""));
            params.add(new Pair("time", getCurrentTimestamp()));
            params.add(new Pair("cat", categorySelected + ""));

           //Getting the response in json
            jsonPostSuggestion = jParser.makeHttpRequest("/getSuggestions", "GET", params);

            return null;
        }


        protected void onPostExecute (String file_url){
            if(jsonPostSuggestion == null) {
                Log.d("OnPostExecute", "jsonPostSuggestion == null");
                return;
            }


            Log.d("PostSuggest", jsonPostSuggestion.toString());

            try {
                //Building a json array for posts
                JSONArray postsJSONArray = jsonPostSuggestion.getJSONArray("posts");


                int N=postsJSONArray.length();

                // No conflict with other posts
                if (N==0) {
                    //so put in in main db
                    initiateTaskForPopulatingTheMainDB();
                    return;
                }

                Report newReport = new Report(Utility.CategoryList.get(categorySelected), imageByteArray, getCurrentTimestamp());


                //start a new intent to show the user a list of similar posts and get his feedback whether to
                //upload the report or not.
                Intent intent = new Intent(getApplicationContext(), PostSuggestion.class);
                intent.putExtra("jsonPostSuggestions", jsonPostSuggestion.toString());
                intent.putExtra("newReport", newReport);

                //the activity created will return a result. Check in onActivityResult
                startActivityForResult(intent, REQUEST_POST_SUGGESTION);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Will initiate the job of inserting the post in main database given that the user has given permission or there is no
     * conflict with the posts already in database
     */


    private void initiateTaskForPopulatingTheMainDB() {
        //first start the task of fetching address in a background thread.And it will take care of the rest
        new StartReverseGeoTaggingTask().execute();
        //return to Homepage
        goToHomeActivity();
    }

    /**
     * Returns to the Main Home Activity of the app
     */
    private void goToHomeActivity() {
        Intent intent = new Intent(this, TabbedHome.class);
        startActivity(intent);
    }


    /**
     * Starts the task of fetching address of a location provided that we have got a valid
     * location with lat,long from google LocationServices.
     */
    class StartReverseGeoTaggingTask extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(Void... args) {

            // ANd we finally begin the task of bringing in address of the location
            startIntentServiceForReverseGeoTagging();
            return null;
        }

        protected void onPostExecute (String a){
        }
    }


}
