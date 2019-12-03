package com.farhad.twitterclone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTweetActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtTweet;
    private ListView viewTweetsListView;
    private Button btnViewTweets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);

        edtTweet = findViewById(R.id.edtSendTweet);

        viewTweetsListView = findViewById(R.id.viewTweet);
        btnViewTweets = findViewById(R.id.btnViewTweet);
        btnViewTweets.setOnClickListener(this);

//        HashMap<String, Integer> numbers = new HashMap<>();
//        numbers.put("Number1", 1);
//        numbers.put("Number2", 2);
//        numbers.get("number1");


    }

    public void sendTweet(View view) {
        ParseObject parseObject = new ParseObject("MyTweet");
        parseObject.put("tweet", edtTweet.getText().toString());
        parseObject.put("user", ParseUser.getCurrentUser().getUsername());
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    FancyToast.makeText(SendTweetActivity.this, "Tweet posted", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    dialog.dismiss();
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(SendTweetActivity.this, tweetList, android.R.layout.simple_list_item_2, new String[]{"tweetUserName", "tweetValue"}, new int[]{android.R.id.text1, android.R.id.text2});
        try{
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("MyTweet");
            parseQuery.whereContainedIn("user", ParseUser.getCurrentUser().getList("fanOf"));
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(objects.size() > 0 && e == null){
                        for(ParseObject tweetObject : objects){
                            HashMap<String, String> userTweet = new HashMap<>();
                            userTweet.put("tweetUserName", tweetObject.getString("user"));
                            userTweet.put("tweetValue", tweetObject.getString("tweet"));
                            tweetList.add(userTweet);
                        }
                        viewTweetsListView.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
