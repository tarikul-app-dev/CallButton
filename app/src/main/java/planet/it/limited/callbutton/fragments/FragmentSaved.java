package planet.it.limited.callbutton.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.activities.ListenCallRecordActivity;
import planet.it.limited.callbutton.activities.ListenSaveRecordActivity;
import planet.it.limited.callbutton.adapter.SaveInfoAdapter;
import planet.it.limited.callbutton.adapter.UserInfoAdapter;
import planet.it.limited.callbutton.database.DataHelper;
import planet.it.limited.callbutton.util.UserInfoModel;


public class FragmentSaved extends Fragment {
    View rootView;
    ListView lvSaveInfo;
    SaveInfoAdapter saveInfoAdapter;
    DataHelper dataHelper;
    public ArrayList<UserInfoModel> saveInfoList;
    static int lPosition = 0;

    public FragmentSaved() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper = new DataHelper(getActivity());
        dataHelper.open();
        saveInfoList = dataHelper.getAllSaveInfo();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_saved, container, false);
        lvSaveInfo = (ListView)rootView.findViewById(R.id.lv_save_info);
        saveInfoAdapter = new SaveInfoAdapter(saveInfoList,getActivity());
        lvSaveInfo.setAdapter(saveInfoAdapter);

        lvSaveInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("Hello!", "Y u no see me?");
                lPosition = position;
                //UserInfoModel userInfoModel = new UserInfoModel();
                String voiceFilePath = saveInfoList.get(position).getVoiceFilePath();
                String phoneNumber = saveInfoList.get(position).getUserNumber();
               // String startTime = saveInfoList.get(position).getStartTime();
               // String endTime = saveInfoList.get(position).getEndTime();
                String voiceDuration = saveInfoList.get(position).getEndTime();
                String dateTime =  saveInfoList.get(position).getDate();

                Intent intent = new Intent(getActivity(), ListenSaveRecordActivity.class);
                intent.putExtra("voice_path",voiceFilePath);
                intent.putExtra("phone_number",phoneNumber);
                intent.putExtra("date_time",dateTime);
                intent.putExtra("voice_duration",voiceDuration);
                startActivity(intent);


            }

        });

        return rootView;
    }




    @Override
    public void onResume() {
        super.onResume();
        if(ListenSaveRecordActivity.isDelete){
            dataHelper.open();
            saveInfoList = dataHelper.getAllSaveInfo();
            saveInfoAdapter = new SaveInfoAdapter(saveInfoList,getActivity());
            lvSaveInfo.setAdapter(saveInfoAdapter);
        }
    }


}
