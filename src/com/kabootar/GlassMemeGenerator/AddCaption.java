package com.kabootar.GlassMemeGenerator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import com.google.android.glass.app.Card;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abandeali
 * Date: 12/3/13
 * Time: 10:23 PM
 */
public class AddCaption extends Activity {

    private static final int TOP_CAPTION = 0;
    private static final int BOTTOM_CAPTION = 1;

    //Not proud of using static variables to store these strings.
    //In a time crunch and had to resort to this -
    // forgive me, programming gods, for I have sinned!
    private static String picture_path;
    private static String top_caption;
    private static String bottom_caption;
    private static String fileName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up instructions for caption
        Card card2 = new Card(getApplicationContext());
        card2.setText(getResources().getString(R.string.intro_text));
        setContentView(card2.toView());

        //grab file path for image captured
        picture_path = getIntent().getStringExtra(getResources().getString(R.string.picture_path));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate add caption menu from xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_captions, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //treat key event as a trigger for showing menu
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            openOptionsMenu();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(fileName == null)
        {
            menu.findItem(R.id.add_caption).setVisible(true);
            menu.findItem(R.id.show_results).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.add_caption).setVisible(false);
            menu.findItem(R.id.show_results).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // let's add some captions or see results based on state
        switch (item.getItemId()) {
            case R.id.add_caption:
                displaySpeechRecognizer(TOP_CAPTION);
                return true;
            case R.id.show_results:
                publish_meme();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void publish_meme() {
        File extStore = Environment.getExternalStorageDirectory();
        fileName = extStore.getPath()+"/DCIM/Camera/"+fileName;
        Log.i("kabootar", "publishing meme file: "+fileName);
        FileObserver fo = new FileObserver(fileName) {
            @Override
            public void onEvent(int i, String s) {
                Log.i("kabootar", "file: "+ fileName +" status"+i);
                //event triggered when file has been written to
                if((i == FileObserver.CLOSE_WRITE) || (i == FileObserver.CLOSE_NOWRITE))
                {
                    Log.i("kabootar", "uploading");
                    ImageOverlay.uploadToImgur(fileName, getApplicationContext());
                    this.stopWatching();
                }
                return;
            }
        }; fo.startWatching();
    }

    private void displaySpeechRecognizer(int request_id) {

        //This is so much awesomeness!
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        String promptString = "";
        switch(request_id){
            case TOP_CAPTION:
                promptString = getResources().getString(R.id.top_caption_prompt);
                break;
            case BOTTOM_CAPTION:
                promptString = getResources().getString(R.id.bottom_caption_prompt);
                break;
            default:
                break;
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promptString);
        startActivityForResult(intent, request_id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode == RESULT_OK)
        {
            if(requestCode == TOP_CAPTION) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                top_caption = spokenText;

                //once top caption is entered,
                //trigger request for bottom caption
                displaySpeechRecognizer(BOTTOM_CAPTION);
            }
            if(requestCode == BOTTOM_CAPTION)
            {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                // Do something with spokenText.
                bottom_caption = spokenText;


                Bitmap workingBitmap = BitmapFactory.decodeFile(picture_path);
                Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap overlaid;
                try {

                    //ALL ABOARD!

                    //use ImageOverlay class to write captions to image as bitmap
                    overlaid = ImageOverlay.overlay(mutableBitmap, top_caption.toUpperCase(), bottom_caption.toUpperCase());

                    //define filename
                    File extStore = Environment.getExternalStorageDirectory();
                    Date now = new Date();
                    DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    fileName = String.format("%s.jpg", sdf.format(now));

                    //save resized bitmap to external storage
                    boolean isImageSaved = ImageOverlay.saveToSD(overlaid, extStore.getPath()+"/DCIM/Camera", fileName);

                    //show preview
                    setContentView(R.layout.main);
                    ImageView img = (ImageView) findViewById(R.id.imageView);
                    img.setImageBitmap(Bitmap.createScaledBitmap(overlaid, 640, 470, true));



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}