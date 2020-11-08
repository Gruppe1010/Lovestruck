package com.dating.services;

import com.dating.models.PostalInfo;
import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.viewModels.datingUser.EditDatingUser;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

public class UserService
{
    UserRepository userRepository = new UserRepository();
    
    ///////////  metoder
    public static boolean resolveSexInput(String sexInput)
    {
        return sexInput.equals("female");
    }
    
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
    
    /////////// datingUser
    public DatingUser updateLoggedInDatingUser(WebRequest dataFromEditProfileForm, DatingUser loggedInDatingUser)
    {
        // TODO: overvej at rykke linjer ud i metode
        int interestedInInput = convertInterestedInStringToInt(dataFromEditProfileForm.getParameter("interestedininput"));
        String usernameInput = dataFromEditProfileForm.getParameter("usernameinput");
        String emailInput = dataFromEditProfileForm.getParameter("emailinput");
        int ageInput = Integer.parseInt(dataFromEditProfileForm.getParameter("ageinput"));
        int zipCodeInput = Integer.parseInt(dataFromEditProfileForm.getParameter("zipcodeinput"));
        String passwordInput = dataFromEditProfileForm.getParameter("passwordinput");
        // TODO private String imagePath;
        String descriptionInput = dataFromEditProfileForm.getParameter("descriptioninput");
        String tagsListInput = dataFromEditProfileForm.getParameter("tagslistinput");
        
        loggedInDatingUser.setInterestedIn(interestedInInput);
        loggedInDatingUser.setUsername(usernameInput);
        loggedInDatingUser.setEmail(emailInput);
        loggedInDatingUser.setAge(ageInput);
        
        if(zipCodeInput!=0) // hvis der er en zipCode
        {
            loggedInDatingUser.setPostalInfo(userRepository.findPostalInfoObjectFromIdPostalInfo(zipCodeInput));
        }
   
        if(passwordInput != null) // hvis nyt password
        {
            loggedInDatingUser.setPassword(passwordInput);
        }
        
        if(descriptionInput != null) // hvis der er description
        {
            loggedInDatingUser.setDescription(descriptionInput);
        }
        
        if(tagsListInput != null) // hvis der er tags
        {
            loggedInDatingUser.setTagsList(loggedInDatingUser.convertStringToTagsList(tagsListInput));
        }
        
        return loggedInDatingUser;
    }
    
    public DatingUser createDatingUser(WebRequest dataFromCreateUserForm)
    {
        DatingUser datingUser = null;
        
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
    
    public boolean checkIfPasswordsMatch(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }
    
    
    /////////// editDatingUser
    public boolean checkIfProfileWasEditted(WebRequest dataFromEditProfileForm, EditDatingUser editDatingUser)
    {
        boolean wasProfileEditted = false;
    
        //
        int interestedInInput = convertInterestedInStringToInt(dataFromEditProfileForm.getParameter("interestedininput"));
        String usernameInput = dataFromEditProfileForm.getParameter("usernameinput");
        String emailInput = dataFromEditProfileForm.getParameter("emailinput");
        int ageInput = Integer.parseInt(dataFromEditProfileForm.getParameter("ageinput"));
        int zipCodeInput = Integer.parseInt(dataFromEditProfileForm.getParameter("zipcodeinput"));
        String passwordInput = dataFromEditProfileForm.getParameter("passwordinput");
        String confirmPasswordInput = dataFromEditProfileForm.getParameter("confirmpasswordinput");
        // TODO private String imagePath;
        String descriptionInput = dataFromEditProfileForm.getParameter("descriptioninput");
        String tagsListInput = dataFromEditProfileForm.getParameter("tagslistinput");
        
        if(!(interestedInInput == editDatingUser.getInterestedIn()) ||
           !(Objects.equals(usernameInput, editDatingUser.getUsername())) ||
           !(Objects.equals(emailInput, editDatingUser.getEmail())) ||
           !(ageInput == editDatingUser.getAge()) ||
           !(zipCodeInput == editDatingUser.getZipCode()) ||
           !(Objects.equals(passwordInput, editDatingUser.getPassword())) ||
           !(Objects.equals(confirmPasswordInput, editDatingUser.getConfirmPassword())) ||
           !(Objects.equals(descriptionInput, editDatingUser.getDescription())) ||
           !(Objects.equals(tagsListInput, editDatingUser.getTagsList())))
        {
            wasProfileEditted = true;
        }
      
        return wasProfileEditted;
    }
    
    public boolean checkUsernameEmailPassword(WebRequest dataFromEditProfileForm, EditDatingUser editDatingUser)
    {
        boolean isEmailAvailable = true;
        boolean isUsernameAvailable = true;
        boolean doesPasswordInputsMatch = true;
        
        // hvis nyt emailinput
        if(!(Objects.equals(dataFromEditProfileForm.getParameter("emailinput"), editDatingUser.getEmail())))
        {
            // hvis email ER available == returneres true
            // hvis email IKKE er available == returneres false
            isEmailAvailable = userRepository.isEmailAvailable(dataFromEditProfileForm.getParameter(
                    "emailinput"));
        }
    
        // hvis nyt usernameinput
        if(!(Objects.equals(dataFromEditProfileForm.getParameter("usernameinput"), editDatingUser.getUsername())))
        {
            // hvis username ER available == returneres true
            // hvis userne IKKE er available == returneres false
            isUsernameAvailable = userRepository.isUsernameAvailable(dataFromEditProfileForm.getParameter(
                    "usernameinput"));
        }
        
        // hvis nyt password
        System.out.println("passwordInput: " + dataFromEditProfileForm.getParameter("passwordinput").equals(""));
        if(!(dataFromEditProfileForm.getParameter("passwordinput").equals("")))
        {
            doesPasswordInputsMatch = checkIfPasswordsMatch(dataFromEditProfileForm.getParameter("passwordinput"),
                    dataFromEditProfileForm.getParameter("confirmpasswordinput"));
        }
    
        // hvis email + username er ledige OG password inputsmatcher == true
        return isEmailAvailable && isUsernameAvailable && doesPasswordInputsMatch;
    }
    
    public EditDatingUser updateEditDatingUser(WebRequest dataFromEditProfileForm)
    {
        // TODO: find ud af om alle de der linjer kan lægges ud i en metode for sig
        int interestedInInput = convertInterestedInStringToInt(dataFromEditProfileForm.getParameter("interestedininput"));
        String usernameInput = dataFromEditProfileForm.getParameter("usernameinput");
        String emailInput = dataFromEditProfileForm.getParameter("emailinput");
        int ageInput = Integer.parseInt(dataFromEditProfileForm.getParameter("ageinput"));
        int zipCodeInput = Integer.parseInt(dataFromEditProfileForm.getParameter("zipcodeinput"));
        String passwordInput = dataFromEditProfileForm.getParameter("passwordinput");
        String confirmPasswordInput = dataFromEditProfileForm.getParameter("confirmpasswordinput");
        // TODO private String imagePath;
        String descriptionInput = dataFromEditProfileForm.getParameter("descriptioninput");
        String tagsListInput = dataFromEditProfileForm.getParameter("tagslistinput");
        
        return new EditDatingUser(interestedInInput, usernameInput, emailInput, ageInput, zipCodeInput, passwordInput,
                confirmPasswordInput, descriptionInput, tagsListInput);
    }
    
    
    
    
}
