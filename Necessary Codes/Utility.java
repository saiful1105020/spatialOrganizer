package com.underconstruction.underconstruction;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shabab on 06/11/2015.
 *
 * Utility class holds essential information that will be needed throughout the entire app.
 */

public class Utility {

    static Context initialContext;      //set in LoginActivity

    static String ip = "http://" + "172.20.31.86" +                //main url
            "/hackThon/UC_Server/index.php/home";                  //root directory
//            "/uc_brac_git/uc_server/index.php/home";
                                                                   //used in the class JSONParser for network connection

    /**
     *
     * @param context The context of the application
     * @return true if device  is online, false otherwise
     */

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



    /**
     * Stores the info of the currently active user
     */

    public static class CurrentUser{

        //The id of the user in string
        private static String userId = "-1";
        //the id of the user in integer
        private static int id=Integer.valueOf(userId);
        //the name of the user
        static String username="";
        //used to check if the provided ip address is ok.Used in debugging
        static boolean ipOK = false;




        public static String getUsername() {
            return username;
        }

        public static void setUsername(String username) {
            CurrentUser.username = username;
        }

        public static String getUserId() {
            return userId;
        }

        public static void setUserId(String userId) {
            CurrentUser.userId = userId;
            CurrentUser.id = Integer.valueOf(userId);
        }


        public static int getId(){return id;}
        public static String getName(){return username;}


        /**
         * Parses the post time in user readable format
         * @param dbString the time in default format
         * @return time in user readable format
         */
        public static String parsePostTime (String dbString) {

            int hr = Integer.parseInt("" + dbString.charAt(11) + dbString.charAt(12));
            String min = "" +  dbString.charAt(14) + dbString.charAt(15);
            String timeOfDay;
            String timeOfUpdate = "";
            boolean isBangla = false;

            if(Settings.get_language(initialContext).equals("bn")) {
                isBangla = true;
                Settings.set_app_language("en", initialContext);
            }

            try
            {
                if(hr>12) {
                    hr = hr%12;
                    timeOfDay = "pm";
                }
                else if(hr == 12) timeOfDay = "pm";
                else timeOfDay = "am";

                if(hr == 0) hr = 12;

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                String today = dateFormat.format(cal.getTime());
                String date = dbString.substring(0, 10);

                if (today.equals(date)) {
                    date = " Today";
                }
                else {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    String yesterday = dateFormat.format((cal.getTime()));
                    Log.d("yesterday", yesterday);
                    if (yesterday.equals(date)) {
                        date = " Yesterday";
                    } else {
                        String monthString = new DateFormatSymbols().getMonths()[Integer.parseInt(date.substring(5,7))-1];
                        Log.d("monthString", monthString);

                        date = " " + monthString.substring(0,3) + " " + date.substring(8,10);
                    }
                }

                timeOfUpdate = hr + ":" + min + timeOfDay + date;
            }
            catch (Exception e)
            {
                timeOfUpdate = dbString;
            }

            if(isBangla == true) {
                Settings.set_app_language("bn", initialContext);
                isBangla = false;
            }

            return timeOfUpdate;
        }

    }

    /**
     * This class will be used to configure settings
     */
    static class Settings
    {
        /**
         * returns the currently active language
         * @param context
         * @return The currently active language
         */
        public static String get_language(Context context)
        {
            String lang;
            SharedPreferences pref = context.getSharedPreferences("LangPref", 0); // 0 - for private mode
            lang = pref.getString("Language", "en");
            return lang;
        }

        /**
         * Sets a new langauge as the app language
         * @param context
         * @param lang the language to set.
         */
        public static void set_language(Context context, String lang)
        {
            SharedPreferences pref = context.getSharedPreferences("LangPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Language", lang);

            editor.commit();
        }

        public static void set_app_language(String lang, Context context)
        {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, null);
        }

    }


    /**
     * Implements dynamic category feature. All the problem categories will be brought back from the main database after
     * login.
     */
    public static class CategoryList
    {

        public CategoryList() {
        }

        //Given a category name, returns its ID
        private static HashMap<String, Integer> categoryMap = new HashMap<String, Integer> ();
        //Given an ID, returns the category name
        private static HashMap<Integer, String> IdMap = new HashMap<Integer, String> ();


        /**
         * Adds a new category name and id. It is used when category list is received from server
         * @param name
         * @param id
         */
        public static void add(String name, int id)
        {
            categoryMap.put(name, id);
            IdMap.put(id, name);
        }



        /**
         *  Used when populating category list dynamically
         * @return an arraylist of the names of category
         */
        public static ArrayList<String> getCategoryList()
        {
            ArrayList<String> temp = new ArrayList<String>();
            Object [] t = categoryMap.keySet().toArray();
            for (int i = 0 ; i< t.length; i++)
                if (!(((String)t[i]).equals("Others")))
                    temp.add((String)t[i]);
            temp.add("Others"); //display Others as the last element in Category list

            Log.d("getCategoryList()", temp.toString());
            return temp;
        }


        /**
         * given a category id, returns its name
         *  //f(id) = CategoryName
         * @param id the id of the category
         * @return the name of the category
         */
        public static String get(int id)
        {
            return IdMap.get(id);
        }



        /**
         *  given a category name, returns its id
         * f(CategoryName) = id
         * @param cat the name of the category
         * @return the id of the category
         */
        public static int get(String cat) {
            return categoryMap.get(cat);
        }


        public void copyCategoryList(CategoryList cat) {
            ArrayList<String> tempCategoryName = new ArrayList<String>();
            tempCategoryName.addAll(cat.getCategoryList());

            ArrayList<Integer> tempCategoryIds = new ArrayList<Integer>();
            tempCategoryIds.addAll(cat.getCategoryIds());

            for(int i=0; i<tempCategoryName.size(); i++) {
                categoryMap.put(tempCategoryName.get(i), tempCategoryIds.get(i));
            }
        }


        public static ArrayList<Integer> getCategoryIds() {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            ArrayList<String> categoryNames = new ArrayList<String>();
            categoryNames.addAll(getCategoryList());
            for (int i = 0 ; i< categoryNames.size(); i++) {
                String categoryName = categoryNames.get(i);
//                if(categoryName.equals("Uncategorized")) {
//                    temp.add(-1);
//                    continue;
//                }
                temp.add(get(categoryName));
            }

            Log.d("getCategoryIds()", temp.toString());

            return temp;
        }

        @Override
        public String toString() {
            ArrayList<String> tempCategoryName = new ArrayList<String>();
            tempCategoryName.addAll(getCategoryList());
            ArrayList<Integer> tempCategoryId = new ArrayList<Integer>();
            tempCategoryId.addAll(getCategoryIds());
            return (tempCategoryName.toString() + "\n" + tempCategoryId.toString());
        }

    }


    /**
     * Used to ease the task of uploading/ not uploading a report based on user input
     */
    public interface UploadDecision{
        int UPLOAD_REPORT = 3;
        int DONT_UPLOAD_REPORT = 4;

    }


}
