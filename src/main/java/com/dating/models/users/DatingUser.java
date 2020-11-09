package com.dating.models.users;

import com.dating.models.PostalInfo;
import com.dating.viewModels.datingUser.EditDatingUser;
import com.dating.viewModels.datingUser.ViewProfileDatingUser;

import java.util.ArrayList;
import java.util.Collections;

public class DatingUser extends User
{
    // det er smart at have denne, fordi så skal vi ikke hente den fra databasen
    // (fx når brugeren skal tilgå sin favoritliste)
    private int idDatingUser;
    private boolean isBlacklisted; // 0 == false, 1 == true
    private boolean sex; // false == mænd, true == kvinder
    private int interestedIn; // 0 == mænd, 1 == kvinder, 2 == begge køn
    private int age;
    private String imagePath;
    // TODO private Image profilePicture;
    private String description;
    private ArrayList<String> tagsList;
    private PostalInfo postalInfo;
    private ArrayList<DatingUser> kandidatListe;
    
    // constructors
    public DatingUser(){}
    public DatingUser(boolean sex, int interestedIn, int age, String username, String email, String password)
    {
        super(username, email, password); // den kalder superklassens constructor
        this.isBlacklisted = false; // en bruger er aldrig blacklisted til at starte med når de opretter sig
        this.sex = sex;
        this.interestedIn = interestedIn;
        this.age = age;
        
        // sættes til standard-billede
        imagePath = "https://i.imgur.com/66Dq0AJ.png";
        description = null;
        tagsList = null;
        postalInfo = null;
        kandidatListe = null;
    }
    
    // getters + setters
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
    
    public int getIdDatingUser()
    {
        return idDatingUser;
    }
    public void setIdDatingUser(int idDatingUser)
    {
        this.idDatingUser = idDatingUser;
    }
    public boolean isBlacklisted()
    {
        return isBlacklisted;
    }
    public void setBlacklisted(boolean blacklisted)
    {
        this.isBlacklisted = blacklisted;
    }
    public boolean getSex()
    {
        return sex;
    }
    public void setSex(boolean sex)
    {
        this.sex = sex;
    }
    public int getInterestedIn()
    {
        return interestedIn;
    }
    public void setInterestedIn(int interestedIn)
    {
        this.interestedIn = interestedIn;
    }
    public int getAge()
    {
        return age;
    }
    public void setAge(int age)
    {
        this.age = age;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public ArrayList<String> getTagsList()
    {
        return tagsList;
    }
    public void setTagsList(ArrayList<String> tags)
    {
        this.tagsList = tags;
    }
    public PostalInfo getPostalInfo()
    {
        return postalInfo;
    }
    public void setPostalInfo(PostalInfo postalInfo)
    {
        this.postalInfo = postalInfo;
    }
    public ArrayList<DatingUser> getKandidatListe()
    {
        return kandidatListe;
    }
    public void setKandidatListe(ArrayList<DatingUser> kandidatListe)
    {
        this.kandidatListe = kandidatListe;
    }
    
    public String getImagePath()
    {
        return imagePath;
    }
    
    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }
    // andre metoder
    /**
     * Konverterer boolean til integer-værdi
     *
     * @param booleanInput Bool'en som skal konverteres
     
     * @return int Den konverterede boolean
     */
    public int convertBooleanToInt(Boolean booleanInput)
    {
        int convertedBoolean = 0; // convertedBoolean er nu sat til false - 0 == false
        
        if(booleanInput==true)
        {
            convertedBoolean = 1;
        }
        
        return convertedBoolean;
    }
    public boolean convertIntToBoolean(int intInput)
    {
        return intInput == 1;
    }
    
    @Override
    public boolean isDatingUser()
    {
        return true;
    }
    
    // TODO: måske overflødig
    public String convertInterestedInToString()
    {
        if(interestedIn==0)
        {
            return "males";
        }
        else if(interestedIn==1)
        {
            return "females";
        }
        
        return "malesandfemales";
    }
    
    public ArrayList<String> convertStringToTagsList(String tagsString)
    {
        ArrayList<String> tagsList = new ArrayList<>();
        
        if(tagsString!=null)
        {
            // splitter tagsString ved # og laver til String-array
            String[] stringArray = tagsString.split("#");
            
            // lægger stringArray over i tagsList-ArrayListen
            Collections.addAll(tagsList, stringArray);
            //fordi Stringen STARTER med et #, sætter den null ind på index 0 - derfor slettes index 0
            
            tagsList.remove(0);
            
            // tilføjer # foran hver string, fordi den sletter #'et siden den splitter ved #
            // TODO: find evt på en anden løsning end split"#" hvor den ikke sletter #'et
            addHashTag(tagsList);
        }
    
        return tagsList;
    }
    
    public void addHashTag(ArrayList<String> tagsList)
    {
        for(int i = 0; i < tagsList.size(); i++)
        {
            tagsList.set(i, "#" + tagsList.get(i));
            System.out.println("#" + tagsList.get(i));
        }
    }
    
    public String convertTagsListToString()
    {
        String tagsString = "";
        
        if(tagsList.size()>0)
        {
            for(String tag : tagsList)
            {
                tagsString += tag;
            }
        }
       return tagsString;
    }
    
    public EditDatingUser convertDatingUserToEditDatingUser()
    {
        int zipCode = 0;
        String tagsListString = null;
        
        if(postalInfo!=null) // hvis der ER noget i postInfo
        {
            zipCode = postalInfo.getZipCode();
        }
        if(tagsList != null) // hvis der ER noget i tagsList
        {
            tagsListString = convertTagsListToString();
        }
        
        return new EditDatingUser(interestedIn, super.getUsername(), super.getEmail(), age, zipCode,
                "", "", description, tagsListString);
    }
    
    // TODO lav denne
    public ViewProfileDatingUser convertDatingUserToViewProfileDatingUser()
    {
        /*
        private String sex; // false == mænd, true == kvinder
        private String username;
        // TODO: private image
        private int age;
        private String zipcodeAndCity;
        private String decription;
        private String tags;
        
         */
    
        int zipCode = 0;
        String tagsListString = null;
    
        if(postalInfo!=null) // hvis der ER noget i postInfo
        {
            zipCode = postalInfo.getZipCode();
        }
        if(tagsList != null) // hvis der ER noget i tagsList
        {
            tagsListString = convertTagsListToString();
        }
        
        convertSexToString()
    
        return new ViewProfileDatingUser(interestedIn, super.getUsername(), super.getEmail(), age, zipCode,
                "", "", description, tagsListString);
        
    }
    
    public String convertSexToString()
    {
        if(sex == true)
        {
            return "Kvinde";
        }
        return "Mand";
    }
    
}





/*
// lav seperate metoder til at finde relevant == username, email, password




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

 
