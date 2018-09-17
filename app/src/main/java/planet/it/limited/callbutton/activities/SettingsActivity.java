package planet.it.limited.callbutton.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.adapter.SettingsAdapter;

public class SettingsActivity extends AppCompatActivity {
    String titleList[] = {  "Record Calls","About" ,"App version"};
    String desList[] = {  "Turn On Call Record","About Call Button" ,"1.0"};
    ListView lvSettings;
    Toolbar toolbar;
    SettingsAdapter settingsAdapter;
    InterstitialAd interstitial;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar)findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        initViews();
    }

    public void initViews(){
        lvSettings = (ListView) findViewById(R.id.lv_settings);
        settingsAdapter = new SettingsAdapter(titleList,desList,SettingsActivity.this);
        lvSettings.setAdapter(settingsAdapter);


        lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



            }
        });

        toUseAdmobAdd();
    }

    public void toUseAdmobAdd(){
        interstitial = new InterstitialAd(SettingsActivity.this);
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
