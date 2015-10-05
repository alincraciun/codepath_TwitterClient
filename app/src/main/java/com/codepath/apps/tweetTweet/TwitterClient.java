package com.codepath.apps.tweetTweet;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


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

		if (this.maxID > 0) { params.put("max_id", (maxID - 1)); }
        //Execute request
        getClient().get(apiURL, params, handler);

    }

    // COMPOSE TWEET
	public void postMessage(AsyncHttpResponseHandler handler, String message) {
		String apiURL = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", message);

		//Execute request
		getClient().post(apiURL, params, handler);

	}

}