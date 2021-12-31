package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import helpers.APIHelper;
import helpers.LoginHelper;
import models.Category;
import models.FinancialData;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActivity extends AppCompatActivity implements WarningDialog.WarningDialogListener {
    private ArrayList<Category> categories = new ArrayList<>();

    private ApiConnector apiConnector;

    private RecyclerView rvCategories;
    private ConstraintLayout clCategoryListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        String alert = getIntent().getStringExtra("alert");
        if(alert != null && alert.equals("AddSuccess")){
            Toast.makeText(this, "Kategoria została dodana pomyślnie.", Toast.LENGTH_LONG).show();
        }
        if(alert != null && alert.equals("EditSuccess")){
            Toast.makeText(this, "Kategoria została zmieniona pomyślnie.", Toast.LENGTH_LONG).show();
        }
        if(alert != null && alert.equals("DeleteSuccess")){
            Toast.makeText(this, "Kategoria oraz wszystkie przypisane do niej wpisy zostały usunięte pomyślnie.", Toast.LENGTH_LONG).show();
        }
        /*if(alert != null && alert.equals("ClearSuccess")){
            Toast.makeText(this, "Wpisy przypisane do kategorii domyślnej zostały usunięte pomyślnie.", Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        rvCategories = findViewById(R.id.rvCategories);
        clCategoryListActivity = findViewById(R.id.clCategoryListActivity);

        apiConnector = APIHelper.Initialize();

        getCategories();
    }

    private void getCategories(){
        Call<ResponseObj<ArrayList<Category>>> call = apiConnector.getCategories("Bearer " + LoginHelper.getToken(this));
        call.enqueue(new Callback<ResponseObj<ArrayList<Category>>>() {
            @Override
            public void onResponse(Call<ResponseObj<ArrayList<Category>>> call, Response<ResponseObj<ArrayList<Category>>> response) {
                if(!response.isSuccessful() || response.body() == null){
                    redirectToError();
                    return;
                }
                categories = response.body().getObj();
                categories.sort(Comparator.comparing(Category::getCategoryName));
                setup();
            }

            @Override
            public void onFailure(Call<ResponseObj<ArrayList<Category>>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void setup(){
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter();
        categoryListAdapter.setData(categories);
        rvCategories.setAdapter(categoryListAdapter);

        categoryListAdapter.setCategoryListAdapterListener(new CategoryListAdapter.CategoryListAdapterListener() {
            @Override
            public void onEditClick(int position) {
                redirectToEdit(position);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteClearConfirmationDialog(position);
            }
        });
        clCategoryListActivity.setVisibility(View.VISIBLE);
    }

    public void showCategoryInfoDialog(View v){
        CategoryInfoDialog categoryInfoDialog = new CategoryInfoDialog();
        categoryInfoDialog.show(getSupportFragmentManager(), "CategoryInfoDialog");
    }

    private void openDeleteClearConfirmationDialog(int position){
        WarningDialog warningDialog = new WarningDialog();
        Bundle args = new Bundle();
        Category categoryForDeletion = categories.get(position);
        String message;
        if(categoryForDeletion.getCategoryId() == 0){
            message = "Czy na pewno chcesz usunąć wszystkie wpisy przypisane do kategorii domyślnej " + categoryForDeletion.getCategoryName() + "?";
        }else{
            message = "Czy na pewno chcesz usunąć kategorię " + categoryForDeletion.getCategoryName() + " oraz wszystkie przypisane do niej wpisy?";
        }
        args.putString("message", message);
        args.putString("warning", "Tego działania nie da się cofnąć!");
        args.putInt("position", position);
        warningDialog.setArguments(args);
        warningDialog.show(getSupportFragmentManager(),"DeleteClearConfirmationDialog");
    }

    @Override
    public void WarningDialogConfirmed(int position) {
        deleteClearCategory(position);
    }

    private void deleteClearCategory(int position){
        Call<ResponseObj<Category>> call;
        if(categories.get(position).getCategoryId() == 0){
            call = apiConnector.clearCategory("Bearer " + LoginHelper.getToken(this), categories.get(position).getCategoryName());
        }else{
            call = apiConnector.deleteCategory("Bearer " + LoginHelper.getToken(this), categories.get(position).getCategoryId());
        }

        call.enqueue(new Callback<ResponseObj<Category>>() {
            @Override
            public void onResponse(Call<ResponseObj<Category>> call, Response<ResponseObj<Category>> response) {
                if(!response.isSuccessful()){
                    redirectToError();
                    return;
                }
                if(categories.get(position).getCategoryId() == 0){
                    showClearSuccessMessage();
                }else {
                    refreshAfterDelete();
                }
            }

            @Override
            public void onFailure(Call<ResponseObj<Category>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void showClearSuccessMessage(){
        Toast.makeText(this, "Wpisy przypisane do kategorii domyślnej zostały usunięte pomyślnie.", Toast.LENGTH_LONG).show();
    }

    private void refreshAfterDelete(){
        Intent i = new Intent(this, CategoryListActivity.class);
        i.putExtra("returnview", getIntent().getStringExtra("returnview"));
        i.putExtra("alert", "DeleteSuccess");
        startActivity(i);
        finish();
    }

    private void redirectToEdit(int position){
        Intent i = new Intent(this, AddEditCategoryActivity.class);
        i.putExtra("returnview", getIntent().getStringExtra("returnview"));
        String dataForPassing = new Gson().toJson(categories.get(position));
        i.putExtra("data", dataForPassing);
        startActivity(i);
        finish();
    }

    public void goToAdd(View v){
        Intent i = new Intent(this, AddEditCategoryActivity.class);
        i.putExtra("returnview", getIntent().getStringExtra("returnview"));
        startActivity(i);
        finish();
    }

    private void redirectToError(){
        Intent i = new Intent(this, ErrorActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i;
        if(getIntent().getStringExtra("returnview") !=null && getIntent().getStringExtra("returnview").equals("cat")) {
            i = new Intent(this, CategoryViewActivity.class);
        }else if(getIntent().getStringExtra("returnview") !=null && getIntent().getStringExtra("returnview").equals("det")){
            i = new Intent(this, DetailViewActivity.class);
        }else{
            i = new Intent(this, MainPageActivity.class);
        }
        startActivity(i);
        finish();
    }
}