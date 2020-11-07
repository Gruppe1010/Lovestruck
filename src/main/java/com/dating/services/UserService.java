package com.dating.services;

import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.viewModels.datingUser.EditDatingUser;
import org.springframework.web.context.request.WebRequest;

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
    
    public static boolean checkIfPasswordsMatch(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }
    
    public boolean checkIfProfileWasEditted(WebRequest dataFromEditProfileForm, EditDatingUser editDatingUser)
    {
        boolean wasProfileEditted = false;
    
        int interestedInInput = convertInterestedInStringToInt(dataFromEditProfileForm.getParameter("interestedininput"));
        String username = dataFromEditProfileForm.getParameter("usernameinput");
        String email = dataFromEditProfileForm.getParameter("emailinput");
        int age = dataFromEditProfileForm.getParameter("ageinput");
        int zipCode = dataFromEditProfileForm.getParameter("zipcodeinput");
        String password = dataFromEditProfileForm.getParameter("usernameinput");
        String confirmPassword;
        // TODO private String imagePath;
        String description;
        String tagsList;
    
       
      
     
        private int zipCode;
        private String password;
        private String confirmPassword;
        // TODO private String imagePath;
        private String description;
        private String tagsList;
       
       
        
        
        if(!(interestedInInput == editDatingUser.getInterestedIn()) ||
           !(dataFromEditProfileForm.getParameter("usernameinput").equals(editDatingUser.getUsername())) ||
           !(dataFromEditProfileForm.getParameter("emailinput").equals(editDatingUser.getEmail())) ||
           !(dataFromEditProfileForm.getParameter("ageinput").equals(editDatingUser.getAge())) ||
           !(dataFromEditProfileForm.getParameter("zipcodeinput").equals(editDatingUser.getZipCode())) ||
           !(dataFromEditProfileForm.getParameter("passwordinput").equals(editDatingUser.getPassword())) ||
            !(dataFromEditProfileForm.getParameter("usernameinput").equals(editDatingUser.getUsername())) ||
        )
        {
            wasProfileEditted = true;
        }
        
        
      
        return wasProfileEditted;
        
    }
    
    
    
}
