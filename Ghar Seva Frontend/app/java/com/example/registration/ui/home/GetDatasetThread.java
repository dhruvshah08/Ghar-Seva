package com.example.registration.ui.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class GetDatasetThread implements Callable<String> {
    String serviceType,location;
   public  GetDatasetThread(String serviceType,String location){
       this.serviceType = serviceType;
       this.location = location;
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
                url = new URL(base1+"/api/v1/"+serviceType+"/"+location);
            }else{
                url = new URL(base2+"/api/v1/"+serviceType+"/"+location);
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
            System.out.println("MalformedURLException "+me.getMessage());
        }
        catch (IOException e) {
            System.out.println("IO EXCEPTION : ");
            e.printStackTrace();
        }
    return "";
}

}
