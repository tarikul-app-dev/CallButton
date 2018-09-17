package planet.it.limited.callbutton.util;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.File;
import java.io.IOException;

import planet.it.limited.callbutton.database.DataHelper;


public class OutGoingReceiver extends BroadcastReceiver {
    MediaRecorder recorder;
    TelephonyManager telManager;
    boolean recordStarted;
    private Context ctx;
    Time today;
    String selected_song_name;
    String voiceFilePath = "";
    PhoneStateListener listener;
    String stringDuration = " ";
    private static final String TAG = "CallReceiver";
    public static  String outgoingNumber = " ";
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        try {
              outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //outGoing(outgoingNumber);
//            String msg = "Intercepted outgoing call: " + outgoingNumber;
//            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();



        } catch (SecurityException | NullPointerException e) {
            Log.e(TAG, "onReceive: " + e.getMessage());
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(ctx,
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(ctx,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };




    public String getVoiceFilePath(){
        return  voiceFilePath;
    }




}
