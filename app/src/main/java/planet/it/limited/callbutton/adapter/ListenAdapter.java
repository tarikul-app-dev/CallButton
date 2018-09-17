package planet.it.limited.callbutton.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.util.ListenModel;
import planet.it.limited.callbutton.util.UserInfoModel;


/**
 * Created by Tarikul on 6/7/2018.
 */

public class ListenAdapter extends BaseAdapter {
    private String[] mTitleList;
    Drawable [] mDrawables;
    private LayoutInflater mInflater;
    Context mContext;

    public ListenAdapter(String[] titleList,Drawable [] drawables, Context context) {
        this.mTitleList = titleList;
        this.mDrawables = drawables;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
       // mcontacts = new ArrayList<>();

    }


    @Override
    public int getCount() {

        return mTitleList.length;
    }


    public Object getItem(int position) {
        return null;
    }




    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position,  View convertView,  ViewGroup parent) {

        final ViewHolder holder;

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.listen_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



            holder.title.setText(mTitleList[position]);
            holder.imvIcon.setBackgroundDrawable(mDrawables[position]);

        return convertView;
    }





    // /////////// //
    // ViewHolder //
    // ///////// //
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) v.findViewById(R.id.txv_title);

        holder.imvIcon = (ImageView) v.findViewById(R.id.imgv_thumb);
        return holder;
    }
    private   class ViewHolder {
        TextView title;
        ImageView imvIcon;

    }




}
