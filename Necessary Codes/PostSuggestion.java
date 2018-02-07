package com.underconstruction.underconstruction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This activity is invoked when the user wants to report something ans there are some similar posts already residing in the database.
 * So, we show the user a list of the matching items. After seeing the list, the user may decide whether to upload the report or abort the task.
 */

public class PostSuggestion extends AppCompatActivity implements  Utility.UploadDecision{
    //If the user presses this button, the report will be uploaded to the main database
    Button btnUpload;
    // //If the user presses this button, the report will be discarded and not saved.
    Button btnCancel;
    //a custom listview to show all the matching posts
    ListView lvwPostSugg;
    //an arraylist to store all the matching posts
    ArrayList<PostSuggestionItem> suggestionItems = new ArrayList<PostSuggestionItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_suggestion);

        Toast.makeText(getApplicationContext(), "Possible Duplicates", Toast.LENGTH_SHORT).show();

        //instantiating the above mentioned variables
        btnUpload = (Button) findViewById(R.id.btnUploadSuggestionAnyway);
        btnCancel = (Button) findViewById(R.id.btnSkipUpload);
        lvwPostSugg = (ListView) findViewById(R.id.lvwPostSuggestion);


        // setting app behavior after user clicks the upload button
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //The user wants to upload the report. So, the parent activity will be notified accordingly
                Intent returnIntent = getIntent();
                returnIntent.putExtra("uploadDecision", UPLOAD_REPORT);
                if (getParent() == null) {
                    setResult(AppCompatActivity.RESULT_OK, returnIntent);
                }
                else
                    getParent().setResult(AppCompatActivity.RESULT_OK, returnIntent);

                //and then finish this activity
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //THe user wants to discard the report. So notify the parent activity accordingly

                Intent returnIntent = getIntent();
                returnIntent.putExtra("uploadDecision", DONT_UPLOAD_REPORT);
                setResult(RESULT_OK, returnIntent);
                finish();

            }
        });
        ImageView imgPostSug = (ImageView)findViewById(R.id.imgPostSuggestion);
        TextView lblPostSugDetails = (TextView)findViewById(R.id.lblPostSuggestionDetails); //Time + (Category)

        Report newReport = (Report) (getIntent().getSerializableExtra("newReport"));
        Bitmap bMap = BitmapFactory.decodeByteArray(newReport.getImage(), 0, newReport.getImage().length);
        imgPostSug.setImageBitmap(bMap);

        lblPostSugDetails.setText(Utility.CurrentUser.parsePostTime(newReport.getTime()) + " (" + newReport.getCategory() + ")");

        // a jsonObject to hold all the post suggestions
        JSONObject jsonPostSuggestions;
        //variable to hold number of posts
        int N=0;

        try {
            //load the json object from the data received from the parent activity
            jsonPostSuggestions = new JSONObject(getIntent().getStringExtra("jsonPostSuggestions"));

            Log.d("PostSuggestion.java", jsonPostSuggestions.toString());

            //build a json array of posts and get its length
            JSONArray postsJSONArray = jsonPostSuggestions.getJSONArray("posts");
            N=postsJSONArray.length();

            //curIndex will be used to iterate through all the posts
            int curIndex=0;

            //clear the arraylist holding posts
            suggestionItems.clear();

            //now go through all the posts
            while(curIndex<N) {
                //get the current post in json and increment the curIndex to look for the next object
                JSONObject curObj = postsJSONArray.getJSONObject(curIndex++);

                //build a new PostSuggestionItem from this json object
                PostSuggestionItem postSuggestionItem = PostSuggestionItem.createPost(curObj);

                //and add it to the arraylist of posts
                suggestionItems.add(postSuggestionItem);

                Log.d("PostSuggestionArrayList", suggestionItems.toString());


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //the adapter to hold the post suggestions
        PostSuggestionAdapter ps_adapter = new PostSuggestionAdapter();
        lvwPostSugg.setAdapter(ps_adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_suggestion, menu);
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

    /**
     * This class shows all the post suggestions in a listview
     */

    class PostSuggestionAdapter extends ArrayAdapter<PostSuggestionItem>
    {

        public PostSuggestionAdapter() {
            super(getApplicationContext(), R.layout.activity_post_suggestion, suggestionItems);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.suggestion_row, parent, false);

            Log.d("PostSuggestionAdapter", "inside getView");

            //extract number of votes the user has received and set sign appropriately
            int voteCount = Integer.valueOf(suggestionItems.get(position).voteCount);
            if(voteCount>0)
                ((TextView) v.findViewById(R.id.lblSuggestionVoteCount)).setText("+" + voteCount);
            else if(voteCount==0)
                ((TextView) v.findViewById(R.id.lblSuggestionVoteCount)).setText(voteCount + "");
            else
                ((TextView) v.findViewById(R.id.lblSuggestionVoteCount)).setText(voteCount + "");

            //and then fill up all the items in the listview

            ((TextView) v.findViewById(R.id.lblSuggestionDate)).setText(Utility.CurrentUser.parsePostTime(suggestionItems.get(position).date));
            ((TextView) v.findViewById(R.id.lblSuggestionInformalLocation)).setText(suggestionItems.get(position).informalLocation);
            ((TextView) v.findViewById(R.id.lblSuggestionInformalProblemDesc)).setText(suggestionItems.get(position).informalProblemDescription);
            ((TextView) v.findViewById(R.id.lblSuggestionUser)).setText(suggestionItems.get(position).username);
            ((ImageView) v.findViewById(R.id.imgSuggestion)).setImageBitmap(BitmapFactory.decodeByteArray(suggestionItems.get(position).img, 0, suggestionItems.get(position).img.length));

            return v;
        }

    }
}
