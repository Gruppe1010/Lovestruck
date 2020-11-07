package com.dating.models.users;

import java.util.ArrayList;

public class Admin extends User
{
    // TODO: overvej om denne skal slettes
    private ArrayList<DatingUser> blacklistedUsersList;
    
    
    // constructors
    public Admin(){}
    public Admin(String username, String email, String password)
    {
        super(username, email, password); // den kalder superklassens constructor
        
        blacklistedUsersList = null;
    }
    
    // getters + setters
    
    // andre metoder
    @Override
    public boolean isAdmin()
    {
        return true;
    }
    
    
    public User createAdmin()
    {
        // lav user-objekt
        // tilføj user-obj til database
        // findUserId(user-objekt) == finder lige id'et på det nye user-objekt
        // opretKandidatliste(idDatingUser);
        
        return new Admin("hej", "hej", "hej");
    }
    
    
    
    
}
