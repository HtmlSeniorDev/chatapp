package ru.readme.chatapp.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 *
 * @author www.codejava.net
 */
public class MultipartUtility {


    public static boolean upload(File file, String token, String id) {
        try {
            URL serverUrl =
                    new URL(Network.LINK + "/message/addattachment/upload");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
            urlConnection.setRequestProperty("token", token);
            urlConnection.setRequestProperty("id", id);


            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            InputStream is = new FileInputStream(file);
            int bytesRead;
            byte[] dataBuffer = new byte[1024];
            while ((bytesRead = is.read(dataBuffer)) != -1) {
                os.write(dataBuffer, 0, bytesRead);
            }
            os.flush();
            os.close();
            if(urlConnection.getResponseCode()==200){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            Log.e("Chat Error", "send file error", e);
            return false;
        }
    }


}