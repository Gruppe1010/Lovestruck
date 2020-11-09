package com.dating.viewModels.datingUser;

public class ViewProfileDatingUser
{
    // tilføj de attributter som vi skal vise på denne html
    // TODO måske slettes permanent: private boolean isBlacklisted;
   
    private String username;
    // TODO: private image sex, age år
    private String sexAndAge; // TODO! overvej at sammensmelte sex og age - da det alligevel skal stå samme sted
    private String zipCodeAndCity;
    private String description;
    private String tags;
    
    
    // constructor
    public ViewProfileDatingUser(){}
    public ViewProfileDatingUser(String username,String sexAndAge, String zipCodeAndCity, String description,
                                 String tags)
    {
        this.username = username;
        this.sexAndAge = sexAndAge;
        this.zipCodeAndCity = zipCodeAndCity;
        this.description = description;
        this.tags = tags;
    }
    
    //getters + setters
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
    
    // metoder
    /*
    public boolean isMale()
    {
        // male: sex == false OG female: sex == true
        // DERFOR:
        // hvis sex == true, er den female, og metoden skal altså returnere false (fordi den tjekker om den er male)
        // hvis sex == false er den male, og metoden skal returne true
        // == derfor skal isMale ALTID returnere den omvendte værdi af sex
        return !sex;
    }
    
    public boolean isFemale()
    {
        // male: sex == false OG female: sex == true
        // DERFOR:
        // hvis sex == true, er den female, og metoden skal returne true (fordi den tjekker om den er female)
        // hvis sex == fale, er den male, og metoden skal returne false
        // == derfor skal isFemale ALTID returnere samme værdi som sex
        return sex;
    }
    
     */
    
}
