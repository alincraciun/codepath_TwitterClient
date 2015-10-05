package com.codepath.apps.tweetTweet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetTweet.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class ComposeMessageActivity extends AppCompatActivity {

    EditText etMessageBody;
    TextView tvCharCounter;
    private TwitterClient client;
    int charCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        final EditText etMessageBody = (EditText) findViewById(R.id.etMessageBody);
        if(getIntent().getStringExtra("body") != null) {
            etMessageBody.setText(getIntent().getStringExtra("body"));
        }
        tvCharCounter = (TextView) findViewById(R.id.tvCharCounter);
        final Button btTweet = (Button) findViewById(R.id.btTweet);
        btTweet.setEnabled(false);

        final TextWatcher twCounter = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                charCount = Integer.parseInt(getString(R.string.max_char_tweet)) - s.length();
                if(charCount < 0) { tvCharCounter.setTextColor(getResources().getColor(R.color.red)); }
                else {tvCharCounter.setTextColor(getResources().getColor(R.color.normal_grey));}
                tvCharCounter.setText(String.valueOf(charCount));

                if(charCount > 0) { btTweet.setEnabled(true);}
                else {  btTweet.setEnabled(true); }

            }

            public void afterTextChanged(Editable s) {
            }

        };

        etMessageBody.addTextChangedListener(twCounter);

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessageBody.getText().toString();
                Log.d("MSG::", message);
                client.postMessage(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        Log.d("MSG:: --> ", "Message posted successfully!");
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 403 || error.getMessage().contains("403")) {
                            Toast.makeText(getApplicationContext(), "I already posted this message!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, message);
            }
        });

        client = TwitterApplication.getRestClient();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_drop) {
            if(charCount > 0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishActivity(0);
                    }
                });
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
            else { finish(); }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
