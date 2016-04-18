package com.delaroystudios.uploadimagetoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyHttpURLConnection {
    public static String getData(RequestPackage requestPackage) {
        // TODO Auto-generated method stub
        BufferedReader reader = null;
        String uri = requestPackage.getUri();

        try {
            URL url = new URL(uri);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod(requestPackage.getMethod());
			/*
			 * passing params via post done here
			 */
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(
                    con.getOutputStream());
            writer.write(requestPackage.getEncodedParams());
            writer.flush();

            con.connect();
            InputStream is = con.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String data = null;
            StringBuilder sb = new StringBuilder();
            while ((data = reader.readLine()) != null) {
                sb.append(data + "\n");
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

}