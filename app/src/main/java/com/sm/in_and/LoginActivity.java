package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import helpers.APIHelper;
import helpers.JSONHelper;
import models.LoginViewModel;
import models.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvInvalidUsernameOrPasswordMessage;

    private ApiConnector apiConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String alert = getIntent().getStringExtra("alert");
        if(alert != null && alert.equals("RegistrationSuccess")){
            Toast.makeText(this, "Rejestracja zakończona powodzeniem", Toast.LENGTH_LONG).show();
        }
    }

    @Override //Launches right after onCreate when app starts and when the user goes back to the app (e.g. back button)
    protected void onResume() {
        super.onResume();

        apiConnector = APIHelper.Initialize();

        etUsername =  findViewById(R.id.etUsername);
        etPassword =  findViewById(R.id.etPassword);
        tvInvalidUsernameOrPasswordMessage = findViewById(R.id.tvInvalidUsernameOrPasswordMessage);
    }

    public void login(View v){
        tvInvalidUsernameOrPasswordMessage.setVisibility(View.INVISIBLE);

        boolean validationResult = validate();
        if(!validationResult){
            return;
        }

        LoginViewModel model = new LoginViewModel(etUsername.getText().toString(), etPassword.getText().toString());

        Call<Response> call = apiConnector.loginUser(model);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if(!response.isSuccessful()){
                    if(response.code() == 400){
                        Response errorResponse;
                        try {
                            errorResponse = JSONHelper.TryParseJson(response.errorBody().string(), Response.class);
                        }catch (Exception e){
                            errorResponse = null;
                        }
                        if(errorResponse != null && errorResponse.getMessage() != null && errorResponse.getMessage().equals("InvalidCredentials")){
                            tvInvalidUsernameOrPasswordMessage.setVisibility(View.VISIBLE);
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

                SharedPreferences loginData = getSharedPreferences("LoginData",MODE_PRIVATE);
                SharedPreferences.Editor loginDataEdit = loginData.edit();
                loginDataEdit.putString("token", response.body().getMessage());
                loginDataEdit.commit();
                redirectToMain();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                redirectToError();
                //Log.d("ConnectionFailure", t.getMessage());
            }
        });
    }

    private boolean validate(){
        boolean successfullyValidated = true;

        if(etUsername.length() == 0){
            etUsername.setError("Nazwa użytkownika jest wymagana");
            successfullyValidated = false;
        }
        else if(etUsername.length() > 30){
            etUsername.setError("Nazwa użytkownika nie może być dłuższa niż 30 znaków");
            successfullyValidated = false;
        }

        if(etPassword.length() == 0){
            etPassword.setError("Hasło jest wymagane");
            successfullyValidated = false;
        }
        else if(etPassword.length() > 30){
            etPassword.setError("Hasło nie może być dłuższe niż 30 znaków");
            successfullyValidated = false;
        }

        return successfullyValidated;
    }

    public void goToRegistration(View v){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToMain(){
        Intent i = new Intent(this, MainPageActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToError(){
        Intent i = new Intent(this, ErrorActivity.class);
        startActivity(i);
        finish();
    }
}