package planet.it.limited.callbutton.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.services.RecordCallService;
import planet.it.limited.callbutton.util.CallLog;

import static planet.it.limited.callbutton.util.SaveValueSharedPreference.getBoleanValueSharedPreferences;
import static planet.it.limited.callbutton.util.SaveValueSharedPreference.saveBoleanValueSharedPreferences;


/**
 * Created by Tarikul on 6/7/2018.
 */

public class SettingsAdapter extends BaseAdapter {
    private String[] mSettings;
    private String[] mDesSetting;
    private LayoutInflater mInflater;
    Context mContext;
    boolean isAppOn;

    public SettingsAdapter(String[] settings,String[] desSetting, Context context) {
        this.mSettings = settings;
        this.mContext = context;
        this.mDesSetting = desSetting;
        mInflater = LayoutInflater.from(context);
       // mcontacts = new ArrayList<>();
        isAppOn = getBoleanValueSharedPreferences("app_on_yes",mContext);

    }


    @Override
    public int getCount() {

        return mSettings.length;
    }

    @Override
    public Object getItem(int position) {
        try {
            if (mSettings != null) {
                return mSettings.length;
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
            convertView = vi.inflate(R.layout.settings_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

            holder.recordItem.setText(mSettings[position]);
            holder.txvDesSetting.setText(mDesSetting[position]);

        if(mSettings[position].equals("Record Calls")){
            holder.btnSwRec.setVisibility(View.VISIBLE);
        }
            if(mSettings[position].equals("About")){
                holder.btnSwRec.setVisibility(View.GONE);
            }

        if(mSettings[position].equals("App version")){
            holder.btnSwRec.setVisibility(View.GONE);
        }


        if(isAppOn){
            holder.btnSwRec.setChecked(true);
        }


        holder.btnSwRec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (!isChecked) {
                    // The toggle is enabled
                     showDialog();
                }else {
                    saveBoleanValueSharedPreferences("app_on_yes",true,mContext);
                    saveBoleanValueSharedPreferences("app_on_no",false,mContext);
                    CallLog callLog = new CallLog();
                    RecordCallService.sartRecording(mContext,callLog);

                }
            }
        });

        return convertView;
    }





    // /////////// //
    // ViewHolder //
    // ///////// //
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.recordItem = (TextView) v.findViewById(R.id.txv_title);
        holder.txvDesSetting = (TextView) v.findViewById(R.id.txv_des);
        holder.btnSwRec = (Switch) v.findViewById(R.id.btn_switch);

        return holder;
    }
    private   class ViewHolder {
        TextView recordItem;
        TextView txvDesSetting;
        Switch btnSwRec;

    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog,null);

        // Specify alert dialog is not cancelable/not ignorable
        builder.setCancelable(false);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                    saveBoleanValueSharedPreferences("app_on_yes",false,mContext);
                    saveBoleanValueSharedPreferences("app_on_no",true,mContext);
                    RecordCallService.stopRecording(mContext);

                dialog.cancel();

            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss/cancel the alert dialog
                //dialog.cancel();
                dialog.dismiss();

            }
        });

        // Display the custom alert dialog on interface
        dialog.show();
        //   recreate();

    }

}
