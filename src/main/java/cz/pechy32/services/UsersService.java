package cz.pechy32.services;

import cz.pechy32.DB.Database;
import cz.pechy32.models.User;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class UsersService {
    private ArrayList<User> allUsers;

    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers() {
        ArrayList<User> listOfUsers = new ArrayList<>();
        Database database = null;
        ResultSet rs = null;

        try {
            // připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //vytvoření SQL dotazu
            String[] columns = {"user_id", "username", "password", "insured_id"};

            //spuštění SQL dotazu
            rs = database.select("users", columns, null, null);

            while (rs.next()){
                listOfUsers.add(new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // uzavření spojení s databází v bloku finally
            if (database != null) {
                try {
                    database.closeConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.allUsers = listOfUsers;
    }

    /**
     * metoda provede dotaz na databázi
     * @param name jméno uživatele
     * @return instance uživatele dle jména
     */
    public User selectUserByUsername(String name){
        Database database = null;
        ResultSet rs = null;
        User user = null;

        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //vytvoření dotazu
            String[] columns = {"user_id", "username", "password", "insured_id"};

            // Spuštění dotazu pomocí metody select() -> SELECT * FROM insurances WHERE insured_id = ?
            rs = database.select("users", columns, "username = ?", new Object[]{name});

            // Vytvoření instance User z ResultSetu
            while (rs.next()) {
                    user = new User(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4)
                    );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // uzavření spojení s databází a ResultSetu
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (database != null) {
                try {
                    database.closeConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return user;
    }
}
