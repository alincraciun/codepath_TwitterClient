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


}
