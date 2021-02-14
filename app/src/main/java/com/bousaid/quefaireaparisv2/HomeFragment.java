package com.bousaid.quefaireaparisv2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    String url = "https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=&rows=5&facet=category&facet=tags&facet=address_name&facet=address_zipcode&facet=address_city&facet=pmr&facet=blind&facet=deaf&facet=access_type&facet=price_type&timezone=Europe%2FBerlin";

    private ProgressDialog pDialog;
    private List<Activite> activiteList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

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

                            activite.setUrl(image_url);
                            activite.setText(title+"\n"+address_street+"\n"+lead_text+"\n");

                            activiteList.add(activite);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data
                    adapter.notifyDataSetChanged();
                },
                error -> error.printStackTrace()
        );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);

        return rootView;
    }
}