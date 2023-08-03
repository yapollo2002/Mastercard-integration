package com.fintrack;

import com.mastercard.developer.oauth.OAuth;
import com.mastercard.developer.utils.AuthenticationUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) throws UnrecoverableKeyException, CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException {
        String baseUrl = "https://sandbox.api.mastercard.com/";
        String consumerKey = "SzdZlmwWMLILAIo-KB4JaU9gOXE_CQR54YfdLzw088b12c1c!81f32b148cea48f2b9c99f4df216a1510000000000000000";
       //PrivateKey signingKey = null; // Replace with your signing key

        PrivateKey signingKey = AuthenticationUtils.loadSigningKey(
                "C:/temp/FinTrack/FinTrack-sandbox.p12",
                "keyalias",
                "keystorepassword");

        String requestJson = "{\n" +
                "  \"requestInfo\": {\n" +
                "    \"xRequestId\": \"123e4567-e89b-12d3-a456-426655440000\"\n" +
                "     }\n" +
                "}";

        try {
            String authHeader = OAuth.getAuthorizationHeader(URI.create(baseUrl), "POST", "",
                    StandardCharsets.UTF_8, consumerKey, signingKey);
           // System.out.println("authHeader"+authHeader);

            URL obj = new URL(baseUrl + "/accounts/aspsp");
            System.out.println(obj);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", authHeader);
            con.setRequestProperty("Content-Type", "application/json");

            System.out.println("Request Headers:");
            Map<String, List<String>> requestHeaders = con.getRequestProperties();
            for (String key : requestHeaders.keySet()) {
                List<String> values = requestHeaders.get(key);
                for (String value : values) {
                    System.out.println(key + ": " + value);
                }
            }

            // Send the request
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                os.write(requestJson.getBytes(StandardCharsets.UTF_8));
                System.out.println(requestJson);
            }

            // Get the response
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
            } else {
                // Handle error response
                System.err.println("Failed to get response. HTTP Error code: " + responseCode);

                // Read error response payload
                BufferedReader errorIn = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String errorInputLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorInputLine = errorIn.readLine()) != null) {
                    errorResponse.append(errorInputLine);
                }
                errorIn.close();
                System.err.println("Error Response:");
                System.err.println(errorResponse.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}