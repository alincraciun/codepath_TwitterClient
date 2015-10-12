package com.codepath.apps.tweetTweet.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alinc on 9/29/15.
 */
public class User implements Parcelable {
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
    private String tagline;
    private int tweetsCount;
    private int followersCount;
    private int followingsCount;
    public static User user;

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }

    // deserialize the user json to User
    public static User fromJSON(JSONObject json) {
        user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
            user.tagline = json.getString("description");
            user.followersCount = json.getInt("followers_count");
            user.followingsCount = json.getInt("friends_count");
            user.tweetsCount = json.getInt("statuses_count");
            
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
        user.setProfileImageUrl("http://pbs.twimg.com/profile_images/652135219315630082/QN2uKzAk_normal.jpg");
        user.setScreenName("uberTweet");
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.tagline);
        dest.writeString(this.profileImageUrl);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.followingsCount);
        dest.writeInt(this.tweetsCount);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.tagline = in.readString();
        this.profileImageUrl = in.readString();
        this.followingsCount = in.readInt();
        this.followersCount = in.readInt();
        this.tweetsCount = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
