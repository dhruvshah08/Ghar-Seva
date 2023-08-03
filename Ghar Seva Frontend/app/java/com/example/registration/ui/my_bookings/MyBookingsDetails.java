package com.example.registration.ui.my_bookings;

public class MyBookingsDetails {
    String serviceproviderName,serviceProviderAddress,serviceProviderType,customerPhone,customerAddress,timeStamp;

    public MyBookingsDetails(String serviceproviderName, String serviceProviderAddress, String serviceProviderType, String customerPhone, String customerAddress, String timeStamp) {
        this.serviceproviderName = serviceproviderName;
        this.serviceProviderAddress = serviceProviderAddress;
        this.serviceProviderType = serviceProviderType;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.timeStamp = timeStamp;
    }

    public String getServiceproviderName() {
        return serviceproviderName;
    }

    public void setServiceproviderName(String serviceproviderName) {
        this.serviceproviderName = serviceproviderName;
    }

    public String getServiceProviderAddress() {
        return serviceProviderAddress;
    }

    public void setServiceProviderAddress(String serviceProviderAddress) {
        this.serviceProviderAddress = serviceProviderAddress;
    }

    public String getServiceProviderType() {
        return serviceProviderType;
    }

    public void setServiceProviderType(String serviceProviderType) {
        this.serviceProviderType = serviceProviderType;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
