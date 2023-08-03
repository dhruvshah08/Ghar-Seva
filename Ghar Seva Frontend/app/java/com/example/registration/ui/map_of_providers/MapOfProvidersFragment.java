package com.example.registration.ui.map_of_providers;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.registration.ChoosingServiceProviderActivity;
import com.example.registration.ExtendedServiceProviderDetails;
import com.example.registration.R;
import com.example.registration.ServiceProviderDetails;
import com.example.registration.databinding.FragmentMapOfProvidersBinding;
import com.example.registration.ui.list_of_providers.GetClosestToMeThread;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MapOfProvidersFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener {
    private MapOfProvidersViewModel mapOfProvidersViewModel;
    private FragmentMapOfProvidersBinding binding;
    private static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    Marker currenLocMarker = null;
    static ExecutorService executorService;
    String serviceType, location;
    public static LatLng inFocus;
    ArrayList<LatLng> listOfPoints = new ArrayList<>();
    Polygon polygon1;
    Spinner spinner;
    PolygonOptions polygonOptions;
    ArrayList<ServiceProviderDetails> lisOfServiceProviders;
    float latitude, longitude;
    NavController navController;
    ProgressBar progressBar;
    ImageButton btnRefresh;
    TextView txtName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapOfProvidersViewModel =
                new ViewModelProvider(this).get(MapOfProvidersViewModel.class);

        binding = FragmentMapOfProvidersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ChoosingServiceProviderActivity choosingServiceProviderActivity = (ChoosingServiceProviderActivity) getActivity();
        serviceType = choosingServiceProviderActivity.getServiceType();
        location = choosingServiceProviderActivity.getLocation();
        lisOfServiceProviders = choosingServiceProviderActivity.getListOfServiceProviders();
        navController = choosingServiceProviderActivity.getNavController();
        progressBar = choosingServiceProviderActivity.getProgressBar();
        spinner = root.findViewById(R.id.spnrWithinDistance);
        btnRefresh = root.findViewById(R.id.btnRefresh);
        txtName = root.findViewById(R.id.txtName);
        String serviceTypeCapitalised = (serviceType.charAt(0)+"").toUpperCase();
        String finalServiceType = serviceTypeCapitalised + serviceType.substring(1);
        String locationTypeCapitalised = (location.charAt(0)+"").toUpperCase();
        String finalLocationType = locationTypeCapitalised + location.substring(1);
        String name = finalServiceType + " in \n"+ finalLocationType;
        txtName.setText(name);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete the polygon and the points
                //add all the markers
                if(polygon1!=null) {
                    polygon1.remove();
                    listOfPoints.clear();
                    polygonOptions.visible(false);
                    polygon1 = null;
                    addMarkers();
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String sortingMethod = parentView.getItemAtPosition(position).toString().trim();
                if ("Within 2km".equals(sortingMethod)) {
                    findWithinDistance(2000);
                } else if ("Within 5km".equals(sortingMethod)) {
                    findWithinDistance(5000);
                } else if ("Within 7km".equals(sortingMethod)) {
                    findWithinDistance(7000);
                } else if ("Show All".equals(sortingMethod)) {
                    addMarkers();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mapOfProvidersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        if (checkPermissions()) {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            latitude = Float.parseFloat(location.getLatitude() + "");
                            longitude = Float.parseFloat(location.getLongitude() + "");
                            if (inFocus == null) {
                                inFocus = currentLocation;
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inFocus, 17.0f));
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inFocus, 17.0f));
                            }
                            currenLocMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                        }
                    }
                });

        return root;
    }

    private void addCurrentMarker(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            latitude = Float.parseFloat(location.getLatitude() + "");
                            longitude = Float.parseFloat(location.getLongitude() + "");
                            currenLocMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.home1)).position(currentLocation).title("Current Location"));
                        }
                    }
                });
    }


    public boolean checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE},
                    1);

        }
        return false;
    }

    private void findWithinDistance(int distance) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new Thread(new Runnable() {
            public void run() {
                ArrayList<ServiceProviderDetails> listOfServiceProviderDetails=new ArrayList<>();
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new GetDatasetWithinDistanceThread(serviceType, location, latitude, longitude, distance));
                try {
                    String jsonData = futureDataset.get();
                    JSONArray array = new JSONArray(jsonData);
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
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
                        String lat = latLngArr[0].trim().substring(1);
                        String lng = latLngArr[1].trim().substring(0, latLngArr[1].length() - 2);
                        float latitude = Float.parseFloat(lat.trim());
                        float longitude = Float.parseFloat(lng.trim());
                        ServiceProviderDetails serviceProviderDetails = new ServiceProviderDetails(name, address, isVerified, rating, longitude, latitude, popularity);
                        listOfServiceProviderDetails.add(serviceProviderDetails);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addMarkers(listOfServiceProviderDetails);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarkers();
        if (mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (polygon1 == null) {
                        System.out.println("Inside here as null");
                        polygonOptions = new PolygonOptions();
                    }
                    listOfPoints.add(latLng);
                    if (listOfPoints.size() > 2) {
                        if (listOfPoints.size() == 3) {
                            polygonOptions.addAll(listOfPoints);
                        } else {
                            polygonOptions.add(latLng);
                        }
                        polygon1 = mMap.addPolygon(polygonOptions);
                        stylePolygon(polygon1);

                        String finalStr1="";
                        for(LatLng latLng1 : polygon1.getPoints()){
                            finalStr1+=latLng1.longitude +" "+latLng1.latitude+",";
                        }
                        finalStr1 = finalStr1.substring(0,finalStr1.length()-1);
                        System.out.println("List of Points : "+finalStr1);
                        withinPolygon(finalStr1);
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 14);
//                        googleMap.setOnPolygonClickListener(MapOfProvidersFragment.this);
                    }
                }
            });
        } else {
        }
    }

    private static final int COLOR_BLACK_ARGB = 0xff000000;

    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    @Override
    public void onPolygonClick(Polygon polygon) {
        stylePolygon(polygon);
        // Flip the values of the red, green, and blue components of the polygon's color.
//        addMarkers();
    }

    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_DARK_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_LIGHT_GREEN_ARGB = 0xff81C784;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    private void stylePolygon(Polygon polygon) {

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        pattern = PATTERN_POLYGON_ALPHA;
        strokeColor = COLOR_DARK_GREEN_ARGB;
        fillColor = COLOR_LIGHT_GREEN_ARGB;

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);


    }

    private void addMarkers() {
        mMap.clear();
        addCurrentMarker();
        for (ServiceProviderDetails serviceProviderDetails : lisOfServiceProviders) {
            LatLng currentLocation = new LatLng(serviceProviderDetails.getLatitude(), serviceProviderDetails.getLongitude());
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker)).position(currentLocation).title(serviceProviderDetails.getName().split(" in ")[0].trim()));
        }
    }

    private void addMarkers(ArrayList<ServiceProviderDetails> listOfServiceProviderDetails) {
        mMap.clear();
        addCurrentMarker();
        for (ServiceProviderDetails serviceProviderDetails : listOfServiceProviderDetails) {
            LatLng currentLocation = new LatLng(serviceProviderDetails.getLatitude(), serviceProviderDetails.getLongitude());
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker)).position(currentLocation).title(serviceProviderDetails.getName().split(" in ")[0].trim()));
        }
    }

    private void withinPolygon(String listOfPoints){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new Thread(new Runnable() {
            public void run() {
                ArrayList<ServiceProviderDetails> listOfServiceProviderDetails=new ArrayList<>();
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new GetDatasetWithinPolygon(serviceType, location, latitude, longitude, listOfPoints));
                try {
                    String jsonData = futureDataset.get();
                    JSONArray array = new JSONArray(jsonData);
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
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
                        String lat = latLngArr[0].trim().substring(1);
                        String lng = latLngArr[1].trim().substring(0, latLngArr[1].length() - 2);
                        float latitude = Float.parseFloat(lat.trim());
                        float longitude = Float.parseFloat(lng.trim());
                        ServiceProviderDetails serviceProviderDetails = new ServiceProviderDetails(name, address, isVerified, rating, longitude, latitude, popularity);
                        listOfServiceProviderDetails.add(serviceProviderDetails);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addMarkers(listOfServiceProviderDetails);
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