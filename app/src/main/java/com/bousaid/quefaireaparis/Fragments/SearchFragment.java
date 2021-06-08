package com.bousaid.quefaireaparis.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bousaid.quefaireaparis.Activite;
import com.bousaid.quefaireaparis.Adapters.CustomListAdapter;
import com.bousaid.quefaireaparis.AppController;
import com.bousaid.quefaireaparis.FavoritesDB;
import com.bousaid.quefaireaparis.GenerateURL;
import com.bousaid.quefaireaparis.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    View activitePopupView;
    ViewGroup cont;

    private TextView dateText;
    SimpleDateFormat format;
    String strDate;
    Calendar calendar;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mMapView;

    private CustomListAdapter adapter;
    private ListView listView;
    private List<Activite> activiteList = new ArrayList<>();

    Button buttonPrix;

    StringBuilder base_url = new StringBuilder("https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=");
    private GenerateURL generateURL = new GenerateURL();
    DateTime dtChoisie = new DateTime();
    boolean datechoisie = false;

    Location location = new Location("null");
    //Paris
    double latitude = 48.864716;
    double longitude = 2.349014;
    Marker marker;
    GoogleMap carte;
    float zoomLevel = 11f;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button close;

    public void activiteDialog(String text){
        dialogBuilder = new AlertDialog.Builder(getContext());
        activitePopupView = getLayoutInflater().inflate(R.layout.popup, null);

        TextView textv = activitePopupView.findViewById(R.id.text_popup);
        textv.setText(text);

        //NetworkImageView image = activitePopupView.findViewById(R.id.image_popup);
        //ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        //image.setImageUrl(url, imageLoader);

        dialogBuilder.setView(activitePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        Button cancel = activitePopupView.findViewById(R.id.button_close);
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public JsonObjectRequest jsonRequestPrice(StringBuilder url, boolean datechoisie) {

        carte.clear();
        return new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        Log.d("URL FINALE", url.toString());
                        JSONArray records = response.getJSONArray("records");
                        activiteList.clear();
                        adapter.notifyDataSetChanged();

                        for (int i = 0; i < records.length(); i++) {
                            Activite activite = new Activite();

                            JSONObject record = records.getJSONObject(i);
                            JSONObject fields = record.getJSONObject("fields");
                            String title = fields.getString("title");
                            String address_street = fields.getString("address_street");
                            String lead_text = fields.getString("lead_text");
                            String image_url = fields.getString("cover_url");
                            String price = "";
                            String dateDescription = fields.getString("date_description");
                            JSONArray lat_lon = fields.getJSONArray("lat_lon");
                            String dateStart = fields.getString("date_start").substring(0, 19);
                            String dateEnd = fields.getString("date_end").substring(0, 19);

                            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
                            DateTime dtStart = formatter.parseDateTime(dateStart);
                            DateTime dtEnd = formatter.parseDateTime(dateEnd);

                            Date dateChoisie = dtChoisie.toDate();
                            Date start = dtStart.toDate();
                            Date end = dtEnd.toDate();

                            dateDescription = dateDescriptionAdapter(dateDescription);
                            activite.setDateDescription(dateDescription);
                            String id = record.getString("recordid");

                            activite.setTitle(title);
                            activite.setAddress(address_street);
                            activite.setText(lead_text);
                            activite.setUrl(image_url);
                            activite.setDateDescription(dateDescription);
                            activite.setKey_id(id);
                            activite.setLatitude(lat_lon.getDouble(0));
                            activite.setLongitude(lat_lon.getDouble(1));

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

                            //Si une date a été choisie :
                            if (datechoisie) {
                                if (isWithinRange(dateChoisie, start, end)) {
                                    activiteList.add(activite);
                                }
                            } else if (!datechoisie) {
                                activiteList.add(activite);
                            }

                            LatLng latLng = new LatLng(activite.getLatitude(), activite.getLongitude());
                            marker = carte.addMarker(new MarkerOptions().position(latLng).title(title)
                                    .snippet(title + "\n" + lead_text + "\n\n" + address_street +"\n\n" + dateDescription + "\n" + price));

                            //On vérifie si l'activité est déjà dans la base de données
                            FavoritesDB favoritesDB = new FavoritesDB(this.getContext());
                            Cursor cursor = favoritesDB.selectFavorites();
                            while (cursor.moveToNext()) {
                                if (id.equals(cursor.getString(cursor.getColumnIndex(FavoritesDB.KEY_ID)))) {
                                    activite.setFavoriteStatus("1");
                                } else {
                                    activite.setFavoriteStatus("0");
                                }
                                adapter.notifyDataSetChanged();
                            }
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
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Date Choisie " + dtChoisie);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        cont = container;

        listView = view.findViewById(R.id.list_search);
        adapter = new CustomListAdapter(getActivity(), activiteList);
        listView.setAdapter(adapter);

        final Button buttonAnimations = view.findViewById(R.id.button_animations);
        final Button buttonConcerts = view.findViewById(R.id.button_concerts);
        final Button buttonEvenements = view.findViewById(R.id.button_evenements);
        final Button buttonExpositions = view.findViewById(R.id.button_expositions);
        final Button buttonSpectacles = view.findViewById(R.id.button_spectacles);
        Button buttonLocaliser = view.findViewById(R.id.button_localiser);
        Button buttonFiltrer = view.findViewById(R.id.button_filtrer);
        Button buttonListe = view.findViewById(R.id.button_liste);
        Button buttonCarte = view.findViewById(R.id.button_carte);
        Button buttonReset = view.findViewById(R.id.button_reset);
        Button buttonDate = view.findViewById(R.id.button_date);
        buttonPrix = view.findViewById(R.id.button_prix);
        ChipGroup chipgroup_animations = view.findViewById(R.id.chipgroup_animations);
        ChipGroup chipgroup_concerts = view.findViewById(R.id.chipgroup_concerts);
        ChipGroup chipgroup_evenements = view.findViewById(R.id.chipgroup_evenements);
        ChipGroup chipgroup_expositions = view.findViewById(R.id.chipgroup_expositions);
        ChipGroup chipgroup_spectacles = view.findViewById(R.id.chipgroup_spectacles);
        mMapView = view.findViewById(R.id.results_map);
        dateText = view.findViewById(R.id.button_date);
        generateURL.setBase_url(base_url);
        generateURL.setFull_url(base_url);
        SearchView searchView = view.findViewById(R.id.search_view);
        final String[] previous_query = {""};

        //ONCLICK LOCALISATION BUTTON
        buttonLocaliser.setOnClickListener(v -> {
            LocationManager locationManager;
            String provider;
            final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;

            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_COURSE_LOCATION);
            }

            // Initialize the location fields
            if (location != null) {
                location = locationManager.getLastKnownLocation(provider);
                onLocationChanged(location);
            } else {
                System.out.println("VOUS N'AVEZ PAS ACTIVE LA LOCALISATION");
            }
        });

        //ONCLICK RESET BUTTON
        buttonReset.setOnClickListener(v -> {
            chipgroup_animations.clearCheck();
            chipgroup_concerts.clearCheck();
            chipgroup_evenements.clearCheck();
            chipgroup_expositions.clearCheck();
            chipgroup_spectacles.clearCheck();
            datechoisie = false;
            StringBuilder base_URL = new StringBuilder("https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=");
            generateURL.setFull_url(base_URL);
            System.out.println(generateURL.getFull_url());
            buttonPrix.setText("PRIX");
            buttonDate.setText("DATE");
            JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(request);
            activiteList.clear();
        });

        //ONQUERY SEARCHVIEW
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    generateURL.clearTags(previous_query[0]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    generateURL.completeURL_forTags(query);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //REQUEST
                JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(request);
                activiteList.clear();
                previous_query[0] = String.copyValueOf(query.toCharArray());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //ONCLICK DATEPICKER BUTTON
        view.findViewById(R.id.button_date).setOnClickListener(v -> {
            showDatePickerDialog();
            datechoisie = true;
            //REQUEST
            JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(request);
            activiteList.clear();
        });

        //ONCLICK PRICEPICKER BUTTON
        buttonPrix.setOnClickListener(v -> {
            String[] priceChoices = {"gratuit", "payant"};

            AlertDialog.Builder priceDialog = new AlertDialog.Builder(getActivity());
            CharSequence[] priceChoice = {"Gratuit", "Payant"};

            priceDialog.setTitle("Prix")
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                    .setItems(priceChoice, (dialog, which) -> {
                        // The 'which' argument contains the index position of the selected item
                        if (which == 0) {
                            buttonPrix.setText(R.string.gratuit);

//                            String str2 = url.toString().replaceAll("vide", priceChoices[0]);
//                            String str4 = str2.replaceAll(priceChoices[1], priceChoices[0]);
//                            url = new StringBuilder(str4);
                            generateURL.completeURL_forPriceChoice("price_type", "gratuit");
                        } else {
                            buttonPrix.setText(R.string.payant);

//                            String str2 = url.toString().replaceAll("vide", priceChoices[1]);
//                            String str4 = str2.replaceAll(priceChoices[0], priceChoices[1]);
//                            url = new StringBuilder(str4);
                            generateURL.completeURL_forPriceChoice("price_type", "payant");
                        }

                        //REQUEST
                        JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(request);
                        activiteList.clear();
                    });

            priceDialog.show();
        });

        //ONCLICK FILTER BUTTON
        buttonFiltrer.setOnClickListener(v -> onClickVisible(view.findViewById(R.id.filter)));

        //ONCLICK ANIMATIONS BUTTON
        buttonAnimations.setOnClickListener(v -> {
            //SET OTHER CHIPGROUPS TO GONE AND OTHER BUTTONS IN BLACK
            chipgroup_concerts.setVisibility(View.GONE);
            chipgroup_evenements.setVisibility(View.GONE);
            chipgroup_expositions.setVisibility(View.GONE);
            chipgroup_spectacles.setVisibility(View.GONE);
            buttonConcerts.setBackgroundColor(Color.BLACK);
            buttonEvenements.setBackgroundColor(Color.BLACK);
            buttonExpositions.setBackgroundColor(Color.BLACK);
            buttonSpectacles.setBackgroundColor(Color.BLACK);

            //THEN FOCUS ON THIS CHIPGROUP AND ITS CORRESPONDING BUTTON
            onClickVisible_CategoryButtons(chipgroup_animations, buttonAnimations);

            //ONCLICKLISTENER FOR EACH CHIP OF THE CHIPGROUP
            onClickChip(chipgroup_animations, buttonAnimations);

        });

        //ONCLICK CONCERTS BUTTON
        buttonConcerts.setOnClickListener(v -> {
            //SET OTHER CHIPGROUPS TO GONE AND OTHER BUTTONS IN BLACK
            chipgroup_animations.setVisibility(View.GONE);
            chipgroup_evenements.setVisibility(View.GONE);
            chipgroup_expositions.setVisibility(View.GONE);
            chipgroup_spectacles.setVisibility(View.GONE);
            buttonAnimations.setBackgroundColor(Color.BLACK);
            buttonEvenements.setBackgroundColor(Color.BLACK);
            buttonExpositions.setBackgroundColor(Color.BLACK);
            buttonSpectacles.setBackgroundColor(Color.BLACK);

            //THEN FOCUS ON THIS CHIPGROUP AND ITS CORRESPONDING BUTTON
            onClickVisible_CategoryButtons(chipgroup_concerts, buttonConcerts);

            //ONCLICKLISTENER FOR EACH CHIP OF THE CHIPGROUP
            onClickChip(chipgroup_concerts, buttonConcerts);
        });

        //ONCLICK EVENEMENTS BUTTON
        buttonEvenements.setOnClickListener(v -> {
            //SET OTHER CHIPGROUPS TO GONE AND OTHER BUTTONS IN BLACK
            chipgroup_animations.setVisibility(View.GONE);
            chipgroup_concerts.setVisibility(View.GONE);
            chipgroup_expositions.setVisibility(View.GONE);
            chipgroup_spectacles.setVisibility(View.GONE);
            buttonAnimations.setBackgroundColor(Color.BLACK);
            buttonConcerts.setBackgroundColor(Color.BLACK);
            buttonExpositions.setBackgroundColor(Color.BLACK);
            buttonSpectacles.setBackgroundColor(Color.BLACK);

            //THEN FOCUS ON THIS CHIPGROUP AND ITS CORRESPONDING BUTTON
            onClickVisible_CategoryButtons(chipgroup_evenements, buttonEvenements);

            //ONCLICKLISTENER FOR EACH CHIP OF THE CHIPGROUP
            onClickChip(chipgroup_evenements, buttonEvenements);
        });

        //ONCLICK EXPOSITIONS BUTTON
        buttonExpositions.setOnClickListener(v -> {
            //SET OTHER CHIPGROUPS TO GONE AND OTHER BUTTONS IN BLACK
            chipgroup_animations.setVisibility(View.GONE);
            chipgroup_concerts.setVisibility(View.GONE);
            chipgroup_evenements.setVisibility(View.GONE);
            chipgroup_spectacles.setVisibility(View.GONE);
            buttonAnimations.setBackgroundColor(Color.BLACK);
            buttonConcerts.setBackgroundColor(Color.BLACK);
            buttonEvenements.setBackgroundColor(Color.BLACK);
            buttonSpectacles.setBackgroundColor(Color.BLACK);

            //THEN FOCUS ON THIS CHIPGROUP AND ITS CORRESPONDING BUTTON
            onClickVisible_CategoryButtons(chipgroup_expositions, buttonExpositions);

            //ONCLICKLISTENER FOR EACH CHIP OF THE CHIPGROUP
            onClickChip(chipgroup_expositions, buttonExpositions);

        });

        //ONCLICK SPECTACLES BUTTON
        buttonSpectacles.setOnClickListener(v -> {
            //SET OTHER CHIPGROUPS TO GONE AND OTHER BUTTONS IN BLACK
            chipgroup_animations.setVisibility(View.GONE);
            chipgroup_concerts.setVisibility(View.GONE);
            chipgroup_evenements.setVisibility(View.GONE);
            chipgroup_expositions.setVisibility(View.GONE);
            buttonAnimations.setBackgroundColor(Color.BLACK);
            buttonConcerts.setBackgroundColor(Color.BLACK);
            buttonEvenements.setBackgroundColor(Color.BLACK);
            buttonExpositions.setBackgroundColor(Color.BLACK);

            //THEN FOCUS ON THIS CHIPGROUP AND ITS CORRESPONDING BUTTON
            onClickVisible_CategoryButtons(chipgroup_spectacles, buttonSpectacles);

            //ONCLICKLISTENER FOR EACH CHIP OF THE CHIPGROUP
            onClickChip(chipgroup_spectacles, buttonSpectacles);
        });

        //AFFICHAGE LISTE/MAP
        MaterialButtonToggleGroup materialButtonToggleGroup = view.findViewById(R.id.toggleButton_affichage);
        materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == buttonListe.getId()) {
                    view.findViewById(R.id.list_search).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.map_container).setVisibility(View.GONE);
                } else if (checkedId == buttonCarte.getId()) {
                    view.findViewById(R.id.map_container).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.list_search).setVisibility(View.GONE);
                }
            }
        });

        //INITIALISATION GOOGLE MAP
        initGoogleMap(savedInstanceState);

        return view;
    }

    private void onClickChip(ChipGroup chipGroup, Button button) {
        //ONCLICKLISTENER FOR EACH CHIP OF THE CHIPGROUP
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            int var = i;
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setOnClickListener(v -> {
                System.out.println("CHIP NUMERO " + Integer.valueOf(var) + " SELECTIONNE");
                //On ajoute à la requete si le bouton est checké et on l'enlève sinon
                try {
                    generateURL.completeURL_forCategoryChoice((String) button.getText(), (String) chip.getText());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //REQUEST
                JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(request);
                activiteList.clear();
            });
        }
    }

    private void onClickVisible(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void onClickVisible_CategoryButtons(ChipGroup chipGroup, Button button) {
        if (chipGroup.getVisibility() == View.VISIBLE) {
            chipGroup.setVisibility(View.GONE);
            button.setBackgroundColor(Color.BLACK);
        } else {
            chipGroup.setVisibility(View.VISIBLE);
            button.setBackgroundColor(0xFF018786);
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

    }

    //DATE PICKER
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(
                SearchFragment.this.getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );


        format = new SimpleDateFormat("EEE d MMM", Locale.FRANCE);
        calendar = Calendar.getInstance();
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        strDate = format.format(calendar.getTime());
        dateText.setText(strDate);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        dtChoisie = new DateTime(cal.getTime());
        System.out.println("Date Choisie " + dtChoisie);

        System.out.println("DATE SELECTIONNEE AVEC URL = " + generateURL.getFull_url());
        datechoisie = true;

        if (buttonPrix.getText().equals("Gratuit"))
            //url = new StringBuilder("https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=&rows=50&facet=price_type&refine.price_type=gratuit");
            generateURL.completeURL_forPriceChoice("price_type", "gratuit");
        else if (buttonPrix.getText().equals("Payant"))
            //url = new StringBuilder("https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=&rows=50&facet=price_type&refine.price_type=payant");
            generateURL.completeURL_forPriceChoice("price_type", "payant");
//        else
//            url = new StringBuilder("https://opendata.paris.fr/api/records/1.0/search/?dataset=que-faire-a-paris-&q=&rows=50&facet=price_type");
        //REQUEST
        JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
        activiteList.clear();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setOnInfoWindowClickListener(this);

        carte = map;
        LatLng latLng = new LatLng(latitude, longitude);

        //LOCATION MARKER
        marker = carte.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Votre position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        carte.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        //GET CURRENT LOCATION
        if (ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public String dateDescriptionAdapter(String date) {
        final String regex = "(?i)<br */?>";
        final String subst = "\n";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher dateDescriptionJava = pattern.matcher(date);

        date = dateDescriptionJava.replaceAll(subst);

        return date;
    }

    // Checks if a date is between two other dates
    boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        marker.remove();
        marker = carte.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Votre position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        carte.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        System.out.println("VOICI VOS LATITUDE ET LONGITUDE :" + latitude + "/" + longitude);

        generateURL.completeURL_forLocation(latitude, longitude);
        //REQUEST
        JsonObjectRequest request = jsonRequestPrice(generateURL.getFull_url(), datechoisie);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
        activiteList.clear();

        System.out.println("URL générée : " + generateURL.getFull_url());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        activiteDialog(marker.getSnippet());
    }

}