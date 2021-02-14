package com.bousaid.quefaireaparisv2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private TextView dateText;

    SimpleDateFormat format;
    String strDate;
    Calendar calendar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dateText = view.findViewById(R.id.button_date);
        view.findViewById(R.id.button_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        return view;
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SearchFragment.this.getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        format = new SimpleDateFormat("EEE d MMM");
        calendar = Calendar.getInstance();
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month+1) + "/" + year;
        calendar.set(year, month, dayOfMonth);
        strDate = format.format(calendar.getTime());
        dateText.setText(strDate);

    }
}