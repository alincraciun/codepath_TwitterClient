package com.codepath.apps.tweetTweet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.tweetTweet.R;
import com.codepath.apps.tweetTweet.TweetDetailActivity;
import com.codepath.apps.tweetTweet.TweetsArrayAdapter;
import com.codepath.apps.tweetTweet.TwitterApplication;
import com.codepath.apps.tweetTweet.listeners.TweetScrollListener;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.utils.Utilities;

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
        lvTweets.setOnScrollListener(new TweetScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!Utilities.checkNetwork(TweetsListFragment.this.getContext())) {
                    Toast.makeText(getContext(), "You are offline!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                loadMoreTweets();
                return true;
            }
        });
        lvTweets.setAdapter(aTweets);
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
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getContext(), tweets);
        hideProgressBar();
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
        Log.d("SIZE:: ", String.valueOf(aTweets.getCount()));
    }

    public void addTweet(Tweet newTweet) {
        aTweets.insert(newTweet, 0);
        aTweets.notifyDataSetChanged();
    }

    public void clearAll() {
        aTweets.clear();
    }

    public TweetsArrayAdapter getaTweets() {
        return aTweets;
    }


    public void loadMoreTweets() {
        Log.d("ID:: ", "LOAD more...");
        populateTimeLine();
        aTweets.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
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

}
