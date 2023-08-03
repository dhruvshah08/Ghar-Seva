package com.example.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;

import com.example.registration.ui.map_of_providers.MapOfProvidersFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ServiceProviderAdapter extends ArrayAdapter<ServiceProviderDetails> {
    private final Activity context;
    OnSwipeTouchListener onSwipeTouchListener;
    ArrayList<ServiceProviderDetails> listOfServiceProviders;

    TextView txtName,txtRating,txtAddress;
    Button btnBook;
    ImageView imgVerified;
    NavController navController;
    String serviceType,location;

    public ServiceProviderAdapter(Activity context, ArrayList<ServiceProviderDetails> listOfServiceProviders,String serviceType,String location,NavController navController) {
        super(context, R.layout.service_provider_card,listOfServiceProviders);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.listOfServiceProviders=listOfServiceProviders;
        this.serviceType = serviceType;
        this.location = location;
        this.navController = navController;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ServiceProviderDetails serviceProvider = getItem(position);
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.service_provider_card, null,true);

        txtName = (TextView)  rowView.findViewById(R.id.txtName);
        txtAddress = (TextView)  rowView.findViewById(R.id.txtAddress);
        txtRating = (TextView)  rowView.findViewById(R.id.txtRating);
        btnBook = (Button) rowView.findViewById(R.id.btnBook);
        imgVerified = (ImageView) rowView.findViewById(R.id.imgVerified);

        if((position%2)==0){
            rowView.setBackgroundResource(R.color.gainsborro);
        }

        txtName.setText(serviceProvider.getName().split(" in ")[0].trim());
        txtRating.setText(serviceProvider.getRating()+"");
        txtAddress.setText(serviceProvider.getAddress());
        if(!serviceProvider.isVerified()){
            imgVerified.setVisibility(View.INVISIBLE);
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ServiceBookingActivity.class);
                intent.putExtra("serviceType",serviceType);
                intent.putExtra("location",location);
                intent.putExtra("serviceProvider",serviceProvider);
                context.startActivity(intent);
            }
        });
        onSwipeTouchListener = new OnSwipeTouchListener(getContext(),txtAddress,serviceProvider,navController);
        onSwipeTouchListener = new OnSwipeTouchListener(getContext(),txtName,serviceProvider,navController);
        onSwipeTouchListener = new OnSwipeTouchListener(getContext(),parent,serviceProvider,navController);
        onSwipeTouchListener = new OnSwipeTouchListener(getContext(),rowView,serviceProvider,navController);
        return rowView;
    }

    public static class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        ServiceProviderDetails serviceProvider;
        Context context;
        NavController navController;
        OnSwipeTouchListener(Context ctx, View mainView,ServiceProviderDetails serviceProvider,NavController navController) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            this.serviceProvider = serviceProvider;
            context = ctx;
            this.navController = navController;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
        public class GestureListener extends
                GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX < 0) {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }

                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        void onSwipeLeft() {
            LatLng latLng = new LatLng(serviceProvider.getLatitude(),serviceProvider.getLongitude());
            MapOfProvidersFragment.inFocus = latLng;
            navController.navigate(R.id.navigation_mapOfProviders);
            this.onSwipe.swipeLeft();
        }
        interface onSwipeListener {
            void swipeLeft();
        }
        onSwipeListener onSwipe;
    }

}
