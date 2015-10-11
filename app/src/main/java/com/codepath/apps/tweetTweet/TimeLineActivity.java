package com.codepath.apps.tweetTweet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.tweetTweet.fragments.HomeTimelineFragment;
import com.codepath.apps.tweetTweet.fragments.MentionsTimeLineFragment;
import com.codepath.apps.tweetTweet.fragments.TweetsListFragment;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.utils.SmartFragmentStatePagerAdapter;

public class TimeLineActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private static final int COMPOSE_REQUEST = 100;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    ViewPager viewPager;
    TweetsListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActiveAndroid.initialize(this);
        setContentView(R.layout.activity_time_line);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.twitter_azure);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.twitter_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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

    public void onProfileView(MenuItem item) {
        // Launch profile view
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("myAccount", true);
        startActivity(i);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == COMPOSE_REQUEST) {
            listFragment = (TweetsListFragment) adapterViewPager.getRegisteredFragment(0);
            listFragment.addTweet(Tweet.newTweetFromMessage(data.getStringExtra("newMessage")));
        }
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {getString(R.string.HomeTab), getString(R.string.MentionsTab)};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimeLineFragment();
            } else
                return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
