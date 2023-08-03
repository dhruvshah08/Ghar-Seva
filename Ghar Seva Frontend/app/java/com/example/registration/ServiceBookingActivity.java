package com.example.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.registration.ui.home.GetDatasetThread;
import com.example.registration.ui.list_of_providers.ListOfProvidersFragment;
import com.example.registration.ui.my_bookings.AddRecommendationThread;
import com.example.registration.ui.my_bookings.GetRecommendationThread;
import com.example.registration.ui.my_bookings.MyBookingsDetails;
import com.example.registration.ui.my_bookings.MyDetailsAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServiceBookingActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView txtServiceProviderName,txtServiceType,txtAddress;
    EditText txtContactNumber;
    Button btnConfirmBooking;
    boolean bookedSuccessfully=false;
    String serviceType,location;
    ServiceProviderDetails serviceProviderDetails;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    Marker currenLocMarker = null;
    LinearLayout recommendation_card;
    String lastService="";
    String finalStr1="";
    static ExecutorService executorService;
    String[] recommendations=new String[3];
    TextView recommendation1,recommendation2,recommendation3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_booking);
        serviceType = getIntent().getStringExtra("serviceType");
        location = getIntent().getStringExtra("location");
        serviceProviderDetails = (ServiceProviderDetails) getIntent().getSerializableExtra("serviceProvider");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        //fetch last service
        txtServiceProviderName = (TextView) findViewById(R.id.txtServiceProviderName);
        txtServiceType = (TextView) findViewById(R.id.txtServiceType);
        txtContactNumber = (EditText) findViewById(R.id.txtContactNumber);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        recommendation_card = (LinearLayout) findViewById(R.id.recommendationBox);
        btnConfirmBooking = (Button) findViewById(R.id.btnConfirmBooking);

        recommendation1 = (TextView) findViewById(R.id.recommendation1);
        recommendation2 = (TextView) findViewById(R.id.recommendation2);
        recommendation3 = (TextView) findViewById(R.id.recommendation3);
        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact = txtContactNumber.getText().toString();
                if( contact.trim().equals(""))
                {
                    txtContactNumber.setError("Enter Phone Number");
                    return;
                }
                if ( !contact.matches("^[789]\\d{9}$") )
                {
                    txtContactNumber.setError("Enter Valid Phone Number");
                    return;
                }

                //Booking Done Here
                String emailAddress = ServicesOfferedActivity.emailAddress;
                String address = txtAddress.getText().toString().trim();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime now = LocalDateTime.now();
                String dateTime[] =dtf.format(now).split(" ");
                String date= dateTime[0];
                String time = dateTime[1];

                //CALL FROM HERE
                BookingDetails bookingDetails = new BookingDetails(serviceType,location,serviceProviderDetails,date,time,contact,address,emailAddress);
                bookingThread(bookingDetails);

            }
        });
        if (checkPermissions()) {
            mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        txtServiceProviderName.setText(serviceProviderDetails.getName().split(" in ")[0].trim());
        txtServiceType.setText(serviceType);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Confirm Booking" + "</font>"));
    }

    private void bookingThread(BookingDetails bookingDetails){
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Thread t = new Thread(new Runnable() {
            public void run(){
                executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new BookingThread(bookingDetails));
                try {
                    String jsonData = futureDataset.get();
                    if(jsonData.equals("200")){
                        bookedSuccessfully=true;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try{
            t.join();
        }catch (InterruptedException e){
        }
        if(bookedSuccessfully){
            //Make Calls to the API HERE!
            //we have current service type and last serviceType already
            fetchMyBookings();
//            System.out.println(lastService + " and "+serviceType);
            Toast.makeText(ServiceBookingActivity.this,"Booking Successfull!",Toast.LENGTH_LONG).show();
            btnConfirmBooking.setText("Booked");
            btnConfirmBooking.setEnabled(false);
            txtContactNumber.setEnabled(false);
            mapFragment.getView().setVisibility(View.GONE);
            recommendation1.setText(recommendations[0].trim());
            recommendation2.setText(recommendations[1].trim());
            recommendation3.setText(recommendations[2].trim());
            recommendation_card.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(ServiceBookingActivity.this,"Sorry!The booking was unsuccessfull!",Toast.LENGTH_LONG).show();
        }
    }


    public boolean checkPermissions(){
        if((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE},
                    1);

        }
        return false;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            currenLocMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.home1)).position(currentLocation).title("Me"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f));
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                txtAddress.setText(addresses.get(0).getAddressLine(0));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        LatLng serviceProviderLocation = new LatLng(serviceProviderDetails.getLatitude(),serviceProviderDetails.getLongitude());
        mMap.addMarker(new MarkerOptions().position(serviceProviderLocation).title(serviceProviderDetails.getName().split(" in ")[0].trim()));

    }
    private void fetchMyBookings(){
        try {
            System.out.println("INSIDE");
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String base=NetworkConnection.ip_address+":8081/mybookings";
                    URL url;
                    try {
                        url = new URL(base+"?email="+ServicesOfferedActivity.emailAddress);
                        URLConnection urlConnection=url.openConnection();
                        urlConnection.connect();
                        BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line="";
                        while(line!=null){
                            if(!line.equals("")){
                                finalStr1+=line;
                            }
                            line=br.readLine();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread1.start();
            thread1.join();
            JSONArray arrOfMyBoookings = new JSONArray(finalStr1);
            for(int i=0;i<arrOfMyBoookings.length();i++){
                JSONObject item = arrOfMyBoookings.getJSONObject(i);
                lastService =item.getString("serviceProviderType");
                break;
            }
            Thread t = new Thread(new Runnable() {
                public void run(){
                    Future<String> futureDataset1 = executorService.submit(new AddRecommendationThread(lastService,serviceType));
                    Future<String> futureDataset2 = executorService.submit(new GetRecommendationThread(serviceType));
                    try {
                        String jsonData1 = futureDataset1.get();
                        String jsonData2 = futureDataset2.get();

                        if(jsonData1.equals("200")){
                            System.out.println("LIST :" +jsonData2);
                            jsonData2=jsonData2.trim();
                            jsonData2 = jsonData2.substring(2,jsonData2.length()-2);
                            recommendations =jsonData2.split("\",\"");
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}