package com.kabootar.GlassMemeGenerator;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

/**
 * Created with IntelliJ IDEA.
 * User: abandeali
 * Date: 12/1/13
 * Time: 5:02 PM
 */
public class MemeService extends Service {
    private LiveCard mLiveCard;
    private TimelineManager mTimelineManager;
    private String LIVE_CARD_ID = "memeCard";

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
        if (mLiveCard == null) {
            mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);

            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.result_layout);
            mLiveCard.setViews(views);
            mLiveCard.setNonSilent(true);

            Intent menuIntent = new Intent(this, LiveCardActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish();
        } else {
            // TODO(alainv): Jump to the LiveCard when API is available.
        }

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
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                        R.layout.result_layout);
                views.setTextViewText(R.id.message, intent.getStringExtra("message"));
                mLiveCard.setViews(views);
            }
        }
    };

    @Override
    public void onDestroy() {

        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
