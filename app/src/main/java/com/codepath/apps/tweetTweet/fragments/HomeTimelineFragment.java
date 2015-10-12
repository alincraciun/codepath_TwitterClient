package com.codepath.apps.tweetTweet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.tweetTweet.TwitterApplication;
import com.codepath.apps.tweetTweet.TwitterClient;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.utils.Utilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by alinc on 10/6/15.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private String TAG = getClass().getName();
    private TwitterClient client;
    private boolean refresh = true;
    private boolean dbRefresh = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        showProgressBar();
        populateTimeLine();
        hideProgressBar();
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from json
    @Override
    public void populateTimeLine() {
        final boolean refreshData = refresh;
        if(refreshData) { client.setMaxID( 0 ); }
        if(!Utilities.checkNetwork(getContext())) {
            Log.d(TAG, "WiFi not available");
            Toast.makeText(getContext(), "Network Connection not available, only offline messages shown.", Toast.LENGTH_SHORT).show();
            addAll(Tweet.fromDBArray());
        } else {
            client.getHomeTimeLineList(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    if (refreshData) {
                        clearAll();
                        dbRefresh = true;
                    }
                    addAll(Tweet.fromJSONArray(json, dbRefresh));
                    hideProgressBar();
                    client.setMaxID(getaTweets().getItem(getaTweets().getCount() - 1).getId());
                    Log.d("ID:: ", String.valueOf(client.maxID));
                    dbRefresh = false;
                    refresh = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(), Utilities.parseAPIError(errorResponse), Toast.LENGTH_SHORT).show();
                    Log.d("onFailure DEBUG:: ", errorResponse.toString());
                }
            });
        }
    }

    @Override
    public void refreshDataAsync(int page) {
        refresh = true;
        populateTimeLine();
        refresh = false;
    }

    public void pushTweet(Tweet t) {
        addTweet(t);
    }
}
