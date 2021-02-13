package com.bousaid.quefaireaparisv2;

import android.media.Image;
import android.os.Bundle;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class HomeFragment extends Fragment {
    private TextView mTextViewResult;
    private ImageView imageView;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mTextViewResult = rootView.findViewById(R.id.element_home);
        mQueue = Volley.newRequestQueue(getContext());

        jsonParse();

        return rootView;
    }

    private void jsonParse() {
        String url = "https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=&rows=1&facet=category&facet=tags&facet=address_name&facet=address_zipcode&facet=address_city&facet=pmr&facet=blind&facet=deaf&facet=access_type&facet=price_type&timezone=Europe%2FBerlin";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray records = response.getJSONArray("records");
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject record = records.getJSONObject(i);
                            JSONObject fields = record.getJSONObject("fields");
                            String title = fields.getString("title");
                            String address_street = fields.getString("address_street");
                            String lead_text = fields.getString("lead_text");
                            String image_url = fields.getString("cover_url");
                            imageView = this.getView().findViewById(R.id.image_home);

                            Glide.with(getActivity()).load(image_url).into(imageView);
                            mTextViewResult.append(title + "\n" + address_street +"\n" + lead_text);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        mQueue.add(request);
    }
}