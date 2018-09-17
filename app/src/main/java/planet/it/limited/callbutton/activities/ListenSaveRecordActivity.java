package planet.it.limited.callbutton.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.adapter.ListenAdapter;
import planet.it.limited.callbutton.adapter.SaveListenAdapter;
import planet.it.limited.callbutton.database.DataHelper;
import planet.it.limited.callbutton.util.ListenModel;

public class ListenSaveRecordActivity extends AppCompatActivity {
    public static final int REQUEST_PERM_CALL = 102;
    String titleList[] = {  "Delete","Share" ,"Call This Number"};
    public Drawable[] drawablesList = new Drawable[3];
    ListView lvListen;
    SaveListenAdapter listenAdapter;
    ArrayList<ListenModel> listenList ;
    Toolbar toolbar;
    TextView txvSeekbarStart,txvSeekbarEnd,txvAudioDuration,txvDateTime,txvToolbarNumber;
    String voiceFilePath = " ";
    String phoneNumber = " ";
    SeekBar audioSeekbar;
    ImageView imgvAudioPlay,imgvPause;
    private MediaPlayer mediaPlayer;
    DataHelper dataHelper;
    Handler seekHandler;
    String selected_song_name;
    Time today;
    String duration = " ";
    public static boolean isDelete ;
    String startTime = " ";
    String endTime = " ";
    String dateTime = " ";

    InterstitialAd interstitial;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_save_record);
        toolbar = (Toolbar)findViewById(R.id.toolbar_listen_save);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        initViews();
        toUseAdmobAdd();

    }

    public void initViews(){

        dataHelper = new DataHelper(ListenSaveRecordActivity.this);
        dataHelper.open();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            voiceFilePath = extras.getString("voice_path");
            phoneNumber = extras.getString("phone_number");
            endTime =  extras.getString("voice_duration");
            dateTime =  extras.getString("date_time");
        } else {
            // handle case
        }

        drawablesList[0] = this.getResources().getDrawable(R.drawable.ic_delete);
        drawablesList[1] = this.getResources().getDrawable(R.drawable.ic_share);
        drawablesList[2] = this.getResources().getDrawable(R.drawable.ic_call);

        txvAudioDuration = (TextView)findViewById(R.id.txv_audio_duration);
        txvDateTime = (TextView)findViewById(R.id.txv_date_time);
        txvSeekbarStart = (TextView)findViewById(R.id.txv_seekbar_start);
        txvSeekbarEnd = (TextView)findViewById(R.id.txv_seekbar_end);
        audioSeekbar = (SeekBar) findViewById(R.id.seekbar);
        imgvAudioPlay = (ImageView) findViewById(R.id.imgv_play);
        imgvPause = (ImageView) findViewById(R.id.imgv_pause);

        txvToolbarNumber = (TextView)findViewById(R.id.txv_number);

        String retNameFromNumber = getContactName(phoneNumber,ListenSaveRecordActivity.this);
        if(retNameFromNumber.length()>0){
            txvToolbarNumber.setText(retNameFromNumber);
        }else {
            txvToolbarNumber.setText(phoneNumber);
        }

        if(dateTime!=null){
            txvDateTime.setText(dateTime);
        }
//        long diffTime = Long.parseLong(endTime) - Long.parseLong(startTime);
//
//        diffTime = (diffTime < 0 ? -diffTime : diffTime);
        try{
            //String timeDuration = gettotaltimestorage(voiceFilePath);
            if(endTime!=null){
                txvAudioDuration.setText(endTime);
            }

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        lvListen = (ListView)findViewById(R.id.lv_listen);
        listenList = new ArrayList<>();


        listenAdapter = new SaveListenAdapter(titleList,drawablesList,ListenSaveRecordActivity.this);
        lvListen.setAdapter(listenAdapter);

        seekHandler = new Handler();


        myMediaPlayer();
        mediaPlayer=new MediaPlayer();

        imgvAudioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audioSeekbar.setVisibility(View.VISIBLE);
                txvSeekbarStart.setVisibility(View.VISIBLE);
                txvSeekbarEnd.setVisibility(View.VISIBLE);
                txvDateTime.setVisibility(View.GONE);

                try{

                    if(voiceFilePath!=null && mediaPlayer!=null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        if(voiceFilePath.length()>0){
                            mediaPlayer.setDataSource(voiceFilePath);
                        }

                    }

                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(
                            new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    try{
                                        if (mp.isPlaying()) {
                                            mp.stop();
                                            mp.reset();
                                        }else {
                                            mp.start();
                                            // set Progress bar values
                                            audioSeekbar.setProgress(0);
                                            audioSeekbar.setMax(100);
                                            seekHandler.postDelayed(moveSeekBarThread, 100);

                                            imgvAudioPlay.setVisibility(View.GONE);
                                            imgvPause.setVisibility(View.VISIBLE);
                                        }

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            }
                    );


                }catch (Exception e){
                    e.printStackTrace();
                }



                audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer != null && fromUser){
                            mediaPlayer.seekTo(progress * 1000);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });



            }
        });


        imgvPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgvAudioPlay.setVisibility(View.VISIBLE);
                imgvPause.setVisibility(View.GONE);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                }

            }
        });


        lvListen.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("Hello!", "Y u no see me?");

                 if(position==0){
                     dataHelper.open();
                     dataHelper.removeSaveItem(phoneNumber);
                     Intent intent = new Intent(ListenSaveRecordActivity.this,MainActivity.class);
                     startActivity(intent);
                     ActivityCompat.finishAffinity(ListenSaveRecordActivity.this);
                     isDelete = true;
                } else if(position==1){

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, voiceFilePath);
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check this Audio!");
                    startActivity(Intent.createChooser(intent, "Share"));
                }else if(position==2){
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(ListenSaveRecordActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERM_CALL);

                    } else{
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber ));
                        startActivity(intent);
                    }

                }



            }

        });

    }


    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if(mediaPlayer != null){
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();
                // Updating progress bar
                txvSeekbarStart.setText(milliSecondsToTimer(currentDuration));
                txvSeekbarEnd.setText(milliSecondsToTimer(totalDuration));

                int progress = getProgressPercentage(currentDuration, totalDuration);
                audioSeekbar.setProgress(progress);

                // Running this thread after 100 milliseconds
                seekHandler.postDelayed(this, 100);

            }

        }
    };

    private void myMediaPlayer() {
        if(mediaPlayer!=null)
        {
            try{
                mediaPlayer.release();
                // ShowDialog();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    //TR: 20170301
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
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


    public String gettotaltimestorage(String filePath) {

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(ListenSaveRecordActivity.this, Uri.parse(filePath));
        //Log.d("time=================>","time=================>");
        String out = "";
        // get mp3 info

        // convert duration to minute:seconds
        String duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // Log.d("time=================>", duration);
        long dur = Long.parseLong(duration);


        String seconds = milliSecondsToTimer(dur);


        // Log.d("seconds===========>", seconds);
        // close object
        metaRetriever.release();
        return  seconds;

    }

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public void toUseAdmobAdd(){
        interstitial = new InterstitialAd(ListenSaveRecordActivity.this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.intersitial_id));

        //Locate the Banner Ad in activity_main.xml
        AdView adView = (AdView) this.findViewById(R.id.banner_AdView);

        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();

        // Add a test device to show Test Ads

        requestNewInterstitial();

        // Load ads into Banner Ads
        adView.loadAd(adRequest);

        // Load ads into Interstitial Ads
        interstitial.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial();
            }


        });

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //Test in Emulator by this code
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0FA20932AFE1095798444FD9AAB7D425")
                //when ready skip this 2 line
                .build();

        interstitial.loadAd(adRequest);
    }
    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
