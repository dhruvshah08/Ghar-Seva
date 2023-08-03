package com.example.registration.ui.my_bookings;

import com.example.registration.NetworkConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class GetRecommendationThread implements Callable<String> {
    String currCategory;
    public  GetRecommendationThread(String currCategory){
        this.currCategory=currCategory;
    }
    @Override
    public String call(){
        try{
            String base= NetworkConnection.ip_address +":8081/recommendation";//CHANGE THIS
            URL url;
            url = new URL(base+"?category="+currCategory);
            URLConnection urlConnection=url.openConnection();
            urlConnection.connect();
            BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line="";
            System.out.println("In Get Recommendation");
            String finalStr="";
            while(line!=null){
                if(!line.equals("")){
                    finalStr+=line;
                }
                line=br.readLine();
            }
            System.out.println("Inside Get Recommendation as : "+finalStr);
            return finalStr;
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
