package planet.it.limited.callbutton.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.activities.MainActivity;
import planet.it.limited.callbutton.util.AppPreferences;
import planet.it.limited.callbutton.util.CallLog;

/**
 * The nitty gritty Service that handles actually recording the conversations
 */

public class RecordCallService extends Service {

    public final static String ACTION_START_RECORDING = "com.tarikul.ACTION_CLEAN_UP";
    public final static String ACTION_STOP_RECORDING = "com.tarikul.ACTION_STOP_RECORDING";
    public final static String EXTRA_PHONE_CALL = "com.tarikul.EXTRA_PHONE_CALL";
    static public final String PREF_RECORD_CALLS = "PREF_RECORD_CALLS";
    static public final String PREF_AUDIO_SOURCE = "PREF_AUDIO_SOURCE";
    static public final String PREF_AUDIO_FORMAT = "PREF_AUDIO_FORMAT";
    private File recording = null;;
    public RecordCallService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentValues parcelableExtra = intent.getParcelableExtra(EXTRA_PHONE_CALL);
        startRecording(new CallLog(parcelableExtra));
        return START_NOT_STICKY ;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    private CallLog phoneCall;

    boolean isRecording = false;

    private void stopRecording() {

        if (isRecording) {
            try {
                phoneCall.setEndTime(Calendar.getInstance());
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                phoneCall.save(getBaseContext());
                displayNotification(phoneCall);


            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(this,
                        "Not Recording",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
        phoneCall = null;
    }


    MediaRecorder mediaRecorder;


    private void startRecording(CallLog phoneCall) {
        if (!isRecording) {
            isRecording = true;
            this.phoneCall = phoneCall;
            //File file = null;
            try {
                this.phoneCall.setSartTime(Calendar.getInstance());
//                File dir = AppPreferences.getInstance(getApplicationContext()).getFilesDirectory();
                mediaRecorder = new MediaRecorder();
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//                    file = File.createTempFile("record", ".m4a", dir);
//                }else {
//                    file = File.createTempFile("record", ".3gp", dir);
//                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RecordCallService.this);

                Boolean shouldRecord = prefs.getBoolean(PREF_RECORD_CALLS, false);
//                if (!shouldRecord) {
//                    Log.i("CallRecord", "RecordService::onStartCommand with PREF_RECORD_CALLS false, not recording");
//                    //return START_STICKY;
//                    return;
//                }

                int audiosource = Integer.parseInt(prefs.getString(PREF_AUDIO_SOURCE, "1"));
                int audioformat = Integer.parseInt(prefs.getString(PREF_AUDIO_FORMAT, "1"));

                recording = makeOutputFile(prefs);
                if (recording == null) {
                    recording = null;
                    return; //return 0;
                }
                this.phoneCall.setPathToRecording(recording.getAbsolutePath());
                mediaRecorder.reset();
                mediaRecorder.setAudioSource(audiosource);
                Log.d("CallRecorder", "set audiosource " + audiosource);
                mediaRecorder.setOutputFormat(audioformat);
                Log.d("CallRecorder", "set output " + audioformat);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//                    mediaRecorder.setAudioEncodingBitRate(48000);
//                } else {
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    mediaRecorder.setAudioEncodingBitRate(64000);
//                }
//                mediaRecorder.setAudioSamplingRate(16000);
                mediaRecorder.setOnErrorListener(errorListener);
                mediaRecorder.setOnInfoListener(infoListener);

                mediaRecorder.setOutputFile(phoneCall.getPathToRecording());

                try {
                    mediaRecorder.prepare();

                    Thread.sleep(2000);

                    mediaRecorder.start();

                    Toast toast = Toast.makeText(RecordCallService.this,
                            "Start Recording",Toast.LENGTH_SHORT);
                    toast.show();

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    isRecording = false;
                    if (recording != null) recording.delete();
                    this.phoneCall = null;
                    isRecording = false;
                    Toast toast = Toast.makeText(this,
                            "Not Recording",
                            Toast.LENGTH_LONG);
                    toast.show();
                }


            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private File makeOutputFile (SharedPreferences prefs)
    {
        File dir = AppPreferences.getInstance(getApplicationContext()).getFilesDirectory();
        // test dir for existence and writeability
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                Log.e("CallRecorder", "RecordService::makeOutputFile unable to create directory " + dir + ": " + e);
                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder was unable to create the directory " + dir + " to store recordings: " + e, Toast.LENGTH_LONG);
                t.show();
                return null;
            }
        } else {
            if (!dir.canWrite()) {
              //  Log.e(TAG, "RecordService::makeOutputFile does not have write permission for directory: " + dir);
                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder does not have write permission for the directory directory " + dir + " to store recordings", Toast.LENGTH_LONG);
                t.show();
                return null;
            }
        }

        // test size

        // create filename based on call data
        //String prefix = "call";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SS");
        String prefix = sdf.format(new Date());

        // add info to file name about what audio channel we were recording
        int audiosource = Integer.parseInt(prefs.getString(PREF_AUDIO_SOURCE, "1"));
        prefix += "-channel" + audiosource + "-";

        // create suffix based on format
        String suffix = "";
        int audioformat = Integer.parseInt(prefs.getString(PREF_AUDIO_FORMAT, "1"));
        switch (audioformat) {
            case MediaRecorder.OutputFormat.THREE_GPP:
                suffix = ".3gpp";
                break;
            case MediaRecorder.OutputFormat.MPEG_4:
                suffix = ".mpg";
                break;
            case MediaRecorder.OutputFormat.RAW_AMR:
                suffix = ".amr";
                break;
        }

        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException e) {
            Log.e("CallRecorder", "RecordService::makeOutputFile unable to create temp file in " + dir + ": " + e);
            Toast t = Toast.makeText(getApplicationContext(), "CallRecorder was unable to create temp file in " + dir + ": " + e, Toast.LENGTH_LONG);
            t.show();
            return null;
        }
    }
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(RecordCallService.this,
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(RecordCallService.this,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };
    public void displayNotification(CallLog phoneCall) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_icon);
        builder.setContentTitle(getApplicationContext().getString(R.string.notification_title));
        builder.setContentText(phoneCall.getPhoneNumber());
        builder.setContentInfo(getApplicationContext().getString(R.string.notification_more_text));
        builder.setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis())); // fake action to force PendingIntent.FLAG_UPDATE_CURRENT
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("RecordingId", phoneCall.getId());

        builder.setContentIntent(PendingIntent.getActivity(this, 0xFeed, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(0xfeed, builder.build());
    }


    public static void sartRecording(Context context, CallLog phoneCall) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_START_RECORDING);
        intent.putExtra(EXTRA_PHONE_CALL, phoneCall.getContent());
        context.startService(intent);
    }


    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_STOP_RECORDING);
        context.stopService(intent);

    }

}
