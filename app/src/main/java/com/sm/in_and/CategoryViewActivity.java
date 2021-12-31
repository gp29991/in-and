package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import helpers.APIHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import helpers.OptionsHelper;
import models.FinancialData;
import models.FinancialDataGrouped;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewActivity extends AppCompatActivity implements SortOptionsDialogCategory.SortOptionsDialogCategoryListener, ViewOptionsDialog.ViewOptionsDialogListener {
    private ArrayList<FinancialDataGrouped> data = new ArrayList<>();
    private Options options;
    private SortOptions sortOptions;
    private CategoryViewAdapter categoryViewAdapter;
    private ConstraintLayout clCategoryViewActivity;

    private ApiConnector apiConnector;

    private TextView tvDataInfo;
    private RecyclerView rvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);

        String alert = getIntent().getStringExtra("alert");
        if(alert != null && alert.equals("AddSuccess")){
            Toast.makeText(this, "Wpis został dodany pomyślnie.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvDataInfo = findViewById(R.id.tvDataInfo);
        rvData = findViewById(R.id.rvData);
        clCategoryViewActivity = findViewById(R.id.clCategoryViewActivity);

        options = OptionsHelper.getOptions(this, "cat");

        apiConnector = APIHelper.Initialize();

        getData();
    }

    private void getData(){
        String url;
        if(options.getPeriod().equals("all")){
            url = String.format("api/financialdata/getall?scope=%s&aggregate=true", options.getViewScope());
        }else{
            url = String.format("api/financialdata/getall?startDate=%tF&endDate=%tF&scope=%s&aggregate=true", options.getStartDate(), options.getEndDate(), options.getViewScope());
        }
        Call<ResponseObj<ArrayList<FinancialDataGrouped>>> call = apiConnector.getGrouped("Bearer " + LoginHelper.getToken(this), url);
        call.enqueue(new Callback<ResponseObj<ArrayList<FinancialDataGrouped>>>() {
            @Override
            public void onResponse(Call<ResponseObj<ArrayList<FinancialDataGrouped>>> call, Response<ResponseObj<ArrayList<FinancialDataGrouped>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                data = response.body().getObj();
                sort(false);
            }

            @Override
            public void onFailure(Call<ResponseObj<ArrayList<FinancialDataGrouped>>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void sort(boolean resort){
        sortOptions = OptionsHelper.getSortOptions(this, options.getViewType());
        switch (sortOptions.getSortBy())
        {
            case "amount":
                data.sort(Comparator.comparing(FinancialDataGrouped::getAmount));
                break;
            default:
                data.sort(Comparator.comparing(FinancialDataGrouped::getCategoryName));
                break;
        }
        if (sortOptions.getSortType().equals("desc"))
        {
            Collections.reverse(data);
        }

        if(resort){
            categoryViewAdapter.updateData(data);
        }else{
            setup();
        }
    }

    private void setup(){
        if(data.isEmpty()){
            tvDataInfo.setText("Brak danych spełniających zadane kryteria.");
        }else{
            String incOrExp;
            switch (options.getViewScope())
            {
                case "inc":
                    incOrExp = "Przychody";
                    break;
                case "exp":
                    incOrExp = "Wydatki";
                    break;
                default:
                    incOrExp = "Przychody i wydatki";
            }

            if(options.getPeriod().equals("all")){
                tvDataInfo.setText("Wszystkie " + incOrExp.toLowerCase() + " pogrupowane wg kategorii");
            }else{
                String startDate = LocalizationHelper.getLocalizedDate(options.getStartDate());
                String endDate = LocalizationHelper.getLocalizedDate(options.getEndDate());
                tvDataInfo.setText(incOrExp + " za okres od " + startDate + " do " + endDate + " pogrupowane wg kategorii");
            }
        }

        rvData.setLayoutManager(new LinearLayoutManager(this));
        categoryViewAdapter = new CategoryViewAdapter();
        categoryViewAdapter.setData(data);
        rvData.setAdapter(categoryViewAdapter);
        clCategoryViewActivity.setVisibility(View.VISIBLE);
    }

    public void openSortOptionsDialog(View v){
        SortOptionsDialogCategory sortOptionsDialog = new SortOptionsDialogCategory();
        sortOptionsDialog.show(getSupportFragmentManager(),"SortOptionsDialog");
    }

    @Override
    public void setSortOptions(String sortBy, String sortType) {
        OptionsHelper.setSortOptions(this, sortBy, sortType);
        sort(true);
    }

    public void openViewOptionsDialog(View v){
        ViewOptionsDialog viewOptionsDialog = new ViewOptionsDialog();
        viewOptionsDialog.show(getSupportFragmentManager(),"ViewOptionsDialog");
    }

    @Override
    public void setViewOptions(String period, String startDate, String endDate, String viewScope, String viewType) {
        OptionsHelper.setOptions(this, period, startDate, endDate, viewScope, viewType);
        if(viewType.equals("det")){
            redirectToDetailView();
        }else{
            refresh();
        }
    }

    private void refresh(){
        Intent i = new Intent(this, CategoryViewActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToDetailView(){
        OptionsHelper.clearSortOptions(this);
        Intent i = new Intent(this, DetailViewActivity.class);
        startActivity(i);
        finish();
    }

    public void goToAdd(View v){
        Intent i = new Intent(this, AddEditActivity.class);
        i.putExtra("returnview", "cat");
        startActivity(i);
        finish();
    }

    public void goToCategoryList(View v){
        Intent i = new Intent(this, CategoryListActivity.class);
        i.putExtra("returnview", "cat");
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