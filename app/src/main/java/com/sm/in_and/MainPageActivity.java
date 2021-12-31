package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import helpers.APIHelper;
import helpers.AuthHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import helpers.OptionsHelper;
import models.FinancialData;
import models.MainPageViewModel;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity extends AppCompatActivity {

    private String username;
    private MainPageViewModel data = new MainPageViewModel();

    private TextView tvUsername;
    private TextView tvIncome;
    private TextView tvExpenditure;
    private TextView tvUpcomingSignificantExpenses;
    private Button btShowUpcomingSignificantExpenses;
    private ConstraintLayout clMainPageActivity;

    private ApiConnector apiConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        OptionsHelper.clearOptions(this);
        OptionsHelper.clearSortOptions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        username = AuthHelper.authorize(LoginHelper.getToken(this), this);
        if(username == null){
            redirectToLogin();
        }

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText("Witaj, " + username);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpenditure = findViewById(R.id.tvExpenditure);
        tvUpcomingSignificantExpenses = findViewById(R.id.tvUpcomingSignificantExpenses);
        btShowUpcomingSignificantExpenses = findViewById(R.id.btShowUpcomingSignificantExpenses);
        clMainPageActivity = findViewById(R.id.clMainPageActivity);

        apiConnector = APIHelper.Initialize();

        getData1();
    }

    private void getData1(){

        Call<ResponseObj<HashMap<String, BigDecimal>>> call = apiConnector.getTotals("Bearer " + LoginHelper.getToken(this),String.format("api/financialdata/getall?startDate=%tF&endDate=%tF&total=true",LocalDate.now().minus(30,ChronoUnit.DAYS),LocalDate.now()));
        call.enqueue(new Callback<ResponseObj<HashMap<String, BigDecimal>>>() {
            @Override
            public void onResponse(Call<ResponseObj<HashMap<String, BigDecimal>>> call, Response<ResponseObj<HashMap<String, BigDecimal>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                data.setTotalInc(response.body().getObj().get("incsum"));
                data.setTotalExp(response.body().getObj().get("expsum"));
                getData2();
            }

            @Override
            public void onFailure(Call<ResponseObj<HashMap<String, BigDecimal>>> call, Throwable t) {
                redirectToError();
            }
        });

    }

    private void getData2(){

        Call<ResponseObj<ArrayList<FinancialData>>> call2 = apiConnector.getAll("Bearer " + LoginHelper.getToken(this), String.format("api/financialdata/getall?startDate=%tF&endDate=%tF&scope=exp&minamount=1000",LocalDate.now(),LocalDate.now().plus(7, ChronoUnit.DAYS)));
        call2.enqueue(new Callback<ResponseObj<ArrayList<FinancialData>>>() {
            @Override
            public void onResponse(Call<ResponseObj<ArrayList<FinancialData>>> call, Response<ResponseObj<ArrayList<FinancialData>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                data.setUpcomingSignificantExpenses(response.body().getObj());
                Collections.sort(data.getUpcomingSignificantExpenses(), Comparator.comparing(FinancialData::getDate));
                setup();
            }

            @Override
            public void onFailure(Call<ResponseObj<ArrayList<FinancialData>>> call, Throwable t) {
                redirectToError();
            }
        });

    }

    private void setup(){
        tvIncome.setText(String.format(new Locale("pl","PL"), "%.2f", data.getTotalInc()));
        tvExpenditure.setText(String.format(new Locale("pl","PL"), "%.2f", data.getTotalExp()));
        if(data.getUpcomingSignificantExpenses().isEmpty()){
            tvUpcomingSignificantExpenses.setTextColor(Color.parseColor("#8A000000"));
            tvUpcomingSignificantExpenses.setText("Brak większych wydatków zaplanowanych w ciągu najbliższych 7 dni.");
            btShowUpcomingSignificantExpenses.setVisibility(View.INVISIBLE);
        }
        else{
            tvUpcomingSignificantExpenses.setTextColor(Color.parseColor("#E91E63"));
            tvUpcomingSignificantExpenses.setText("Uwaga! W ciągu najbliższych 7 dni występują większe wydatki!");
            btShowUpcomingSignificantExpenses.setVisibility(View.VISIBLE);
        }
        clMainPageActivity.setVisibility(View.VISIBLE);
    }

    public void goToSignificantExpensesView(View v){
        Intent i = new Intent(this, UpcomingSignificantExpensesViewActivity.class);
        String dataForPassing = new Gson().toJson(data.getUpcomingSignificantExpenses());
        i.putExtra("data", dataForPassing);
        startActivity(i);
        finish();
    }

    public void goToDetailView(View v){
        Intent i = new Intent(this, DetailViewActivity.class);
        startActivity(i);
        finish();
    }

    public void goToCategoryList(View v){
        Intent i = new Intent(this, CategoryListActivity.class);
        i.putExtra("returnview", "main");
        startActivity(i);
        finish();
    }

    public void logout(View v){
        LoginHelper.logout(this);
        redirectToLogin();
    }

    private void redirectToLogin(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToError(){
        Intent i = new Intent(this, ErrorActivity.class);
        startActivity(i);
        finish();
    }
}