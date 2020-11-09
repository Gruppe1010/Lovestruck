package com.dating.viewModels.datingUser;

public class ViewProfileDatingUser
{
    // tilføj de attributter som vi skal vise på denne html
    // TODO måske slettes permanent: private boolean isBlacklisted;
    private String sex; // false == mænd, true == kvinder
    private String username;
    // TODO: private image
    private int age; // tODO lav om til String - 25 år fx
    private String zipCodeAndCity;
    private String description;
    private String tags;
    
    
    // constructor
    public ViewProfileDatingUser(){}
    public ViewProfileDatingUser(String sex, String username, int age, String zipCodeAndCity, String description,
                                 String tags)
    {
        this.sex = sex;
        this.username = username;
        this.age = age;
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
    public int getAge()
    {
        return age;
    }
    public void setAge(int age)
    {
        this.age = age;
    }
    public String getSex()
    {
        return sex;
    }
    public void setSex(String sex)
    {
        this.sex = sex;
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
