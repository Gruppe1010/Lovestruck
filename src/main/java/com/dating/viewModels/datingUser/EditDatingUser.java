package com.dating.viewModels.datingUser;

import com.dating.models.PostalInfo;
import com.dating.models.users.DatingUser;

import java.util.ArrayList;

public class EditDatingUser
{
    // de ting på DatingUser som vi skal vise i viewet på /editProfile:
    private int interestedIn; // 0 == mænd, 1 == kvinder, 2 == begge køn
    private String username;
    private String email;
    private int age;
    private int zipCode;
    private String password;
    private String confirmPassword;
    // TODO private String imagePath;
    private String description;
    private String tagsList;
    
    // constructor
    public EditDatingUser(int interestedIn, String username, String email, int age, int zipCode, String password, String confirmPassword, String description, String tagsList)
    {
        this.interestedIn = interestedIn;
        this.username = username;
        this.email = email;
        this.age = age;
        this.zipCode = zipCode;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.description = description;
        this.tagsList = tagsList;
    }
    
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
    public void setPassword(String password)
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
    
    // andre metoder
    
    public String checkIfInterestedInMales()
    {
        if(interestedIn == 0)
        {
            return ""+ interestedIn;
        }
        return null;
    }
    
    public String checkIfInterestedInFemales()
    {
        if(interestedIn == 1)
        {
            return "" + interestedIn;
        }
        return null;
    }
    
    public String checkIfInterestedInMalesAndFemales()
    {
        if(interestedIn == 2)
        {
            return ""+ interestedIn;
        }
        return null;
    }
    
    
    public String displayZipCode()
    {
        if(zipCode == 0)
        {
            return "";
        }
        return Integer.toString(zipCode);
    }
    
}
