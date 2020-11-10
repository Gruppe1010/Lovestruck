package com.dating.viewModels.datingUser;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.InputStream;

/**
 * Oplysningerne fra DatingUser som ses når bruger vises i lille firkant på forsiden, søg og på favoritlisten
 *
 *
 * */
public class PreviewDatingUser
{
    private int id;
    private byte[] profilePictureBytes;
    private String username;
    private int age;
    private String base64;
    
    // Constructor
    public PreviewDatingUser(){}
    public PreviewDatingUser(int id, byte[] profilePictureBytes, String username, int age)
    {
        this.id = id;
        this.profilePictureBytes = profilePictureBytes;
        this.username = username;
        this.age = age;
        this.base64 = byteArrayAs64String();
    }
    
    // getters + setters
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
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
    public int getAge()
    {
        return age;
    }
    public void setAge(int age)
    {
        this.age = age;
    }
    public String getBase64()
    {
        return base64;
    }
    public void setBase64(String base64)
    {
        this.base64 = base64;
    }
    
    // Andre metoder
    private String byteArrayAs64String()
    {
        return Base64.encodeBase64String(this.profilePictureBytes);
    }
}
