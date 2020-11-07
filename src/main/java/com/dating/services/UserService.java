package com.dating.services;

import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import org.springframework.web.context.request.WebRequest;

public class UserService
{
    UserRepository userRepository = new UserRepository();
    
    
    public DatingUser createDatingUser(WebRequest dataFromCreateUserForm)
    {
        DatingUser datingUser = null;
        
        boolean sex = resolveSexInput(dataFromCreateUserForm.getParameter("sexinput"));
        int interestedIn = resolveInterestedInInput(dataFromCreateUserForm.getParameter("interestedininput"));
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
    
    public static int resolveInterestedInInput(String interestedInInput)
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
    
    
}
