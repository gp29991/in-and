package com.sm.in_and;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.reflect.TypeToken;

import helpers.APIHelper;
import helpers.AuthHelper;
import helpers.JSONHelper;
import helpers.LoginHelper;
import models.Category;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditCategoryActivity extends AppCompatActivity {
    private boolean editMode = false;
    private Category category = null;

    private ApiConnector apiConnector;

    private TextView tvAddEditCategory;
    private EditText etCategoryName;
    private TextView tvCategoryAlreadyExistsMessage;
    private Button btAddEditCategory;
    private ConstraintLayout clAddEditCategoryActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvAddEditCategory = findViewById(R.id.tvAddEditCategory);
        etCategoryName = findViewById(R.id.etCategoryName);
        tvCategoryAlreadyExistsMessage = findViewById(R.id.tvCategoryAlreadyExistsMessage);
        btAddEditCategory = findViewById(R.id.btAddEditCategory);
        clAddEditCategoryActivity = findViewById(R.id.clAddEditCategoryActivity);

        String categorySerialized = getIntent().getStringExtra("data");
        if(categorySerialized != null){
            category = JSONHelper.TryParseJson(categorySerialized, Category.class);
        }
        if(category != null){
            editMode = true;
        }

        apiConnector = APIHelper.Initialize();

        setup();
    }

    private void setup(){
        if(editMode){
            tvAddEditCategory.setText("Edycja kategorii");
            btAddEditCategory.setText("Zmień");
            etCategoryName.setText(category.getCategoryName());
        }
        clAddEditCategoryActivity.setVisibility(View.VISIBLE);
    }

    public void addOrEdit(View v) {
        tvCategoryAlreadyExistsMessage.setVisibility(View.INVISIBLE);

        boolean validationResult = validate();
        if (!validationResult) {
            return;
        }

        Category model = setModel();

        postOrPutModel(model);
    }

    private boolean validate(){
        boolean successfullyValidated = true;

        if(etCategoryName.length() == 0){
            etCategoryName.setError("Kategoria jest wymagana");
            successfullyValidated = false;
        }
        else if(etCategoryName.length() > 30){
            etCategoryName.setError("Nazwa kategorii nie może być dłuższa niż 30 znaków");
            successfullyValidated = false;
        }

        return successfullyValidated;
    }

    private Category setModel(){
        int categoryId = 0;
        if(editMode){
            categoryId = category.getCategoryId();
        }

        String categoryName = etCategoryName.getText().toString();

        String username;
        if(editMode){
            username = category.getUsername();
        }else{
            username = AuthHelper.authorize(LoginHelper.getToken(this), this);
            if(username == null){
                redirectToLogin();
            }
        }

        return new Category(categoryId, categoryName, username);
    }

    private void postOrPutModel(Category model){
        Call<ResponseObj<Category>> call;
        if(editMode){
            call = apiConnector.editCategory("Bearer " + LoginHelper.getToken(this), model, model.getCategoryId());
        }else{
            call = apiConnector.addCategory("Bearer " + LoginHelper.getToken(this), model);
        }

        call.enqueue(new Callback<ResponseObj<Category>>() {
            @Override
            public void onResponse(Call<ResponseObj<Category>> call, Response<ResponseObj<Category>> response) {
                if(!response.isSuccessful()){
                    if(response.code() == 400){
                        ResponseObj<Category> errorResponse;
                        try {
                            errorResponse = JSONHelper.TryParseJson(response.errorBody().string(), new TypeToken<ResponseObj<Category>>(){}.getType());
                        }catch (Exception e){
                            errorResponse = null;
                        }
                        if(errorResponse != null && errorResponse.getMessage() != null && errorResponse.getMessage().equals("CategoryAlreadyExists")){
                            tvCategoryAlreadyExistsMessage.setVisibility(View.VISIBLE);
                        }
                        else{
                            redirectToError();
                        }
                    }
                    else{
                        redirectToError();
                    }
                    return;
                }
                redirectSuccess();
            }

            @Override
            public void onFailure(Call<ResponseObj<Category>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void redirectSuccess(){
        Intent i = new Intent(this, CategoryListActivity.class);

        if(editMode) {
            i.putExtra("alert", "EditSuccess");
        }else{
            i.putExtra("alert", "AddSuccess");
        }

        i.putExtra("returnview", getIntent().getStringExtra("returnview"));
        startActivity(i);
        finish();
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, CategoryListActivity.class);
        i.putExtra("returnview", getIntent().getStringExtra("returnview"));
        startActivity(i);
        finish();
    }
}