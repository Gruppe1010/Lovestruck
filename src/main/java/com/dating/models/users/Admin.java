package com.dating.models.users;

public class Admin extends User
{
    
    // constructors
    public Admin(){}
    public Admin(String username, String email, String password)
    {
        // den kalder superklassens constructor
        super(username, email, password);
    }
    
    // getters + setters fra SUPER-klasse
    public String getUsername()
    {
        return super.getUsername();
    }
    public void setUsername(String username)
    {
        super.setUsername(username);
    }
    public String getEmail()
    {
        return super.getEmail();
    }
    public void setEmail(String email)
    {
        super.setEmail(email);
    }
    public String getPassword()
    {
        return super.getPassword();
    }
    public void setPassword(String password)
    {
        super.setPassword(password);
    }
    
    // andre metoder
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
    
}
