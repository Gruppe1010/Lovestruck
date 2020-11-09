package com.dating.viewModels.datingUser;

import java.io.InputStream;

/**
 * Oplysningerne fra DatingUser som ses når bruger vises i lille firkant på forsiden, søg og på favoritlisten
 *
 *
 * */
public class PreviewDatingUser
{
    private InputStream imagePath;   // TODO: skal det være noget andet end inputstream??
    private String username;
    private int age;
    
    // Constructor
    public PreviewDatingUser(){}
    public PreviewDatingUser(InputStream imagePath, String username, int age)
    {
        this.imagePath = imagePath;
        this.username = username;
        this.age = age;
    }
    
    // getters + setters
    public InputStream getImagePath()
    {
        return imagePath;
    }
    public void setImagePath(InputStream imagePath)
    {
        this.imagePath = imagePath;
    }
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
}
