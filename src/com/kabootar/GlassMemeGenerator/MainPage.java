package com.kabootar.GlassMemeGenerator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import com.google.android.glass.app.Card;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainPage extends Activity {

    private int actionCode = 0;
    private Uri outputFileUri;
    private Uri mCapturedImageURI;
    private static FileObserver fo;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == actionCode && resultCode == Activity.RESULT_OK)
        {
            setContentView(R.layout.main);
            Bundle extras = data.getExtras();

            //find path to file and trigger activity to input voice captions
            if(extras.containsKey("picture_file_path"))
            {
                String picture_file_path = extras.getString("picture_file_path");
                Intent mIntent = new Intent(getBaseContext(), AddCaption.class);
                mIntent.putExtra(getResources().getString(R.string.picture_path), picture_file_path);
                startActivity(mIntent);
                finish();
            }

        }
        else
        {
            //image capture cancelled
            Card card2 = new Card(getApplicationContext());
            card2.setText("No camera");
            setContentView(card2.toView());
            //TO DO: allow for retry from menu
        }
    }
}
