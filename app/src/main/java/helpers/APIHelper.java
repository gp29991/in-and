package helpers;

import com.sm.in_and.ApiConnector;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {

    public static ApiConnector Initialize(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiConnector.class);
    }

}
