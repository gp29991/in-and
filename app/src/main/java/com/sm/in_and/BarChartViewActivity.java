package com.sm.in_and;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import helpers.APIHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import helpers.OptionsHelper;
import models.ChartViewModel;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarChartViewActivity extends AppCompatActivity implements ChartOptionsDialog.ChartOptionsDialogListener {
    private ChartViewModel data = new ChartViewModel();
    private ChartOptions chartOptions;
    private ConstraintLayout clBarChartViewActivity;

    private ApiConnector apiConnector;

    private TextView tvChartInfo;
    private BarChart bcChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvChartInfo = findViewById(R.id.tvChartInfo);
        bcChart = findViewById(R.id.bcChart);
        clBarChartViewActivity = findViewById(R.id.clBarChartViewActivity);

        chartOptions = OptionsHelper.getChartOptions(this);

        apiConnector = APIHelper.Initialize();

        getData();
    }

    private void getData(){
        String url;
        if(chartOptions.getPeriod().equals("all")){
            url = "api/financialdata/getall?total=true";
        }else{
            url = String.format("api/financialdata/getall?startDate=%tF&endDate=%tF&total=true", chartOptions.getStartDate(), chartOptions.getEndDate());
        }
        Call<ResponseObj<HashMap<String, BigDecimal>>> call = apiConnector.getTotals("Bearer " + LoginHelper.getToken(this), url);
        call.enqueue(new Callback<ResponseObj<HashMap<String, BigDecimal>>>() {
            @Override
            public void onResponse(Call<ResponseObj<HashMap<String, BigDecimal>>> call, Response<ResponseObj<HashMap<String, BigDecimal>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                data.setTotalInc(response.body().getObj().get("incsum"));
                data.setTotalExp(response.body().getObj().get("expsum"));
                setup();
            }

            @Override
            public void onFailure(Call<ResponseObj<HashMap<String, BigDecimal>>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void setup(){
        if(data.getTotalInc().equals(new BigDecimal(0)) && data.getTotalExp().equals(new BigDecimal(0))){
            tvChartInfo.setText("W zadanym okresie nie występują ani przychody, ani wydatki.");
            bcChart.setVisibility(View.INVISIBLE);
        }else{
            if(chartOptions.getPeriod().equals("all")){
                tvChartInfo.setText("Całkowite saldo przychodów i wydatków");
            }else{
                String startDate = LocalizationHelper.getLocalizedDate(chartOptions.getStartDate());
                String endDate = LocalizationHelper.getLocalizedDate(chartOptions.getEndDate());
                tvChartInfo.setText("Saldo przychodów i wydatków za okres od " + startDate + " do " + endDate);
            }
            setupChart();
        }

        clBarChartViewActivity.setVisibility(View.VISIBLE);
    }

    public void setupChart(){
        //setup chart
        //data
        ArrayList<BarEntry> chartData = new ArrayList<>();
        chartData.add(new BarEntry(0f, data.getTotalInc().floatValue()));
        chartData.add(new BarEntry(1f, data.getTotalExp().multiply(new BigDecimal(-1)).floatValue()));
        BarDataSet dataSet = new BarDataSet(chartData, "");
        //appearance
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#E91E63"));
        dataSet.setValueFormatter(new ChartValueFormatter());
        dataSet.setValueTextSize(14f);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        bcChart.setFitBars(true);
        bcChart.getDescription().setEnabled(false);
        bcChart.getXAxis().setEnabled(false);
        bcChart.getAxisRight().setEnabled(false);
        bcChart.getAxisLeft().setAxisMaximum(Math.max(data.getTotalInc().floatValue(), data.getTotalExp().multiply(new BigDecimal(-1)).floatValue()));
        bcChart.getAxisLeft().setAxisMinimum(0f);
        bcChart.getAxisLeft().setTextSize(14f);
        bcChart.getAxisLeft().setGridLineWidth(0.8f);
        bcChart.getAxisLeft().setAxisLineWidth(0.8f);
        //legend
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("Przychody", Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, Color.parseColor("#4CAF50")));
        legendEntries.add(new LegendEntry("Wydatki", Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, Color.parseColor("#E91E63")));
        bcChart.getLegend().setCustom(legendEntries);
        bcChart.getLegend().setTextSize(14f);
        bcChart.getLegend().setXEntrySpace(15f);
        //set data and refresh
        bcChart.setData(barData);
        //bcChart.invalidate();
        bcChart.animateY(750);
        //chart setup complete
    }

    public void openChartOptionsDialog(View v){
        ChartOptionsDialog chartOptionsDialog = new ChartOptionsDialog();
        chartOptionsDialog.show(getSupportFragmentManager(),"ChartOptionsDialog");
    }

    @Override
    public void setChartOptions(String period, String startDate, String endDate, String chartType) {
        OptionsHelper.setChartOptions(this, period, startDate, endDate, chartType);
        if(chartType.equals("pieinc") || chartType.equals("pieexp")){
            redirectToPieChartView();
        }else{
            refresh();
        }
    }

    private void refresh(){
        Intent i = new Intent(this, BarChartViewActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToPieChartView(){
        Intent i = new Intent(this, PieChartViewActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToError(){
        Intent i = new Intent(this, ErrorActivity.class);
        startActivity(i);
        finish();
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