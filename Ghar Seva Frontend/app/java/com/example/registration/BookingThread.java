package com.example.registration;

import android.os.StrictMode;
import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BookingThread implements Callable<String> {
    BookingDetails bookingDetails;
    static ExecutorService executorService;
    boolean flag=false;
    public BookingThread(BookingDetails bookingDetails){
        this.bookingDetails = bookingDetails;
        try{
            createUserAndServiceProviderObjects(bookingDetails);
        }catch (InterruptedException e){
        }

    }
    @Override
    public String call() {
        return flag?"200":"400";
    }
    private void createUserAndServiceProviderObjects(BookingDetails bookingDetails) throws  InterruptedException{
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Thread t = new Thread(new Runnable() {
            public void run(){
                executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> addSPfutureDataset = executorService.submit(new AddServiceProviderThread(bookingDetails.getServiceType(),bookingDetails.getServiceProviderDetails().getName(),bookingDetails.getServiceProviderDetails().getAddress(),bookingDetails.getServiceProviderDetails().isVerified(),bookingDetails.getServiceProviderDetails().getRating(),bookingDetails.getServiceProviderDetails().getLatitude(),bookingDetails.getServiceProviderDetails().getLongitude(),bookingDetails.getServiceProviderDetails().getPopularity()));
                try {
                    String result2 = addSPfutureDataset.get();
                    if("200".equals(result2)){
                        Future<String> settingRelationship = executorService.submit(new AddBookingThread(bookingDetails.getEmail(),bookingDetails.getServiceProviderDetails().getName(),bookingDetails.getServiceType(),bookingDetails.getOrderDate(),bookingDetails.getOrderTime(),bookingDetails.getCustomerNumber(),bookingDetails.getCustomerAddress()));
                        String finalResult = settingRelationship.get();
                        if("200".equals(finalResult)){
                           flag=true;
                        }
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
    }
}
