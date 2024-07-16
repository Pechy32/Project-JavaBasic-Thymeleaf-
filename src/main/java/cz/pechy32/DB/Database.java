package cz.pechy32.DB;

import java.sql.*;

public class Database {

    //připojení k databázi
    protected Connection connection;

    //instance třídy query
    protected Query query;

    /**
     * Konstruktor třídy Database
     *
     * @param db název databáze
     * @param userName uživatelské jméno
     * @param password heslo
     * @throws SQLException
     */
    public Database(String db, String userName, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/" + db, userName, password);
        System.out.println("Spojení s databází bylo navázáno.");
    }

    /**
     * Metoda vrátí počet ovlivněných řádků v databázi
     * @param query
     * @param params
     * @return
     * @throws SQLException
     */
    private int query(String query, Object[] params) throws SQLException{
        PreparedStatement ps = connection.prepareStatement(query);
        if (params != null){
            int index = 1;
            for(Object param : params){
                ps.setObject(index, param);
                index++;
            }
        }
        return ps.executeUpdate();
    }

    /**
     * Metoda vymaže údaje z databázové tabulky
     * @param table - název tabulky
     * @param requirement - název sloupce (např. id = ?)
     * @param param - parametr sloupce (např. 1)
     * @return
     * @throws SQLException
     */
    public int delete(String table, String requirement, Object[] param) throws SQLException{
        query = new Query();
        query.delete(table)
                .where(requirement);
        return query(query.getQuery(), param);
    }

    /**
     * Metoda uloží do databázové tabulky 1 subjekt
     * @param table název tabulky
     * @param params parametry
     * @return
     * @throws java.sql.SQLException
     */
    public int insert(String table, Object[] params) throws SQLException{
        query = new Query();
        query.insert(table)
                .values(params);
        return query(query.getQuery(), params);
    }

    /**
     * Metoda pro qktualizaci dat uložených v databázové tabulce
     * @param table název tabulky
     * @param columns sloupce
     * @param requirement název sloupce
     * @param params parametr sloupce
     * @return
     * @throws SQLException
     */
    public int update(String table, String[] columns, String requirement, Object[] params) throws SQLException {
        query = new Query();
        query.update(table)
                .set(columns)
                .where(requirement);

        return query(query.getQuery(), params);
    }


    /**
     * Metoda pro získání údajů z databázové tabulky
     * @param table název tabulky (např insureds, nebo insureds JOIN addresses ON id=id)
     * @param columns sloupce, které chci vybrat (null = všechny)
     * @param requirement název sloupce (např. id = ?)
     * @param params parametr sloupce (např. 1)
     * @return result set obsahující data z databázové tabulky(ek)
     * @throws SQLException
     */
    public ResultSet select(String table, Object[] columns, String requirement, Object[] params) throws SQLException {
        Query query = new Query();
        query.select(columns).from(table);

        if (requirement != null && !requirement.isEmpty()) {
            query.where(requirement);
        }

        PreparedStatement ps = connection.prepareStatement(query.getQuery());
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
        return ps.executeQuery();
    }

    /**
     * Metoda pro získání počtu záznamů
     * @param table název tabulky
     * @return
     * @throws SQLException
     */
    public int count(String table) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM "+table);
        ResultSet result = ps.executeQuery();
        result.next();
        return result.getInt(1);
    }

    /**
     * Metoda pro získání ID posledně vloženého záznamu
     *
     * @return ID posledně vloženého záznamu
     * @throws SQLException
     */
    public int getLastInsertId() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            throw new SQLException("Nepodařilo se získat poslední vložené ID.");
        }
    }

    /**
     * Metoda pro uzavření spojení s databází
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("Spojení s databází bylo ukončeno.");
        }
    }

}
