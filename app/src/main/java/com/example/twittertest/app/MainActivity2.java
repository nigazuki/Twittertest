package com.example.twittertest.app;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainActivity2 extends FragmentActivity {

        private EditText mInputText;
        private Twitter mTwitter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);

            mTwitter = TwitterUtils.getTwitterInstance(this);

            mInputText = (EditText) findViewById(R.id.input_text);

            findViewById(R.id.tweet_post).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweet();
                }
            });
        }

        private void tweet() {
            AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        mTwitter.updateStatus(params[0]);
                        return true;
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        showToast("ツイートが完了しました！");
                        finish();
                    } else {
                        showToast("ツイートに失敗しました。。。");
                    }
                }
            };
            task.execute(mInputText.getText().toString());
        }

        private void showToast(String text) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }