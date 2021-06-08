package com.bousaid.quefaireaparis.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bousaid.quefaireaparis.Activite;
import com.bousaid.quefaireaparis.Adapters.CustomListAdapter;
import com.bousaid.quefaireaparis.AppController;
import com.bousaid.quefaireaparis.FavoritesDB;
import com.bousaid.quefaireaparis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {
    String url = "https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=&rows=5&facet=category&facet=tags&facet=address_name&facet=address_zipcode&facet=address_city&facet=pmr&facet=blind&facet=deaf&facet=access_type&facet=price_type&timezone=Europe%2FBerlin";

    private List<Activite> activiteList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;

    View rootView = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FavoritesDB favoritesDB = new FavoritesDB(this.getContext());
        SQLiteDatabase db = favoritesDB.getReadableDatabase();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);

            listView = rootView.findViewById(R.id.list_home);
            adapter = new CustomListAdapter(getActivity(), activiteList);
            listView.setAdapter(adapter);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray records = response.getJSONArray("records");
                            for (int i = 0; i < records.length(); i++) {
                                Activite activite = new Activite();

                                JSONObject record = records.getJSONObject(i);
                                JSONObject fields = record.getJSONObject("fields");
                                String title = fields.getString("title");
                                String address_street = fields.getString("address_street");
                                String lead_text = fields.getString("lead_text");
                                String image_url = fields.getString("cover_url");
                                String price;

                                if (fields.getString("price_type").equals("payant")) {
                                    if (fields.has("price_detail")) {
                                        price = fields.getString("price_detail");
                                    } else {
                                        price = "Payant";
                                    }
                                } else {
                                    price = "Gratuit";
                                }

                                activite.setPrice(price);

                                String dateDescription = fields.getString("date_description");
                                String id = record.getString("recordid");

                                //On vérifie si l'activité est déjà dans la base de données
                                Cursor cursor = favoritesDB.selectFavorites();
                                while (cursor.moveToNext()) {
                                    if (id.equals(cursor.getString(cursor.getColumnIndex(FavoritesDB.KEY_ID)))) {
                                        activite.setFavoriteStatus("1");

                                        System.out.println("KEY IDS : " + id + "/" + cursor.getString(cursor.getColumnIndex(FavoritesDB.KEY_ID)));
                                        System.out.println("TITLES : " + title + "/" + cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_TITLE)));
                                        System.out.println("FAV STATUS : " + activite.getFavoriteStatus() + "/" + cursor.getString(cursor.getColumnIndex(FavoritesDB.FAVORITE_STATUS)));

                                    } else {
                                        activite.setFavoriteStatus("0");
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                activite.setUrl(image_url);
                                activite.setTitle(title);
                                activite.setAddress(address_street);
                                activite.setText(lead_text);
                                activite.setDateDescription(dateDescription);
                                activite.setKey_id(id);
                                System.out.println("CURRENT ID" + id);

                                dateDescription = dateDescriptionAdapter(dateDescription);
                                activite.setDateDescription(dateDescription);

                                activiteList.add(activite);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    },
                    Throwable::printStackTrace
            );

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(request);

        }
        return rootView;
    }


    public String dateDescriptionAdapter(String date) {
        final String regex = "(?i)<br */?>";
        final String subst = "\n";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher dateDescriptionJava = pattern.matcher(date);

        date = dateDescriptionJava.replaceAll(subst);

        return date;
    }

}