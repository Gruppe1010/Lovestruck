package com.dating.repositories;

import com.dating.models.PostalInfo;
import com.dating.models.users.Admin;
import com.dating.models.users.DatingUser;
import org.springframework.web.context.request.WebRequest;

import java.io.*;
import java.sql.*;

public class UserRepository
{
    Connection lovestruckConnection = null;
    Connection favouriteslistConnection = null;

    DatingUser loggedInDatingUser = new DatingUser();
    Admin loggedInAdmin = new Admin();
    
    
    /**
     * Laver en connection til lovestruck-databasen
     *
     * @param dbName Navnet på db som vi connecter til
     *
     * @return Connection Den oprettede connection ELLER null ved fejl i oprettelsen af connection
     */
    public Connection establishConnection(String dbName)
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+ dbName +
                    "?serverTimezone=UTC", "gruppe10", "gruppe10");
        }
        catch(SQLException e)
        {
            System.out.println("Error in establishConnection: " + e.getMessage());
        }
        
        return connection;
    }

    public void writePictureToDb(int idDatingUser)
    {
        lovestruckConnection = establishConnection("lovestruck");

        try
        {
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement("UPDATE dating_users SET profilepicture = ? WHERE id_dating_user = ?");

            InputStream inputStream = new FileInputStream("C:\\Users\\rasmu\\IdeaProjects\\Lovestruck\\src\\main\\resources\\static\\image\\textLogo.png");

            preparedStatement.setBlob(1, inputStream);
            preparedStatement.setInt(2, idDatingUser);

            preparedStatement.executeUpdate();



        } catch(SQLException | FileNotFoundException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public void readPictureFromDb(int idDatingUser, String fileName)
    {
        lovestruckConnection = establishConnection("lovestruck");

        try {
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement("SELECT profilepicture FROM dating_users WHERE id_dating_user = ?");

            preparedStatement.setInt(1, idDatingUser);

            ResultSet resultSet = preparedStatement.executeQuery();

            File file = new File(fileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            //String filePath = file.getAbsolutePath(); gemmer fils path
            System.out.println("Writing to file: " + file.getAbsolutePath());

            while (resultSet.next())
            {
                InputStream inputStream = resultSet.getBinaryStream("profilepicture");
                byte[] buffer = new byte[128];
                while (inputStream.read(buffer) > 0)
                {
                    outputStream.write(buffer);
                }
            }


            }
            catch(Exception exception)
            {
                System.out.println("Error in readPictureFromDb: " + exception.getMessage());
            }

    }
    
    /**
     * Tilføjer DatingUser-objekt til dating_users-tabellen i db
     * OG sætter dets id-attribut
     * OG opretter ny favourites_list-tabel i db knyttet til brugeren
     *
     * @param datingUser
     *
     * @return boolean Om det lykkedes eller ej
     */
    public boolean addDatingUserToDb(DatingUser datingUser)
    {
        try
        {
            lovestruckConnection = establishConnection("lovestruck");
            
            // TODO: tilføj: image_path som kolonne i database - og så tilføj den sqlCommanden her
            String sqlCommand = "INSERT into dating_users(blacklisted, sex, interested_in, age, username, email, " +
                    "password) " +
                    "values (?,?,?,?,?,?,?);";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, datingUser.convertBooleanToInt(datingUser.isBlacklisted()));
            preparedStatement.setInt(2, datingUser.convertBooleanToInt(datingUser.getSex()));
            preparedStatement.setInt(3, datingUser.getInterestedIn());
            preparedStatement.setInt(4, datingUser.getAge());
            preparedStatement.setString(5, datingUser.getUsername());
            preparedStatement.setString(6, datingUser.getEmail());
            preparedStatement.setString(7, datingUser.getPassword());
            
            // user tilføjes til database
            preparedStatement.executeUpdate();
            
            // nu hvor user er blevet oprettet, tilføjer vi databasens genererede id_dating_user til datingUser-objektet
            int idDatingUser = retrieveDatingUserIdFromDb(datingUser);
            
            if(idDatingUser!=-1)
            {
                // setter id'et, hvis det er genereret korrekt
                datingUser.setIdDatingUser(idDatingUser);
                // genererer en favourites_list tabel i databasen - knyttet til user-entitet via id_dating_user
                // fx til en user med id_dating_user 3 oprettes tabellen: favourites_list_3
                createFavouritesListTableDb(idDatingUser);
                loggedInDatingUser = datingUser;
            }
            
            return true;
        }
        catch(SQLException e)
        {
            System.out.println("Error in addDatingUserToDb: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Opretter ny favourites_list tabel ud fra idDatingUser-int
     *
     * @param idDatingUser id'et som skal være i db-tabellens navn: fx favourites_list_3
     *
     * @return void
     */



    public void createFavouritesListTableDb(int idDatingUser)
    {
        try
        {
            favouriteslistConnection = establishConnection("lovestruck_favourites_list");
            
            // lovestruc_favourites_list == den database som vi laver tabellen i
            // favourites_list_? == navnet på tabellen
            // id_dating_user INT NOT NULL,== navn på ny kolonne og hvilke bokse der er krydset af
            // PRIMARY KEY(id_dating_user) == siger at det er kolonnen id_dating_user som er primary key
            String sqlCommand = "CREATE TABLE lovestruck_favourites_list.favourites_list_? (id_dating_user INT NOT NULL, PRIMARY " +
                    "KEY (id_dating_user));";
            
            PreparedStatement preparedStatement = favouriteslistConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, idDatingUser);
            
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in createFavouritesListTableDb: " + e.getMessage());
        }
        
    }
    
    /**
     * Finder id_dating_user-værdien gemt i database på et givent user-objekt
     *
     * @param datingUser user-objektet som vi finder id_dating_user-værdien på/til
     
     *
     * @return int id_dating_user-værdien som er fundet - -1 hvis ikke fundet
     */
    public int retrieveDatingUserIdFromDb(DatingUser datingUser)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        int idDatingUser = -1;
        
        try
        {
            String sqlCommand = "SELECT * FROM dating_users WHERE username like ?";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setString(1, "%" + datingUser.getUsername() + "%");
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            
            // TODO Find ud af hvorfor vi skal skrive next
            if(resultSet.next())
            {
                idDatingUser = resultSet.getInt(1);
            }
            
            
            
            //System.out.println(idDatingUser);
        }
        catch(SQLException e)
        {
            System.out.println("Error in retrieveIdDatingUserFromDb: " + e.getMessage());
        }
        
        return idDatingUser;
    }
    
    /**
     * Tjekker om username allerede er gemt på anden user i db
     *
     * @param username Username som tjekkes for om den er optaget
     
     *
     * @return Boolean Svaret på om username'et er ledig
     */
    public boolean isUsernameAvailable(String username)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        boolean usernameIsAvailable = true; // sættes til at være available by default
        
        try
        {
            String sqlCommand = "SELECT * FROM dating_users WHERE username = ?";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setString(1, username);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // TODO Find ud af hvorfor vi skal skrive next
            if(resultSet.next())
            {
                usernameIsAvailable = false;
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in isUsernameAvailable: " + e.getMessage());
        }
        
        return usernameIsAvailable;
    }
    
    /**
     * Tjekker om email allerede er gemt på anden user i db
     *
     * @param email Email som tjekkes for om den er optaget
     
     *
     * @return Boolean Svaret på om email'en er ledig
     */
    public boolean isEmailAvailable(String email)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        boolean emailIsAvailable = true; // sættes til at være available by default
        
        try
        {
            String sqlCommand = "SELECT * FROM dating_users WHERE email = ?";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setString(1, email);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // TODO Find ud af hvorfor vi skal skrive next
            if(resultSet.next())
            {
                emailIsAvailable = false;
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in isEmailAvailable: " + e.getMessage());
        }
        
        return emailIsAvailable;
    }
    
    
    /**
     * Tjekker om bruger som prøver på at logge ind findes i enten admins eller dating_users tabel i db
     *
     * @param dataFromLogInForm WebRequest som bruges til at hente data fra login-form
     *
     * @return User Returnerer enten NULL hvis user ikke findes i db - eller enten admin eller datingUser
     */
    /*public User checkIfUserExists(WebRequest dataFromLogInForm)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        try
        {
            ResultSet resultSet = findUserInDb(dataFromLogInForm, "admins");
            
            if(resultSet.next()) // hvis det er en admin
            {
                loggedInUser = loggedInAdmin;

                loggedInUser.setUsername(resultSet.getString(3));
                loggedInUser.setEmail(resultSet.getString(4));
                loggedInUser.setPassword(resultSet.getString(5));
              
            }
            else // når det ikke er en admin, så tjekker vi om det er en datingUser
            {
                resultSet = findUserInDb(dataFromLogInForm, "dating_users");

                if(resultSet.next()) // hvis det er en datingUser
                {
                    loggedInUser = loggedInDatingUser;

                    loggedInUser.setUsername(resultSet.getString(3));
                    loggedInUser.setEmail(resultSet.getString(4));
                    loggedInUser.setPassword(resultSet.getString(5));
                   
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in isEmailAvailable: " + e.getMessage());
        }
    
        return loggedInUser;
    }
    
     */
    
    
    public Admin checkIfUserExistsInAdminsTable(WebRequest dataFromLogInForm)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        try
        {
            ResultSet resultSet = findUserInDb(dataFromLogInForm, "admins");
            
            if(resultSet.next()) // hvis admin er fundet i db
            {
                loggedInAdmin.setUsername(resultSet.getString(3));
                loggedInAdmin.setEmail(resultSet.getString(4));
                loggedInAdmin.setPassword(resultSet.getString(5));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in isEmailAvailable: " + e.getMessage());
        }
        
        return loggedInAdmin;
    }
    
    public DatingUser checkIfUserExistsInDatingUsersTable(WebRequest dataFromLogInForm)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        try
        {
            ResultSet resultSet = findUserInDb(dataFromLogInForm, "dating_users");
            
            if(resultSet.next()) // hvis det er en datingUser
            {
                loggedInDatingUser.setIdDatingUser(resultSet.getInt(1));
                loggedInDatingUser.setBlacklisted(loggedInDatingUser.convertIntToBoolean(resultSet.getInt(2)));
                loggedInDatingUser.setUsername(resultSet.getString(3));
                loggedInDatingUser.setEmail(resultSet.getString(4));
                loggedInDatingUser.setPassword(resultSet.getString(5));
                loggedInDatingUser.setAge(resultSet.getInt(6));
                loggedInDatingUser.setSex(loggedInDatingUser.convertIntToBoolean(resultSet.getInt(7)));
                loggedInDatingUser.setInterestedIn(resultSet.getInt(8));
                //TODO PROFILEPICTURE loggedInDatingUser.setInterestedIn(resultSet.getInt(9));
                loggedInDatingUser.setDescription(resultSet.getString(10));
                loggedInDatingUser.setTagsList(loggedInDatingUser.convertStringToTagsList(resultSet.getString(11)));
                loggedInDatingUser.setPostalInfo(findPostalInfoObjectFromIdPostalInfo(resultSet.getInt(12)));
            }
            
        }
        catch(SQLException e)
        {
            System.out.println("Error in isEmailAvailable: " + e.getMessage());
        }
        
        return loggedInDatingUser;
    }
    
    
    /**
     * Finder en user i valgfri tabel ud fra username og password
     *
     * @param dataFromLogInForm WebRequest som bruges til at hente data fra login-form
     * @param table tabel user findes i
     *
     * @return ResultSet Fundet user-entitet er gemt i ResultSettet - ALDRIG null, MEN tom, hvis user ikke findes i
     * tabel
     */
    public ResultSet findUserInDb(WebRequest dataFromLogInForm, String table)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        ResultSet resultSet = null;
        try
        {
            String sqlCommand = "SELECT * FROM " + table +" WHERE username = ? AND password = ? ";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setString(1, dataFromLogInForm.getParameter("usernameinput"));
            preparedStatement.setString(2, dataFromLogInForm.getParameter("passwordinput"));
            
            resultSet = preparedStatement.executeQuery();
            /*
            if(!resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                resultSet = null;
            }

             */
        }
        catch(SQLException e)
        {
            System.out.println("Error in findUserInDb: " + e.getMessage());
        }
        return resultSet;
    }
    
    /**
     * Nulstiller loggedInAdmin og loggedInDatingUser-klassevariabel
     *
     * @return void
     */
    public void setLoggedInUserToNull()
    {
        loggedInAdmin = null;
        loggedInDatingUser = null;
    }
    
    /* TODO : slet metode
    public boolean checkIfProfileWasEditted(WebRequest dataFromEditProfileForm, DatingUser loggedInUser)
    {
        // return værdi
        boolean wasProfileEditted = false;
        
        boolean wasPasswordEditted; //password ændret?
        boolean wasOtherInfoEditted = false; // andre værdier ændret?
        
        // henter værdi der er indtastet i form
        // hvis INGEN værdi indtastet henter den: ""
        String passwordInput = dataFromEditProfileForm.getParameter("passwordinput");
        //hvis password IKKE er ændret er det == ""
        // DERFOR hvis password == "", er det IKKE blevet ændret
        // wasPasswordEditted skal altså have den omvendte værdi
        
        
        wasPasswordEditted = !(passwordInput == "");
        
        
        // hvis de HAR fået en nye værdi
        else if(!userService.checkIfPasswordsMatch(passwordInput, confirmPasswordInput))
        {
            // hvis de nye værdier IKKE matcher, sker der en fejlmeddelelse
           String errorMessage = "Dine to adgangskodeinput-matcher ikke";
            
            // hvis de nye værdier matcher så fortsætter de med at have samme input
        }
        
        try
        {
            // hvis ALT input fra form er HELT det samme, som ligger på en bruger i db, er der IKKE blevet ændret noget
           
            String sqlCommand = "SELECT * FROM dating_users WHERE insterested_in = ? AND " +
                                        "age = ? AND username = ? AND email = ? ";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, dataFromEditProfileForm.getParameter("usernameinput"));
            preparedStatement.setString(2, dataFromEditProfileForm.getParameter("passwordinput"));
        
            ResultSet resultSet = preparedStatement.executeQuery();
      
        }
        catch(SQLException e)
        {
            System.out.println("Error in checkIfProfileWasEditted: " + e.getMessage());
        }
        
        
        if(wasPasswordEditted || wasOtherInfoEditted)
        {
        
        }
        
        return wasProfileEditted;
     
    }
    */
    
    public DatingUser retrieveLoggedInDatingUser()
    {
        return loggedInDatingUser;
    }
    
    public Admin retrieveLoggedInAdmin()
    {
        return loggedInAdmin;
    }
    
    
    public void updateLoggedInDatingUserInDb(DatingUser loggedInDatingUser)
    {
        int postalId = findIdPostalInfoFromPostalInfoObject(loggedInDatingUser.getPostalInfo());
        String tagsListString = loggedInDatingUser.convertTagsListToString();
        
        try
        {
            lovestruckConnection = establishConnection("lovestruck");
            
            // TODO: tilføj: image_path som kolonne i database - og så tilføj den sqlCommanden her
            String sqlCommand = "UPDATE dating_users SET interested_in = ?, " +
                                        "username = ?, " +
                                        "email = ?, " +
                                        "age = ?, " +
                                        "id_postal_info = ?, " +
                                        "password = ?, " +
                                        "description = ?, " +
                                        "tags = ? " +
                                        "WHERE id_dating_user = ?";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, loggedInDatingUser.getInterestedIn());
            preparedStatement.setString(2, loggedInDatingUser.getUsername());
            preparedStatement.setString(3, loggedInDatingUser.getEmail());
            preparedStatement.setInt(4, loggedInDatingUser.getAge());
            preparedStatement.setInt(5, postalId);
            preparedStatement.setString(6, loggedInDatingUser.getPassword());
            preparedStatement.setString(7, loggedInDatingUser.getDescription());
            preparedStatement.setString(8, tagsListString);
            preparedStatement.setInt(9, loggedInDatingUser.getIdDatingUser());
        
            // user tilføjes til database
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in updateLoggedInDatingUserInDb: " + e.getMessage());
    
        }
    
    
    }
    
    /**
     * Finder PostalInfo-entitet knyttet til bestemt idPostalInfo
     *
     * @param id idPostalInfo som PostalInfo-entitet findes ud fra
     *
     * @return PostalInfo Returnerer PostalInfo-entiteten omdannet til postalInfo-obj
     */
    public PostalInfo findPostalInfoObjectFromIdPostalInfo(int id)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        PostalInfo postalInfo = null;
        
        try
        {
            String sqlCommand = "SELECT * FROM postal_info WHERE id_postal_info = ?";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, id);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                postalInfo = new PostalInfo(resultSet.getInt(2), resultSet.getString(3));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in findPostalInfoObjectFromIdPostalInfo: " + e.getMessage());
        }
        
        return postalInfo;
    }
    
    /**
     * Finder idPostalInfo knyttet til et givent postInfo-obj i db
     *
     * @param postalInfo PostalInfo-objektet som tilsvarende id findes til
     *
     * @return int IdPostalInfo-værdien som er fundet i db
     */
    public int findIdPostalInfoFromPostalInfoObject(PostalInfo postalInfo)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        int idPostalInfo = 0;
    
        try
        {
            String sqlCommand = "SELECT * FROM postal_info WHERE zip_code = ?";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, postalInfo.getZipCode());
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if(resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                idPostalInfo = resultSet.getInt(1);
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in findIdPostalInfoFromPostalInfoObject: " + e.getMessage());
        }
    
        return idPostalInfo;
    
    }
    
    public PostalInfo findPostalInfoObjectFromZipCodeInput(int zipCode)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        PostalInfo postalInfo = null;
    
        try
        {
            String sqlCommand = "SELECT * FROM postal_info WHERE zip_code = ?";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, zipCode);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if(resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                postalInfo = new PostalInfo(resultSet.getInt(2), resultSet.getString(3));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in findPostalInfoObjectFromZipCodeInput: " + e.getMessage());
        }
    
        return postalInfo;
    }
    
    
    public void updateProfile()
    {
        String tagsString = " ";
        
        // tagsString = tagsString contains" " erstat med "";
        
    }
    
    
}
