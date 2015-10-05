package com.codepath.apps.tweetTweet.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alinc on 10/4/15.
 */
public class Media {

    public String media_url;
    public long media_id;
    public static Media media;


    // deserialize the user json to User
    public static Media fromJSON(JSONObject json) {
        media = new Media();
        try {
            JSONArray jsonArray = json.getJSONArray("media");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject m = jsonArray.getJSONObject(i);
                media.media_id = m.optLong("id");
                media.media_url = m.optString("media_url");
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
