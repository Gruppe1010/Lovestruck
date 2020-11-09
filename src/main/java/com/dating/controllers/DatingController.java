package com.dating.controllers;

import com.dating.models.users.Admin;
import com.dating.models.users.DatingUser;
import com.dating.repositories.UserRepository;
import com.dating.services.UserService;
import com.dating.viewModels.datingUser.EditDatingUser;
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
    
    EditDatingUser editDatingUser = null;
    
    Error error = null;
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
        // TODO: lav en startPageDatingUser
        addAttributeToUserModel(adminModel, datingUserModel);
        
        
        return "startpage"; // html
    }
    
    @GetMapping("/chatPage")
    public String chatPage(Model adminModel, Model datingUserModel)
    {
        addAttributeToUserModel(adminModel, datingUserModel);
        
        return "chatpage"; // html
    }
    
    @GetMapping("/candidatePage")
    public String candidatePage(Model adminModel, Model datingUserModel)
    {
        addAttributeToUserModel(adminModel, datingUserModel);
        return "candidatepage"; // html
    }
    
    @GetMapping("/searchPage")
    public String searchPage(Model adminModel, Model datingUserModel)
    {
        addAttributeToUserModel(adminModel, datingUserModel);
        return "searchpage"; // html
    }
    
    @GetMapping("/ViewProfile")
    public String viewProfile(Model adminModel, Model datingUserModel)
    {
        //TODO: lav en viewProfileDatingUserModel i stedet
        addAttributeToUserModel(adminModel, datingUserModel);
        return "viewprofile";
    }
    
    // TODO HER ER JEG I GANG
    @GetMapping("/editProfile")
    public String editProfile(Model editDatingUserModel)
    {
       
        editDatingUser = loggedInDatingUser.convertDatingUserToEditDatingUser();
        
        editDatingUserModel.addAttribute("editDatingUser", editDatingUser);
        
        return "editprofile"; // html
    }
    
    
     /* TODO: DEN GAMLE METODER
    @GetMapping("/editProfile")
    public String editProfile(Model datingUserModel, Model adminModel, Model postalInfoModel, Model tagsListModel,
                              Model errorModel)
    {
        addAttributeToUserModel(adminModel, datingUserModel);
        
        postalInfoModel.addAttribute("postalInfoModel", loggedInDatingUser.getPostalInfo());
        
        tagsListModel.addAttribute("tagsList", loggedInDatingUser.getTagsList());
        
        errorModel.addAttribute("error", error);
        
        return "editprofile"; // html
    }
    
      */
    
    
    
    @GetMapping("/editProfileConfirmation")
    public String editProfileConfirmation(Model adminModel, Model datingUserModel)
    {
        addAttributeToUserModel(adminModel, datingUserModel);
        
        return "editprofileconfirmation"; // html
    }
    
    @GetMapping("/logOut")
    public String logOut()
    {
        resetLoggedInUsers();
        
        return "logout"; // html
    }
    
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
    
    // TODO: ER I GANG MED DENNE
    @PostMapping("/postEditProfile")
    public String postEditProfile(WebRequest dataFromEditProfileForm, Model editDatingUserModel)
    {
        // tjekker om brugeren har indtastet ny info
        boolean userAddedChanges = userService.checkForProfileAlterations(dataFromEditProfileForm, editDatingUser);
    
        // hvis bruger har indtastet ny info
        if(userAddedChanges)
        {
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
        editDatingUser = userService.updateEditDatingUser(dataFromEditProfileForm);
        editDatingUserModel.addAttribute("editDatingUser", editDatingUser);
        
        return "editprofile"; // url // TODO: hvis vi laver viewModel- skriv: "editprofile"
    }
    
 
    
    
    /*TODO: DEN GAMLE METODE
    @PostMapping("/postEditProfile")
    public String postEditProfile(WebRequest dataFromEditProfileForm, Model datingUserModel, Model adminModel,
                                  Model postalInfoModel, Model tagsListModel, Model errorModel)
    {
        // TODO: evt nyt navn til metoden: didUserAddChanges()
        // tjekker om brugeren har indtastet nye oplysninger
        boolean userAddedChanges = userRepository.checkIfProfileWasEditted(dataFromEditProfileForm, loggedInDatingUser);
    
        // if checkIfChangesWereMade == true
        // tjek om email er ledig
        // tjek om username er ledigt
        // tjek om password-inputs matcher
        // det matcher ikke = error = new Error("fejl i password")
    
    
        // if alle er true
        // == userService.applyChangesToProfile() (metoden ændrer både loggedInUser OG brugeren i database)
        // return "redirect:/editProfileConfirmation"; // url
        // else: send fejlmeddelelse alt ud fra hvad der gik galt
        //else (hvis brugeren ikke indtastede ændringer)
        // sker der ingenting?
        
        if(true)
        {
            return "redirect:/editProfileConfirmation"; // url
        }
     
        
        //setDatingUserModel(datingUserModel, dataFromEditProfileForm);
        
        //errorModel.addAttribute("errorModel", new Error("Adgangskoder matcher ikke.")
        
        return "redirect:/editprofile"; // url // TODO: hvis vi laver viewModel- skriv: "editprofile"
    }
     */
    
    
    /*
    
    public void setDatingUserModel(Model datingUserModel, WebRequest dataFromEditProfileForm)
    {
        
        int interestedIn = userService.resolveInterestedInInput(dataFromEditProfileForm.getParameter(
                "interestedininput"));
        int age = Integer.parseInt(dataFromEditProfileForm.getParameter("ageinput"));
        String username = dataFromEditProfileForm.getParameter("usernameinput");
        String email =  dataFromEditProfileForm.getParameter("emailinput");
        String password = dataFromEditProfileForm.getParameter("passwordinput");
        String confirmPassword = dataFromEditProfileForm.getParameter("confirmpasswordinput");
    
        if(userService.checkIfPasswordsMatch(password, confirmPassword))
        
        DatingUser datingUser = new DatingUser(sex, interestedIn, age, username, email, password);
        
    }
    
     */
    
    
    public void addAttributeToUserModel(Model adminModel, Model datingUserModel)
    {
        /*
        loggedInDatingUser = userRepository.retrieveLoggedInDatingUser();
        loggedInAdmin = userRepository.retrieveLoggedInAdmin();
         */
        
        // fordi vi skal bruge den samme loggedInUser-reference i html'en
        if(loggedInAdmin != null)
        {
            adminModel.addAttribute("loggedInAdmin", loggedInAdmin);
        }
        else if(loggedInDatingUser != null)
        {
            datingUserModel.addAttribute("loggedInDatingUser", loggedInDatingUser);
        }
    }
    
    
    public void resetLoggedInUsers()
    {
        loggedInAdmin = null;
        loggedInDatingUser = null;
    }
    
    
    
}
