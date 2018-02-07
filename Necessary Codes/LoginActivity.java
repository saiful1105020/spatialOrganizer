package com.underconstruction.underconstruction;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to maintain login credential
 *
 */
public class LoginActivity extends Activity {

    String savedEmailId, savedPassword;
    String savedUserId, savedUserName;
    public EditText txtEmail, txtPassword;
    public Button btnLogin;
    public LinearLayout layoutWait;
    public TextView errorText;
    CheckBox chkSave;
    String email, password;
    Context context;
    DBHelper helper;
    boolean isLoggedIn = false;
    Button btnRegistration, btnGmail, btnSeeLogin;
    public static final int REQUEST_LOCATION_SERVICE_BEFORE_DIRECT_LOGIN = 1;
    public static final int REQUEST_LOCATION_SERVICE_AFTER_NEW_LOGIN = 2;
    SharedPreferences pref;
    String gmail_id;
    @Override
    protected void onResume() {
        //Check what language is currently set
        Log.d("Resume", "Language set " + Utility.Settings.get_language(getApplicationContext()));

        //Utility.Settings.set_app_language(Utility.Settings.get_language(getApplicationContext()), getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //==Language Support (en/bn)=====================
        //Locale locale = new Locale("en");
        Utility.Settings.set_app_language(Utility.Settings.get_language(getApplicationContext()), getApplicationContext());
        //=============================================

        setContentView(R.layout.activity_login);
        Utility.initialContext = getApplicationContext();

//        requestGPS();           //ask to enable gps if required

        txtEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtPassword = (EditText) findViewById(R.id.txtLoginPassword);
        errorText = (TextView) findViewById(R.id.lblLoginError);
        chkSave = (CheckBox) findViewById(R.id.chkLoginRemember);
        layoutWait = (LinearLayout) findViewById(R.id.layoutLoginWait);
        final LinearLayout layoutGmail = (LinearLayout) findViewById(R.id.layoutGmail);
        btnRegistration = (Button) findViewById(R.id.btnLoginRegistration);
        btnGmail = (Button) findViewById(R.id.btnGmail);
        btnSeeLogin = (Button) findViewById(R.id.btnSeeLogin);

        //Login using Google plus account

        gmail_id = getGmailId();

//            layoutGmail.setVisibility(View.GONE);
//        Toast.makeText(this, gmail_id, Toast.LENGTH_SHORT).show();
        btnGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gmail_id == null) {
//                    btnGmail.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Permission required or no valid Gmail account found", Toast.LENGTH_LONG).show();
                }
                else {
                    new UpdateCategoryListTask().execute();

                    if (!chkSave.isChecked())
                    {
                        LoginActivity.this.savedEmailId = savedPassword = "";
                    }
                    busy_session(true);
                    GmailLoginTask loginTask = new GmailLoginTask();
                    loginTask.execute();
                }
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(k);
            }
        });

        btnSeeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout)findViewById(R.id.layoutUserPass)).setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                btnSeeLogin.setVisibility(View.GONE);
                layoutGmail.setVisibility(View.GONE);
            }
        });

        TextView lblForgetPassword = (TextView) findViewById(R.id.lblLoginForgot);
        lblForgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Intent k = new Intent(LoginActivity.this, ForgetActivity.class);
                Intent k = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(k);
            }
        });

        context = getApplicationContext();
        helper = new DBHelper(context);             //will be used for communication with SQLite Database

        btnLogin = (Button) findViewById(R.id.btnLoginLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                if (incompleteFields())
                    errorText.setText("Please enter email and password.");
                else
                {
                    new UpdateCategoryListTask().execute();

                    if (!chkSave.isChecked())
                    {
                        LoginActivity.this.savedEmailId = savedPassword = "";
                    }
                    busy_session(true);
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute();
                }

                //Intent k = new Intent(LoginActivity.this, VerificationActivity.class);
                //startActivity(k);
            }
        });

        /**
         * check if the activity has been directed from RegistrationActivity
         */
        String emailFromReg = getIntent().getStringExtra("email");
        String passwordFromReg = getIntent().getStringExtra("password");

        if(emailFromReg != null && passwordFromReg != null) {
            savedEmailId = emailFromReg;
            savedPassword = passwordFromReg;
            chkSave.setChecked(true);

            txtEmail.setText(savedEmailId);         //pre-fill with RegistrationActivity details
            txtPassword.setText(savedPassword);
            return;
        }

        /**
         * check if user is already logged in
         */
        pref = getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
        isLoggedIn = pref.getBoolean("IsLoggedIn", false);
        if(isLoggedIn == true) {
            if(!isLocationEnabled(context)) {
                requestGPS(REQUEST_LOCATION_SERVICE_BEFORE_DIRECT_LOGIN);
                return;
            }
            Log.d("LoginActivity", "User is already logged in");
            Utility.CurrentUser.setUserId(pref.getString("UserID", "-1"));
            Utility.CurrentUser.setUsername(pref.getString("UserName", ""));

            new UpdateCategoryListTask().execute();

            Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
            startActivity(intent);
            finish();
            return;
        }


        /**
         * login normally
         */
        restoreInstance();      //some fields are pre-filled based on user preferences

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(TAG,"returned from intent");

        if(requestCode == REQUEST_LOCATION_SERVICE_BEFORE_DIRECT_LOGIN){
            if(!isLocationEnabled(this)) {
                Toast.makeText(this, "Your Location Service should be turned on", Toast.LENGTH_LONG).show();
            }

            if(isLoggedIn) {
                Utility.CurrentUser.setUserId(pref.getString("UserID", "-1"));
                Utility.CurrentUser.setUsername(pref.getString("UserName", ""));

                new UpdateCategoryListTask().execute();

                Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
                startActivity(intent);
                finish();
            }
        }

        else if(requestCode == REQUEST_LOCATION_SERVICE_AFTER_NEW_LOGIN){

            Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
            startActivity(intent);
            finish();
            if(!isLocationEnabled(this)) {
                Toast.makeText(this, "Your Location Service should be turned on", Toast.LENGTH_LONG).show();

            }
        }
    }



    private boolean incompleteFields()
    {
        if (email.isEmpty() || password.isEmpty())
            return true;
        return false;
    }
    /**
     * Disable Registration button when the app is working
     * @return
     */
    void busy_session(boolean flag)
    {
        if (flag)
        {
            btnLogin.setEnabled(false);
            btnRegistration.setEnabled(false);
            btnGmail.setEnabled(false);
            btnSeeLogin.setEnabled(false);
            layoutWait.setVisibility(View.VISIBLE);
        }
        else
        {
            txtPassword.setText("");
            txtEmail.setText("");
            txtEmail.requestFocus();
            btnSeeLogin.setEnabled(true);
            //((EditText) findViewById(R.id.txtRegistrationPassword)).setText("");
            //((EditText) findViewById(R.id.txtRegistrationConfirmPassword)).setText("");
            //((EditText) findViewById(R.id.txtRegistrationUsername)).setText("");
            btnRegistration.setEnabled(true);
            btnGmail.setEnabled(true);

            btnLogin.setEnabled(true);
            layoutWait.setVisibility(View.GONE);
        }
    }

    class UpdateCategoryListTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonCategoryList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Utility.CategoryList categoryList = new Utility.CategoryList();         //populating from db initially
                categoryList.copyCategoryList(helper.getCategoryList());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            jsonCategoryList = jParser.makeHttpRequest("/getcategorylist", "GET", null);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonCategoryList == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                return;
            }

            Log.d("categories received", jsonCategoryList.toString());
            Utility.CategoryList categoryList = new Utility.CategoryList();

            try {
                JSONArray categories = jsonCategoryList.getJSONArray("catList");
                int n = categories.length();
                int curIndex = 1;                       //Skipping first category i.e. Others
                while(curIndex<n) {
                    JSONObject curObj = categories.getJSONObject(curIndex++);

                    String categoryName = curObj.getString("name");
                    int categoryId = curObj.getInt("categoryId");

                    categoryList.add(categoryName, categoryId);
                }

                categoryList.add("Others", -1);

                helper.insertCategory(categoryList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class LoginTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonSignUp, jsonLocations;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("email",email));
            params.add(new Pair("password", password));
            // getting JSON string from URL
            jsonSignUp = jParser.makeHttpRequest("/login", "GET", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonSignUp == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                errorText.setText("Please check your internet connection");
                Log.d("LoginTask", "Internet problem, email: " + email);
                busy_session(false);
                txtEmail.setText(email);
                txtPassword.setText(password);
                return;
            }
            String userId = new String ("");
            String userName = new String ("");
            String isVerified = new String ("");
            try {
                userId = jsonSignUp.getString("userId");
                userName = jsonSignUp.getString("userName");
                isVerified = jsonSignUp.getString("isVerified");

                //String uid = jsonSignUp.getString("userId");
                //Utility.CurrentUser.setUserId(uid);
            }catch(JSONException e){
                e.printStackTrace();
            }

            if(userId.equals("0")){
                errorText.setText("Incorrect password, please try again.");
                busy_session(false);
                txtEmail.setText(email);
                return;
            }

            /**
             * Merging without verification activity for now
             *
             */

//            else if (isVerified.equals("0"))
//            {
//                Utility.CurrentUser.setUserId(userId);
//                Intent k = new Intent(LoginActivity.this, VerificationActivity.class);
//                startActivity(k);
//                finish();
//
//            }
            else {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                errorText.setText("Logging in...");
                Utility.CurrentUser.setUserId(userId);
          //      Log.d("Logging in", "My ID: " + Utility.CurrentUser.getUserId());
                Utility.CurrentUser.setUsername(userName);
                if (chkSave.isChecked())
                {
                    savedEmailId = email;
                    savedPassword = password;
                    savedUserId = userId;
                    savedUserName = userName;
                }
                saveInstance();
                if(!isLocationEnabled(context)) {
                    requestGPS(REQUEST_LOCATION_SERVICE_AFTER_NEW_LOGIN);
                    return;
                }
                finish();

                /**
                 * Bypassing without linking login user id to home page received
                 *
                 */
                Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
                startActivity(intent);
            }
        }
    }
    class GmailLoginTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonSignUp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("email",gmail_id));
            params.add(new Pair("password",gmail_id + "abc"));          //auto-generate password
            params.add(new Pair("userName", gmail_id.replace("@gmail.com", "")));
            // getting JSON string from URL
            jsonSignUp = jParser.makeHttpRequest("/gmailLogin", "GET", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonSignUp == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                errorText.setText("Please check your internet connection");
                Log.d("GmailLoginTask", "Internet problem, Gmail id: " + gmail_id);
                busy_session(false);
                txtEmail.setText(email);
                txtPassword.setText(password);
                return;
            }
            String userId = new String ("");
            String userName = new String ("");
            //String isVerified = new String ("");
            try {
                userId = jsonSignUp.getString("userId");
                userName = gmail_id.replace("@gmail.com", ""); //jsonSignUp.getString("userName");
                //isVerified = jsonSignUp.getString("isVerified");

                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                errorText.setText("Logging in...");
                Utility.CurrentUser.setUserId(userId);
                //      Log.d("Logging in", "My ID: " + Utility.CurrentUser.getUserId());
                Utility.CurrentUser.setUsername(userName);
                if (chkSave.isChecked())
                {
                    savedEmailId = email;
                    savedPassword = password;
                    savedUserId = userId;
                    savedUserName = userName;
                }
                saveInstance();
                if(!isLocationEnabled(context)) {
                    requestGPS(REQUEST_LOCATION_SERVICE_AFTER_NEW_LOGIN);
                    return;
                }
                finish();

                /**
                 * Bypassing without linking login user id to home page received
                 *
                 */
                Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
                startActivity(intent);
            }catch(JSONException e){
                e.printStackTrace();
            }

//            if(userId.equals("0")){
//                errorText.setText("Incorrect password, please try again.");
//                busy_session(false);
//                txtEmail.setText(email);
//                return;
//                return;
//            }

            /**
             * Merging without verification activity for now
             *
             */

//            else if (isVerified.equals("0"))
//            {
//                Utility.CurrentUser.setUserId(userId);
//                Intent k = new Intent(LoginActivity.this, VerificationActivity.class);
//                startActivity(k);
//                finish();
//
//            }
//            else {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                errorText.setText("Logging in...");
                Utility.CurrentUser.setUserId(userId);
                //      Log.d("Logging in", "My ID: " + Utility.CurrentUser.getUserId());

                Utility.CurrentUser.setUsername(userName);
                if (chkSave.isChecked())
                {
                    savedEmailId = email;
                    savedPassword = password;
                    savedUserId = userId;
                    savedUserName = userName;
                }
                saveInstance();
                if(!isLocationEnabled(context)) {
                    requestGPS(REQUEST_LOCATION_SERVICE_AFTER_NEW_LOGIN);
                    return;
                }
                finish();

                /**
                 * Bypassing without linking login user id to home page received
                 *
                 */
                Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
                startActivity(intent);
  //          }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("Email", savedEmailId);
        savedInstanceState.putString("Password", savedPassword);
        // etc.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    void requestGPS(int requestId)
    {
        Log.d("requestGPS()", "Checking if GPS is enabled");
        Toast.makeText(context, "Please enable GPS Service" , Toast.LENGTH_LONG).show();
        Log.d("LoginActivity", "Location not enabled");
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(gpsOptionsIntent, requestId);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
    void saveInstance()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Save", chkSave.isChecked());

        if (chkSave.isChecked())
        {
            editor.putString("Email", savedEmailId);
            editor.putString("Password", savedPassword);
            editor.putString("UserID", savedUserId);
            editor.putString("UserName", savedUserName);
            editor.putBoolean("IsLoggedIn", true);
        }
        editor.commit();
    }
    void restoreInstance()
    {
        chkSave.setChecked(false);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
        //SharedPreferences.Editor editor = pref.edit();
        if (pref.getBoolean("Save", false))
        {
            chkSave.setChecked(true);
            txtEmail.setText(pref.getString("Email", null));
            txtPassword.setText(pref.getString("Password", null));
        }
    }
    /*
    **Fetch Google plus id
     */
    String getGmailId()
    {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String gmail = null;

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                Log.d("Acc", account.name);
                gmail = account.name;
                break;
            }
        }
        return gmail;
    }
}

