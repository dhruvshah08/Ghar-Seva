package com.example.registration;

public class BookingDetails {
    String serviceType;
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    ServiceProviderDetails serviceProviderDetails;
    String orderDate;
    String orderTime;
    String customerNumber;
    String customerAddress;
    String location;
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceProviderDetails getServiceProviderDetails() {
        return serviceProviderDetails;
    }

    public void setServiceProviderDetails(ServiceProviderDetails serviceProviderDetails) {
        this.serviceProviderDetails = serviceProviderDetails;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public BookingDetails(String serviceType, String location,ServiceProviderDetails serviceProviderDetails, String orderDate, String orderTime,String customerNumber,String customerAddress,String email) {
        this.serviceType = serviceType;
        this.serviceProviderDetails = serviceProviderDetails;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.location=location;
        this.email=email;
        this.customerAddress=customerAddress;
        this.customerNumber=customerNumber;
    }
}
