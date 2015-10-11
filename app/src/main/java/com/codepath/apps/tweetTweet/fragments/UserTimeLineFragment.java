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
 * Created by alinc on 10/7/15.
 */
public class UserTimeLineFragment extends TweetsListFragment {

    private String TAG = this.getClass().getName();
    private TwitterClient client;
    private boolean refresh = false;
    private boolean dbRefresh = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeLine();

    }

    public static UserTimeLineFragment newInstance(String screenName) {
        UserTimeLineFragment userFragment = new UserTimeLineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from json
    @Override
    public void populateTimeLine() {
        String screenName = getArguments().getString("screen_name");
        final boolean refreshData = refresh;
        if(refreshData) { client.setMaxID(0); }
        if (!Utilities.checkNetwork(getContext())) {
            Log.d(TAG, "WiFi not available");
            Toast.makeText(getContext(), "Network Connection not available, only offline messages displayed.", Toast.LENGTH_SHORT).show();
            //addAll(Tweet.userTimelineFromDBArray());
        } else {
            showProgressBar();
            client.getUserTimeLine(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    if (refreshData) {
                        clearAll();
                    }
                    addAll(Tweet.fromJSONArray(json, dbRefresh));
                    hideProgressBar();
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
}
