package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import helpers.APIHelper;
import helpers.AuthHelper;
import helpers.JSONHelper;
import helpers.LocalizationHelper;
import helpers.LoginHelper;
import helpers.OptionsHelper;
import models.Category;
import models.FinancialData;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private boolean editMode = false;
    private ArrayList<String> categories = new ArrayList<>();
    private FinancialData data = null;

    private ApiConnector apiConnector;

    private TextView tvAddEdit;
    private EditText etAmount;
    private RadioGroup rgType;
    private TextView tvDate;
    private EditText etName;
    private EditText etDescription;
    private Spinner spCategory;
    private Button btAddEdit;
    private ConstraintLayout clAddEditActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvAddEdit = findViewById(R.id.tvAddEdit);
        etAmount = findViewById(R.id.etAmount);
        rgType = findViewById(R.id.rgType);
        tvDate = findViewById(R.id.tvDate);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        spCategory = findViewById(R.id.spCategory);
        btAddEdit = findViewById(R.id.btAddEdit);
        clAddEditActivity = findViewById(R.id.clAddEditActivity);

        String dataSerialized = getIntent().getStringExtra("data");
        if(dataSerialized != null){
            data = JSONHelper.TryParseJson(dataSerialized, FinancialData.class);
        }
        if(data != null){
            editMode = true;
        }

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
                ArrayList<Category> retrievedCategories = response.body().getObj();
                for (Category category : retrievedCategories){
                    categories.add(category.getCategoryName());
                }
                Collections.sort(categories);
                setup();
            }

            @Override
            public void onFailure(Call<ResponseObj<ArrayList<Category>>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private void setup(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
        if(editMode){
            tvAddEdit.setText("Edycja wpisu");
            btAddEdit.setText("Zapisz");
            etAmount.setText(String.format(new Locale("pl","PL"), "%.2f", data.getAmount().abs()));
            if(data.getAmount().compareTo(new BigDecimal(0)) < 0){
                rgType.check(R.id.rbExp);
            }else{
                rgType.check(R.id.rbInc);
            }
            tvDate.setText(LocalizationHelper.getLocalizedDate(data.getDate()));
            tvDate.setTag(data.getDate().substring(0,10));
            etName.setText(data.getName());
            etDescription.setText(data.getDescription());
            spCategory.setSelection(adapter.getPosition(data.getCategoryName()));
        }else{
            tvDate.setText(LocalizationHelper.getLocalizedDate(LocalDate.now()));
            tvDate.setTag(String.format("%tF", LocalDate.now()));
        }
        clAddEditActivity.setVisibility(View.VISIBLE);
    }

    public void addOrEdit(View v){
        boolean validationResult = validate();
        if(!validationResult){
            return;
        }

        FinancialData model = setModel();

        postOrPutModel(model);
    }

    private boolean validate(){
        boolean successfullyValidated = true;

        if(etAmount.length() == 0){
            etAmount.setError("Kwota jest wymagana");
            successfullyValidated = false;
        }
        else if(!etAmount.getText().toString().matches("^[0-9]{1,7},[0-9][0-9]$")){
            etAmount.setError("Należy wprowadzić kwotę w zakresie od 0,00 do 9999999,99 w formacie \"0,00\"");
            successfullyValidated = false;
        }

        if(etName.length() == 0){
            etName.setError("Nazwa jest wymagana");
            successfullyValidated = false;
        }
        else if(etName.length() > 30){
            etName.setError("Nazwa nie może być dłuższa niż 30 znaków");
            successfullyValidated = false;
        }

        if(etDescription.length() > 90){
            etDescription.setError("Opis nie może być dłuższy niż 90 znaków");
            successfullyValidated = false;
        }

        return successfullyValidated;
    }

    private FinancialData setModel(){
        int financialDataId = 0;
        if(editMode){
            financialDataId = data.getFinancialDataId();
        }

        String amountAsString = etAmount.getText().toString().replace(',', '.');
        BigDecimal amount = new BigDecimal(amountAsString);
        if(rgType.getCheckedRadioButtonId() == R.id.rbExp){
            amount = amount.multiply(new BigDecimal(-1));
        }

        String date = (String) tvDate.getTag();

        String name = etName.getText().toString();

        String description = etDescription.getText().toString();
        if(description.equals("")){
            description = null;
        }

        String username;
        if(editMode){
            username = data.getUsername();
        }else{
            username = AuthHelper.authorize(LoginHelper.getToken(this), this);
            if(username == null){
                redirectToLogin();
            }
        }

        String categoryName = spCategory.getSelectedItem().toString();

        return new FinancialData(financialDataId, amount, date, name, description, username, categoryName);
    }

    private void postOrPutModel(FinancialData model){
        Call<ResponseObj<FinancialData>> call;
        if(editMode){
            call = apiConnector.editEntry("Bearer " + LoginHelper.getToken(this), model, model.getFinancialDataId());
        }else{
            call = apiConnector.addEntry("Bearer " + LoginHelper.getToken(this), model);
        }

        call.enqueue(new Callback<ResponseObj<FinancialData>>() {
            @Override
            public void onResponse(Call<ResponseObj<FinancialData>> call, Response<ResponseObj<FinancialData>> response) {
                if(!response.isSuccessful()){
                    redirectToError();
                    return;
                }
                redirectSuccess();
            }

            @Override
            public void onFailure(Call<ResponseObj<FinancialData>> call, Throwable t) {
                redirectToError();
            }
        });
    }

    public void showDatePickerDialog(View v){
        DialogFragment datePicker = new AddEditDatePickerDialog();
        Bundle args = new Bundle();
        args.putString("date", (String) tvDate.getTag());
        datePicker.setArguments(args);
        datePicker.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
        tvDate.setText(LocalizationHelper.getLocalizedDate(date));
        tvDate.setTag(String.format("%tF", date));
    }

    private void redirectSuccess(){
        Intent i;
        if(getIntent().getStringExtra("returnview") !=null && getIntent().getStringExtra("returnview").equals("cat")){
            i = new Intent(this, CategoryViewActivity.class);
        }else{
            i = new Intent(this, DetailViewActivity.class);
        }

        if(editMode) {
            i.putExtra("alert", "EditSuccess");
        }else{
            i.putExtra("alert", "AddSuccess");
        }

        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i;
        if(getIntent().getStringExtra("returnview") !=null && getIntent().getStringExtra("returnview").equals("cat")){
            i = new Intent(this, CategoryViewActivity.class);
        }else{
            i = new Intent(this, DetailViewActivity.class);
        }
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
}