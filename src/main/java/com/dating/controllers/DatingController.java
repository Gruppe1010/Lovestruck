package com.dating.controllers;

import com.dating.models.users.Admin;
import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.services.UserService;
import com.dating.viewModels.datingUser.EditDatingUser;
import com.dating.viewModels.datingUser.ViewProfileDatingUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;


@Controller
public class DatingController
{
    Admin loggedInAdmin = null;
    DatingUser loggedInDatingUser = null;
    
    ViewProfileDatingUser viewProfileDatingUser = null;
    EditDatingUser editDatingUser = null;
    
    
    Error error = null;
    UserService userService = new UserService();
    UserRepository userRepository = new UserRepository();
    
    
    /*------------------ GET -------------------*/
    
    /**
     * Returnerer index-html-side ved /-request
     *
     * @return String html-siden
     */
    @GetMapping("/")
    public String index()
    {
        return "index";
    }
    
    @GetMapping("/logIn")
    public String logIn()
    {
        return "login"; // html
    }
    
    @GetMapping("/startPage")
    public String startPage(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/startpage"; // html
    }
    
    @GetMapping("/chatPage")
    public String chatPage(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/chatpage"; // html
    }
    
    @GetMapping("/favouritesPage")
    public String favouritesPage(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/favouritespage"; // html
    }
    
    @GetMapping("/searchPage")
    public String searchPage(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/searchpage"; // html
    }
    
    @GetMapping("/viewProfile")
    public String viewProfile(Model viewProfileDatingUserModel)
    {
        viewProfileDatingUser = loggedInDatingUser.convertDatingUserToViewProfileDatingUser();
    
        System.out.println(viewProfileDatingUser.getZipCodeAndCity());
    
        viewProfileDatingUserModel.addAttribute("viewProfileDatingUser", viewProfileDatingUser);
        
        return "DatingUser/viewprofile";
    }
    
    @GetMapping("/editProfile")
    public String editProfile(Model editDatingUserModel)
    {
        editDatingUser = loggedInDatingUser.convertDatingUserToEditDatingUser();
        
        editDatingUserModel.addAttribute("editDatingUser", editDatingUser);
        
        return "DatingUser/editprofile"; // html
    }
    
    @GetMapping("/editProfileConfirmation")
    public String editProfileConfirmation(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/editprofileconfirmation"; // html
    }
    
    @GetMapping("/logOut")
    public String logOut()
    {
        resetLoggedInUsers();
        
        return "logout"; // html
    }
    
    /*------------------ POST -------------------*/
    
    // I PostMappingens "/" SKAL der stå "post" FØRST! : fx IKKE "/createUser" men "/postCreateUser"
    @PostMapping("/postCreateUser")
    public String postCreateUser(WebRequest dataFromCreateUserForm)
    {
        loggedInDatingUser = userService.createDatingUser(dataFromCreateUserForm);
        
        if(loggedInDatingUser!=null)
        {
            userRepository.addDatingUserToDb(loggedInDatingUser);
            
            return "redirect:/editProfile";
        }
        
        return "redirect:/";
    }
    
    @PostMapping("/postLogIn")
    public String postLogIn(WebRequest dataFromLogInForm)
    {
        // vi tjekker admin-tabellen først fordi den er kortest (selvom det nok oftere er en datingUser som vil logge
        // ind)
        loggedInAdmin = userRepository.checkIfUserExistsInAdminsTable(dataFromLogInForm);
        
        if(loggedInAdmin.getUsername()!=null) // hvis den har fundet en admin
        {
            return "redirect:/startPageAdmin"; // url
        }
        // ellers tjekker vi om det er en datingUser
        loggedInDatingUser = userRepository.checkIfUserExistsInDatingUsersTable(dataFromLogInForm);
        
        // hvis brugeren IKKE er null OG IKKE blacklisted
        if(loggedInDatingUser.getUsername()!=null && !(loggedInDatingUser.isBlacklisted()))
        {
            loggedInAdmin = null;
            return "redirect:/startPage"; // url
        }
        // TODO: evt. skriv metode som sætter begge user-ting til null - fordi lige nu har de fået tildelt tomme brugere
        // brugeren er ikke fundet
        loggedInAdmin = null;
        loggedInDatingUser = null;
        return "redirect:/logIn"; // url
    }
    
    @PostMapping("/postEditProfile")
    public String postEditProfile(WebRequest dataFromEditProfileForm, Model editDatingUserModel)
    {
        // tjekker om brugeren har indtastet ny info
        boolean userAddedChanges = userService.checkForProfileAlterations(dataFromEditProfileForm, editDatingUser);
    
        // hvis bruger har indtastet ny info
        if(userAddedChanges)
        {
            // TODO NICE lav måske en errorPage som skriver hvilken fejl det er og skifter tilbage til
            //  editProfile-html'en
            // TODO: fix at den siger confirmationPage ved zipcode 0000 - ved ikkesisterende-zipcode
            boolean isUsernameEmailPasswordValid = userService.checkUsernameEmailPassword(dataFromEditProfileForm, editDatingUser);
            
            if(isUsernameEmailPasswordValid)
            {
                loggedInDatingUser = userService.updateLoggedInDatingUser(dataFromEditProfileForm, loggedInDatingUser);
                userRepository.updateLoggedInDatingUserInDb(loggedInDatingUser);
                
                return "redirect:/editProfileConfirmation"; // url
            }
        }
        
        // Hvis INGEN ny info ELLER hvis usernameEmailPassword er invalid
        // editDatingUser opdateres fordi viewet skal vise det brugeren skrev ind!!!!!!!!!!!!!!!!
        editDatingUser = userService.updateEditDatingUser(dataFromEditProfileForm,
                loggedInDatingUser.getUsername(),
                loggedInDatingUser.getEmail());
        editDatingUserModel.addAttribute("editDatingUser", editDatingUser);
        
        return "DatingUser/editprofile"; // html
    }
    
    
    /*------------------ ANDRE METODER -------------------*/
    /**
     *
     *
     *
     *
     * */
    
    
    public void resetLoggedInUsers()
    {
        loggedInAdmin = null;
        loggedInDatingUser = null;
    }
    
}
