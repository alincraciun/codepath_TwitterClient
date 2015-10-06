package com.codepath.apps.tweetTweet.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alinc on 10/4/15.
 */
public class Media {
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
                //Log.d("My json array: ", media.media_url.toString() + " h: " + media.large_height + " w: " + media.large_width);
            }
            else {
                media.media_id = 0;
                //Log.d("My json array: ", "Empty array");
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
}
