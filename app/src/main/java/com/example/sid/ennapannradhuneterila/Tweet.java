package com.example.sid.ennapannradhuneterila;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

/**
 * Created by S.I.D on 16/01/18.
 */

public class Tweet extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("kalidas700")
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int len = adapter.getCount();
                if(len!=0)
                {
                    setListAdapter(adapter);
                }
            }
        });
    }

    public void goToHomePage()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

