package com.codepath.apps.tweetTweet.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alinc on 10/4/15.
 */
public class Media implements Parcelable {
    public int getLarge_height() {
        return large_height;
    }

    public int getLarge_width() {
        return large_width;
    }

    private String media_url;
    private long media_id;
    public static Media media;
    private int large_height = 0;
    private int large_width = 0;


    // deserialize the user json to User
    public static Media fromJSON(JSONObject json) {
        media = new Media();
        try {
            JSONArray jsonArray = json.optJSONArray("media");

            if(jsonArray != null) {
                JSONObject o = jsonArray.getJSONObject(0);
                media.media_id = o.getLong("id");
                media.media_url = o.getString("media_url");

                media.large_height = o.getJSONObject("sizes").getJSONObject("large").getInt("h");
                media.large_width = o.getJSONObject("sizes").getJSONObject("large").getInt("w");
            }
            else {
                media.media_id = 0;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    public String getMedia_url() {
        return media_url;
    }

    public long getMedia_id() {
        return media_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.media_url);
        dest.writeLong(this.media_id);
        dest.writeInt(this.large_height);
        dest.writeInt(this.large_width);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.media_url = in.readString();
        this.media_id = in.readLong();
        this.large_height = in.readInt();
        this.large_width = in.readInt();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
