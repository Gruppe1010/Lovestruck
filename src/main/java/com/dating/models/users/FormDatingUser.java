package com.dating.models.users;

import com.dating.models.PostalInfo;

import java.util.ArrayList;

public class FormDatingUser
{
    private int interestedIn; // 0 == mænd, 1 == kvinder, 2 == begge køn
    private String username;
    private String email;
    private int age;
    private int zipCode;
    private String password;
    private String confirmPassword;
    private String description;
    private String tagsList;
    
    // getters + setters
    public int getInterestedIn()
    {
        return interestedIn;
    }
    public void setInterestedIn(int interestedIn)
    {
        this.interestedIn = interestedIn;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public int getAge()
    {
        return age;
    }
    public void setAge(int age)
    {
        this.age = age;
    }
    public int getZipCode()
    {
        return zipCode;
    }
    public void setZipCode(int zipCode)
    {
        this.zipCode = zipCode;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String passwordInput)
    {
        this.password = password;
    }
    public String getConfirmPassword()
    {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword)
    {
        this.confirmPassword = confirmPassword;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getTagsList()
    {
        return tagsList;
    }
    public void setTagsList(String tagsList)
    {
        this.tagsList = tagsList;
    }
}
