package com.dating.services;

import com.dating.models.users.DatingUser;
import com.dating.models.users.FormDatingUser;
import com.dating.repositories.UserRepository;
import com.dating.viewModels.datingUser.EditDatingUser;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class UserService
{
    UserRepository userRepository = new UserRepository();
    FormDatingUser input = new FormDatingUser();
    
    //---------------------------------------------- GENERAL -----------------------------------------------------//
    
    public void setInput(WebRequest form)
    {
        input.setInterestedIn(convertInterestedInStringToInt(form.getParameter("interestedininput")));
        input.setUsername(form.getParameter("usernameinput"));
        input.setEmail(form.getParameter("emailinput"));
        input.setAge(Integer.parseInt(form.getParameter("ageinput")));
        input.setPassword(form.getParameter("passwordinput"));
        input.setPassword(form.getParameter("confirmpasswordinput"));
        input.setDescription(form.getParameter("descriptioninput"));
        input.setTagsList(form.getParameter("tagslistinput"));
        
        int zipCodeInput = 0;
        String zipCodeInputString = form.getParameter("zipcodeinput");
        if(!(zipCodeInputString.equals("")))
        {
            zipCodeInput = Integer.parseInt(zipCodeInputString);
        }
        
        input.setZipCode(zipCodeInput);
    }
    
    /**
     * Konverterer en sex-værdi repræsenteret som String om til boolsk værdi
     * (male == false, female == true)
     *
     * @param sexInput Sex-String som konverteres til bool
     *
     * @return boolean Sex-værdien konverteret til boolsk værdi
     * */
    public static boolean resolveSexInput(String sexInput)
    {
        return sexInput.equals("female");
    }
    
    /**
     * Konverterer en interestedIn repræsenteret som String om til int værdi
     * (1 == males, 2 == females, 3 == males and females)
     *
     * @param interestedInInput InterestedIn-String som konverteres til int
     *
     * @return int InterestedIn konverteret til int
     * */
    public static int convertInterestedInStringToInt(String interestedInInput)
    {
        int interestedIn = -1;
        
        if(interestedInInput.equals("males"))
        {
            interestedIn = 0;
        }
        else if(interestedInInput.equals("females"))
        {
            interestedIn = 1;
        }
        else
        {
            interestedIn = 2;
        }
        
        return interestedIn;
    }
    
    public boolean checkIfPasswordsMatch(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }
    
    //---------------------------------------------- DATINGUSER -----------------------------------------------------//
    
    /**
     * Opretter (og returns) nyt DatingUser-obj. ud fra Create-Profile-Form
     *
     * @param dataFromCreateUserForm WebRequest knyttet til Create-Profile-Formen - her hentes info til nyt
     *                                DatingUser-obj.
     * @return DatingUser Det nye DatingUser-obj. som er blevet oprettet
     * */
    public DatingUser createDatingUser(WebRequest dataFromCreateUserForm)
    {
        DatingUser datingUser = null;
        
        // TODO: måske lave en metode som gør alt dette - returnerer en editdatinguser med alle disse fields
        boolean sex = resolveSexInput(dataFromCreateUserForm.getParameter("sexinput"));
        int interestedIn = convertInterestedInStringToInt(dataFromCreateUserForm.getParameter("interestedininput"));
        int age = Integer.parseInt(dataFromCreateUserForm.getParameter("ageinput"));
        String username = dataFromCreateUserForm.getParameter("usernameinput");
        String email =  dataFromCreateUserForm.getParameter("emailinput");
        String password = dataFromCreateUserForm.getParameter("passwordinput");
        String confirmPassword = dataFromCreateUserForm.getParameter("confirmpasswordinput");
        
        boolean usernameIsAvailable = userRepository.isUsernameAvailable(username);
        boolean emailIsAvailable = userRepository.isEmailAvailable(email);
        
        // brugeren bliver kun oprettet i systemet hvis:
        if(age >= 16 && usernameIsAvailable && emailIsAvailable && checkIfPasswordsMatch(password, confirmPassword))
        {
            datingUser = new DatingUser(sex, interestedIn, age, username, email, password);
        }
        
        return datingUser;
    }
    
    /**
     * Opdaterer (og returner) DatingUser-objekt til at indeholde ny data fra Edit-Profile-Form'en
     *
     * @param dataFromEditProfileForm WebRequest knyttet til Edit-Profile-Formen som bruges til at hente nye værdie fra
     * @param loggedInDatingUser DatingUser-objektet som opdateres og returneres
     *
     * @return DatingUser Det DatingUser-obj. fra param med opdateret data fra dataFromEditProfileForm
     * */
    public DatingUser updateLoggedInDatingUser(MultipartFile profilePictureFile, WebRequest dataFromEditProfileForm,
                                               DatingUser loggedInDatingUser)
    {
        try
        {
            setInput(dataFromEditProfileForm);
     
            // setter de attributter som en bruger SKAL have
            loggedInDatingUser.setInterestedIn(input.getInterestedIn());
            loggedInDatingUser.setUsername(input.getUsername());
            loggedInDatingUser.setEmail(input.getEmail());
            loggedInDatingUser.setAge(input.getAge());
            // setter de andre
            loggedInDatingUser.setDescription(input.getDescription());
            
            String test1 = input.getTagsList();
            ArrayList<String> test2 = loggedInDatingUser.convertStringToTagsList(input.getTagsList());
            
            
            loggedInDatingUser.setTagsList(loggedInDatingUser.convertStringToTagsList(input.getTagsList()));
            
            loggedInDatingUser.setPostalInfo(userRepository.findPostalInfoObjectFromZipCodeInput(input.getZipCode()));
            
            // Hvis NYT passwordInput, opdateres det - fordi ellers stilles det til ""
            if(input.getPassword()!=null)//!(input.getPassword().equals("")))
            {
                loggedInDatingUser.setPassword(input.getPassword());
            }
            
            // Hvis der ER profilePictureInput
            if(profilePictureFile.getBytes().length > 0)
            {
                // Hvis NYT profilePictureInput, opdateres det
                if(!(Arrays.equals(profilePictureFile.getBytes(), loggedInDatingUser.getProfilePictureBytes())))
                {
                    loggedInDatingUser.setProfilePictureBytes(profilePictureFile.getBytes());
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error in updateLoggedInDatingUser: " + e.getMessage());
        }
        
        return loggedInDatingUser;
    }
    
    //---------------------------------------------- EDIT PROFILE ------------------------------------------------//
    
    public boolean checkForProfileAlterations(MultipartFile profilePictureFile, WebRequest dataFromEditProfileForm,
                                              EditDatingUser editDatingUser)
    {
        boolean wasProfileAltered = false;
    
        try
        {
            setInput(dataFromEditProfileForm);
            
            boolean profilePictureAltered = false;
            
            // Hvis der ER profilePictureInput
            if(profilePictureFile.getBytes().length > 0)
            {
                // Hvis NYT profilePictureInput, opdateres det
                if(!(Arrays.equals(profilePictureFile.getBytes(), editDatingUser.getProfilePictureBytes())))
                {
                    profilePictureAltered = true;
                }
            }
            
            if(!(input.getInterestedIn() == editDatingUser.getInterestedIn()) ||
               !(Objects.equals(input.getUsername(), editDatingUser.getUsername())) ||
               !(Objects.equals(input.getEmail(), editDatingUser.getEmail())) ||
               !(input.getAge() == editDatingUser.getAge()) ||
               profilePictureAltered ||
               !(input.getZipCode() == editDatingUser.getZipCode()) ||
               !(Objects.equals(input.getPassword(), editDatingUser.getPassword())) ||
               !(Objects.equals(input.getConfirmPassword(), editDatingUser.getConfirmPassword())) ||
               !(Objects.equals(input.getDescription(), editDatingUser.getDescription())) ||
               !(Objects.equals(input.getTagsList(), editDatingUser.getTagsList())))
            {
                wasProfileAltered = true;
            }
        }
        catch(IOException e)
        {
            System.out.println("Error in checkForProfileAlterations: " + e.getMessage());
        }
      
        return wasProfileAltered;
    }
    
    public boolean checkUsernameEmailPasswordZipCode(WebRequest dataFromEditProfileForm, EditDatingUser editDatingUser)
    {
        boolean isEmailAvailable = true;
        boolean isUsernameAvailable = true;
        boolean doesPasswordInputsMatch = true;
        boolean isZipCodeValid = true;
        
        // hvis nyt emailinput
        if(!(dataFromEditProfileForm.getParameter("emailinput").equals(editDatingUser.getEmail())))
        {
            // hvis email ER available == returneres true
            // hvis email IKKE er available == returneres false
            isEmailAvailable = userRepository.isEmailAvailable(dataFromEditProfileForm.getParameter(
                    "emailinput"));
        }
    
        // hvis nyt usernameinput
        if(!(dataFromEditProfileForm.getParameter("usernameinput").equals(editDatingUser.getUsername())))
        {
            // hvis username ER available == returneres true
            // hvis userne IKKE er available == returneres false
            isUsernameAvailable = userRepository.isUsernameAvailable(dataFromEditProfileForm.getParameter(
                    "usernameinput"));
        }
        
        // hvis nyt password
        if(!(dataFromEditProfileForm.getParameter("passwordinput").equals("")))
        {
            doesPasswordInputsMatch = checkIfPasswordsMatch(dataFromEditProfileForm.getParameter("passwordinput"),
                    dataFromEditProfileForm.getParameter("confirmpasswordinput"));
        }
        String zipCodeInput = dataFromEditProfileForm.getParameter("zipcodeinput");
        
        // hvis ny zipCode
        if(!(zipCodeInput.equals("")))
        {
            if(Integer.parseInt(zipCodeInput) == (editDatingUser.getZipCode()))
            {
                int zipCode = Integer.parseInt(dataFromEditProfileForm.getParameter("zipcodeinput"));
    
                isZipCodeValid = userRepository.checkIfValidZipCode(zipCode);
            }
        }
        
    
        // hvis email + username er ledige OG password inputsmatcher == true
        return isEmailAvailable && isUsernameAvailable && doesPasswordInputsMatch && isZipCodeValid;
    }
    
    public EditDatingUser updateEditDatingUser(MultipartFile profilePictureFile, WebRequest dataFromEditProfileForm,
                                               String oldUsername, String oldEmail)
    {
        EditDatingUser editDatingUser = null;
        try
        {
            byte[] profilePictureBytes = profilePictureFile.getBytes();
            
            // TODO METODE TILFØJET
            setInput(dataFromEditProfileForm);
            /*
            int interestedInInput = convertInterestedInStringToInt(dataFromEditProfileForm.getParameter("interestedininput"));
            String usernameInput = dataFromEditProfileForm.getParameter("usernameinput");
            String emailInput = dataFromEditProfileForm.getParameter("emailinput");
            int ageInput = Integer.parseInt(dataFromEditProfileForm.getParameter("ageinput"));
            int zipCodeInput = Integer.parseInt(dataFromEditProfileForm.getParameter("zipcodeinput"));
            String passwordInput = dataFromEditProfileForm.getParameter("passwordinput");
            String confirmPasswordInput = dataFromEditProfileForm.getParameter("confirmpasswordinput");
            String descriptionInput = dataFromEditProfileForm.getParameter("descriptioninput");
            String tagsListInput = dataFromEditProfileForm.getParameter("tagslistinput");
            */
            
            // hvis nyt usernameInput IKKE er available
            if(!(userRepository.isUsernameAvailable(input.getUsername())))
            {
                input.setUsername(oldUsername);
            }
    
            if(!(userRepository.isEmailAvailable(input.getEmail())))
            {
                input.setEmail(oldEmail);
            }
    
            editDatingUser = new EditDatingUser(input.getInterestedIn(), input.getUsername(), input.getEmail(),
                    input.getAge(), input.getZipCode(), input.getPassword(), input.getConfirmPassword(),
                    profilePictureBytes, input.getDescription(), input.getTagsList());
            
        }
        catch(IOException e)
        {
            System.out.println("Error in updateEditDatingUser: " + e.getMessage());
        }
        
        return editDatingUser;
    }
    
    
}
