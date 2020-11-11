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
    
    /**
     * Siger at et objekt af klassen er en Admin - Overskriver superklassen User's metode som siger false
     *
     * @return boolean Returns altid true, hvis denne overskrevede metode kaldes - fordi det er en Admin
     */
    @Override
    public boolean isAdmin()
    {
        return true;
    }
    
    // TODO: slet??
    public User createAdmin()
    {
        
        return new Admin("hej", "hej", "hej");
    }
    
    
    
    
}
