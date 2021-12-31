package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import helpers.AuthHelper;
import helpers.JSONHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import models.FinancialData;

public class UpcomingSignificantExpensesViewActivity extends AppCompatActivity {

    private ArrayList<FinancialData> data;

    private TextView tvUpcomingSIgnificantExpensesInfo;
    private RecyclerView rvUpcomingSignificantExpenses;
    private ConstraintLayout clUpcomingSignificantExpensesViewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_significant_expenses_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvUpcomingSIgnificantExpensesInfo = findViewById(R.id.tvUpcomingSignificantExpensesInfo);
        rvUpcomingSignificantExpenses = findViewById(R.id.rvUpcomingSignificantExpenses);
        clUpcomingSignificantExpensesViewActivity = findViewById(R.id.clUpcomingSignificantExpensesViewActivity);

        String dataSerialized = getIntent().getStringExtra("data");
        if(dataSerialized != null){
            data = JSONHelper.TryParseJson(dataSerialized, new TypeToken<ArrayList<FinancialData>>(){}.getType());
        }
        if(data == null || data.isEmpty()){
            redirectToMain();
        }

        setup();
    }

    private void setup(){
        String startDate = LocalizationHelper.getLocalizedDate(LocalDate.now());
        String endDate = LocalizationHelper.getLocalizedDate(LocalDate.now().plus(7, ChronoUnit.DAYS));
        tvUpcomingSIgnificantExpensesInfo.setText("Nadchodzące większe wydatki w okresie od " + startDate + " do " + endDate);

        rvUpcomingSignificantExpenses.setLayoutManager(new LinearLayoutManager(this));
        UpcomingSignificantExpensesAdapter upcomingSignificantExpensesAdapter = new UpcomingSignificantExpensesAdapter();
        upcomingSignificantExpensesAdapter.setData(data);
        rvUpcomingSignificantExpenses.setAdapter(upcomingSignificantExpensesAdapter);

        clUpcomingSignificantExpensesViewActivity.setVisibility(View.VISIBLE);
    }

    private void redirectToMain(){
        Intent i = new Intent(this, MainPageActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        redirectToMain();
    }
}