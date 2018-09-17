package planet.it.limited.callbutton.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.database.DataHelper;
import planet.it.limited.callbutton.util.BlackListModel;


/**
 * Created by Tarikul on 6/7/2018.
 */

public class BlackListAdapter extends BaseAdapter {
    private ArrayList<BlackListModel> mBlackList;
    private LayoutInflater mInflater;
    Context mContext;
    DataHelper dataHelper;
    private AdapterCallback mAdapterCallback;


    public BlackListAdapter(ArrayList blackList, Context context,AdapterCallback adapterCallback) {
        this.mBlackList = blackList;
        this.mContext = context;
        this.mAdapterCallback = adapterCallback;
        mInflater = LayoutInflater.from(context);
       // mcontacts = new ArrayList<>();
        dataHelper = new DataHelper(mContext);
        dataHelper.open();

    }


    @Override
    public int getCount() {

        return mBlackList.size();
    }

    @Override
    public BlackListModel getItem(int position) {
        try {
            if (mBlackList != null) {
                return mBlackList.get(position);
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
        final BlackListModel blackListModel = getItem(position);

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.black_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


            final String blackListItem = blackListModel.getMobNum();


            holder.txvBlackListItem.setText(blackListItem);

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    dataHelper.removeBlackListItem(blackListItem);
//                    notifyDataSetChanged();
                    mAdapterCallback.onMethodCallback(blackListItem);

                }
            });

        return convertView;
    }





    // /////////// //
    // ViewHolder //
    // ///////// //
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txvBlackListItem = (TextView) v.findViewById(R.id.txv_title);
        holder.btnRemove = (Button) v.findViewById(R.id.btn_remove);
        return holder;
    }
    private   class ViewHolder {
        TextView txvBlackListItem;
        Button btnRemove;

    }

    public interface AdapterCallback {
        void onMethodCallback(String bListNumber);
    }

}
