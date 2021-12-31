package helpers;

import android.content.Context;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.sm.in_and.R;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AuthHelper {

    public static String authorize(String token, Context context){

        JWT jwt;
        try {
            jwt = new JWT(token);
        }
        catch (Exception e){
            return null;
        }

        if(jwt.isExpired(10)){
            return null;
        }

        String secret = context.getString(R.string.key);
        String[] parts = token.split("\\.");
        String data = parts[0] + "." + parts[1];
        String signature = jwt.getSignature();
        if(!verifySignature(secret,data,signature)){
            return null;
        }

        String validIssuer = context.getString(R.string.validIssuer);
        String validAudience = context.getString(R.string.validAudience);
        if(!jwt.getIssuer().equals(validIssuer) || !jwt.getAudience().get(0).equals(validAudience)){
            return null;
        }

        return jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name").asString();
    }

    private static boolean verifySignature(String secret, String data, String expectedSignature){
        String generatedSignature;
        try {
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            generatedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return false;
        }
        return expectedSignature.equals(generatedSignature);
    }

}
