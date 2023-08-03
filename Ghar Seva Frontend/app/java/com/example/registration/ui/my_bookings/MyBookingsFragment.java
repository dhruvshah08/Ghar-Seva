package com.example.registration.ui.my_bookings;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.registration.ChoosingServiceProviderActivity;
import com.example.registration.NetworkConnection;
import com.example.registration.ServicesOfferedActivity;
import com.example.registration.databinding.FragmentHomeBinding;
import com.example.registration.ui.home.HomeViewModel;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.registration.R;
import com.example.registration.databinding.FragmentMyBookingsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MyBookingsFragment extends Fragment {

    private MyBookingsViewModel myBookingsViewModel;
    private FragmentMyBookingsBinding binding;
    ArrayList<MyBookingsDetails> listOfMyBookings = new ArrayList<>();
    ListView listOfBookings1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myBookingsViewModel =
                new ViewModelProvider(this).get(MyBookingsViewModel.class);

        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listOfBookings1 = root.findViewById(R.id.listOfBookings1);
        listOfBookings1.setDivider(null);
        ((ServicesOfferedActivity) getActivity()). getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "My Bookings" + "</font>"));

        myBookingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        fetchMyBookings();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchMyBookings(){
        try{
            String base= NetworkConnection.ip_address +":8081/mybookings";//CHANGE THIS
            URL url;
            url = new URL(base+"?email="+ServicesOfferedActivity.emailAddress);
            System.out.println("ADDRESS: "+url.toString());
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
            JSONArray arrOfMyBoookings = new JSONArray(finalStr);
            for(int i=0;i<arrOfMyBoookings.length();i++){
                JSONObject item = arrOfMyBoookings.getJSONObject(i);
                String serviceproviderName =item.getString("serviceproviderName");
                String serviceProviderAddress =item.getString("serviceProviderAddress");
                String serviceProviderType =item.getString("serviceProviderType");
                String customerPhone =item.getString("customerPhone");
                String customerAddress =item.getString("customerAddress");
                String timeStamp =item.getString("timeStamp");
                String arr[]=timeStamp.split(" ");
                String date =arr[2] + " " + arr[1] + ", " + arr[5];
                String timeArr[] = arr[3].split(":");
                String time = timeArr[0] + ":"+timeArr[1];
                timeStamp = date + " "+time;
                MyBookingsDetails bookingsDetails = new MyBookingsDetails(serviceproviderName,serviceProviderAddress,serviceProviderType,customerPhone,customerAddress,timeStamp);
                listOfMyBookings.add(bookingsDetails);
            }
            MyDetailsAdapter adapter = new MyDetailsAdapter(getActivity(),listOfMyBookings);
            listOfBookings1.setAdapter(adapter);
            if(listOfMyBookings.isEmpty()){
                Toast.makeText(getContext(),"No Bookings have been made yet!",Toast.LENGTH_LONG).show();
            }
        }
        catch (MalformedURLException me){
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}