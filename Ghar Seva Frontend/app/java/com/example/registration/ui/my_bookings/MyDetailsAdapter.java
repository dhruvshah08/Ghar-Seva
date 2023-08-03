package com.example.registration.ui.my_bookings;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.example.registration.ExtendedServiceProviderDetails;
import com.example.registration.R;

import java.util.ArrayList;
import java.util.Random;

public class MyDetailsAdapter extends ArrayAdapter<MyBookingsDetails> {

    private final Activity context;
    ArrayList<MyBookingsDetails> listOfMyBookings;
    TextView txtServiceType,txtTime,txtCustomerPhoneNumber,txtCustomerAddress,txtServiceProviderName,txtServiceProviderAddress;

    public MyDetailsAdapter(Activity context, ArrayList<MyBookingsDetails> listOfMyBookings) {
        super(context, R.layout.my_booking_card,listOfMyBookings);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.listOfMyBookings=listOfMyBookings;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyBookingsDetails bookingsDetails = getItem(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.my_booking_card, null, true);
        LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.innerArea);
        Random rand = new Random();
        float r = rand.nextFloat() / 2f + 0.5f;
        float g = rand.nextFloat() / 2f + 0.5f;
        float b = rand.nextFloat() / 2f + 0.5f;
        layout.setBackgroundColor(Color.rgb(r, g, b));
        txtServiceType = rowView.findViewById(R.id.txtServiceType);
        txtTime= rowView.findViewById(R.id.txtTime);
        txtCustomerPhoneNumber = rowView.findViewById(R.id.txtCustomerPhoneNumber);
        txtCustomerAddress = rowView.findViewById(R.id.txtCustomerAddress);
        txtServiceProviderName = rowView.findViewById(R.id.txtServiceProviderName);
        txtServiceProviderAddress = rowView.findViewById(R.id.txtServiceProviderAddress);

        txtCustomerAddress.setText(bookingsDetails.getCustomerAddress());
        txtCustomerPhoneNumber.setText(bookingsDetails.getCustomerPhone());
        txtServiceProviderName.setText(bookingsDetails.getServiceproviderName());
        txtServiceProviderAddress.setText(bookingsDetails.getServiceProviderAddress());
        txtServiceType.setText(bookingsDetails.getServiceProviderType().toUpperCase());
        txtTime.setText(bookingsDetails.getTimeStamp());

        return rowView;
    }
}
