package com.dating.models;

public class PostalInfo
{
    // attributter
    private int zipCode;
    private String city;
    
    // constructor
    public PostalInfo(int zipCode, String city)
    {
        this.zipCode = zipCode;
        this.city = city;
    }
    
    // getter og setters
    public int getZipCode()
    {
        return zipCode;
    }
    public void setZipCode(int zipCode)
    {
        this.zipCode = zipCode;
    }
    public String getCity()
    {
        return city;
    }
    public void setCity(String city)
    {
        this.city = city;
    }
    
    
    
    
    
    
}





/*
// lav seperate metoder til at finde relevant == username, email, password

public void createUser()
{
    // lav user-objekt
    // tilføj user-obj til database
    // findUserId(user-objekt) == finder lige id'et på det nye user-objekt
    // opretKandidatliste(idDatingUser);

}


public void opretKandidatListe(int idDatingUser)
{
    // sql-command: "create table kandidatList" + idDatingUser
}

public void tilføjBrugerTilKandidatListe(int idDatingUser, User targetUser)
{
    // findUserId(targetUser)
    // sql: "insert ? into ?"
    
    // 1.? == idTargetUser
    // 2.? == KandidatList+idDatingUser (== navnet på kandidatlisten)

public int findUserId(User user)
{
    // find idDatingUser på user-objekt
    // return
}

 */

 
