package planet.it.limited.callbutton.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import planet.it.limited.callbutton.listeners.PhoneListener;
import planet.it.limited.callbutton.services.ScheduledService;
import planet.it.limited.callbutton.util.AppPreferences;


/**
 * Handle the Phone call related BroadcastActions
 * <action android:name="android.intent.action.PHONE_STATE" />
 * <action android:name="android.intent.action.NEW_OUTGOING_CALL" />
 */

public class MyCallReceiver extends BroadcastReceiver {
//    private static final int PERIOD=900000; // 15 minutes
//    private static final int INITIAL_DELAY=5000; // 5 seconds
    private static final int PERIOD=90000; // 15 minutes
    private static final int INITIAL_DELAY=2000; // 5 seconds

    public MyCallReceiver() {
    }

    static TelephonyManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("callbutton", "MyCallReceiver.onReceive ");
        if (intent.getAction() == null) {
            ScheduledService.enqueueWork(context);
        }
        else {
            scheduleAlarms(context);
        }


        if (!AppPreferences.getInstance(context).isRecordingEnabled()) {
            removeListener();
            return;
        }

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            if (!AppPreferences.getInstance(context).isRecordingOutgoingEnabled()) {
                removeListener();
                return;
            }
            PhoneListener.getInstance(context).setOutgoing(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
        } else {
            if (!AppPreferences.getInstance(context).isRecordingIncomingEnabled()) {
                removeListener();
                return;
            }
        }

        // Start Listening to the call....
        if (null == manager) {
            manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (null != manager)
            manager.listen(PhoneListener.getInstance(context), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void removeListener() {
        if (null != manager) {
            if (PhoneListener.hasInstance())
                manager.listen(PhoneListener.getInstance(null), PhoneStateListener.LISTEN_NONE);
        }
    }

    static void scheduleAlarms(Context ctxt) {
        AlarmManager mgr=
                (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(ctxt, MyCallReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(ctxt, 0, i, 0);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INITIAL_DELAY,
                PERIOD, pi);

    }

}


