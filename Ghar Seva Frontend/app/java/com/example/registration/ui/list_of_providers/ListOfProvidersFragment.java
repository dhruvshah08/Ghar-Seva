package com.example.registration.ui.list_of_providers;

import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.registration.ChoosingServiceProviderActivity;
import com.example.registration.ExtendedServiceProviderAdapter;
import com.example.registration.ExtendedServiceProviderDetails;
import com.example.registration.R;
import com.example.registration.ServiceProviderAdapter;
import com.example.registration.ServiceProviderDetails;
import com.example.registration.databinding.FragmentListOfProvidersBinding;
import com.example.registration.ui.home.GetDatasetThread;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ListOfProvidersFragment extends Fragment {

    private ListOfProvidersViewModel listOfProvidersViewModel;
    private FragmentListOfProvidersBinding binding;
    ListView listView;
    ArrayList<ServiceProviderDetails> listOfServiceProviderDetails;
    ArrayList<ExtendedServiceProviderDetails> listOfClosestServiceProviderDetails;
    String serviceType,location;
    NavController navController;
    ToggleButton btnVerifiedOnly;
    EditText txtSearch;
    private FusedLocationProviderClient fusedLocationClient;
    Spinner spnrSortBy;
    ProgressBar progressBar;
    TextView txtName;
    static ExecutorService executorService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listOfProvidersViewModel =
                new ViewModelProvider(this).get(ListOfProvidersViewModel.class);
        final ChoosingServiceProviderActivity choosingServiceProviderActivity =(ChoosingServiceProviderActivity) getActivity();
        serviceType = choosingServiceProviderActivity.getServiceType();
        location = choosingServiceProviderActivity.getLocation();
        listOfServiceProviderDetails = choosingServiceProviderActivity.getListOfServiceProviders();
        navController = choosingServiceProviderActivity.getNavController();
        progressBar = choosingServiceProviderActivity.getProgressBar();
        binding = FragmentListOfProvidersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listOfClosestServiceProviderDetails = new ArrayList<>();
        btnVerifiedOnly = (ToggleButton) root.findViewById(R.id.btnVerifiedOnly);
        spnrSortBy = (Spinner) root.findViewById(R.id.spnrSortBy);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        txtName = root.findViewById(R.id.txtName);
        String serviceTypeCapitalised = (serviceType.charAt(0)+"").toUpperCase();
        String finalServiceType = serviceTypeCapitalised + serviceType.substring(1);
        String locationTypeCapitalised = (location.charAt(0)+"").toUpperCase();
        String finalLocationType = locationTypeCapitalised + location.substring(1);
        String name = finalServiceType + " in "+ finalLocationType;
        txtName.setText(name);
        txtSearch = (EditText) root.findViewById(R.id.txtSearch);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString();
                searchByName(searchText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView = root.findViewById(R.id.listOfServiceProviders);

        btnVerifiedOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                * If true then show only verified else show all
                * */
                showToggleVerified(btnVerifiedOnly.isChecked());
            }
        });

        spnrSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String sortingMethod = parentView.getItemAtPosition(position).toString().trim();
                if("Popularity-Asc".equals(sortingMethod)){
                    sortByPopularityAsc();
                }else if("Popularity-Desc".equals(sortingMethod)){
                    sortByPopularityDesc();
                }else if("Closest to me-Asc".equals(sortingMethod)){
                    sortByClosestToMeAsc();
                }
                else if("Rating-Asc".equals(sortingMethod)){
                    sortByRatingAsc();
                }
                else if("Rating-Desc".equals(sortingMethod)){
                    sortByRatingDesc();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        listOfProvidersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    private void searchByName(String searchText){
        ArrayList<ServiceProviderDetails> list = new ArrayList<>();
        list.addAll(listOfServiceProviderDetails);
        //Perform function here
        list = (ArrayList<ServiceProviderDetails>) list.stream()
                .filter(e->e.getName().split(" in ")[0].trim().contains(searchText))
                .collect(Collectors.toList());
        ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
        listView.setAdapter(adapter);
    }

    private void sortByPopularityAsc(){
        ArrayList<ServiceProviderDetails> list = new ArrayList<>();
        list.addAll(listOfServiceProviderDetails);
        //Perform function here
        list = (ArrayList<ServiceProviderDetails>) list.stream().sorted(new Comparator<ServiceProviderDetails>() {
            @Override
            public int compare(ServiceProviderDetails t1, ServiceProviderDetails t2) {
                if(t1.getPopularity() < t2.getPopularity()){
                    return -1;
                }else if(t1.getPopularity() > t2.getPopularity()){
                    return 1;
                }else{
                    return 0;
                }
            }
        }).collect(Collectors.toList());
        ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
        listView.setAdapter(adapter);
    }
    private void sortByPopularityDesc(){
        ArrayList<ServiceProviderDetails> list = new ArrayList<>();
        list.addAll(listOfServiceProviderDetails);
        //Perform function here
        list = (ArrayList<ServiceProviderDetails>) list.stream().sorted(new Comparator<ServiceProviderDetails>() {
            @Override
            public int compare(ServiceProviderDetails t1, ServiceProviderDetails t2) {
                if(t1.getPopularity() < t2.getPopularity()){
                    return 1;
                }else if(t1.getPopularity() > t2.getPopularity()){
                    return -1;
                }else{
                    return 0;
                }
            }
        }).collect(Collectors.toList());
        ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
        listView.setAdapter(adapter);
    }
    private void sortByClosestToMeAsc(){
        getLocationFirst(serviceType,location);
    }
    private void sortByRatingAsc(){
        ArrayList<ServiceProviderDetails> list = new ArrayList<>();
        list.addAll(listOfServiceProviderDetails);
        //Perform function here
        list = (ArrayList<ServiceProviderDetails>) list.stream().sorted(new Comparator<ServiceProviderDetails>() {
            @Override
            public int compare(ServiceProviderDetails t1, ServiceProviderDetails t2) {
                if(t1.getRating() < t2.getRating()){
                    return -1;
                }else if(t1.getRating() > t2.getRating()){
                    return 1;
                }else{
                    return 0;
                }
            }
        }).collect(Collectors.toList());
        ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
        listView.setAdapter(adapter);
    }
    private void sortByRatingDesc(){
        ArrayList<ServiceProviderDetails> list = new ArrayList<>();
        list.addAll(listOfServiceProviderDetails);
        //Perform function here
        list = (ArrayList<ServiceProviderDetails>) list.stream().sorted(new Comparator<ServiceProviderDetails>() {
            @Override
            public int compare(ServiceProviderDetails t1, ServiceProviderDetails t2) {
                if(t1.getRating() < t2.getRating()){
                    return 1;
                }else if(t1.getRating() > t2.getRating()){
                    return -1;
                }else{
                    return 0;
                }
            }
        }).collect(Collectors.toList());
        ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
        listView.setAdapter(adapter);
    }

    private void showToggleVerified(boolean showOnlyVerified){
        ArrayList<ServiceProviderDetails> list = new ArrayList<>();
        list.addAll(listOfServiceProviderDetails);
        if(showOnlyVerified){
            //show only verified
            btnVerifiedOnly.setBackgroundResource(R.drawable.spinnerdsg);

            list = (ArrayList<ServiceProviderDetails>) list.stream()
                    .filter(ele->ele.isVerified())
                    .collect(Collectors.toList());
            ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
            listView.setAdapter(adapter);
        }else{
            //show all
            ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),list,serviceType,location,navController);
            listView.setAdapter(adapter);
            btnVerifiedOnly.setBackgroundResource(R.drawable.registration_buttons_rounded);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(listOfServiceProviderDetails!=null){
            displayList(listOfServiceProviderDetails);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    public void displayList(ArrayList<ServiceProviderDetails> listOfServiceProviderDetails){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ServiceProviderAdapter adapter = new ServiceProviderAdapter(getActivity(),listOfServiceProviderDetails,serviceType,location,navController);
                listView.setAdapter(adapter);
            }
        });
    }

    public void displayClosestList(ArrayList<ExtendedServiceProviderDetails> listOfServiceProviderDetails){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExtendedServiceProviderAdapter adapter = new ExtendedServiceProviderAdapter(getActivity(),listOfServiceProviderDetails,serviceType,location,navController);
                listView.setAdapter(adapter);
            }
        });
    }

    private void getLocationFirst(String serviceType,String location1){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            getClosestToMe(serviceType,location1,Float.parseFloat(location.getLatitude()+""),Float.parseFloat(location.getLongitude()+""));
                        }
                    }
                });

    }
    private void getClosestToMe(String serviceType,String location,float latitude,float longitude){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new Thread(new Runnable() {
            public void run(){
                listOfClosestServiceProviderDetails.clear();
                executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new GetClosestToMeThread(serviceType,location,latitude,longitude));
                try {
                    String jsonData = futureDataset.get();
                    JSONArray array = new JSONArray(jsonData);
                    int length =array.length();
                    for(int i=0;i<length;i++){
                        JSONObject item = array.getJSONObject(i);
                        System.out.println("ITEM : "+item);
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
                        float distance = Float.parseFloat(item.getString("distance"));
                        ExtendedServiceProviderDetails serviceProviderDetails = new ExtendedServiceProviderDetails(name,address,isVerified,rating,longitude,latitude,popularity,distance);
                        listOfClosestServiceProviderDetails.add(serviceProviderDetails);
                    }
                    displayClosestList(listOfClosestServiceProviderDetails);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });

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
}