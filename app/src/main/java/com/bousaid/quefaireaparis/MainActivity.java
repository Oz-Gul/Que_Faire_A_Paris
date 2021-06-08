package com.bousaid.quefaireaparis;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bousaid.quefaireaparis.Fragments.FavoritesFragment;
import com.bousaid.quefaireaparis.Fragments.HomeFragment;
import com.bousaid.quefaireaparis.Fragments.SearchFragment;
import com.bousaid.quefaireaparis.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    Fragment homeFragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    Fragment selectedFragment = null;
    Fragment searchFragment = new SearchFragment();
    Fragment favoritesFragment = new FavoritesFragment();
    Fragment settingsFragment = new SettingsFragment();
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                if (item.getItemId() == R.id.nav_home)
                    selectedFragment = homeFragment;
                else if (item.getItemId() == R.id.nav_search)
                    selectedFragment = searchFragment;
                else if (item.getItemId() == R.id.nav_favorites)
                    selectedFragment = favoritesFragment;
                else if (item.getItemId() == R.id.nav_settings)
                    selectedFragment = settingsFragment;
                else
                    selectedFragment = homeFragment;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                return true;
            };

}