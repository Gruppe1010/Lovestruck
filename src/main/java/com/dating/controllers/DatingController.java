package com.dating.controllers;

import com.dating.models.users.Admin;
import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.services.UserService;
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
    UserService userService = new UserService();
    UserRepository userRepository = new UserRepository();
    
    
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
    public String startPage(Model adminModel, Model datingUserModel)
    {
    
        addAttributeToUserModel(adminModel, datingUserModel);
        /*
        if(loggedInAdmin!=null)
        {
            adminModel.addAttribute("loggedInUser", loggedInAdmin);
        }
        else if(loggedInDatingUser!=null)
        {
            datingUserModel.addAttribute("loggedInUser", loggedInDatingUser);
        }
        
         */
        
        //addAttributeToUserModel(userModel);
        
        return "startpage"; // html
    }
    
    @GetMapping("/chatPage")
    public String chatPage()
    {
        return "chatpage"; // html
    }
    
    @GetMapping("/candidatePage")
    public String candidatePage()
    {
        return "candidatepage"; // html
    }
    
    @GetMapping("/searchPage")
    public String searchPage()
    {
        return "searchpage"; // html
    }
    
    @GetMapping("/editProfile")
    public String editProfile(Model datingUserModel, Model adminModel, Model postalInfoModel, Model tagsListModel)
    {
        addAttributeToUserModel(adminModel, datingUserModel);
    
        postalInfoModel.addAttribute("postalInfoModel", loggedInDatingUser.getPostalInfo());
    
        tagsListModel.addAttribute("tagsList", loggedInDatingUser.getTagsList());
        
        return "editprofile"; // html
    }
    
    @GetMapping("/editProfileConfirmation")
    public String editProfileConfirmation()
    {
        
        return "editprofileconfirmation"; // html
    }
    
    
    // I PostMappingens "/" SKAL der stå "post" FØRST! : fx IKKE "/createUser" men "/postCreateUser"
    @PostMapping("/postCreateUser")
    public String postCreateUser(WebRequest dataFromCreateUserForm)
    {
        DatingUser datingUser = userService.createDatingUser(dataFromCreateUserForm);
        
        if(datingUser!=null)
        {
            userRepository.addDatingUserToDb(datingUser);
            
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
        
        if(loggedInDatingUser.getUsername()!=null)
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
    public String postEditProfile(WebRequest dataFromEditProfileForm)
    {
        boolean isProfileEditted = userRepository.checkIfProfileWasEditted(dataFromEditProfileForm);
        
        // hvis checkIfChangesWereMade == true
        
        // updateUser()
        
        if(true)
        {
            return "redirect:/editProfileConfirmation"; // url
        }
        
        return "redirect:/editProfile"; // url
    }
    
    
    public void addAttributeToUserModel(Model adminModel, Model datingUserModel)
    {
        /*
        loggedInDatingUser = userRepository.retrieveLoggedInDatingUser();
        loggedInAdmin = userRepository.retrieveLoggedInAdmin();
         */
        
        // fordi vi skal bruge den samme loggedInUser-reference i html'en
        if(loggedInAdmin != null)
        {
            adminModel.addAttribute("loggedInUser", loggedInAdmin);
        }
        else if(loggedInDatingUser != null)
        {
            datingUserModel.addAttribute("loggedInUser", loggedInDatingUser);
        }
    }
    
    
    
    
}
