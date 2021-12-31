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

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import helpers.APIHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import helpers.OptionsHelper;
import models.FinancialData;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewActivity extends AppCompatActivity implements SortOptionsDialog.SortOptionsDialogListener, ViewOptionsDialog.ViewOptionsDialogListener, WarningDialog.WarningDialogListener {
    private ArrayList<FinancialData> data = new ArrayList<>();
    private Options options;
    private SortOptions sortOptions;
    private DetailViewAdapter detailViewAdapter;
    private ConstraintLayout clDetailViewActivity;

    private ApiConnector apiConnector;

    private TextView tvDataInfo;
    private RecyclerView rvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        String alert = getIntent().getStringExtra("alert");
        if(alert != null && alert.equals("AddSuccess")){
            Toast.makeText(this, "Wpis został dodany pomyślnie.", Toast.LENGTH_LONG).show();
        }
        if(alert != null && alert.equals("EditSuccess")){
            Toast.makeText(this, "Wpis został zmieniony pomyślnie.", Toast.LENGTH_LONG).show();
        }
        if(alert != null && alert.equals("DeleteSuccess")){
            Toast.makeText(this, "Wpis został usunięty pomyślnie.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvDataInfo = findViewById(R.id.tvDataInfo);
        rvData = findViewById(R.id.rvData);
        clDetailViewActivity = findViewById(R.id.clDetailViewActivity);

        options = OptionsHelper.getOptions(this, "det");

        apiConnector = APIHelper.Initialize();

        getData();
    }

    private void getData(){
        String url;
        if(options.getPeriod().equals("all")){
            url = String.format("api/financialdata/getall?scope=%s", options.getViewScope());
        }else{
            url = String.format("api/financialdata/getall?startDate=%tF&endDate=%tF&scope=%s", options.getStartDate(), options.getEndDate(), options.getViewScope());
        }
        Call<ResponseObj<ArrayList<FinancialData>>> call = apiConnector.getAll("Bearer " + LoginHelper.getToken(this), url);
        call.enqueue(new Callback<ResponseObj<ArrayList<FinancialData>>>() {
            @Override
            public void onResponse(Call<ResponseObj<ArrayList<FinancialData>>> call, Response<ResponseObj<ArrayList<FinancialData>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                data = response.body().getObj();
                sort(false);
            }

            @Override
            public void onFailure(Call<ResponseObj<ArrayList<FinancialData>>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void sort(boolean resort){
        sortOptions = OptionsHelper.getSortOptions(this, options.getViewType());
        switch (sortOptions.getSortBy())
        {
            case "amount":
                data.sort(Comparator.comparing(FinancialData::getAmount));
                break;
            case "categoryname":
                data.sort(Comparator.comparing(FinancialData::getCategoryName));
                break;
            case "name":
                data.sort(Comparator.comparing(FinancialData::getName));
                break;
            default:
                data.sort(Comparator.comparing(FinancialData::getDate));
                break;
        }
        if (sortOptions.getSortType().equals("desc"))
        {
            Collections.reverse(data);
        }

        if(resort){
            detailViewAdapter.updateData(data);
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
                tvDataInfo.setText("Wszystkie " + incOrExp.toLowerCase());
            }else{
                String startDate = LocalizationHelper.getLocalizedDate(options.getStartDate());
                String endDate = LocalizationHelper.getLocalizedDate(options.getEndDate());
                tvDataInfo.setText(incOrExp + " za okres od " + startDate + " do " + endDate);
            }
        }

        rvData.setLayoutManager(new LinearLayoutManager(this));
        detailViewAdapter = new DetailViewAdapter();
        detailViewAdapter.setData(data);
        rvData.setAdapter(detailViewAdapter);

        detailViewAdapter.setDetailViewAdapterListener(new DetailViewAdapter.DetailViewAdapterListener() {
            @Override
            public void onEditClick(int position) {
                redirectToEdit(position);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteConfirmationDialog(position);
            }
        });
        clDetailViewActivity.setVisibility(View.VISIBLE);
    }

    public void openSortOptionsDialog(View v){
        SortOptionsDialog sortOptionsDialog = new SortOptionsDialog();
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
        if(viewType.equals("cat")){
            redirectToCategoryView();
        }else{
            refresh();
        }
    }

    private void openDeleteConfirmationDialog(int position){
        WarningDialog warningDialog = new WarningDialog();
        Bundle args = new Bundle();
        FinancialData itemForDeletion = data.get(position);
        String description = itemForDeletion.getDescription();
        if(description == null){
            description = "(Brak opisu)";
        }
        String message = "Czy na pewno chcesz usunąć następujący wpis:\n\n"
                + "Kwota: " + String.format(new Locale("pl","PL"), "%.2f", itemForDeletion.getAmount()) + "\n"
                + "Data: " + LocalizationHelper.getLocalizedDate(itemForDeletion.getDate()) + "\n"
                + "Kategoria: " + itemForDeletion.getCategoryName() + "\n"
                + "Nazwa: " + itemForDeletion.getName() + "\n"
                + "Opis: " + description;
        args.putString("message", message);
        args.putString("warning", "Tego działania nie da się cofnąć!");
        args.putInt("position", position);
        warningDialog.setArguments(args);
        warningDialog.show(getSupportFragmentManager(),"DeleteConfirmationDialog");
    }

    @Override
    public void WarningDialogConfirmed(int position) {
        deleteItem(position);
    }

    private void deleteItem(int position){
        Call<ResponseObj<FinancialData>> call = apiConnector.deleteEntry("Bearer " + LoginHelper.getToken(this), data.get(position).getFinancialDataId());
        call.enqueue(new Callback<ResponseObj<FinancialData>>() {
            @Override
            public void onResponse(Call<ResponseObj<FinancialData>> call, Response<ResponseObj<FinancialData>> response) {
                if(!response.isSuccessful()){
                    redirectToError();
                    return;
                }
                refreshAfterDelete();
            }

            @Override
            public void onFailure(Call<ResponseObj<FinancialData>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void refresh(){
        Intent i = new Intent(this, DetailViewActivity.class);
        startActivity(i);
        finish();
    }

    private void refreshAfterDelete(){
        Intent i = new Intent(this, DetailViewActivity.class);
        i.putExtra("alert", "DeleteSuccess");
        startActivity(i);
        finish();
    }

    private void redirectToCategoryView(){
        OptionsHelper.clearSortOptions(this);
        Intent i = new Intent(this, CategoryViewActivity.class);
        startActivity(i);
        finish();
    }

    public void goToAdd(View v){
        Intent i = new Intent(this, AddEditActivity.class);
        i.putExtra("returnview", "det");
        startActivity(i);
        finish();
    }

    public void redirectToEdit(int position){
        Intent i = new Intent(this, AddEditActivity.class);
        i.putExtra("returnview", "det");
        String dataForPassing = new Gson().toJson(data.get(position));
        i.putExtra("data", dataForPassing);
        startActivity(i);
        finish();
    }

    public void goToCategoryList(View v){
        Intent i = new Intent(this, CategoryListActivity.class);
        i.putExtra("returnview", "det");
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