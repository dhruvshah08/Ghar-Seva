package com.example.registration.ui.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.registration.ChoosingServiceProviderActivity;
import com.example.registration.R;
import com.example.registration.ServiceProviderDetails;
import com.example.registration.ServicesOfferedActivity;
import com.example.registration.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    static ExecutorService executorService;
    private String location,serviceType;
    Button btnACRepair,btnSalon,btnSpa,btnSanitizing,btnElectricians,btnCarpenters,btnElectronicGoods,btnHomePainting,btnPestControl;


    private List<The_Slide_Items_Model_Class> listItems;
    private ViewPager page;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        ServicesOfferedActivity servicesOfferedActivity = (ServicesOfferedActivity) getActivity();
        btnACRepair = (Button) root.findViewById(R.id.btnACRepair);
        btnSalon = (Button) root.findViewById(R.id.btnSalon);
        btnSpa = (Button) root.findViewById(R.id.btnSpa);
        btnSanitizing = (Button) root.findViewById(R.id.btnSanitizing);
        btnElectricians = (Button) root.findViewById(R.id.btnElectricians);
        btnCarpenters = (Button) root.findViewById(R.id.btnCarpenters);
        btnElectronicGoods = (Button) root.findViewById(R.id.btnElectronicGoods);
        btnHomePainting = (Button) root.findViewById(R.id.btnHomePainting);
        btnPestControl = (Button) root.findViewById(R.id.btnPestControl);

        page = root.findViewById(R.id.my_pager) ;
        tabLayout = root.findViewById(R.id.my_tablayout);
        ((ServicesOfferedActivity) getActivity()). getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Ghar Seva" + "</font>"));
        location="mumbai";
        listItems = new ArrayList<>() ;
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.slider1));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.slider2));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.slider3));
        The_Slide_items_Pager_Adapter itemsPager_adapter = new The_Slide_items_Pager_Adapter(getActivity(), listItems);
        page.setAdapter(itemsPager_adapter);
        tabLayout.setupWithViewPager(page,true);

        btnACRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "ACRepair";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnSalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "Salon";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnSpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "Spa";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnSanitizing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "Sanitization";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnElectricians.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "Electricians";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnCarpenters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "Carpenters";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);

            }
        });

        btnElectronicGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "ElectronicGoods";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnHomePainting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "HomePainting";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        btnPestControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "PestControl";
                Intent intent = new Intent(getActivity(), ChoosingServiceProviderActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
