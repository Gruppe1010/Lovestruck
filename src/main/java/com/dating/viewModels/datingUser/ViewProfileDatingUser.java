package com.dating.viewModels.datingUser;

import org.apache.tomcat.util.codec.binary.Base64;

public class ViewProfileDatingUser
{
    // tilføj de attributter som vi skal vise på denne html
    // TODO måske slettes permanent: private boolean isBlacklisted;
   
    private int idViewProfileDatingUser;
    private String username;
    private String sexAndAge; // TODO! overvej at sammensmelte sex og age - da det alligevel skal stå samme sted
    private String zipCodeAndCity;
    private String description;
    private String tags;
    private byte[] profilePictureBytes;
    private String base64;
    private boolean isOnFavouritesList;
    
    // constructor
    public ViewProfileDatingUser(){}
    public ViewProfileDatingUser(int idViewProfileDatingUser, String username,String sexAndAge, String zipCodeAndCity,
                                 String description, String tags, byte[] profilePictureBytes)
    {
        this.idViewProfileDatingUser = idViewProfileDatingUser;
        this.username = username;
        this.sexAndAge = sexAndAge;
        this.zipCodeAndCity = zipCodeAndCity;
        this.description = description;
        this.tags = tags;
        this.profilePictureBytes = profilePictureBytes;
        this.base64 = byteArrayAs64String();
        this.isOnFavouritesList = false;
    }
    
    
    //getters + setters
    public int getIdViewProfileDatingUser()
    {
        return idViewProfileDatingUser;
    }
    public void setIdViewProfileDatingUser(int idViewProfileDatingUser)
    {
        this.idViewProfileDatingUser = idViewProfileDatingUser;
    }
    public byte[] getProfilePictureBytes()
    {
        return profilePictureBytes;
    }
    public void setProfilePictureBytes(byte[] profilePictureBytes)
    {
        this.profilePictureBytes = profilePictureBytes;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getSexAndAge()
    {
        return sexAndAge;
    }
    public void setSexAndAge(String sexAndAge)
    {
        this.sexAndAge = sexAndAge;
    }
    public String getZipCodeAndCity()
    {
        return zipCodeAndCity;
    }
    public void setZipCodeAndCity(String zipcodeAndCity)
    {
        this.zipCodeAndCity = zipCodeAndCity;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getTags()
    {
        return tags;
    }
    public void setTags(String tags)
    {
        this.tags = tags;
    }
    public String getBase64()
    {
        return base64;
    }
    public void setBase64(String base64)
    {
        this.base64 = base64;
    }
    public boolean isOnFavouritesList()
    {
        return isOnFavouritesList;
    }
    public void setIsOnFavouritesList(boolean onFavouritesList)
    {
        this.isOnFavouritesList = onFavouritesList;
    }
    
    // andre metoder
    private String byteArrayAs64String()
    {
        return Base64.encodeBase64String(this.profilePictureBytes);
    }
    
    
    
}
