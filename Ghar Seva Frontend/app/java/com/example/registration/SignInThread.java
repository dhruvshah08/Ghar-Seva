package com.example.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;


public class SignInThread implements Callable<String> {
    String email,password;
    public  SignInThread(String email,String password){
        this.email = email;
        this.password = password;
    }

    @Override
    public String call(){
        try{
            String base1="https://urbanclap-clone-1.herokuapp.com";
            URL url;

            url = new URL(base1+"/api/v1/login?email="+email+"&password="+password);
            URLConnection urlConnection=url.openConnection();
            urlConnection.connect();
            BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line="";
            String finalStr="";
            while(line!=null){
                if(!line.equals("")){
                    finalStr+=line;
                }
                line=br.readLine();
            }
            return finalStr;
        }
        catch (MalformedURLException me){
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
