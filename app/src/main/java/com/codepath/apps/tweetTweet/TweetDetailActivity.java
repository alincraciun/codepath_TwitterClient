package com.codepath.apps.tweetTweet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.codepath.apps.tweetTweet.R;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.models.User;
import com.codepath.apps.tweetTweet.utils.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

public class TweetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.twitter_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("Tweet");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        DynamicHeightImageView ivProfile = (DynamicHeightImageView) findViewById(R.id.ivdProfileImage);
        DynamicHeightImageView ivMedia = (DynamicHeightImageView) findViewById(R.id.ivMedia);
        TextView tvUserName = (TextView) findViewById(R.id.tvdUserName);
        TextView tvProfileName = (TextView) findViewById(R.id.tvdProfileName);
        TextView tvBody = (TextView) findViewById(R.id.tvdBody);
        TextView tvCreatedDate = (TextView) findViewById(R.id.tvCreatedDate);
        TextView tvRetweets = (TextView) findViewById(R.id.tvdRetweets);
        TextView tvFavorites = (TextView) findViewById(R.id.tvdFavorites);

        Typeface defaultTypeface =  Typeface.createFromAsset(this.getAssets(), "roboto.ttf");
        Typeface robotoBoldTypeface =  Typeface.createFromAsset(this.getAssets(), "roboto_bold.ttf");
        Typeface robotoLightTypeface =  Typeface.createFromAsset(this.getAssets(), "roboto_light.ttf");

        tvUserName.setTypeface(robotoBoldTypeface);
        tvProfileName.setTypeface(robotoLightTypeface);
        tvBody.setTypeface(defaultTypeface);
        tvCreatedDate.setTypeface(robotoLightTypeface);
        tvRetweets.setTypeface(robotoLightTypeface);
        tvFavorites.setTypeface(robotoLightTypeface);

        Intent i = getIntent();
        Tweet tweet = (Tweet) i.getParcelableExtra("Tweet");
        tvUserName.setText(tweet.getUser().getName());
        tvProfileName.setText("@" + tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvCreatedDate.setText(tweet.getCreatedTimeStamp());
        tvRetweets.setText(tweet.getRetweet_count() + " " + getResources().getString(R.string.Retweets));
        tvFavorites.setText(tweet.getFavourites_count() + " " + getResources().getString(R.string.favorites));

        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfile);

        if(tweet.getMedia().getMedia_id() > 0) {
            float heightRatio=(float)tweet.getMedia().getLarge_height() / (float)tweet.getMedia().getLarge_width();
            ivMedia.setHeightRatio(heightRatio);
            Picasso.with(this).load(tweet.getMedia().getMedia_url()).fit().into(ivMedia);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
