package helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginHelper {

    public static String getToken(Context context){
        SharedPreferences loginData = context.getSharedPreferences("LoginData",context.MODE_PRIVATE);
        String token = loginData.getString("token", null);
        return token;
    }

    public static void logout(Context context){
        SharedPreferences loginData = context.getSharedPreferences("LoginData",context.MODE_PRIVATE);
        SharedPreferences.Editor loginDataEdit = loginData.edit();
        loginDataEdit.clear();
        loginDataEdit.commit();
    }

}
