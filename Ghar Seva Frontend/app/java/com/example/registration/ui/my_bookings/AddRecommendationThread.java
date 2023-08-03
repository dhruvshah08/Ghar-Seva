package com.example.registration.ui.my_bookings;

import com.example.registration.NetworkConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class AddRecommendationThread implements Callable<String> {
    String prevCategory,currCategory,category="";
    public  AddRecommendationThread(String prevCategory,String currCategory){
        this.prevCategory=prevCategory;
        this.currCategory=currCategory;
        if("salon".equals(currCategory)){
            category="after_salon";
        }else if("sanitization".equals(currCategory)){
            category="after_sanitizing";
        }else if("spa".equals(currCategory)){
            category="after_spa";
        }else if("electronicgoods".equals(currCategory)){
            category="after_electronics";
        }else if("homepainting".equals(currCategory)){
            category="after_home_painting";
        }else if("carpenters".equals(currCategory)){
            category="after_carpenter";
        }else if("acrepair".equals(currCategory)){
            category="after_ac_repair";
        }else if("pestcontrol".equals(currCategory)){
            category="after_pest_control";
        }else if("electricians".equals(currCategory)){
            category="after_electrician";
        }
    }
    @Override
    public String call(){
        try{
            String base= NetworkConnection.ip_address +":8081/add";
            URL url;
            System.out.println("In Add Recommendation");
            url = new URL(base+"?category="+prevCategory+"&currCategory="+category);
            System.out.println("FOUND! :"+url.toString());
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
            System.out.println("Inside Add Recommendation as : "+finalStr);
            return finalStr;
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
