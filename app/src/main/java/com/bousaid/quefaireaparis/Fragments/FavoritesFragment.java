package com.bousaid.quefaireaparis.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bousaid.quefaireaparis.Activite;
import com.bousaid.quefaireaparis.Adapters.FavoritesAdapter;
import com.bousaid.quefaireaparis.FavoritesDB;
import com.bousaid.quefaireaparis.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private List<Activite> activiteList = new ArrayList<>();
    private ListView listView;
    private FavoritesAdapter adapter;
    private Activite favoriteItem;
    private FavoritesDB favoritesDB;

    View rootView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

            listView = rootView.findViewById(R.id.list_favorites);
            adapter = new FavoritesAdapter(getActivity(), activiteList);
            listView.setAdapter(adapter);

            favoritesDB = new FavoritesDB(this.getContext());
        }

        loadData();

        return rootView;
    }

    private void loadData(){
        if(activiteList != null){
            activiteList.clear();
        }

        SQLiteDatabase db = favoritesDB.getReadableDatabase();

        Cursor cursor = favoritesDB.selectFavorites();

        try {
            while (cursor.moveToNext()){
                String title = cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_TITLE));
                String text = cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_TEXT));
                String address = cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_ADDRESS));
                String url = cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_URL));
                String id = cursor.getString(cursor.getColumnIndex(FavoritesDB.KEY_ID));
                String favoriteStatus = cursor.getString(cursor.getColumnIndex(FavoritesDB.FAVORITE_STATUS));
                String dateDescription = cursor.getString(cursor.getColumnIndex(FavoritesDB.DATE_DESCRIPTION));
                String price = cursor.getString(cursor.getColumnIndex(FavoritesDB.PRICE));

                favoriteItem = new Activite();
                favoriteItem.setUrl(url);
                favoriteItem.setKey_id(id);
                favoriteItem.setTitle(title);
                favoriteItem.setAddress(address);
                favoriteItem.setFavoriteStatus(favoriteStatus);
                favoriteItem.setText(text);
                favoriteItem.setDateDescription(dateDescription);
                favoriteItem.setPrice(price);
                activiteList.add(favoriteItem);

                Log.d("STATUS DU FAVORIS", favoriteItem.getFavoriteStatus());
                adapter.notifyDataSetChanged();
            }
        } finally {
            if(cursor!=null && cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
        adapter.notifyDataSetChanged();
    }

}