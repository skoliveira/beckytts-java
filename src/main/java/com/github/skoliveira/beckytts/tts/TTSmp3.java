package com.github.skoliveira.beckytts.tts;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class TTSmp3 {
    private HttpsURLConnection conn;

    public static void main(String[] args) throws Exception {
        String url = "https://ttsmp3.com/makemp3_new.php";
        TTSmp3 http = new TTSmp3();
        String msg = "precisa de fogo no rabo";
        String postParams = "msg=" + URLEncoder.encode(msg, "UTF-8") + "&lang=Ricardo&source=ttsmp3";
        http.sendPost(url, postParams);
    }

    private void sendPost(String url, String postParams) throws Exception {
        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        
        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject o = new JSONObject(response.toString());
        String mp3 = o.has("URL") ? o.getString("URL") : response.toString(); 
        System.out.println(mp3);

    }

}