package com.bousaid.quefaireaparis.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bousaid.quefaireaparis.R;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView textView = rootView.findViewById(R.id.settings_text);

        String texte = "L'application de la démarche Open Data de la Ville de Paris.\nVous trouverez ici l'ensemble des évènements sur Paris en fonction de votre localisation, de vos goûts et de vos envies.\n\nLes données sont publiées par les services de la Ville et de ses partenaires sous licence ODbL.\n\nPFE 2021 - SUP GALILEE TELECOM3\n\nOzen Gul Bahar | Bousaid Farah";
        textView.setText(texte);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}