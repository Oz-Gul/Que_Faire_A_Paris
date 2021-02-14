package com.bousaid.quefaireaparisv2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Activite> activityitems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Activite> activityitems) {
        this.activity = activity;
        this.activityitems = activityitems;
    }

    @Override
    public int getCount() {
        return activityitems.size();
    }

    @Override
    public Object getItem(int location) {
        return activityitems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_home, parent, false);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = convertView
                .findViewById(R.id.image_home);
        TextView title = convertView.findViewById(R.id.text_home);

        // getting activite data for the row
        Activite m = activityitems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getUrl(), imageLoader);

        // text
        title.setText(m.getText());

        return convertView;
    }

}