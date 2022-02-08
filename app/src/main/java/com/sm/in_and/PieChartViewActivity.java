package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import helpers.APIHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import helpers.OptionsHelper;
import models.ChartViewModel;
import models.FinancialDataGrouped;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PieChartViewActivity extends AppCompatActivity implements ChartOptionsDialog.ChartOptionsDialogListener {
    private ChartViewModel data = new ChartViewModel();
    private ChartOptions chartOptions;
    private ConstraintLayout clPieChartViewActivity;

    private ApiConnector apiConnector;

    private TextView tvChartInfo;
    private PieChart pcChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvChartInfo = findViewById(R.id.tvChartInfo);
        pcChart = findViewById(R.id.pcChart);
        clPieChartViewActivity = findViewById(R.id.clPieChartViewActivity);

        chartOptions = OptionsHelper.getChartOptions(this);

        apiConnector = APIHelper.Initialize();

        getData();
    }

    private void getData(){
        String url;
        if(chartOptions.getPeriod().equals("all")){
            url = String.format("api/financialdata/getall?scope=%s&aggregate=true", chartOptions.getChartType().substring(3));
        }else{
            url = String.format("api/financialdata/getall?startDate=%tF&endDate=%tF&scope=%s&aggregate=true", chartOptions.getStartDate(), chartOptions.getEndDate(), chartOptions.getChartType().substring(3));
        }
        Call<ResponseObj<ArrayList<FinancialDataGrouped>>> call = apiConnector.getGrouped("Bearer " + LoginHelper.getToken(this), url);
        call.enqueue(new Callback<ResponseObj<ArrayList<FinancialDataGrouped>>>() {
            @Override
            public void onResponse(Call<ResponseObj<ArrayList<FinancialDataGrouped>>> call, Response<ResponseObj<ArrayList<FinancialDataGrouped>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                data.setFinancialDataGrouped(response.body().getObj());
                setup();
            }

            @Override
            public void onFailure(Call<ResponseObj<ArrayList<FinancialDataGrouped>>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void setup(){
        if(data.getFinancialDataGrouped().isEmpty()){
            tvChartInfo.setText("Brak danych spełniających zadane kryteria.");
            pcChart.setVisibility(View.INVISIBLE);
        }else{
            if(chartOptions.getPeriod().equals("all")){
                tvChartInfo.setText("Wszystkie " + (chartOptions.getChartType().equals("pieinc") ? "przychody" : "wydatki") + " pogrupowane wg kategorii");
            }else{
                String startDate = LocalizationHelper.getLocalizedDate(chartOptions.getStartDate());
                String endDate = LocalizationHelper.getLocalizedDate(chartOptions.getEndDate());
                tvChartInfo.setText((chartOptions.getChartType().equals("pieinc") ? "Przychody" : "Wydatki") + " za okres od " + startDate + " do " + endDate + " pogrupowane wg kategorii");
            }
            setupChart();
        }

        clPieChartViewActivity.setVisibility(View.VISIBLE);
    }

    public void setupChart(){
        //setup chart
        //data
        ArrayList<PieEntry> chartData = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#87CEEB"));
        colors.add(Color.parseColor("#32CD32"));
        colors.add(Color.parseColor("#BA55D3"));
        colors.add(Color.parseColor("#F08080"));
        colors.add(Color.parseColor("#4682B4"));
        colors.add(Color.parseColor("#9ACD32"));
        colors.add(Color.parseColor("#40E0D0"));
        colors.add(Color.parseColor("#FF69B4"));
        colors.add(Color.parseColor("#F0E68C"));
        colors.add(Color.parseColor("#D2B48C"));
        int index = 0;
        for (FinancialDataGrouped item : data.getFinancialDataGrouped()){
            chartData.add(new PieEntry((chartOptions.getChartType().equals("pieexp") ? item.getAmount().multiply(new BigDecimal(-1)).floatValue() : item.getAmount().floatValue()), item.getCategoryName()));
            if(index > 9) {
                colors.add(((int) (Math.random() * 16777215)) | (0xFF << 24));
            }
            index++;
        }
        PieDataSet dataSet = new PieDataSet(chartData, "");
        //appearance
        dataSet.setColors(colors);
        dataSet.setValueFormatter(new ChartValueFormatter());
        dataSet.setValueTextSize(14f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData pieData = new PieData(dataSet);
        pcChart.getDescription().setEnabled(false);
        pcChart.setEntryLabelColor(Color.BLACK);
        //legend
        pcChart.getLegend().setTextSize(14f);
        //set data and refresh
        pcChart.setData(pieData);
        //pcChart.invalidate();
        pcChart.animateY(750);
        //chart setup complete
    }

    public void openChartOptionsDialog(View v){
        ChartOptionsDialog chartOptionsDialog = new ChartOptionsDialog();
        chartOptionsDialog.show(getSupportFragmentManager(),"ChartOptionsDialog");
    }

    @Override
    public void setChartOptions(String period, String startDate, String endDate, String chartType) {
        OptionsHelper.setChartOptions(this, period, startDate, endDate, chartType);
        if(chartType.equals("bartotals")){
            redirectToBarChartView();
        }else{
            refresh();
        }
    }

    private void refresh(){
        Intent i = new Intent(this, PieChartViewActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToBarChartView(){
        Intent i = new Intent(this, BarChartViewActivity.class);
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