package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import helpers.APIHelper;
import helpers.JSONHelper;
import models.LoginViewModel;
import models.RegisterViewModel;
import models.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private TextView tvErrorText;

    private ApiConnector apiConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onResume() {
        super.onResume();

        apiConnector = APIHelper.Initialize();

        etEmail = findViewById(R.id.etEmail);
        etUsername =  findViewById(R.id.etUsername);
        etPassword =  findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvErrorText = findViewById(R.id.tvErrorText);
    }

    public void register(View v){
        tvErrorText.setVisibility(View.INVISIBLE);

        boolean validationResult = validate();
        if(!validationResult){
            return;
        }

        RegisterViewModel model = new RegisterViewModel(etEmail.getText().toString(), etUsername.getText().toString(), etPassword.getText().toString(), etConfirmPassword.getText().toString());

        Call<Response> call = apiConnector.registerUser(model);
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
                        if(errorResponse != null){
                            if(errorResponse.getMessage() != null && errorResponse.getMessage().equals("UsernameAlreadyInUse")){
                                tvErrorText.setText("Podana nazwa użytkownika już jest w użyciu");
                                tvErrorText.setVisibility(View.VISIBLE);
                            }
                            else if(errorResponse.getMessage() != null && errorResponse.getMessage().equals("EmailAlreadyInUse")){
                                tvErrorText.setText("Podany adres e-mail już jest w użyciu");
                                tvErrorText.setVisibility(View.VISIBLE);
                            }
                            else{
                                redirectToError();
                            }
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

                redirectToLoginSuccess();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                redirectToError();
            }
        });
    }

    private boolean validate(){
        boolean successfullyValidated = true;

        if(etEmail.length() == 0){
            etEmail.setError("Adres e-mail jest wymagany");
            successfullyValidated = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()){
            etEmail.setError("Należy wprowadzić prawidłowy adres e-mail");
            successfullyValidated = false;
        }
        else if(etEmail.length() > 30){
            etEmail.setError("Adres e-mail nie może być dłuższy niż 30 znaków");
            successfullyValidated = false;
        }

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

        if(etConfirmPassword.length() == 0){
            etConfirmPassword.setError("Potwierdzenie hasła jest wymagane");
            successfullyValidated = false;
        }
        else if(!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())){
            etConfirmPassword.setError("Hasła muszą być takie same");
            successfullyValidated = false;
        }
        else if(etConfirmPassword.length() > 30){
            etConfirmPassword.setError("Hasło nie może być dłuższe niż 30 znaków");
            successfullyValidated = false;
        }

        return successfullyValidated;
    }

    public void goToLogin(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToError(){
        Intent i = new Intent(this, ErrorActivity.class);
        startActivity(i);
        finish();
    }

    private void redirectToLoginSuccess(){
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("alert", "RegistrationSuccess");
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}