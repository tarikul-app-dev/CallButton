package planet.it.limited.callbutton.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.util.UserInfoModel;


/**
 * Created by Tarikul on 6/7/2018.
 */

public class RecordFilesAdapter extends BaseAdapter {
    private ArrayList mRecordList;
    private LayoutInflater mInflater;
    Context mContext;

    public RecordFilesAdapter(ArrayList recordList, Context context) {
        this.mRecordList = recordList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
       // mcontacts = new ArrayList<>();

    }


    @Override
    public int getCount() {

        return mRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        try {
            if (mRecordList != null) {
                return mRecordList.get(position);
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
       // final UserInfoModel userInfoModel = getItem(position);

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.record_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            String recordItem = mRecordList.get(position).toString();


            holder.recordItem.setText(recordItem);


        return convertView;
    }





    // /////////// //
    // ViewHolder //
    // ///////// //
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.recordItem = (TextView) v.findViewById(R.id.txv_title);

        return holder;
    }
    private   class ViewHolder {
        TextView recordItem;

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



}
