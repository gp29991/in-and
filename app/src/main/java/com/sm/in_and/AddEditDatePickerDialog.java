package com.sm.in_and;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

public class AddEditDatePickerDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String date = getArguments().getString("date");
        int year, month, day;
        if(date == null){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            LocalDate dateConverted = LocalDate.parse(date);
            year = dateConverted.getYear();
            month = dateConverted.getMonthValue() - 1;
            day = dateConverted.getDayOfMonth();
        }

        return new android.app.DatePickerDialog(getActivity(), (android.app.DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
