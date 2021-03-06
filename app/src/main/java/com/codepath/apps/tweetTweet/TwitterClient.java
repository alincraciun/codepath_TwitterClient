package com.codepath.apps.tweetTweet;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Resource URL: https://api.twitter.com/1.1/statuses/home_timeline.json
 *              GET statuses/home_timeline.json
 *              count = 25
 *              since_id = 1 (all tweets sorted by most recent)
 *
 * Consumer Key (API Key):	KBxPOpSYphCegVxqvqEGZ1R6e
 * Consumer Secret (API Secret):	vcxOYx0m8gNjklllUSMLL2UuEUA9NZeDS7nJpqURMpEW8VOKWz
 */

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "KBxPOpSYphCegVxqvqEGZ1R6e";       // Change this
	public static final String REST_CONSUMER_SECRET = "vcxOYx0m8gNjklllUSMLL2UuEUA9NZeDS7nJpqURMpEW8VOKWz"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)
	public long maxID = 0;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void setMaxID(long maxID) {
		this.maxID = maxID;
	}
/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

    public void getHomeTimeLineList(AsyncHttpResponseHandler handler) {
        String apiURL = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);
		Log.d("MAX ID:: ", String.valueOf(maxID));
		if (this.maxID > 0) { params.put("max_id", (maxID - 1)); }
        //Execute request
        getClient().get(apiURL, params, handler);
	}

    // COMPOSE TWEET
	public void postMessage(AsyncHttpResponseHandler handler, String message, long messageId) {
		String apiURL = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", message);
		if(messageId != 0) { params.put("in_reply_to_status_id", messageId); }

		//Execute request
		getClient().post(apiURL, params, handler);

	}

	public void getMentionsTimeLine(AsyncHttpResponseHandler handler) {
		String apiURL = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 50);

		if (this.maxID > 0) { params.put("max_id", (maxID - 1)); }
		//Execute request
		getClient().get(apiURL, params, handler);
	}

	public void getUserTimeLine(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void postCreateFavorite(AsyncHttpResponseHandler handler, long msgID) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", msgID);
		getClient().post(apiUrl, params, handler);
	}

	public void postDestroyFavorite(AsyncHttpResponseHandler handler, long msgID) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", msgID);
		getClient().post(apiUrl, params, handler);
	}

	public void postReTweet(AsyncHttpResponseHandler handler, long msgID) {
		String apiUrl = getApiUrl("statuses/retweet/" + msgID + ".json");
		getClient().post(apiUrl, handler);
	}

	public void postDestroyReTweet(AsyncHttpResponseHandler handler, long msgID) {
		String apiUrl = getApiUrl("statuses/destroy/" + msgID + ".json");
		Log.d("DESTROY:: ", apiUrl);
		getClient().post(apiUrl, handler);
	}

	public void getSearchTweets(AsyncHttpResponseHandler handler, String q) {
		String apiURL = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		try {
			params.put("q", URLEncoder.encode(q, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.put("count", 25);
		params.put("since_id", 1);

		if (this.maxID > 0) { params.put("max_id", (maxID - 1)); }
		//Execute request
		getClient().get(apiURL, params, handler);
	}


	public void getTopTrendLine(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("trends/place.json");
		RequestParams params = new RequestParams();
		params.put("id", 1);
		getClient().get(apiUrl, params, handler);
	}
}