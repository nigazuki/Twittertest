package com.example.twittertest.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;



public class MainActivity2 extends FragmentActivity {

    private EditText mInputText;
    private Twitter mTwitter;
    private static final int REQUEST_GALLERY = 0;
    private ImageUpload imageUpload;
    private File path = null;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mTwitter = TwitterUtils.getTwitterInstance(this);
        imgView = (ImageView) findViewById(R.id.imageView);
        mInputText = (EditText) findViewById(R.id.input_text);

        findViewById(R.id.tweet_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });
        findViewById(R.id.picture_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPicture();
            }
        });
    }

    private void AddPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            String[] columns = {MediaStore.Images.Media.DATA};
            Cursor c = cr.query(uri, columns, null, null, null);
            c.moveToFirst();
            path = new File(c.getString(0));
            if (!path.exists()) return;
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                Bitmap img = BitmapFactory.decodeStream(in);
                imgView.setImageBitmap(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
            builder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
            builder.setOAuthAccessToken(getString(R.string.twitter_access_token));
            builder.setOAuthAccessTokenSecret(getString(R.string.twitter_access_token_secret));
            // ここでMediaProviderをTWITTERにする
            builder.setMediaProvider("TWITTER");
            twitter4j.conf.Configuration conf = builder.build();
            imageUpload = new ImageUploadFactory(conf)
                    .getInstance();
        }
    }


    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                if (path == null) {
                    try {
                        mTwitter.updateStatus(params[0] + " #にがうり");
                        return true;
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        return false;
                    }
                }else{
                    try {
                        imageUpload.upload(path, params[0] + " #にがうり");
                        path = null;
                        return true;
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result == true) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (result == false) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}