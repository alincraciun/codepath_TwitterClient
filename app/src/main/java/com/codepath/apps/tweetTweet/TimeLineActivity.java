package com.codepath.apps.tweetTweet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.codepath.apps.tweetTweet.listeners.TweetScrollListener;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimeLineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity:: ";
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private static final int COMPOSE_REQUEST = 100;
    private SwipeRefreshLayout swipeContainer;
    private boolean refresh = false;
    private boolean dbRefresh = true;
    private boolean WiFi = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.initialize(this);

        setContentView(R.layout.activity_time_line);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        checkNetwork();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkNetwork();
                if(!WiFi) {
                    Toast.makeText(getApplicationContext(), "Network Connection not available, only offline messages available", Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                }
                else { fetchTimelineAsync(0); }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.twitter_azure,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.twitter_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);

        lvTweets.setOnScrollListener(new TweetScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!WiFi) {
                    return false;
                }

                loadMoreTweets();
                return true;
            }
        });

        client = TwitterApplication.getRestClient();      // singleton
        populateTimeLine();
        setupDetailListener();
    }

    private Boolean checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        WiFi = activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        return WiFi;
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from json
    private void populateTimeLine() {
        if(!WiFi) {
            Log.d(TAG, "WiFi not available");
            Toast.makeText(getApplicationContext(), "Network Connection not available, only offline messages displayed.", Toast.LENGTH_SHORT).show();
            aTweets.addAll(Tweet.fromDBArray());
        } else {
            client.getHomeTimeLineList(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    if (refresh) {
                        aTweets.clear();
                        dbRefresh = true;
                    }
                    aTweets.addAll(Tweet.fromJSONArray(json, dbRefresh));
                    client.maxID = aTweets.getItem(aTweets.getCount() - 1).getId();
                    dbRefresh = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("onFailure DEBUG:: ", errorResponse.toString());
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_compose:
                Intent i = new Intent(this, ComposeMessageActivity.class);
                this.startActivityForResult(i, COMPOSE_REQUEST);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMoreTweets() {
        refresh = false;
        populateTimeLine();
        aTweets.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == COMPOSE_REQUEST) {
            aTweets.clear();
            //populateTimeLine();
            aTweets.add(Tweet.newTweetFromMessage(data.getStringExtra("newMessage")));
            aTweets.notifyDataSetChanged();
        }
    }

    public void fetchTimelineAsync(int page) {
        refresh = true;
        populateTimeLine();
        swipeContainer.setRefreshing(false);
        refresh = false;
    }

    private void setupDetailListener() {
        lvTweets.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                        Intent i = new Intent(TimeLineActivity.this, TweetDetailActivity.class);
                        i.putExtra("pos", pos);
                        i.putExtra("id", id);
                        i.putExtra("tid", tweets.get(pos).getId());
                        i.putExtra("body", tweets.get(pos).getBody());
                        i.putExtra("created_at", tweets.get(pos).getCreatedTimeStamp());
                        i.putExtra("favourites", tweets.get(pos).getFavourites_count());
                        i.putExtra("retweets", String.valueOf(tweets.get(pos).getRetweet_count()));
                        i.putExtra("userName", tweets.get(pos).getUser().getName());
                        i.putExtra("profileImage", tweets.get(pos).getUser().getProfileImageUrl());
                        i.putExtra("screenName", tweets.get(pos).getUser().getScreenName());
                        i.putExtra("media_id", tweets.get(pos).getMedia().getMedia_id());
                        i.putExtra("media_url", tweets.get(pos).getMedia().getMedia_url());
                        i.putExtra("media_height", tweets.get(pos).getMedia().getLarge_height());
                        i.putExtra("media_width", tweets.get(pos).getMedia().getLarge_width());
                        startActivity(i);
                    }
                }
        );

    }
}
