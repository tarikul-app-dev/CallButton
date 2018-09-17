package planet.it.limited.callbutton.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import planet.it.limited.callbutton.R;

import static planet.it.limited.callbutton.util.SaveValueSharedPreference.getBoleanValueSharedPreferences;

public class SplashActivity extends AppCompatActivity {
    boolean isPermitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isPermitted = getBoleanValueSharedPreferences("permission",SplashActivity.this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(isPermitted){
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                        ActivityCompat.finishAffinity(SplashActivity.this);

                    }else {
                        Intent intent = new Intent(SplashActivity.this,AllPermissionActivity.class);
                        startActivity(intent);
                    }


                }else {
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    ActivityCompat.finishAffinity(SplashActivity.this);
                }

            }
        }, 2000);
    }


}
