package com.example.registration.ui.list_of_providers;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class GetClosestToMeThread implements Callable<String> {
    String serviceType,location;
    float currentLatitude,currentLongitude;
    public  GetClosestToMeThread(String serviceType, String location, float currentLatitude,float currentLongitude){
        this.serviceType = serviceType;
        this.location = location;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    @Override
    public String call(){
        try{
            String base1="",base2="";
            if("mumbai".equals(location)){
                base1="https://urbanclap-clone-1.herokuapp.com";
                base2="https://urbanclap-clone-2.herokuapp.com";
            }else if("thane".equals(location)){
                base1="https://urbanclap-clone-3.herokuapp.com";
                base2="https://urbanclap-clone-4.herokuapp.com";
            }
            else if("vashi".equals(location)){
                base1="https://urbanclap-clone-5.herokuapp.com";
                base2="https://urbanclap-clone-6.herokuapp.com";
            }

            URL url;
            char ch = serviceType.charAt(0);
            if(ch >= 'a' && ch <='o'){
                url = new URL(base1+"/api/v1/"+serviceType+"/"+location+"/closest?longitude="+currentLongitude+"&latitude="+currentLatitude);
//                                        /api/v1/acrepair/mumbai/closest?longitude=72.8548&latitude=19.299
            }else{
                url = new URL(base2+"/api/v1/"+serviceType+"/"+location+"/closest?longitude="+currentLongitude+"&latitude="+currentLatitude);
            }
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
