package com.sm.in_and;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import models.Category;
import models.FinancialData;
import models.FinancialDataGrouped;
import models.LoginViewModel;
import models.RegisterViewModel;
import models.Response;
import models.ResponseObj;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiConnector {

    @POST("api/user/login")
    Call<Response> loginUser(@Body LoginViewModel model);

    @POST("api/user/register")
    Call<Response> registerUser(@Body RegisterViewModel model);

    @GET
    Call<ResponseObj<HashMap<String, BigDecimal>>> getTotals(@Header ("Authorization") String header, @Url String url);

    @GET
    Call<ResponseObj<ArrayList<FinancialData>>> getAll(@Header ("Authorization") String header, @Url String url);

    @GET
    Call<ResponseObj<ArrayList<FinancialDataGrouped>>> getGrouped(@Header ("Authorization") String header, @Url String url);

    @GET("api/category/getall")
    Call<ResponseObj<ArrayList<Category>>> getCategories(@Header ("Authorization") String header);

    @POST("api/financialdata/add")
    Call<ResponseObj<FinancialData>> addEntry(@Header ("Authorization") String header, @Body FinancialData model);

    @PUT("api/financialdata/edit/{id}")
    Call<ResponseObj<FinancialData>> editEntry(@Header ("Authorization") String header, @Body FinancialData model, @Path("id") int id);

    @DELETE("api/financialdata/delete/{id}")
    Call<ResponseObj<FinancialData>> deleteEntry(@Header ("Authorization") String header, @Path("id") int id);

    @POST("api/category/add")
    Call<ResponseObj<Category>> addCategory(@Header ("Authorization") String header, @Body Category model);

    @PUT("api/category/edit/{id}")
    Call<ResponseObj<Category>> editCategory(@Header ("Authorization") String header, @Body Category model, @Path("id") int id);

    @DELETE("api/category/delete/{id}")
    Call<ResponseObj<Category>> deleteCategory(@Header ("Authorization") String header, @Path("id") int id);

    @DELETE("api/category/clear/{defcat}")
    Call<ResponseObj<Category>> clearCategory(@Header ("Authorization") String header, @Path("defcat") String defcat);
}
