package com.codepath.apps.tweetTweet.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetTweet.R;
import com.codepath.apps.tweetTweet.TweetDetailActivity;
import com.codepath.apps.tweetTweet.TweetsArrayAdapter;
import com.codepath.apps.tweetTweet.TwitterApplication;
import com.codepath.apps.tweetTweet.TwitterClient;
import com.codepath.apps.tweetTweet.listeners.TweetScrollListener;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.utils.Utilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alinc on 10/6/15.
 */
public abstract class TweetsListFragment extends Fragment {

    private String TAG = getClass().getName();
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private MenuItem miActionProgressItem;
    private ProgressBar v;
    private TwitterClient client;

    private SwipeRefreshLayout swipeContainer;

    // Abstract method to be overridden
    protected abstract void populateTimeLine();
    protected abstract void refreshDataAsync(int i);
    //protected abstract void searchTweets(String q);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.srl);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        lvTweets.setOnScrollListener(new TweetScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!Utilities.checkNetwork(TweetsListFragment.this.getContext())) {
                    return false;
                }
                loadMoreTweets();
                return true;
            }
        });

        setupDetailListener();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Utilities.checkNetwork(TweetsListFragment.this.getContext())) {
                    Toast.makeText(getContext(), "Network Connection not available, only offline messages shown", Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                } else {
                    showProgressBar();
                    refreshDataAsync(0);
                    swipeContainer.setRefreshing(false);

                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.twitter_azure,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getContext(), tweets);
        hideProgressBar();
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void clearAll() {
        aTweets.clear();
    }

    public TweetsArrayAdapter getaTweets() {
        return aTweets;
    }


    public void loadMoreTweets() {
        populateTimeLine();
        aTweets.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.twitter_azure));
        //searchView.setBackgroundColor(getResources().getColor(R.color.twitter_azure));


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                populateTimeLine();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!Utilities.checkNetwork(getContext())) {
                    Log.d(TAG, "WiFi not available");
                    Toast.makeText(getContext(), "Network Connection not available, search not available.", Toast.LENGTH_SHORT).show();
                } else {
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }


    public void setupDetailListener() {
        lvTweets.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                        Intent i = new Intent(getContext(), TweetDetailActivity.class);
                        i.putExtra("pos", pos);
                        i.putExtra("id", id);
                        i.putExtra("Tweet", tweets.get(pos));
                        startActivity(i);
                    }
                }
        );

    }

    public void addTweet(Tweet newTweet) {
        aTweets.add(newTweet);
        aTweets.notifyDataSetChanged();
    }

}
