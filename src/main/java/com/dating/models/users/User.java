package com.dating.models.users;

public class User
{
    private String username;
    private String email;
    private String password;
    
    // constructors
    public User(){}
    public User(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    
    // getters + setters
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
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    
    // andre metoder
    /**
     * Siger at et objekt af klassen IKKE er en Admin - denne overskrives inde i Admin(hvor den returnerer true i
     * stedet)
     *
     * @return boolean Returns altid false
     */
    public boolean isAdmin()
    {
        return false;
    }
    /**
     * Siger at et objekt af klassen IKKE er en DatingUser - denne overskrives inde i DatingUser(hvor den returnerer
     * true i
     * stedet)
     *
     * @return boolean Returns altid false
     */
    public boolean isDatingUser()
    {
        return false;
    }
}
