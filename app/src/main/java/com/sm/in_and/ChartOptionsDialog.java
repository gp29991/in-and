package com.sm.in_and;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import helpers.LocalizationHelper;
import helpers.OptionsHelper;

public class ChartOptionsDialog extends DialogFragment implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    private RadioGroup rgChartType;
    private Spinner spPeriod;
    private TextView tvStartDateDescription;
    private TextView tvStartDate;
    private TextView tvEndDateDescription;
    private TextView tvEndDate;
    private ImageButton ibStartDatePicker;
    private ImageButton ibEndDatePicker;

    private boolean changingEndDate;

    private ChartOptionsDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.chart_options_dialog, null);

        rgChartType = view.findViewById(R.id.rgChartType);
        spPeriod = view.findViewById(R.id.spPeriod);
        tvStartDateDescription = view.findViewById(R.id.tvStartDateDescription);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDateDescription = view.findViewById(R.id.tvEndDateDescription);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        ibStartDatePicker = view.findViewById(R.id.ibStartDatePicker);
        ibEndDatePicker = view.findViewById(R.id.ibEndDatePicker);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, OptionsHelper.periodsText);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPeriod.setAdapter(adapter);
        spPeriod.setOnItemSelectedListener(this);

        setDates(-30);

        ibStartDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingEndDate = false;
                DialogFragment datePicker = new ViewSettingsDatePickerDialog();
                Bundle args = new Bundle();
                args.putString("date", (String) tvStartDate.getTag());
                args.putString("minimumdate", null);
                datePicker.setArguments(args);
                datePicker.show(getChildFragmentManager(), "DatePicker");
            }
        });

        ibEndDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingEndDate = true;
                DialogFragment datePicker = new ViewSettingsDatePickerDialog();
                Bundle args = new Bundle();
                args.putString("date", (String) tvEndDate.getTag());
                args.putString("minimumdate", (String) tvStartDate.getTag());
                datePicker.setArguments(args);
                datePicker.show(getChildFragmentManager(), "DatePicker");
            }
        });

        builder.setView(view)
                .setTitle("Opcje widoku")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String period = (String) spPeriod.getTag();
                        String startDate = (String) tvStartDate.getTag();
                        String endDate = (String) tvEndDate.getTag();
                        String chartType = "";
                        int checkedId = rgChartType.getCheckedRadioButtonId();
                        if(checkedId == R.id.rbBartotals){
                            chartType = "bartotals";
                        }else if(checkedId == R.id.rbPieinc){
                            chartType = "pieinc";
                        }else if(checkedId == R.id.rbPieexp){
                            chartType = "pieexp";
                        }
                        listener.setChartOptions(period, startDate, endDate, chartType);
                    }
                });

        return builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                setDates(-30);
                spPeriod.setTag("-30");
                break;
            case 1:
                setDates(-60);
                spPeriod.setTag("-60");
                break;
            case 2:
                setDates(-90);
                spPeriod.setTag("-90");
                break;
            case 3:
                setDates(30);
                spPeriod.setTag("30");
                break;
            case 4:
                spPeriod.setTag("other");
                break;
            case 5:
                spPeriod.setTag("all");
                break;
        }
        if(position == 4){
            tvStartDateDescription.setVisibility(View.VISIBLE);
            tvStartDate.setVisibility(View.VISIBLE);
            tvEndDateDescription.setVisibility(View.VISIBLE);
            tvEndDate.setVisibility(View.VISIBLE);
            ibStartDatePicker.setVisibility(View.VISIBLE);
            ibEndDatePicker.setVisibility(View.VISIBLE);
        }else{
            tvStartDateDescription.setVisibility(View.INVISIBLE);
            tvStartDate.setVisibility(View.INVISIBLE);
            tvEndDateDescription.setVisibility(View.INVISIBLE);
            tvEndDate.setVisibility(View.INVISIBLE);
            ibStartDatePicker.setVisibility(View.INVISIBLE);
            ibEndDatePicker.setVisibility(View.INVISIBLE);
        }
    }

    private void setDates(int daysToAdd){
        LocalDate startDate;
        LocalDate endDate;
        if(daysToAdd > 0) {
            startDate = LocalDate.now();
            endDate = LocalDate.now().plus(daysToAdd, ChronoUnit.DAYS);
        }else{
            startDate = LocalDate.now().plus(daysToAdd, ChronoUnit.DAYS);
            endDate = LocalDate.now();
        }
        tvStartDate.setText(LocalizationHelper.getLocalizedDate(startDate));
        tvStartDate.setTag(String.format("%tF", startDate));
        tvEndDate.setText(LocalizationHelper.getLocalizedDate(endDate));
        tvEndDate.setTag(String.format("%tF", endDate));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate startDate;
        LocalDate endDate;
        if(changingEndDate){
            startDate = LocalDate.parse((String) tvStartDate.getTag());
            endDate = LocalDate.of(year, month + 1, dayOfMonth);
            tvEndDate.setText(LocalizationHelper.getLocalizedDate(endDate));
            tvEndDate.setTag(String.format("%tF", endDate));
        }else{
            startDate = LocalDate.of(year, month + 1, dayOfMonth);
            endDate = LocalDate.parse((String) tvEndDate.getTag());
            tvStartDate.setText(LocalizationHelper.getLocalizedDate(startDate));
            tvStartDate.setTag(String.format("%tF", startDate));
        }
        if(startDate.isAfter(endDate)){
            tvEndDate.setText(LocalizationHelper.getLocalizedDate(startDate));
            tvEndDate.setTag(String.format("%tF", startDate));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (ChartOptionsDialogListener) context;
    }

    public interface ChartOptionsDialogListener{
        void setChartOptions(String period, String startDate, String endDate, String chartType);
    }
}
