package com.codepath.apps.tweetTweet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.tweetTweet.TwitterApplication;
import com.codepath.apps.tweetTweet.TwitterClient;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.utils.Utilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alinc on 10/7/15.
 */
public class SearchListFragment extends TweetsListFragment {

    private String TAG = this.getClass().getName();
    private TwitterClient client;
    private MenuItem miActionProgressItem;
    private ProgressBar v;
    private String searchKey;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        setHasOptionsMenu(true);
        populateTimeLine();
    }

    public static SearchListFragment newInstance() {
        SearchListFragment searchFragment = new SearchListFragment();
        Bundle args = new Bundle();
        //args.putString("q", query);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from json
    @Override
    public void populateTimeLine() {
        if(searchKey != null) {
            populateTimeLine(searchKey);
        }
    }

    public void populateTimeLine(String query) {
        searchKey = query;
        if (!Utilities.checkNetwork(getContext())) {
            Log.d(TAG, "WiFi not available");
            Toast.makeText(getContext(), "Network Connection not available, search not available!", Toast.LENGTH_SHORT).show();
        } else {
            showProgressBar();
            client.getSearchTweets(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    clearAll();
                    try {
                        addAll(Tweet.fromJSONArray(json.getJSONArray("statuses"), false));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressBar();
                    client.maxID = getaTweets().getItem(getaTweets().getCount() - 1).getId();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(), Utilities.parseAPIError(errorResponse), Toast.LENGTH_SHORT).show();
                    Log.d("onFailure DEBUG:: ", errorResponse.toString());
                }
            }, query);
        }
    }

    @Override
    public void refreshDataAsync(int page) {
        populateTimeLine();
    }

}
