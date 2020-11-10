package com.dating.controllers;

import com.dating.models.users.Admin;
import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.services.UserService;
import com.dating.viewModels.datingUser.EditDatingUser;
import com.dating.viewModels.datingUser.PreviewDatingUser;
import com.dating.viewModels.datingUser.ViewProfileDatingUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;


@Controller
public class DatingController
{
    Admin loggedInAdmin = null;
    //DatingUser loggedInDatingUser = null; // TODO det er denne som skal kopieres ind når vi ikke tester
    DatingUser loggedInDatingUser = new DatingUser(true, 2, 24, "tester", "tester@hotmail.com", "hej");
    
    ViewProfileDatingUser viewProfileDatingUser = null;
    EditDatingUser editDatingUser = null;
    
    Error error = null;
    UserService userService = new UserService();
    UserRepository userRepository = new UserRepository();
    
    /*TODO: overvej at dele Controlleren op i flere controllers!!! -
    //  fx GenerelController, AdminController, DatingUserController*/
    
    //------------------ GET GENEREL -------------------//
    
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
    
    @GetMapping("/logOut")
    public String logOut()
    {
        // TODO: slet måske denne metode - fordi måske giver det mest mening bare skrive koden her
        resetLoggedInUsers();
        
        return "logout"; // html
    }
    
    //------------------ GET DATINGUSER -------------------//
    
    @GetMapping("/startPage")
    public String startPage(Model datingUserModel, Model previewDatingUsersListModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        ArrayList<PreviewDatingUser> previewDatingUsersList =
                userRepository.createListOfAllDatingUsersFromDb(loggedInDatingUser.getIdDatingUser());
    
        previewDatingUsersListModel.addAttribute("previewDatingUsersList", previewDatingUsersList);
        
        //urlModel.addAttribute("baseurl", "/viewProfile");
        
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
        /*
        Possibly noget ala:
        tager som param:
             - Model favouritesListModel
        
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        favouritesListModel.addAttribute("favouritesList", loggedInUser.getFavouritesList());
        
       og så thymeleafer vi en array på favouritespage-html'en
         */
        
        
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/favouritespage"; // html
    }
    
    @GetMapping("/searchPage")
    public String searchPage(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/searchpage"; // html
    }
    
    @GetMapping("/viewMyProfile")
    public String viewMyProfile(Model viewProfileDatingUserModel)
    {
        viewProfileDatingUser = loggedInDatingUser.convertDatingUserToViewProfileDatingUser();
    
        viewProfileDatingUserModel.addAttribute("viewProfileDatingUser", viewProfileDatingUser);
        
        return "DatingUser/viewmyprofile";
    }
    
    /*
    @GetMapping("/viewProfile")
    public String viewProfile(Model viewProfileDatingUserModel, Model loggedInDatingUserModel)
    {
        // Vi har sat klasse-variablen viewProfileDatingUser i vores RequestMapping(/viewProfile?{idViewDatingUser})
        // (så variablen ER opdateret til at indeholde den profil vi skal vise)
        
        viewProfileDatingUserModel.addAttribute("viewProfileDatingUser", viewProfileDatingUser);
    
        loggedInDatingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/viewprofile";
    }
    
     */
    
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
    
    //------------------ RequestMapping DatingUser -------------------//
    
    @RequestMapping("/viewProfile")
    public String viewProfileIdDatingUser(@RequestParam int id, Model viewProfileDatingUserModel,
                                          Model loggedInDatingUserModel)
    {
        viewProfileDatingUser = userRepository.findDatingUserInDbToView(id);
        
        viewProfileDatingUserModel.addAttribute("viewProfileDatingUser", viewProfileDatingUser);
        loggedInDatingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
     
        return "DatingUser/viewprofile";
    }
    
    
    //------------------ GET ADMIN -------------------//
    
    @GetMapping("/startPageAdmin")
    public String startPageAdmin(Model adminModel)
    {
        adminModel.addAttribute("loggedInAdmin", loggedInAdmin);
        
        return "Admin/startpageadmin"; // html
    }
    
    
    
    /* TODO: probably slet denne
    @GetMapping("/chatPageAdmin")
    public String chatPageAdmin(Model datingUserModel)
    {
        datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        
        return "DatingUser/chatpage"; // html
    }
    
     */
    
    @GetMapping("/blacklistedUsersPageAdmin")
    public String blacklistedUsersPageAdmin(Model adminModel)
    {
        /*
        Possibly noget ala:
        tager som param:
             - Model blacklistedUsersListModel
        
        adminModel.addAttribute("loggedInAdmin", loggedInAdmin);
        blacklistedUsersListModel.addAttribute("blacklistedUsersList", loggedInAdmin.getBlacklistedUsersList());
        
       og så thymeleafer vi en array på favouritespage-html'en
         */
        
        
        return "Admin/blacklisteduserspageadmin"; // html
    }
    
    @GetMapping("/searchPageAdmin")
    public String searchPageAdmin(Model adminModel)
    {
        adminModel.addAttribute("loggedInAdmin", loggedInAdmin);
        
        return "DatingUser/searchpage"; // html
    }
    
    @GetMapping("/viewProfileAdmin")
    public String viewProfileAdmin(Model adminModel, Model viewProfileDatingUserModel)
    {
        adminModel.addAttribute("loggedInAdmin", loggedInAdmin);
        
        viewProfileDatingUser = loggedInDatingUser.convertDatingUserToViewProfileDatingUser();
        
        viewProfileDatingUserModel.addAttribute("viewProfileDatingUser", viewProfileDatingUser);
        
        return "DatingUser/viewprofile";
    }
    
    @GetMapping("/editProfileAdmin")
    public String editProfileAdmin(Model editDatingUserModel)
    {
        editDatingUser = loggedInDatingUser.convertDatingUserToEditDatingUser();
        
        editDatingUserModel.addAttribute("editDatingUser", editDatingUser);
        
        return "DatingUser/editprofile"; // html
    }
    
    
    //------------------ POST GENEREL -------------------//
    
    // TODO: denne her gør det tricky at dele Controlleren op i flere Controller-klasser
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
    
    //------------------ POST DATINGUSER -------------------//
    
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
    
    //------------------ POST ADMIN -------------------//
    
    @PostMapping("/postEditProfileAdmin")
    public String postEditProfileAdmin(WebRequest dataFromEditProfileForm, Model editDatingUserModel)
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
    
    
    //------------------ ANDRE METODER -------------------//
    
    /**
     * Nulstiller attributterne loggedInAdmin og loggedInDatingUser DatingController-klassen
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
