package com.codepath.apps.tweetTweet.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.query.Select;
import com.codepath.apps.tweetTweet.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.codepath.apps.tweetTweet.models.OfflineTweets.Tweets;

/**
 * Parse the JSON + Store the data, encapsulate state or display logic
 *
 */

public class Tweet implements Parcelable {

    //list out the attributes
    private long id;
    private String body;
    private User user;
    private long uid;
    private Media media;
    private String createdAt;
    private int retweet_count = 0;
    private int favourites_count = 0;
    private String createdTimeStamp;

    public String getBody() {
        return body;
    }

    public Media getMedia() {
        return media;
    }

    public User getUser() {
        return user;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getUid() {
        return uid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }

    public void setCreatedTimeStamp(String createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getCreatedAt() {
        return createdAt;

    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public String getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    //Deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        // Extract the values from the JSON and store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.createdAt = Utilities.timeAgoFromDate(jsonObject.getString("created_at"));
            tweet.createdTimeStamp = jsonObject.getString("created_at");
            tweet.retweet_count = jsonObject.optInt("retweet_count");
            tweet.favourites_count = jsonObject.optInt("favorite_count");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.media = Media.fromJSON(jsonObject.optJSONObject("entities"));
            tweet.id = jsonObject.getLong("id");

            //tweet.mUrl = jsonObject.getString("");
            //  Log.d("FAV::  ", String.valueOf(jsonObject.optInt("favorite_count")));
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return tweet;
    }

    public long getId() {
        return id;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray json, boolean refresh) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(json.getJSONObject(i));
                if(tweet != null) {
                    tweets.add(tweet);
                    if(refresh) {
                        if(i == 0) { truncateTables(); }
                        insertTweet(tweet); }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    private static void truncateTables() {
        Tweets tweetsTable = new Tweets();
        tweetsTable.truncateTable();

    }

    public static void insertTweet(Tweet record) {

        /*Users addUser = new Users();
        addUser.uid = record.user.getUid();
        addUser.name = record.user.getName();
        addUser.screen_name = record.user.getScreenName();
        addUser.profile_image_url = record.user.getProfileImageUrl();
        addUser.save();*/

        Tweets addTweet = new Tweets();
        addTweet.tid = record.id;
        //addTweet.user = addUser;
        addTweet.created_at = record.createdAt;
        addTweet.favourites_count = record.favourites_count;
        addTweet.retweet_count = record.retweet_count;
        addTweet.text = record.body;
        addTweet.uid = record.user.getUid();
        addTweet.userName = record.user.getName();
        addTweet.screen_name = record.user.getScreenName();
        addTweet.profile_image_url = record.user.getProfileImageUrl();
        addTweet.save();
    }

    public static ArrayList<Tweet> fromDBArray() {
        ArrayList<Tweet> tweets = new ArrayList<>();
        List<Tweets> queryResults = new Select().from(Tweets.class)
                .orderBy("tid DESC").limit(20).execute();
        for(int i = 0; i < queryResults.size(); i++) {
            Tweet tweet = new Tweet();
            User user = new User();

            user.name = queryResults.get(i).userName;
            user.profileImageUrl = queryResults.get(i).profile_image_url;
            user.screenName = queryResults.get(i).screen_name;
            user.uid = queryResults.get(i).uid;

            tweet.id = queryResults.get(i).tid;
            tweet.favourites_count = queryResults.get(i).favourites_count;
            tweet.body = queryResults.get(i).text;
            tweet.createdAt = queryResults.get(i).created_at;
            tweet.retweet_count = queryResults.get(i).retweet_count;
            tweet.uid = queryResults.get(i).uid;
            tweet.user = user;

            tweets.add(tweet);
        }



        return tweets;
    }

    public static Tweet newTweetFromMessage(String message) {
        Tweet tweet = new Tweet();
        User user = new User();
        Random randomnLong = new Random();
        SimpleDateFormat current = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        tweet.setBody(message);
        tweet.setCreatedTimeStamp(current.format(new Date()).toString());
        tweet.setFavourites_count(0);
        tweet.setId(randomnLong.nextLong());
        tweet.setRetweet_count(0);
        tweet.setUser(user.currentUser());

        return tweet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.body);
        dest.writeParcelable(this.user, flags);
        dest.writeLong(this.uid);
        dest.writeParcelable(this.media, flags);
        dest.writeString(this.createdAt);
        dest.writeInt(this.retweet_count);
        dest.writeInt(this.favourites_count);
        dest.writeString(this.createdTimeStamp);
    }

    public Tweet() {
    }

    protected Tweet(Parcel in) {
        this.id = in.readLong();
        this.body = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.uid = in.readLong();
        this.media = in.readParcelable(Media.class.getClassLoader());
        this.createdAt = in.readString();
        this.retweet_count = in.readInt();
        this.favourites_count = in.readInt();
        this.createdTimeStamp = in.readString();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
