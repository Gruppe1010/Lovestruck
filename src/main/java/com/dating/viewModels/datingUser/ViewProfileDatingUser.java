package com.dating.viewModels.datingUser;

public class ViewProfileDatingUser
{
    // tilføj de attributter som vi skal vise på denne html
    private boolean isBlacklisted;
    private boolean sex; // false == mænd, true == kvinder
    private String username;
    // TODO: private image
    private int age;
    private String decription;
    // TODO: overvej om by skal på
    private String tags;
    
    
    
    
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
    
}
