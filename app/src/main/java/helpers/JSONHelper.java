package helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JSONHelper {

    public static <T> T TryParseJson(String obj, Class<T> type){
        Gson gson = new Gson();
        try{
            return gson.fromJson(obj, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T TryParseJson(String obj, Type type){
        Gson gson = new Gson();
        try{
            return gson.fromJson(obj, type);
        } catch (Exception e) {
            return null;
        }
    }
}
