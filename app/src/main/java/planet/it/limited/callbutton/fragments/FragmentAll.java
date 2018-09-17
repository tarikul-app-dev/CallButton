package planet.it.limited.callbutton.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.activities.ListenCallRecordActivity;
import planet.it.limited.callbutton.activities.ShowAllRecordFilesActivity;
import planet.it.limited.callbutton.adapter.UserInfoAdapter;
import planet.it.limited.callbutton.database.DataHelper;
import planet.it.limited.callbutton.database.Database;
import planet.it.limited.callbutton.util.AppPreferences;
import planet.it.limited.callbutton.util.CallLog;
import planet.it.limited.callbutton.util.UserInfoModel;

import static planet.it.limited.callbutton.util.SaveValueSharedPreference.getBoleanValueSharedPreferences;


public class FragmentAll extends Fragment {
     View rootView;
     ListView lvUserInfo;
     TextView txvHideText;
     UserInfoAdapter userInfoAdapter;
     DataHelper dataHelper;
     public ArrayList<CallLog> userInfoList;
     static int lPosition = 0;
     long totalUser = 0;
     static boolean isFirstTime ;
     Database database;


    public FragmentAll() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper = new DataHelper(getActivity());
        dataHelper.open();
        database = new Database(getActivity());

        userInfoList = database.getAllCalls();
        totalUser = database.count();
        isFirstTime = getBoleanValueSharedPreferences("first_time",getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_all, container, false);
        lvUserInfo = (ListView)rootView.findViewById(R.id.lv_user_info);
        txvHideText = (TextView) rootView.findViewById(R.id.txv_hide_text);
        userInfoAdapter = new UserInfoAdapter(userInfoList,getActivity());
        lvUserInfo.setAdapter(userInfoAdapter);

        lvUserInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("Hello!", "Y u no see me?");
                lPosition = position;

                //CallLog userInfoModel = new CallLog();
                String voiceFilePath = userInfoList.get(position).getPathToRecording();
                String phoneNumber = userInfoList.get(position).getPhoneNumber();

                SimpleDateFormat dateFormat = new SimpleDateFormat();
                String dateTime = dateFormat.format(userInfoList.get(position).getStartTime().getTime());

                Intent intent = new Intent(getActivity(), ListenCallRecordActivity.class);
                intent.putExtra("voice_path",voiceFilePath);
                intent.putExtra("phone_number",phoneNumber);
                intent.putExtra("date_time",dateTime);
                startActivity(intent);


            }

        });

        if(isFirstTime){
            txvHideText.setText("After 100 records , automatically remove recordings");
        }
        txvHideText.postDelayed(new Runnable() {
            public void run() {
                txvHideText.setVisibility(View.GONE);
            }
        }, 3000);

        if(totalUser==100){
            File folder = AppPreferences.getInstance(getActivity()).getFilesDirectory();
            deleteRecursive(folder);
        }

        return rootView;
    }


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();


    }

    @Override
    public void onResume() {
        super.onResume();

        if(ListenCallRecordActivity.isDelete){

            userInfoList = database.getAllCalls();
            userInfoAdapter = new UserInfoAdapter(userInfoList,getActivity());
            lvUserInfo.setAdapter(userInfoAdapter);

        }


    }




}
