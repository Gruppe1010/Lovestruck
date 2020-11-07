package com.dating.services;

import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.viewModels.datingUser.EditDatingUser;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

public class UserService
{
    UserRepository userRepository = new UserRepository();
    
    
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
    
    public boolean checkIfPasswordsMatch(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }
    
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
        // boolean to return
        boolean isInputValid = false;
        
        boolean isEmailAvailable = true;
        boolean isUsernameAvailable = true;
        boolean doPasswordInputsMatch = checkIfPasswordsMatch(dataFromEditProfileForm.getParameter("passwordinput"),
                                                              dataFromEditProfileForm.getParameter("confirmpasswordinput"));
    
        // hvis nyt emailinput
        if(Objects.equals(dataFromEditProfileForm.getParameter("emailinput"), editDatingUser.getEmail()))
        {
            isEmailAvailable = userRepository.isEmailAvailable(dataFromEditProfileForm.getParameter(
                    "emailinput"));
        }
    
        // hvis nyt usernameinput
        if(Objects.equals(dataFromEditProfileForm.getParameter("usernamenput"), editDatingUser.getUsername()))
        {
            isUsernameAvailable = userRepository.isUsernameAvailable(dataFromEditProfileForm.getParameter(
                    "usernameinput"));
        }
    

        
        
        
        
            return isInputValid;
    }
    
    public boolean emailWasChanged()
    {
    
    }
    
    
    
}
