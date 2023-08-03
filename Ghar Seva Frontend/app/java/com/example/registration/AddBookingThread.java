package com.example.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class AddBookingThread implements Callable<String> {
    String email,name,servicetype,orderdate,ordertime,phonenumber,address;

    public  AddBookingThread(String email,String name,String servicetype,String orderdate,String ordertime,String phonenumber,String address){
       this.email=email;
       this.name=name;
       this.servicetype=servicetype;
       this.orderdate=orderdate;
       this.ordertime=ordertime;
       this.phonenumber=phonenumber;
       this.address=address;
    }

    @Override
    public String call(){
        try{
            String base=NetworkConnection.ip_address+":8081/add/booking";//CHANGE HERE
            URL url;
            url = new URL(base+"?email="+email+"&name="+name+"&servicetype="+servicetype+"&orderdate="+orderdate+"&ordertime="+ordertime+"&phonenumber="+phonenumber+"&address="+address);
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
