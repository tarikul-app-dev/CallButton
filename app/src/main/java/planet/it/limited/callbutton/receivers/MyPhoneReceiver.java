package planet.it.limited.callbutton.receivers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.activities.MainActivity;
import planet.it.limited.callbutton.database.DataHelper;
import planet.it.limited.callbutton.util.OutGoingReceiver;


public class MyPhoneReceiver extends BroadcastReceiver {
    MediaRecorder recorder;

    TelephonyManager telManager;

    boolean recordStarted;

    private Context ctx;

    static boolean status = false;
    Time today;
    String phoneNumber = " ";

    String selected_song_name;
    String voiceFilePath = "";
    String searchByMobNumber = "";

    private Dialog loadingDialog;
    private ITelephony telephonyService;
    // Get the object of SmsManager
    // final SmsManager sms = SmsManager.getDefault();
    public static final String SMS_BUNDLE = "pdus";
    String state = " ";
    String stringDuration = " ";
    DataHelper dataHelper;
    public static boolean isBlackList;
    String duration = " ";
    static long startTime;
    static long endTime;
    private File instanceRecord;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        dataHelper = new DataHelper(ctx);
        dataHelper.open();

        Bundle extras = intent.getExtras();
        phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String action = intent.getAction();

        if(phoneNumber!=null && phoneNumber.length()>0){
            isBlackList = dataHelper.isBlackList(phoneNumber);
        }


        if(isBlackList){
            TelephonyManager telephony = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                Class c = Class.forName(telephony.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                telephonyService = (ITelephony) m.invoke(telephony);
                //telephonyService.silenceRinger();

                telephonyService.endCall();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Log.i("action>>>>>>>>>>>>>>>>>", "" + action);

        today = new Time(Time.getCurrentTimezone());

        today.setToNow();

        if (status == false) {

            try {
                startTime = System.currentTimeMillis();

                recorder = new MediaRecorder();
                recorder.reset();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.setAudioSamplingRate(16000);
                recorder.setAudioChannels(1);
                recorder.setOnErrorListener(errorListener);
                recorder.setOnInfoListener(infoListener);

                String date = today.monthDay + "_" + (today.month + 1) + "_"

                        + today.year;
                File instanceRecordDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "Call_Button");

                if(!instanceRecordDirectory.exists()){
                    instanceRecordDirectory.mkdirs();
                }

                instanceRecord = new File(instanceRecordDirectory.getAbsolutePath() + File.separator + date + "cb_recordings.mp4");
                if(!instanceRecord.exists()){
                    instanceRecord.createNewFile();
                }

              //  recorder.setOutputFile(instanceRecord.getAbsolutePath());




//                String time = today.format("%k_%M_%S");
//
//
//                File file = createDirIfNotExists(date + "_" + time);
//
                voiceFilePath = instanceRecord.getAbsolutePath();

                recorder.setOutputFile(voiceFilePath);

                recorder.prepare();

                recorder.start();

                recordStarted = true;

                status = true;


            } catch (Exception ex) {


                ex.printStackTrace();


            }




        if (extras != null) {

            // OFFHOOK

             state = extras.getString(TelephonyManager.EXTRA_STATE);

            Log.w("DEBUG", "aa" + state);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(phoneNumber!=null){
                    Toast.makeText(ctx, phoneNumber, Toast.LENGTH_SHORT).show();
                    incomingcallrecord(action, context);
                    incoming(phoneNumber);
                    showNotificationBar(phoneNumber);
                }else {
                    final String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    incoming(outgoingNumber);
                    showNotificationBar(outgoingNumber);
                }





            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

               // phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.i("number >>>>>>>>>>>>>>", "" + this.getResultData());
                  incomingcallrecord(action, context);
                  incoming(OutGoingReceiver.outgoingNumber);
                  showNotificationBar(OutGoingReceiver.outgoingNumber);

                if (recordStarted) {
                    endTime =  System.currentTimeMillis();
                    dataHelper.open();
                    dataHelper.updateEndTime(OutGoingReceiver.outgoingNumber,String.valueOf(endTime));
                    //recorder.stop();
                    stopRecording();
//                    recorder.reset();
//
//                    recorder.release();

                    recorder = null;

                    recordStarted = false;

                }
            }

            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(ctx, "Called When App Close", Toast.LENGTH_SHORT).show();


                if (recordStarted) {
                    endTime =  System.currentTimeMillis();
                    dataHelper.open();
                    dataHelper.updateEndTime(OutGoingReceiver.outgoingNumber,String.valueOf(endTime));
//                    recorder.stop();
//
//                    recorder.reset();
//
//                    recorder.release();
                    stopRecording();
                    recorder = null;

                    recordStarted = false;

                }


            }

            else if(state.equals(TelephonyManager.CALL_STATE_OFFHOOK)){
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                incomingcallrecord(action, context);
                incoming(phoneNumber);
                showNotificationBar(phoneNumber);
                //Toast.makeText(ctx, phoneNumber, Toast.LENGTH_SHORT).show();

                if (recordStarted) {
                    endTime =  System.currentTimeMillis();
                    dataHelper.open();
                    dataHelper.updateEndTime(phoneNumber,String.valueOf(endTime));
//                    recorder.stop();
//
//                    recorder.reset();
//
//                    recorder.release();
                    stopRecording();
                    recorder = null;

                    recordStarted = false;

                }
            }


        }


        } else {


            status = false;

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

    private void incoming(String number) {

        Date date = new Date();
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        endTime =  System.currentTimeMillis();
      //  String time = today.format("%k_%M_%S");
       // Toast.makeText(ctx, String.valueOf(endTime), Toast.LENGTH_SHORT).show();
        if(number.length()>0){
            dataHelper.insertUserProfile(number, stringDate,String.valueOf(startTime),String.valueOf(endTime),voiceFilePath);
        }

        dataHelper.close();


    }



    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }


    private void incomingcallrecord(String action, Context context) {

        // TODO Auto-generated method stub

        if (action.equals("android.intent.action.PHONE_STATE")) {

            telManager = (TelephonyManager) context

                    .getSystemService(Context.TELEPHONY_SERVICE);

            telManager.listen(phoneListener,

                    PhoneStateListener.LISTEN_CALL_STATE);

        }



    }



    private final PhoneStateListener phoneListener = new PhoneStateListener() {



        @Override

        public void onCallStateChanged(int state, String incomingNumber) {

            Log.d("calling number", "calling number" + incomingNumber);

            try {

                switch (state) {



                    case TelephonyManager.CALL_STATE_RINGING: {


                        if(incomingNumber==null) {
                            //outgoing call
                            incoming(OutGoingReceiver.outgoingNumber);
                        } else {
                            //incoming call

                            incoming(phoneNumber);
                        }




                        Log.e("CALL_STATE_RINGING", "CALL_STATE_RINGING");



                        break;

                    }

                    case TelephonyManager.CALL_STATE_OFFHOOK: {



                        Log.e("CALL_STATE_OFFHOOK", "CALL_STATE_OFFHOOK");


                        break;

                    }

                    case TelephonyManager.CALL_STATE_IDLE: {



                        Log.e("CALL_STATE_IDLE", "CALL_STATE_IDLE");



                        if (recordStarted) {

                            endTime =  System.currentTimeMillis();
                            dataHelper.open();
                            dataHelper.updateEndTime(phoneNumber,String.valueOf(endTime));

//                            recorder.stop();
//
//                            recorder.reset();
//
//                            recorder.release();
                            stopRecording();
                            recorder = null;

                            recordStarted = false;

                        }



                        break;

                    }

                    default: {

                    }

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }



        }



//        private void encriptCurrentRecordedFile() {
//
//
//
//            SimpleCrypto simpleCrypto = new SimpleCrypto();
//
//
//
//            try {
//
//
//
//                incrept = simpleCrypto.encrypt("abc", getAudioFileFromSdcard());
//
//
//
//                FileOutputStream fos = new FileOutputStream(new File(
//
//                        "/sdcard/PhoneCallRecording/" + selected_song_name
//
//                                + ".3GPP"));
//
//                fos.write(incrept);
//
//                fos.close();
//
//
//
//            } catch (Exception e) {
//
//
//
//                e.printStackTrace();
//
//
//
//            }
//
//
//
//        }



//        private byte[] getAudioFileFromSdcard() throws FileNotFoundException {
//
//
//
//            byte[] inarry = null;
//
//
//
//            try {
//
//
//
//                File sdcard = new File(
//
//                        Environment.getExternalStorageDirectory()+ "/PhoneCallRecording");
//
//
//
//                File file = new File(sdcard, selected_song_name + ".3GPP");
//
//
//
//                FileInputStream fileInputStream = null;
//
//
//
//                byte[] bFile = new byte[(int) file.length()];
//
//
//
//                // convert file into array of bytes
//
//                fileInputStream = new FileInputStream(file);
//
//                fileInputStream.read(bFile);
//
//                fileInputStream.close();
//
//                inarry = bFile;
//
//
//
//            } catch (IOException e) {
//
//                // TODO Auto-generated catch block
//
//                e.printStackTrace();
//
//            }
//
//
//
//            return inarry;
//
//        }

    };

    public  void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch(RuntimeException e) {
                instanceRecord.delete();  //you must delete the outputfile when the recorder stop failed.
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }


    public File createDirIfNotExists(String path) {



        selected_song_name = path;



        File folder = new File(Environment.getExternalStorageDirectory()

                + "/PhoneCallRecording");

        if (!folder.exists()) {



            if (!folder.mkdirs()) {



                Log.e("TravellerLog :: ", "folder is created");



            }

        }



        File file = new File(folder, path + ".mp4");

        try {

            if (!file.exists()) {



                if (file.createNewFile()) {



                    Log.e("TravellerLog :: ", "file is created");

                }

            }



        } catch (IOException e) {



            // TODO Auto-generated catch block

            e.printStackTrace();



        }

        return file;



    }

    public String getVoiceFilePath(){
        return  voiceFilePath;
    }


    public void showNotificationBar(String phoneNumber){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx);
        mBuilder.setSmallIcon(R.drawable.ic_icon);
        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent resultPendingIntent = PendingIntent.getActivity(
                        ctx,
                        0,
                        notificationIntent,
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setContentTitle("Call Button");
        mBuilder.setContentText(phoneNumber);
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(455, mBuilder.build());
    }


}
