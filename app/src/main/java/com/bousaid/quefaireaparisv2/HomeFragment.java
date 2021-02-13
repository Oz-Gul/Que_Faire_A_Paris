package com.bousaid.quefaireaparisv2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private TextView mTextViewResult;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mTextViewResult = rootView.findViewById(R.id.element_home);
        mQueue = Volley.newRequestQueue(this.getContext());

        jsonParse();

        return rootView;
    }

    private void jsonParse() {
        String url = "https://tools.learningcontainer.com/sample-json.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject("address");
                        for (int i = 0; i<1; i++){
                            String streetAddress = jsonObject.getString("streetAddress");
                            String city = jsonObject.getString("city");
                            String state = jsonObject.getString("state");
                            String postalCode = jsonObject.getString("postalCode");

                            mTextViewResult.append(streetAddress +", "+ city+", "+state+", "+postalCode+"\n");
                        }
                    } catch (JSONException e) {
                        Log.d("MESSAGE", "TEST");
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        mQueue.add(request);
    }
}