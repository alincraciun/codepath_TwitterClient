package com.codepath.apps.tweetTweet.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alinc on 9/29/15.
 */
public class User {
    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    public static User user;

    // deserialize the user json to User
    public static User fromJSON(JSONObject json) {
        user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public User currentUser() {
        User user = new User();
        user.setUid(Long.valueOf("3723831794"));
        user.setName("uberAlin");
        user.setProfileImageUrl("https://abs.twimg.com/sticky/default_profile_images/default_profile_4_normal.png");
        user.setScreenName("uberTweet");

        return user;
    }
}
