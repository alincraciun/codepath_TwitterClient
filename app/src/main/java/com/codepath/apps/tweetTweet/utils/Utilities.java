package com.codepath.apps.tweetTweet.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by alinc on 10/1/15.
 */
public class Utilities {

    public static void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String timeAgoFromDate(String timestamp) {
        String relativeTimeSpan = "";
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            Date parsedDate = dateFormat.parse(timestamp);
            Timestamp time = new java.sql.Timestamp(parsedDate.getTime());
            relativeTimeSpan = DateUtils.getRelativeTimeSpanString(time.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        return shortLapseTime(relativeTimeSpan);
    }

    public static String shortLapseTime(String lapseTime) {
        String shortTime = lapseTime.replaceAll("ago", "");
        //shortTime = shortTime.replaceAll("in", "");
        shortTime = shortTime.replaceAll("years", "y");
        shortTime = shortTime.replaceAll("days", "d");
        shortTime = shortTime.replaceAll("hour", "h");
        shortTime = shortTime.replaceAll("minutes", "m");
        shortTime = shortTime.replaceAll("minute", "m");
        shortTime = shortTime.replaceAll("hs", "h");
        shortTime = shortTime.replaceAll("mutes", "m");
        shortTime = shortTime.replaceAll("seconds", "s");
        return shortTime.replaceAll("\\s", "");
    }

    public static Boolean checkNetwork(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        return true; //activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static String parseAPIError(JSONObject error) {
        String errorMessage = null;
        try {
            errorMessage = error.getJSONArray("errors").getJSONObject(0).getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(errorMessage !=null && errorMessage.contains("Rate limit exceeded")) {
            return "Twitter API error: Rate limit exceeded! Try again later...";
        }
        else
            return errorMessage;
    }

    public static String shortDigits(int number) {
        String formattedNumber = String.valueOf(number);
        if(formattedNumber.length() >= 7) {
            formattedNumber = String.format("%.2fM", number / 1000000.0);
        } else if (formattedNumber.length() == 6) {
            formattedNumber = String.format("%.2fK", number / 100000.0);
        } else if (formattedNumber.length() == 5) {
            formattedNumber = String.format("%.2fK", number/ 10000.0);
        } else if (formattedNumber.length() == 4) {
            formattedNumber = String.format("%.2fK", number/ 1000.0);
        }

        return formattedNumber;
    }
}
