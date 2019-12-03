package com.farhad.twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class TwitterUsers extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayList<String> tUsers;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FancyToast.makeText(getApplicationContext(), "Welcome " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG, FancyToast.INFO, false).show();
        listView = findViewById(R.id.listView);
        tUsers = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, tUsers);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);
        try{
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if(objects.size() > 0 && e == null){
                        for(ParseUser twitterUser : objects){
                            tUsers.add(twitterUser.getUsername());
                        }
                        listView.setAdapter(adapter);
                        for(String twitterUser : tUsers){
                            if(ParseUser.getCurrentUser().getList("fanOf") != null) {
                                if (ParseUser.getCurrentUser().getList("fanOf").contains(twitterUser)) {
                                    listView.setItemChecked(tUsers.indexOf(twitterUser), true);
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }


        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutUserItem:
                ParseUser.logOut();
                finish();
                Intent intent = new Intent(TwitterUsers.this, SignUp.class);
                startActivity(intent);
            case R.id.sendTweetItem:
                Intent intent1 = new Intent(TwitterUsers.this, SendTweetActivity.class);
                startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;
        if(checkedTextView.isChecked()){
            FancyToast.makeText(TwitterUsers.this, tUsers.get(position) + " is now followed", Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
            ParseUser.getCurrentUser().add("fanOf", tUsers.get(position));
        }else {
            FancyToast.makeText(TwitterUsers.this, tUsers.get(position) + " is now unfollowed", Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
            ParseUser.getCurrentUser().getList("fanOf").remove(tUsers.get(position));
            List currentUserFanOfList = ParseUser.getCurrentUser().getList("fanOf");
            ParseUser.getCurrentUser().remove("fanOf");
            ParseUser.getCurrentUser().put("fanOf", currentUserFanOfList);
        }
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    FancyToast.makeText(TwitterUsers.this, "Saved", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

                }
            }
        });
    }
}
