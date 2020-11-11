package com.dating.models;

public class PostalInfo
{
    // attributter
    private int zipCode;
    private String city;
    
    // constructor
    public PostalInfo(int zipCode, String city)
    {
        this.zipCode = zipCode;
        this.city = city;
    }
    
    // getter og setters
    public int getZipCode()
    {
        return zipCode;
    }
    public void setZipCode(int zipCode)
    {
        this.zipCode = zipCode;
    }
    public String getCity()
    {
        return city;
    }
    public void setCity(String city)
    {
        this.city = city;
    }
}
 
