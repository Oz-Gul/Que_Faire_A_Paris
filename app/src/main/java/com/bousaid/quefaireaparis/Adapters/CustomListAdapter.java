package com.bousaid.quefaireaparis.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bousaid.quefaireaparis.FavoritesDB;
import com.bousaid.quefaireaparis.Activite;
import com.bousaid.quefaireaparis.AppController;
import com.bousaid.quefaireaparis.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Activite> activityitems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    FavoritesDB favoritesDB;

    boolean firstStart = true;


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

        favoritesDB = new FavoritesDB(parent.getContext());

        //Create new table
        if (firstStart) {
            createTableOnFirstStart();
        }

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

        //Add the current item to the favorites
        FloatingActionButton favoriteButton = convertView.findViewById(R.id.favorite_button);
        Activite favoriteItem = activityitems.get(position);
        favoriteItem.setFavoriteStatus("0");
        favoriteButton.setOnClickListener(v -> {
            if(favoriteItem.getFavoriteStatus().equals("0")){
                favoriteItem.setFavoriteStatus("1");
                favoriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                //Si l'activité n'est pas déjà lickée, on l'ajoute
                Cursor cursor = favoritesDB.selectFavorites();
                boolean exists = false;
                while(cursor.moveToNext()){
                    String status = cursor.getString(cursor.getColumnIndex(FavoritesDB.KEY_ID));
                    if(status.equals(favoriteItem.getKey_id())){
                        exists = true;
                    }
                }
                if(!exists){
                    favoritesDB.insertIntoDatabase(favoriteItem.getTitle(),
                            favoriteItem.getAddress(),
                            favoriteItem.getText(),
                            favoriteItem.getUrl(),
                            favoriteItem.getFavoriteStatus(),
                            favoriteItem.getDateDescription(),
                            favoriteItem.getKey_id(),
                            favoriteItem.getPrice());
                }

            }
            else{
                favoriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                favoriteItem.setFavoriteStatus("0");
                favoritesDB.removeFavorite(favoriteItem.getKey_id());
            }
        });

        // getting activite data for the row
        Activite m = activityitems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getUrl(), imageLoader);

        // text
        title.setText(m.getTitle() + "\n" + m.getText() + "\n\n" + m.getDateDescription() + "\n" + m.getAddress() + "\n" + m.getPrice());

        return convertView;
    }

    private void createTableOnFirstStart() {
        //favoritesDB.insertEmpty();

        firstStart=false;
    }
}