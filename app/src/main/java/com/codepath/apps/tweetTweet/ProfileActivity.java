package com.codepath.apps.tweetTweet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.codepath.apps.tweetTweet.fragments.UserTimeLineFragment;
import com.codepath.apps.tweetTweet.models.User;
import com.codepath.apps.tweetTweet.utils.DynamicHeightImageView;
import com.codepath.apps.tweetTweet.utils.Utilities;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        client = TwitterApplication.getRestClient();
        // Get user info
        String screen_name = getIntent().getStringExtra("screen_name");
        if(getIntent().getBooleanExtra("myAccount", true)) {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#55acee\">" + "@" + user.getScreenName() + "</font>"));
                    populateProfileHeader(user);
                }
            });
        }
        else {
            user = getIntent().getParcelableExtra("user");
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#55acee\">" + "@" + user.getScreenName() + "</font>"));
            populateProfileHeader(user);
        }

        if (savedInstanceState == null) {
            //Create the UserTimeLine fragment
            UserTimeLineFragment userTimeLineFragment = UserTimeLineFragment.newInstance(screen_name);
            //Display user fragment (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimeLineFragment);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvpName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvTweets = (TextView) findViewById(R.id.tvTweets);
        DynamicHeightImageView ivProfileImage = (DynamicHeightImageView) findViewById(R.id.ivpProfileImage);
        tvName.setText(user.getName());
        tvTagline.setText(Html.fromHtml(user.getTagline()));
        if(user.getFollowersCount() <= 1)
            tvFollowers.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(user.getFollowersCount()) + "</font></b><br/>" + getString(R.string.follower)));
        else
            tvFollowers.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(user.getFollowersCount()) + "</font></b><br/>" + getString(R.string.followers)));
        tvFollowing.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(user.getFollowingsCount()) + "</font></b><br/>" + getString(R.string.following)));
        tvTweets.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(user.getTweetsCount()) + "</font></b><br/>" + getString(R.string.tweets)));
        Log.d("ME:: ", user.getProfileImageUrl());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        MenuItem miProfile = menu.findItem(R.id.miProfile);
        miProfile.setVisible(false);
        return true;
    }

    public void onProfileView(MenuItem item) {
        // Launch profile view
        return;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

}
