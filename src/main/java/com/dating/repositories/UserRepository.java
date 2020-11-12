package com.dating.repositories;

import com.dating.models.PostalInfo;
import com.dating.models.chat.Chat;
import com.dating.models.chat.Message;
import com.dating.models.users.Admin;
import com.dating.models.users.DatingUser;
import com.dating.viewModels.datingUser.PreviewDatingUser;
import com.dating.viewModels.datingUser.ViewProfileDatingUser;
import org.springframework.web.context.request.WebRequest;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;
import java.util.ArrayList;

public class UserRepository
{
    Connection lovestruckConnection = null;
    Connection favouritesListConnection = null;
    Connection chatsListConnection = null;
    Connection chatConnection = null;

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

    public void closeConnections()
    {
        try
        {
            if(lovestruckConnection != null)
            {
                lovestruckConnection.close();
            }
            if(favouritesListConnection != null)
            {
                favouritesListConnection.close();
            }
            if(chatsListConnection != null)
            {
                chatsListConnection.close();
            }
            if(chatConnection != null)
            {
                chatConnection.close();
            }
        }
        catch(SQLException throwables)
        {
            throwables.printStackTrace();
        }

    }


    /**
     * Tilføjer DatingUser-objekt til dating_users-tabellen i db
     * OG sætter dets id-attribut
     * OG opretter ny favourites_list-tabel i db knyttet til brugeren
     *
     * @param datingUser DatingUser-obj. som skal tilføjes til db
     */
    public void addDatingUserToDb(DatingUser datingUser)
    {
        lovestruckConnection = establishConnection("lovestruck");
        try
        {
            Blob profilePicture = new SerialBlob(datingUser.getProfilePictureBytes());
            int idPostalInfo = findIdPostalInfoFromPostalInfoObject(datingUser.getPostalInfo());
            
         
            String sqlCommand = "INSERT into dating_users(blacklisted, sex, interested_in, profile_picture, age, " +
                                        "username, email, password, id_postal_info) " +
                    "values (?,?,?,?,?,?,?,?,?);";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, datingUser.convertBooleanToInt(datingUser.isBlacklisted()));
            preparedStatement.setInt(2, datingUser.convertBooleanToInt(datingUser.getSex()));
            preparedStatement.setInt(3, datingUser.getInterestedIn());
            preparedStatement.setBlob(4, profilePicture);
            preparedStatement.setInt(5, datingUser.getAge());
            preparedStatement.setString(6, datingUser.getUsername());
            preparedStatement.setString(7, datingUser.getEmail());
            preparedStatement.setString(8, datingUser.getPassword());
            preparedStatement.setInt(9, idPostalInfo);
            
            // user tilføjes til database
            preparedStatement.executeUpdate();
            
            // nu hvor user er blevet oprettet, tilføjer vi databasens genererede id_dating_user til datingUser-objektet
            int idDatingUser = retrieveDatingUserIdFromDb(datingUser);
            
            if(idDatingUser != -1)
            {
                // setter id'et, hvis det er genereret korrekt
                datingUser.setIdDatingUser(idDatingUser);
                // genererer en favourites_list tabel i databasen - knyttet til user-entitet via id_dating_user
                // fx til en user med id_dating_user 3 oprettes tabellen: favourites_list_3
                createFavouritesListTableDb(idDatingUser);
                // genererer chatsList-tabel i db
                createChatsListTableDb(idDatingUser);
                
                loggedInDatingUser = datingUser;
            }
        }
        catch(Exception e)
        {
            System.out.println("Error in addDatingUserToDb: " + e.getMessage());
        }

    }
    
    /**
     * Opretter ny favourites_list tabel ud fra idDatingUser-int
     *
     * @param idDatingUser id'et som skal være i db-tabellens navn: fx favourites_list_3
     */
    public void createFavouritesListTableDb(int idDatingUser)
    {
        favouritesListConnection = establishConnection("lovestruck_favourites_list");
        
        try
        {
            // lovestruc_favourites_list == den database som vi laver tabellen i
            // favourites_list_? == navnet på tabellen
            // id_dating_user INT NOT NULL,== navn på ny kolonne og hvilke bokse der er krydset af
            // PRIMARY KEY(id_dating_user) == siger at det er kolonnen id_dating_user som er primary key
            String sqlCommand = "CREATE TABLE lovestruck_favourites_list.favourites_list_? (id_dating_user INT NOT NULL, PRIMARY " +
                    "KEY (id_dating_user));";
            
            PreparedStatement preparedStatement = favouritesListConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, idDatingUser);
            
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in createFavouritesListTableDb: " + e.getMessage());
        }
        
    }
    
    /**
     * Tilføjer DatingUser-obj.til datingUser's favouritesList
     * DatingUser-obj. som tilføjes hentes fra den sidste plads i loggedInDatingUser's favouritesList
     *
     * @param datingUser Den bruger som skal tilføje anden bruger til sin favouritesList
     */
    public void addDatingUserToFavouritesListInDb(DatingUser datingUser)
    {
        favouritesListConnection = establishConnection("lovestruck_favourites_list");
        
        // hvis der ER noget på datingUser-obj.s favouritesList
        if(datingUser.getFavouritesList() != null)
        {
            // find sidste index plads' nummer
            int lastIndex = datingUser.getFavouritesList().size() - 1;
    
            // find favouritesList -- find datingUser-obj. på sidste index -- find id'et på denne
            int idDatingUserToAdd = datingUser.getFavouritesList().get(lastIndex).getIdDatingUser();
    
            try
            {
                String sqlCommand = "INSERT into favourites_list_? (id_dating_user) values(?)";
        
                PreparedStatement preparedStatement = favouritesListConnection.prepareStatement(sqlCommand);
        
                preparedStatement.setInt(1, datingUser.getIdDatingUser());
                preparedStatement.setInt(2, idDatingUserToAdd);
        
                preparedStatement.executeUpdate();
        
            }
            catch(SQLException e)
            {
                System.out.println("Error in addDatingUserToFavouritesListInDb: " + e.getMessage());
            }
        }
        
    }
    
    /**
     * Fjerner DatingUser-obj.fra datingUser's favouritesList
     *
     * @param datingUserTableToUpdate Den bruger som skal fjerne anden bruger til sin favouritesList
     * @param datingUserToRemove Den bruger som skal fjernes fra listen
     */
    public void removeDatingUserFromFavouritesListInDb(DatingUser datingUserTableToUpdate,
                                                     DatingUser datingUserToRemove)
    {
        favouritesListConnection = establishConnection("lovestruck_favourites_list");
        
        try
        {
            String sqlCommand = "DELETE from favourites_list_? WHERE id_dating_user = ?";
        
            PreparedStatement preparedStatement = favouritesListConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, datingUserTableToUpdate.getIdDatingUser());
            preparedStatement.setInt(2, datingUserToRemove.getIdDatingUser());
        
            preparedStatement.executeUpdate();
        
        }
        catch(SQLException e)
        {
            System.out.println("Error in removeDatingUserToFavouritesListInDb: " + e.getMessage());
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
    
    /**
     * Tjekker via data fra dataFromLogInForm om der er en tilsvarende Admin-bruger i admin-db
     *
     * @param dataFromLogInForm Den form som indeholde log ind dataen som skal bruges
     *
     * @return Admin Returnerer et Admin-obj. som enten (hvis den ikke fandtes i db) har ingen værdier eller ()
     * er blevet
     * tildelt væri
     */
    public Admin checkIfUserExistsInAdminsTable(WebRequest dataFromLogInForm)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        try
        {
            ResultSet resultSet = findUserInDb(dataFromLogInForm, "admins");
            
            if(resultSet.next()) // hvis admin er fundet i db
            {
                loggedInAdmin.setUsername(resultSet.getString(2));
                loggedInAdmin.setEmail(resultSet.getString(3));
                loggedInAdmin.setPassword(resultSet.getString(4));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in checkIfUserExistsInAdminsTable: " + e.getMessage());
        }
        
        return loggedInAdmin;
    }
    
    public DatingUser checkIfUserExistsInDatingUsersTable(WebRequest dataFromLogInForm)
    {
        ResultSet resultSet = findUserInDb(dataFromLogInForm, "dating_users");
        
        loggedInDatingUser = createDatingUserFromResultSet(resultSet);
        
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
        }
        catch(SQLException e)
        {
            System.out.println("Error in findUserInDb: " + e.getMessage());
        }
        return resultSet;
    }
    
    // Overload
    public ResultSet findUserInDb(int idDatingUser)
    {
        lovestruckConnection = establishConnection("lovestruck");
    
        ResultSet resultSet = null;
        try
        {
            String sqlCommand = "SELECT * FROM dating_users WHERE id_dating_user = ?";
    
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
    
            preparedStatement.setInt(1, idDatingUser);
    
            resultSet = preparedStatement.executeQuery();
        }
        catch(SQLException e)
        {
            System.out.println("Error in findUserInDb: " + e.getMessage());
        }
        return resultSet;
    }
    
    public DatingUser retrieveDatingUserFromDb(int idDatingUser)
    {
        ResultSet resultSet = findUserInDb(idDatingUser);
    
        return createDatingUserFromResultSet(resultSet);
    }
    
    
    public ViewProfileDatingUser findDatingUserInDbToView(int idDatingUser)
    {
        // opretter viewProfileDatingUser som returneres
        ViewProfileDatingUser viewProfileDatingUser = null;
    
        // finder DatingUser i db ud fra idDatingUser og gemmer i resultSet
        ResultSet resultSet = findUserInDb(idDatingUser);
    
        // opretter datingUser-obj. ud fra resultSet
        DatingUser datingUser = null;
        
        datingUser = createDatingUserFromResultSet(resultSet);
        
        // konverterer datingUser-obj. til viewProfileDatingUser-obj. - som gemmes i retur-variablen
        viewProfileDatingUser = datingUser.convertDatingUserToViewProfileDatingUser();
      
        return viewProfileDatingUser;
    }
    
    
    // TODO HER
    public void updateLoggedInDatingUserInDb(DatingUser loggedInDatingUser)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        // sætter postalId
        int idPostalInfo = findIdPostalInfoFromPostalInfoObject(loggedInDatingUser.getPostalInfo());
        String tagsListString = loggedInDatingUser.convertTagsListToString();
        
        try
        {
            Blob profilePictureBlob = new SerialBlob(loggedInDatingUser.getProfilePictureBytes());
            
            // TODO: tilføj: image_path som kolonne i database - og så tilføj den sqlCommanden her
            String sqlCommand = "UPDATE dating_users SET interested_in = ?, " +
                                        "username = ?, " +
                                        "email = ?, " +
                                        "age = ?, " +
                                        "id_postal_info = ?, " +
                                        "password = ?, " +
                                        "description = ?, " +
                                        "tags = ?, " +
                                        "profile_picture = ?" +
                                        "WHERE id_dating_user = ?";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, loggedInDatingUser.getInterestedIn());
            preparedStatement.setString(2, loggedInDatingUser.getUsername());
            preparedStatement.setString(3, loggedInDatingUser.getEmail());
            preparedStatement.setInt(4, loggedInDatingUser.getAge());
            preparedStatement.setInt(5, idPostalInfo);
            preparedStatement.setString(6, loggedInDatingUser.getPassword());
            preparedStatement.setString(7, loggedInDatingUser.getDescription());
            preparedStatement.setString(8, tagsListString);
            preparedStatement.setBlob(9, profilePictureBlob);
            preparedStatement.setInt(10, loggedInDatingUser.getIdDatingUser());
        
            // user tilføjes til database
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in updateLoggedInDatingUserInDb: " + e.getMessage());
    
        }
    }
    
    //------------------ POSTALINFO METODER -------------------//
    
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
        if(postalInfo != null)
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
        return -1;
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
    
    public boolean checkIfValidZipCode(int zipCodeInput)
    {
        boolean doesZipCodeExitsInDb = false;
    
        lovestruckConnection = establishConnection("lovestruck");
    
        PostalInfo postalInfo = null;
    
        try
        {
            String sqlCommand = "SELECT * FROM postal_info WHERE zip_code = ?";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, zipCodeInput);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if(resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                doesZipCodeExitsInDb = true;
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in checkIfValidZipCode: " + e.getMessage());
        }
    
        return doesZipCodeExitsInDb;
    }
    
    /**
     * Laver ArrayList med PreviewDatingUser-objekter ud fra ALLE datingUsers i tabel (undtaget
     * datingUser-obj med idDatingUser OG dem som er blacklisted)
     *
     * @param idDatingUser id som henviser til bruger der IKKE skal gemmes på listen
     *
     * @return ArrayList<PreviewDatingUser> Returnerer liste med ALLE PreviewDatingUser's
     */
    public ArrayList<PreviewDatingUser> createListOfAllDatingUsersFromDb(int idDatingUser)
    {
        lovestruckConnection = establishConnection("lovestruck");
    
        ArrayList<PreviewDatingUser> datingUsersList = new ArrayList<>();
    
        try
        {
            String sqlCommand = "SELECT * FROM dating_users WHERE NOT id_dating_user = ? AND NOT blacklisted = 1";
        
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            // henter ALLE datingUsers fra tabel BORTSET fra den der er logget ind
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, idDatingUser);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            while(resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                PreviewDatingUser previewDatingUser = createPreviewDatingUserFromResultSet(resultSet);
                datingUsersList.add(previewDatingUser);
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in findPostalInfoObjectFromZipCodeInput: " + e.getMessage());
        }
        return datingUsersList;
    }
    
    
    //------------------ ADMIN -------------------//
    
    /**
     * Laver ArrayList med PreviewDatingUser-objekter ud fra ALLE datingUsers i tabel
     * - som (alt efter param blacklisted) enten IKKE er blacklisted eller ER blacklisted
     *
     * @param blacklisted Fortæller om det er blacklisted eller IKKE blacklisted users den skal hente
     *
     * @return ArrayList<PreviewDatingUser> Returnerer liste med ALLE PreviewDatingUser's
     */
    public ArrayList<PreviewDatingUser> createListOfAllDatingUsersToAdmin(int blacklisted)
    {
        lovestruckConnection = establishConnection("lovestruck");
        
        ArrayList<PreviewDatingUser> datingUsersList = new ArrayList<>();
        
        try
        {
            String sqlCommand = "SELECT * FROM dating_users WHERE blacklisted = ?";
            
            // det er vores SQL sætning som vi beder om at få prepared til at blive sendt til databasen:
            // henter ALLE datingUsers fra tabel BORTSET fra den der er logget ind
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
    
            preparedStatement.setInt(1, blacklisted);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()) // hvis der IKKE ligger noget i resultSettet sættes det til null
            {
                PreviewDatingUser previewDatingUser = createPreviewDatingUserFromResultSet(resultSet);
                datingUsersList.add(previewDatingUser);
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in findPostalInfoObjectFromZipCodeInput: " + e.getMessage());
        }
        return datingUsersList;
    }
    
    
    
    public void updateDatingUsersBlacklistedColumn(int idDatingUser, int blacklisted)
    {
        lovestruckConnection = establishConnection("lovestruck");
    
        try
        {
            String sqlCommand = "UPDATE dating_users SET blacklisted = ? WHERE id_dating_user = ?";
        
            PreparedStatement preparedStatement = lovestruckConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, blacklisted);
            preparedStatement.setInt(2, idDatingUser);
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in updateDatingUsersBlacklistedColumn: " + e.getMessage());
        }
    }
  
   
    
    
    //------------------ CHAT -------------------//
    
    public ArrayList<DatingUser> retrieveDatingUsersThatLoggedInUserChattedWithListFromDb(DatingUser loggedInDatingUser)
    {
        // resultSet som indeholder alle idDatingUser's på chat_list_idDatingUser
        ResultSet resultSet = retrieveChatsList(loggedInDatingUser.getIdDatingUser());
        
        // TODO: HER er den liste som vi skal vise på chatPage
       return createDatingUserArrayListFromResultSet(resultSet);
    }
    
    /**
     * Tjekker om en chat_id_id-tabel findes i lovestruck_chat-db
     *
     * @param idDatingUserToChatWith første tal i tabel-navn
     * @param idLoggedInDatingUser anden tal i tabel-navn
     *
     * @return boolean Svaret på om tabellen eksisterer i db'en
     * */
    public boolean checkIfChatsListTableExists(int idLoggedInDatingUser, int idDatingUserToChatWith)
    {
        boolean doesTableExist = false;
        
        chatsListConnection = establishConnection("lovestruck_chat");
        
        try
        {
            DatabaseMetaData dbm = chatsListConnection.getMetaData();
            
            // check om tabellen er der
            ResultSet tables = dbm.getTables(null, null,
                    "chat_" + idLoggedInDatingUser + "_" + idDatingUserToChatWith, null);
            if(tables.next())
            {
                doesTableExist = true;
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in checkIfChatsListTableExists: " + e.getMessage());
        }
        
        return doesTableExist;
    }
    
    public Chat findChatTable(int idLoggedInDatingUser, int idDatingUserToChatWith)
    {
        Chat chat = null;
    
        chatConnection = establishConnection("lovestruck_chat");
    
        try
        {
            // find tabellen i chats_list-db'en som hedder: chats_list_id_id
            String sqlCommand = "SELECT * FROM lovestruck_chat.chat_?_?;";
        
            PreparedStatement preparedStatement = chatConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, idLoggedInDatingUser);
            preparedStatement.setInt(2, idDatingUserToChatWith);
        
            ResultSet resultSet = preparedStatement.executeQuery();
            
            chat = createChatFromResultSet(resultSet);
        }
        catch(SQLException e)
        {
            System.out.println("Error in findChatTable: " + e.getMessage());
        }
        return chat;
    }
    
    public Chat createChatFromResultSet(ResultSet resultSet)
    {
        Chat chat = null;
        
        try
        {
            ArrayList<Message> messagesList = new ArrayList<>();
        
            while(resultSet.next())
            {
                messagesList.add(0,  new Message(resultSet.getString("message"),
                        resultSet.getString("author")));
            }
            
            chat = new Chat(messagesList);
        
        }
        catch(SQLException e)
        {
            System.out.println("Error in createChatFromResultSet: " + e.getMessage());
        }
        
        return chat;
    }
    
    
    public ArrayList<DatingUser> createDatingUserArrayListFromResultSet(ResultSet resultSet)
    {
        ArrayList<DatingUser> allDatingUsersThatLoggedInDatingUserHasChattedWith = new ArrayList<>();
        try
        {
            while(resultSet.next())
            {
                allDatingUsersThatLoggedInDatingUserHasChattedWith.add
                   (retrieveDatingUserFromDb(resultSet.getInt(1)));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in createDatingUserArrayListFromResultSet: " + e.getMessage());
        }
    
        return allDatingUsersThatLoggedInDatingUserHasChattedWith;
        
        
    }
    
    
    public ResultSet retrieveChatsList(int idDatingUser)
    {
        ResultSet resultSet = null;
        
        chatsListConnection = establishConnection("lovestruck_chats_list");
        
        try
        {
            String sqlCommand = "SELECT * FROM lovestruck_chats_list.chats_list_?;";
            
            PreparedStatement preparedStatement = chatsListConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, idDatingUser);
            
            resultSet = preparedStatement.executeQuery();
            
        }
        catch(SQLException e)
        {
            System.out.println("Error in retrieveChatsList: " + e.getMessage());
        }
        return resultSet;
    }
    
    
    public void insertMessageInChatTable(WebRequest messageFromForm, String currentChatTable,
                                         DatingUser loggedInDatingUser)
    {
        chatConnection = establishConnection("lovestruck_chat");
      
        // for String'en currentChatTable altid sådan ud: tal_tal: fx: 3_5
        int id1 = Integer.parseInt(String.valueOf(currentChatTable.charAt(0)));
        int id2 = Integer.parseInt(String.valueOf(currentChatTable.charAt(2)));
        
                System.out.println("ID: " + id1 + id2);
        
    
        try
        {
            String sqlCommand = "INSERT into chat_?_?(message, author) values(?,?)";
        
            PreparedStatement preparedStatement = chatConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            preparedStatement.setString(3, messageFromForm.getParameter("messageinput"));
            preparedStatement.setString(4, loggedInDatingUser.getUsername());
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in insertMessageInChatTable: " + e.getMessage());
        }
    }
    
    public void createChatsListTableDb(int idDatingUser)
    {
        chatsListConnection = establishConnection("lovestruck_chats_list");
        
        try
        {
            // lovestruc_favourites_list == den database som vi laver tabellen i
            // favourites_list_? == navnet på tabellen
            // id_dating_user INT NOT NULL,== navn på ny kolonne og hvilke bokse der er krydset af
            // PRIMARY KEY(id_dating_user) == siger at det er kolonnen id_dating_user som er primary key
            String sqlCommand = "CREATE TABLE lovestruck_chats_list.chats_list_? (id_dating_user INT NOT NULL, " +
                                        "PRIMARY " +
                                        "KEY (id_dating_user));";
            
            PreparedStatement preparedStatement = chatsListConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, idDatingUser);
            
            preparedStatement.executeUpdate();
            
            // adder dummy-datingUser til chatsList-tabellen for at ResultSettet senere ikke er tomt, når vi tjekker
            // for om det eksisterer
            // addDummyDatingUserToChatsList(idDatingUser, chatsListConnection);
            
        }
        catch(SQLException e)
        {
            System.out.println("Error in createChatsListTableDb: " + e.getMessage());
        }
    }
    
    
    /**
     * Tilføjer dummy-datingUser til chatsList-tabellen for at ResultSettet senere ikke er tomt, når vi tjekker
     * for om det eksisterer
     *
     * @param idDatingUser tallet til tabellen som dummy-DatingUser-obj. skal tilføjes til
     * @param chatslistConnection connection til chats_list-db
     * */
    public void addDummyDatingUserToChatsList(int idDatingUser, Connection chatslistConnection)
    {
        try
        {
            String sqlCommand = "INSERT into chats_list_?(id_dating_user) values(?)";
        
            PreparedStatement preparedStatement = chatslistConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, idDatingUser);
            preparedStatement.setInt(2, 0);
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in addDatingUserToChatsListTable: " + e.getMessage());
        }
    }
    
    
    
    /**
     * Laver ny chat_id_id-tabel
     *
     * @param idLoggedInDatingUser id som skal bruges til at oprette tabel-navn
     *
     * @param idDatingUserToChatWith id som skal bruges til at oprette tabel-navn
     *
     * @return Chat Returnerer den nye chat vi har oprettet i db
     * */
    public void createChatTableInDb(int idLoggedInDatingUser, int idDatingUserToChatWith)
    {
        chatConnection = establishConnection("lovestruck_chat");
    
        try
        {
            // lovestruc_favourites_list == den database som vi laver tabellen i
            // favourites_list_? == navnet på tabellen
            // id_dating_user INT NOT NULL,== navn på ny kolonne og hvilke bokse der er krydset af
            // PRIMARY KEY(id_dating_user) == siger at det er kolonnen id_dating_user som er primary key
            String sqlCommand = "CREATE TABLE lovestruck_chat.chat_?_? (id_chat INT NOT NULL AUTO_INCREMENT, message " +
                                        "VARCHAR" +
                                        "(14000) " +
                                        "NOT NULL, author VARCHAR(45) NOT NULL, " +
                                        "PRIMARY KEY (id_chat));";
        
            PreparedStatement preparedStatement = chatConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, idLoggedInDatingUser);
            preparedStatement.setInt(2, idDatingUserToChatWith);
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in createChatsListTableDb: " + e.getMessage());
        }
    }
    
    public void addDatingUsersToEachOthersChatsListsInDb(int idLoggedInDatingUser, int idDatingUserToChatWith)
    {
        // tilføjer datingUser-obj til loggedInDatingUser's tabel (liste over chats)
        addDatingUserToChatsListTable(idLoggedInDatingUser, idDatingUserToChatWith);
    
        // tilføjer datingUser-obj til datingUserToChatWith's tabel (liste over chats)
        addDatingUserToChatsListTable(idDatingUserToChatWith, idLoggedInDatingUser);
    }
    
    public void addDatingUserToChatsListTable(int chatsListId, int idDatingUserToAdd)
    {
        chatsListConnection = establishConnection("lovestruck_chats_list");
    
        try
        {
            String sqlCommand = "INSERT into chats_list_?(id_dating_user) values(?)";
            // String sqlCommand = "UPDATE chats_list_? SET id_dating_user = ?";
        
            PreparedStatement preparedStatement = chatsListConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, chatsListId);
            preparedStatement.setInt(2, idDatingUserToAdd);
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Error in addDatingUserToChatsListTable: " + e.getMessage());
        }
    }
    
    
    
    
    //------------------ IKKE DB-METODER -------------------//
    
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
    
    /**
     * Opretter PreviewDatingUser-obj ud fra entitet på resultSet
     *
     * @param resultSet ResultSet som PreviewDatingUser-obj dannes ud fra
     *
     * @return PreviewDatingUser Returnerer det oprettede PreviewDatingUser-obj
     */
    public PreviewDatingUser createPreviewDatingUserFromResultSet(ResultSet resultSet)
    {
        PreviewDatingUser previewDatingUser = null;
        try
        {
            Blob profilePictureBlob = resultSet.getBlob(9);
            byte[] profilePictureBytes = profilePictureBlob.getBytes(1, (int) profilePictureBlob.length());
            
            
            // TODO: skal det være noget andet end inputstream??
            previewDatingUser = new PreviewDatingUser(resultSet.getInt(1), profilePictureBytes,
                    resultSet.getString(3),
                    resultSet.getInt(6));
        }
        catch(SQLException e)
        {
            System.out.println("Error in createPreviewDatingUserFromResultSet: " + e.getMessage());
        }
        return previewDatingUser;
    }
    
    /**
     * Opretter DatingUser-obj ud fra resultSet
     *
     * @param resultSet ResultSet som DatingUser-obj dannes ud fra
     *
     * @return DatingUser Returnerer det oprettede DatingUser-obj
     */
    public DatingUser createDatingUserFromResultSet(ResultSet resultSet)
    {
        DatingUser datingUser = new DatingUser();
        try
        {
            if(resultSet.next())
            {
                Blob profilePictureBlob = resultSet.getBlob(9);
                byte[] profilePictureBytes = profilePictureBlob.getBytes(1, (int) profilePictureBlob.length());
                ArrayList<DatingUser> favouritesList =
                        convertResultSetToFavouritesList(retrieveFavouritesList(resultSet.getInt(1)));
                
                datingUser.setIdDatingUser(resultSet.getInt(1));
                datingUser.setBlacklisted(datingUser.convertIntToBoolean(resultSet.getInt(2)));
                datingUser.setUsername(resultSet.getString(3));
                datingUser.setEmail(resultSet.getString(4));
                datingUser.setPassword(resultSet.getString(5));
                datingUser.setAge(resultSet.getInt(6));
                datingUser.setSex(datingUser.convertIntToBoolean(resultSet.getInt(7)));
                datingUser.setInterestedIn(resultSet.getInt(8));
                datingUser.setProfilePictureBytes(profilePictureBytes);
                datingUser.setDescription(resultSet.getString(10));
                datingUser.setTagsList(datingUser.convertStringToTagsList(resultSet.getString(11)));
                datingUser.setPostalInfo(findPostalInfoObjectFromIdPostalInfo(resultSet.getInt(12)));
                datingUser.setFavouritesList(favouritesList);
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in createDatingUserFromResulSet: " + e.getMessage());
        }
        
        return datingUser;
    }
    
    
    public ResultSet retrieveFavouritesList(int idDatingUser)
    {
        ResultSet resultSet = null;
        
        favouritesListConnection = establishConnection("lovestruck_favourites_list");
        
        try
        {
            String sqlCommand = "SELECT * FROM lovestruck_favourites_list.favourites_list_?;";
            
            PreparedStatement preparedStatement = favouritesListConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, idDatingUser);
            
            resultSet = preparedStatement.executeQuery();
            
        }
        catch(SQLException e)
        {
            System.out.println("Error in retrieveFavouritesList: " + e.getMessage());
        }
        return resultSet;
    }
    
    public ArrayList<DatingUser> convertResultSetToFavouritesList(ResultSet resultSet)
    {
        ArrayList<DatingUser> favouritesList = new ArrayList<>();
        try
        {
            while(resultSet.next())
            {
                favouritesList.add(retrieveDatingUserFromDb(resultSet.getInt(1)));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error in convertResultSetToFavouritesList: " + e.getMessage());
        }
        
        return favouritesList;
    }
    
    public void resetLoggedInUser()
    {
        loggedInDatingUser = null;
        loggedInAdmin = null;
    }
    
    
    
    
}
