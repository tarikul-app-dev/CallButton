package planet.it.limited.callbutton.activities;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.adapter.RecordFilesAdapter;
import planet.it.limited.callbutton.util.AppPreferences;

public class ShowAllRecordFilesActivity extends AppCompatActivity {
    ArrayList<File> files ;
    ListView lvRecordFile;
    RecordFilesAdapter recordFilesAdapter;
    Toolbar toolbar;
    InterstitialAd interstitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_record_files);
        toolbar = (Toolbar)findViewById(R.id.toolbar_record_file);
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
        File retFile = AppPreferences.getInstance(ShowAllRecordFilesActivity.this).getFilesDirectory();
        files = getListFiles(retFile);
        lvRecordFile = (ListView)findViewById(R.id.lv_record_file);
        recordFilesAdapter = new RecordFilesAdapter(files,ShowAllRecordFilesActivity.this);
        lvRecordFile.setAdapter(recordFilesAdapter);
    }

    private ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        try{
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    if(file.getName().endsWith(".amr") || file.getName().endsWith(".mpg") || file.getName().endsWith(".3gpp")){
                        inFiles.add(file);
                    }
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }



        return inFiles;
    }

    public void toUseAdmobAdd(){
        interstitial = new InterstitialAd(ShowAllRecordFilesActivity.this);
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
