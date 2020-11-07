package com.dating.models.users;

public class User
{
    private String username;
    private String email;
    private String password;
    
    // constructors
    public User(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public User(){}
    
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
    
    public boolean isAdmin()
    {
        return false;
    }
    public boolean isDatingUser()
    {
        return false;
    }
}
