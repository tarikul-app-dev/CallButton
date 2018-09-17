package planet.it.limited.callbutton.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.media.MediaMetadataRetriever;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.util.UserInfoModel;


/**
 * Created by Tarikul on 6/7/2018.
 */

public class SaveInfoAdapter extends BaseAdapter {
    private ArrayList<UserInfoModel> mUserInfoList;
    private LayoutInflater mInflater;
    Context mContext;

    public SaveInfoAdapter(ArrayList<UserInfoModel> userInfo, Context context) {
        this.mUserInfoList = userInfo;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
       // mcontacts = new ArrayList<>();

    }


    @Override
    public int getCount() {

        return mUserInfoList.size();
    }

    @Override
    public UserInfoModel getItem(int position) {
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
        final UserInfoModel userInfoModel = getItem(position);

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.saveinfo_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            String userNumber = userInfoModel.getUserNumber();
            String retNameFromNumber = getContactName(userNumber,mContext);
            String date = userInfoModel.getDate();

            String timeD = userInfoModel.getEndTime();
            if(retNameFromNumber.length()>0){
                holder.txvUserName.setText(retNameFromNumber);
            }else {
                holder.txvUserName.setText(userNumber);
            }



            holder.txvDate.setText(date);
            if(timeD!=null){
                holder.txvDuration.setText(timeD);
            }


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
