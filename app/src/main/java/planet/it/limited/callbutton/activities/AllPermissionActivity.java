package planet.it.limited.callbutton.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import planet.it.limited.callbutton.R;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static planet.it.limited.callbutton.util.SaveValueSharedPreference.getBoleanValueSharedPreferences;
import static planet.it.limited.callbutton.util.SaveValueSharedPreference.saveBoleanValueSharedPreferences;

public class AllPermissionActivity extends AppCompatActivity {
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Button btnAllPermission,btnDone;
    public static boolean checkAutoStartPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_permission);
         initViews();

    }

    public void initViews(){
        saveBoleanValueSharedPreferences("first_time",true,AllPermissionActivity.this);
        btnAllPermission = (Button)findViewById(R.id.btn_permission);
        btnDone = (Button)findViewById(R.id.btn_done);
        btnDone.setEnabled(false);
        checkAutoStartPermission = getBoleanValueSharedPreferences("auto_start",AllPermissionActivity.this);
        if(checkAutoStartPermission==false){
            try {
                Intent intent = new Intent();
                String manufacturer = android.os.Build.MANUFACTURER;
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    //checkAutoStartPermission = true;
                    saveBoleanValueSharedPreferences("auto_start",true,AllPermissionActivity.this);
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    saveBoleanValueSharedPreferences("auto_start",true,AllPermissionActivity.this);
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    saveBoleanValueSharedPreferences("auto_start",true,AllPermissionActivity.this);
                }else if ("huawei".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    saveBoleanValueSharedPreferences("auto_start",true,AllPermissionActivity.this);
                }

                List<ResolveInfo> list = AllPermissionActivity.this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if  (list.size() > 0) {
                    AllPermissionActivity.this.startActivity(intent);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        btnAllPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mayRequestPermission()){
                    btnDone.setEnabled(true);
                    return;
                }
//                if(mayRequestPermission()){
//                     Intent intent = new Intent(AllPermissionActivity.this,MainActivity.class);
//                     startActivity(intent);
//                     ActivityCompat.finishAffinity(AllPermissionActivity.this);
//                    saveBoleanValueSharedPreferences("permission",true,AllPermissionActivity.this);
//                }

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mayRequestPermission()){
                    Intent intent = new Intent(AllPermissionActivity.this,MainActivity.class);
                    startActivity(intent);
                    ActivityCompat.finishAffinity(AllPermissionActivity.this);
                    saveBoleanValueSharedPreferences("permission",true,AllPermissionActivity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    private boolean mayRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permCallPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int permOutgoingCall = ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);
        int permContactRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permCallPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (permOutgoingCall != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }
        if (permContactRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
