package com.kabootar.GlassMemeGenerator;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abandeali
 * Date: 12/1/13
 * Time: 5:02 PM
 */
public class MemeService extends Service {

    private TimelineManager mTimelineManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimelineManager = TimelineManager.from(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getApplicationContext().getResources().getString(R.string.memeBroadcast));

        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Trigger main activity that will redirect user to image capture
        Intent mIntent = new Intent(getBaseContext(), MainPage.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(mIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(context.getResources().getString(R.string.memeBroadcast))){
                //updateview
                Card showMeme = new Card(getApplicationContext());
                String url = intent.getStringExtra("message");
                String fileName = intent.getStringExtra("fileName");
                showMeme.setText(url);
                showMeme.addImage(Uri.fromFile(new File(fileName)));
                mTimelineManager.insert(showMeme);
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
