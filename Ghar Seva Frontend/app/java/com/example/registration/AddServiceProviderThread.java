package com.example.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class AddServiceProviderThread implements Callable<String> {
    String servicetype;
    String name;
    String address;
    boolean isverified;
    float rating;
    float latitude;
    float longitude;
    int popularity;
    public AddServiceProviderThread(String servicetype, String name, String address, boolean isverified, float rating, float latitude, float longitude, int popularity) {
        this.servicetype = servicetype;
        this.name = name;
        this.address = address;
        this.isverified = isverified;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.popularity = popularity;
    }

    @Override
    public String call(){
        try{
            String base=NetworkConnection.ip_address+":8081/add/serviceprovider";
            URL url;
            url = new URL(base+"?servicetype="+servicetype+"&name="+name+"&address="+address+"&isverified="+isverified+"&rating="+rating+"&latitude="+latitude+"&longitude="+longitude+"&popularity="+popularity);
            System.out.println("URL : "+url.toString());
            URLConnection urlConnection=url.openConnection();
            System.out.println("PRINTED1");
            urlConnection.connect();
            System.out.println("PRINTED2");
            BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line="";
            String finalStr="";
            System.out.println("PRINTED: "+line + " ");
            while(line!=null){
                if(!line.equals("")){
                    finalStr+=line;
                }
                line=br.readLine();
            }
            System.out.println("Out of here with : "+finalStr);
            return finalStr;

        }
        catch (MalformedURLException me){
            System.out.println("Error1 : "+me.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error2: "+e.getMessage());
        }
        return "";
    }

}
