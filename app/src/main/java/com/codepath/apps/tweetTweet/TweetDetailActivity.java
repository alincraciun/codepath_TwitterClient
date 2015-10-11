package com.codepath.apps.tweetTweet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetTweet.R;
import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.models.User;
import com.codepath.apps.tweetTweet.utils.DynamicHeightImageView;
import com.codepath.apps.tweetTweet.utils.Utilities;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TweetDetailActivity extends AppCompatActivity {

    private static final int COMPOSE_REQUEST = 100;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        client = TwitterApplication.getRestClient();


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
        final TextView tvRetweets = (TextView) findViewById(R.id.tvdRetweets);
        final TextView tvFavorites = (TextView) findViewById(R.id.tvdFavorites);

        final ImageView ivReply = (ImageView) findViewById(R.id.tvdReply);
        final ImageView ivRetweetMsg = (ImageView) findViewById(R.id.tvdRetweetMsg);
        final ImageView ivFavMsg = (ImageView) findViewById(R.id.tvdFavMsg);

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
        final Tweet tweet = (Tweet) i.getParcelableExtra("Tweet");

        tvUserName.setText(tweet.getUser().getName());
        tvProfileName.setText("@" + tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvCreatedDate.setText(tweet.getCreatedTimeStamp());

        if(tweet.getRetweeted().contains("true")) {
            ivRetweetMsg.setImageResource(R.drawable.retweet_blue);
        }
        if(tweet.getFavorited().contains("true")) {
            ivFavMsg.setImageResource(R.drawable.star_yellow);
        }

        tvRetweets.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(tweet.getRetweet_count()) + "</font></b> " + getString(R.string.Retweets)));
        tvFavorites.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(tweet.getFavourites_count()) + "</font></b> " + getString(R.string.favorites)));

        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfile);

        if(tweet.getMedia().getMedia_id() > 0) {
            float heightRatio=(float)tweet.getMedia().getLarge_height() / (float)tweet.getMedia().getLarge_width();
            ivMedia.setHeightRatio(heightRatio);
            Picasso.with(this).load(tweet.getMedia().getMedia_url()).fit().into(ivMedia);
        }

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TweetDetailActivity.this, ComposeMessageActivity.class);
                i.putExtra("tid", tweet.getId());
                i.putExtra("body", "@" + tweet.getUser().getScreenName() + " ");
                i.putExtra("title", tweet.getUser().getName());
                startActivity(i);
            }
        });

        ivRetweetMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TweetDetailActivity.this);
                if(tweet.getRetweeted().contentEquals("false")) {
                    builder.setPositiveButton(R.string.postRetweet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            client.postReTweet(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers,  JSONObject json) {
                                    tweet.setRetweeted("true");
                                    try {
                                        tweet.setRetweetid(Tweet.ridFromJSON(json));
                                        Log.d("RID:: ", String.valueOf(tweet.getRetweetid()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    tweet.setRetweet_count(tweet.getRetweet_count() + 1);
                                    ivRetweetMsg.setImageResource(R.drawable.retweet_blue);
                                    tvRetweets.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(tweet.getRetweet_count()) + "</font></b> " + getString(R.string.Retweets)));
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Toast.makeText(TweetDetailActivity.this, Utilities.parseAPIError(errorResponse), Toast.LENGTH_SHORT).show();
                                    Log.d("onFailure DEBUG:: ", errorResponse.toString());
                                }
                            }, tweet.getId());
                        }
                    });
                } else if(tweet.getRetweeted().contentEquals("true")) {
                    builder.setPositiveButton(R.string.undo_retweet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            client.postDestroyReTweet(new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    tweet.setRetweeted("false");
                                    tweet.setRetweet_count(tweet.getRetweet_count() - 1);
                                    ivRetweetMsg.setImageResource(R.drawable.retweet_default);
                                    tvRetweets.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(tweet.getRetweet_count()) + "</font></b> " + getString(R.string.Retweets)));
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Log.d("DEBUG:: ", error.getMessage());
                                }
                            }, tweet.getRetweetid());

                        }
                    });
                }
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ivFavMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.getFavorited().contentEquals("false")) {
                    client.postCreateFavorite(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.setFavorited("true");
                            tweet.setFavourites_count(tweet.getFavourites_count() + 1);
                            ivFavMsg.setImageResource(R.drawable.star_yellow);
                            tvFavorites.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(tweet.getFavourites_count()) + "</font></b> " + getString(R.string.favorites)));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("DEBUG:: ", error.getMessage());
                        }
                    }, tweet.getId());
                } else if(tweet.getFavorited().contentEquals("true")) {
                    client.postDestroyFavorite(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.setFavorited("false");
                            tweet.setFavourites_count(tweet.getFavourites_count() - 1);
                            ivFavMsg.setImageResource(R.drawable.star_default);
                            tvFavorites.setText(Html.fromHtml("<b><font color=\"#000000\">" + Utilities.shortDigits(tweet.getFavourites_count()) + "</font></b> " + getString(R.string.favorites)));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("DEBUG:: ", error.getMessage());
                        }
                    }, tweet.getId());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        return true;
    }

    public void onProfileView(MenuItem item) {
        // Launch profile view
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("myAccount", true);
        startActivity(i);

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
