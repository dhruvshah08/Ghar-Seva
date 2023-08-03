package com.example.registration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;


public class SignUpThread implements Callable<String> {
    String email,password;
    public  SignUpThread(String email,String password){
        this.email = email;
        this.password = password;
    }

    @Override
    public String call(){
        try{
            URL url= new URL("https://urbanclap-clone-1.herokuapp.com/api/v1/signup");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"email\":\""+email+"\",\"password\":\""+password+"\"}";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int responseCode = con.getResponseCode();
            if(responseCode==201){
                return "0";
            }else if(responseCode == 400){
                return "1";
            }else{
                return "2";
            }
        }
        catch (MalformedURLException me){
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "3";
    }

}

