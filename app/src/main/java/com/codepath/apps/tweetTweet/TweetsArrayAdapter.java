package com.codepath.apps.tweetTweet;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.apps.tweetTweet.models.Tweet;
import com.codepath.apps.tweetTweet.utils.DynamicHeightImageView;
import com.codepath.apps.tweetTweet.utils.LinkifiedTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;
import java.util.Random;

/**
 * Created by alinc on 9/29/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private final Random mRandom;
    private Target imageHolder;
    ViewHolder vh;

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
        this.mRandom = new Random();
    }

    static class ViewHolder {
        DynamicHeightImageView ivProfileImage;
        TextView tvUserName;
        LinkifiedTextView tvBody;
        TextView tvProfileName;
        TextView tvLapseTime;
        TextView tvRetweets;
        TextView tvFavorites;
        TextView tvReply;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Get the tweet
        final Tweet tweet = getItem(position);
        // 2. Find or inflate the template
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            vh = new ViewHolder();

            vh.ivProfileImage = (DynamicHeightImageView) convertView.findViewById(R.id.ivProfileImage);
            vh.tvUserName = (TextView) convertView.findViewById(R.id.tvuserName);
            vh.tvBody = (LinkifiedTextView) convertView.findViewById(R.id.tvBody);
            vh.tvReply = (TextView) convertView.findViewById(R.id.tvReply);
            vh.tvProfileName = (TextView) convertView.findViewById(R.id.tvProfileName);
            vh.tvLapseTime = (TextView) convertView.findViewById(R.id.tvLapseTime);
            vh.tvRetweets = (TextView) convertView.findViewById(R.id.tvRetweets);
            vh.tvFavorites = (TextView) convertView.findViewById(R.id.tvFavorites);

            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }
        Typeface defaultTypeface =  Typeface.createFromAsset(getContext().getAssets(), "roboto.ttf");
        Typeface robotoBoldTypeface =  Typeface.createFromAsset(getContext().getAssets(), "roboto_bold.ttf");
        Typeface robotoLightTypeface =  Typeface.createFromAsset(getContext().getAssets(), "roboto_light.ttf");

        vh.tvUserName.setTypeface(robotoBoldTypeface);
        vh.tvProfileName.setTypeface(robotoLightTypeface);
        vh.tvBody.setTypeface(defaultTypeface);
        vh.tvLapseTime.setTypeface(robotoLightTypeface);
        vh.tvRetweets.setTypeface(robotoLightTypeface);
        vh.tvFavorites.setTypeface(robotoLightTypeface);

        String retweets = String.valueOf(tweet.getRetweet_count());

        vh.tvUserName.setText(tweet.getUser().getName());
        vh.tvProfileName.setText("@" + tweet.getUser().getScreenName());
        vh.tvBody.setText(Html.fromHtml(tweet.getBody()));
        vh.tvLapseTime.setText(tweet.getCreatedAt());
        vh.ivProfileImage.setImageResource(android.R.color.transparent);
        vh.tvRetweets.setText(retweets);
        vh.tvFavorites.setText(String.valueOf(tweet.getFavourites_count()));

        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)vh.tvFavorites.getLayoutParams();
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int leftMargin = 45;
        if(retweets.length() == 1) {
            leftMargin = 58;
        }
        else if(retweets.length() == 2) {
                leftMargin = 52;
        }
        lpt.setMargins((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                leftMargin,
                metrics
            ), lpt.topMargin,lpt.rightMargin,lpt.bottomMargin);
        vh.tvFavorites.setLayoutParams(lpt);

        //loadBitmap(tweet.getUser().getProfileImageUrl());
        String biggerImage = tweet.getUser().getProfileImageUrl().replace("_normal.", "_bigger.");
        Picasso.with(getContext()).load(biggerImage).into(vh.ivProfileImage);

        vh.tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), TweetDetailActivity.class);
                i.putExtra("tid", tweet.getId());
                i.putExtra("body", tweet.getBody());
                i.putExtra("created_at", tweet.getCreatedTimeStamp());
                i.putExtra("favourites", String.valueOf(tweet.getFavourites_count()));
                i.putExtra("retweets", String.valueOf(tweet.getRetweet_count()));
                i.putExtra("userName", tweet.getUser().getName());
                i.putExtra("profileImage", tweet.getUser().getProfileImageUrl());
                i.putExtra("screenName", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });

        vh.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ComposeMessageActivity.class);
                i.putExtra("tid", tweet.getId());
                i.putExtra("body", "@" + tweet.getUser().getScreenName() + " ");
                i.putExtra("title", tweet.getUser().getName());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 of the width
    }

    public void loadBitmap(String url) {

        if (imageHolder == null) {
            imageHolder = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    measureBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }

            };
            Picasso.with(getContext()).load(url).into(imageHolder);
        }


    }

    public void measureBitmap(Bitmap b) {
        //b.getHeight();
        float heightRatio=(float) b.getHeight() / (float)b.getWidth();
        vh.ivProfileImage.setHeightRatio(heightRatio);
    }
}
