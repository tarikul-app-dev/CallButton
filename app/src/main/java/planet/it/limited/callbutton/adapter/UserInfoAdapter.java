package planet.it.limited.callbutton.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.util.CallLog;
import planet.it.limited.callbutton.util.UserInfoModel;



/**
 * Created by Tarikul on 6/7/2018.
 */

public class UserInfoAdapter extends BaseAdapter {
    private ArrayList<CallLog> mUserInfoList;
    private LayoutInflater mInflater;
    Context mContext;
    String  timeD = " ";
   // private AdapterCallback mAdapterCallback;
   private MediaPlayer mediaPlayer;
    long totalDuration = 0;

    public UserInfoAdapter(ArrayList<CallLog> userInfo, Context context) {
        this.mUserInfoList = userInfo;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
       // mcontacts = new ArrayList<>();
        //this.mAdapterCallback = adapterCallback;
        mediaPlayer=new MediaPlayer();
    }


    @Override
    public int getCount() {

        return mUserInfoList.size();
    }

    @Override
    public CallLog getItem(int position) {
        try {
            if (mUserInfoList != null) {
                return mUserInfoList.get(position);
            } else {
                return null;
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position,  View convertView,  ViewGroup parent) {

        final ViewHolder holder;
        final CallLog userInfoModel = getItem(position);

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.userinfo_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            String userNumber = userInfoModel.getPhoneNumber();

            String retNameFromNumber = getContactName(userNumber,mContext);
        String voiceFilePath = userInfoModel.getPathToRecording().toString();

        try{
            String timeDuration = gettotaltimestorage(voiceFilePath);

            if(timeDuration!=null){
                holder.txvDuration.setText(timeDuration);
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

//        float time =  userInfoModel.getEndTime().getTimeInMillis() - userInfoModel.getStartTime().getTimeInMillis();
//          holder.txvDuration.setText(String.format("%.2f", ((time / 1000) / 60)));
          SimpleDateFormat dateFormat = new SimpleDateFormat();
          holder.txvDate.setText(dateFormat.format(userInfoModel.getStartTime().getTime()));

            if(retNameFromNumber.length()>0){
                holder.txvUserName.setText(retNameFromNumber);
            }else {
                holder.txvUserName.setText(userNumber);
            }



               // holder.txvDuration.setText(String.valueOf(diffHours)+ ":" + String.valueOf(diffMinutes)+ ":" + String.valueOf(diffSeconds));

        return convertView;
    }





    // /////////// //
    // ViewHolder //
    // ///////// //
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txvUserName = (TextView) v.findViewById(R.id.txv_user_number);
        holder.txvDate = (TextView) v.findViewById(R.id.txv_date);
        holder.txvDuration = (TextView) v.findViewById(R.id.txv_time_duration);
        return holder;
    }
    private   class ViewHolder {
        TextView txvUserName,txvDate,txvDuration;

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
    public String gettotaltimestorage(String filePath) {

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mContext, Uri.parse(filePath));
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

}
