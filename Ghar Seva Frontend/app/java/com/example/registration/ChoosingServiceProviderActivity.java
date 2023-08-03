package com.example.registration;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.registration.ui.home.GetDatasetThread;
import com.example.registration.ui.list_of_providers.ListOfProvidersFragment;
import com.example.registration.ui.map_of_providers.MapOfProvidersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.registration.databinding.ActivityChoosingServiceProviderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChoosingServiceProviderActivity extends AppCompatActivity {

    private ActivityChoosingServiceProviderBinding binding;
    static ExecutorService executorService;
    public ArrayList<ServiceProviderDetails> listOfServiceProviderDetails = new ArrayList<>();
    String serviceType,location;
    NavController navController;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceType =getIntent().getStringExtra("serviceType");
        location =getIntent().getStringExtra("location");
        binding = ActivityChoosingServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        serviceType = serviceType.toLowerCase();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_listOfProviders, R.id.navigation_mapOfProviders)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_choosing_service_provider);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        getSupportActionBar().hide();
        progressBar = findViewById(R.id.progressBar_cyclic);
        progressBar.setIndeterminate(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getDataSet(serviceType,location,navController);
    }

    private void getDataSet(String serviceType, String location, NavController navController){
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new Thread(new Runnable() {

            public void run(){
                listOfServiceProviderDetails.clear();
                executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new GetDatasetThread(serviceType,location));
                try {
                    String jsonData = futureDataset.get();
                    JSONArray array = new JSONArray(jsonData);
                    int length =array.length();
                    for(int i=0;i<length;i++){
                        JSONObject item = array.getJSONObject(i);
                        String id = item.getString("id");
                        String name = item.getString("name");
                        float rating = Float.parseFloat(item.getString("rating"));
                        int popularity = Integer.parseInt(item.getString("rating_count"));
                        String address = item.getString("address");
                        String verified = item.getString("verified");
                        boolean isVerified = "Verified".equals(verified);
                        String loc = item.getString("location");
                        String location = loc.split(":")[2];
                        String latLngArr[] = location.split(",");
                        String lat =latLngArr[0].trim().substring(1);
                        String lng =latLngArr[1].trim().substring(0,latLngArr[1].length()-2);
                        float latitude = Float.parseFloat(lat.trim());
                        float longitude = Float.parseFloat(lng.trim());
                        ServiceProviderDetails serviceProviderDetails = new ServiceProviderDetails(name,address,isVerified,rating,longitude,latitude,popularity);
                        listOfServiceProviderDetails.add(serviceProviderDetails);
                    }
                    Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_choosing_service_provider);
                    if(navHostFragment!=null){
                        ListOfProvidersFragment listOfProvidersFragment = (ListOfProvidersFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                        listOfProvidersFragment.displayList(listOfServiceProviderDetails);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });
                    }


                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public ArrayList<ServiceProviderDetails> getListOfServiceProviders(){
        return this.listOfServiceProviderDetails;
    }
    public String getServiceType(){
        return this.serviceType;
    }
    public String getLocation(){
        return this.location;
    }

    public NavController getNavController(){
        return navController;
//        navController.navigate(R.id.navigation_mapOfProviders);
    }

    public ProgressBar getProgressBar(){
        return progressBar;
//        navController.navigate(R.id.navigation_mapOfProviders);
    }
}