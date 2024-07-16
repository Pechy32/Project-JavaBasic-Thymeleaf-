package cz.pechy32.services;

import cz.pechy32.DB.Database;
import cz.pechy32.models.Address;
import cz.pechy32.models.Insured;
import cz.pechy32.models.InsuredsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cz.pechy32.generators.SocialNumberGenerator;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Třída slouží ke správě pojištěných
 */
@Service
public class InsuredsService {
    private ArrayList<Insured> allInsureds;

    public ArrayList<Insured> getAllInsureds() {
        return allInsureds;
    }

    /**
     *
     * @param principal
     * @return
     */
    public List<Insured> getInsuredsByUser(Principal principal) {

        String loggedInUsername = principal.getName(); // Získání uživatelského jména aktuálně přihlášeného uživatele
        List<String> userRoles = new ArrayList<>();

        // Získání instance aktuálního uživatele z SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(authentication.getPrincipal());//kontrola

        // Kontrola, zda je uživatel přihlášen
        if (authentication != null) {
            // Získání přihlášeného uživatele (Principal)
            Object loggedUser = authentication.getPrincipal();

            UserDetails userDetails = (UserDetails)loggedUser;

            //naplnění Listu<String> rolemi daného uživatele
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                userRoles.add(authority.getAuthority());}
        }

        // Implementace getteru pro získání všech pojištěnců a jejich filtrování podle aktuálního uživatele
        List<Insured> allInsureds = getAllInsureds(); // Načtení všech pojištěnců
        List<Insured> filteredInsureds = new ArrayList<>();

        System.out.println(userRoles);//kontrola

        //pokud přihlášený uživatel není admin - získání instance majitele účtu pomocí dotazu na databázi
        if (!userRoles.contains("ROLE_ADMIN")) {
            Database database = null;
            ResultSet rs = null;
            int insuredId = 0;
            try {
                //připojení k databázi
                database = new Database("insurance-app_db", "root", "");

                //vytvoření SQL dotazu
                String table = "users";
                String[] columns = {"insured_id"};
                Object[] params = {loggedInUsername};

                //spuštění sql dotazu
                rs = database.select(table, columns, "username = ?", params);
                if (rs.next()) {
                    insuredId = rs.getInt(1);
                }
                System.out.println(insuredId);//pro kontrolu zda funguje

                //naplnění seznamu filteredInsureds - bude přidán zpravidla 1 záznam odpovídající id uživatele
                for (Insured insured : getAllInsureds()){
                    if (insured.getId() == insuredId){
                        filteredInsureds.add(insured);
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                //uzavření spojení s databází
                if (database != null) {
                    try {
                        database.closeConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return filteredInsureds;
        }
        else {
            return allInsureds;
        }
    }

    /**
     * Metoda pro přidání pojištěného do databáze pojištěných
     * tabulky insureds a addresses
     * @param insuredsDTO - data získaná z formuláře
     */
    public void addInsuredToDatabase(@ModelAttribute InsuredsDTO insuredsDTO) {
        SocialNumberGenerator generator = new SocialNumberGenerator();
        Database database = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //do pole objektů params si uložím data získané z formuláře
            Object[] insuredParams = {
                    null,//id
                    insuredsDTO.getName().trim(),
                    insuredsDTO.getSurname().trim(),
                    insuredsDTO.getDateOfBirth(),
                    generator.generateSocialNumber(insuredsDTO.getDateOfBirth(), insuredsDTO.getGender()),//vygenerování rč
                    insuredsDTO.getPhone().trim(),
                    insuredsDTO.getEmail().trim()
            };

            //vložení parametrů do tabulky insureds
            database.insert("insureds", insuredParams);

            //získání id posledního vloženého záznamu do databáze - získání FK pro záznam v tabulce addresses
            int lastInsertID = database.getLastInsertId();

            //vytvoření obejktu parametrů pro vložení do tabulky address
            Object[] addressParams = {
                    null,//id
                    insuredsDTO.getStreet(),
                    insuredsDTO.getCity(),
                    null,//country
                    insuredsDTO.getPostalCode(),
                    lastInsertID, // FK insured_id
                    insuredsDTO.getHouseNumber()
            };

            //vložení parametrů do tabulky address
            database.insert("addresses", addressParams);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //uzavření spojení s databází
            if (database != null) {
                try {
                    database.closeConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void updateInsured(int id, String name, String surname, String email, String phone, String street, String houseNumber, String city, String postalCode){
        Database database = null;
        try {
            // připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //Vytvoření update dotazu na databázi
            //tabulky
            String tables = "insureds JOIN addresses ON insureds.insured_id = addresses.insured_id";

            //sloupce k aktualizaci
            String[] columns = {
                    "insureds.name",
                    "insureds.surname",
                    "insureds.phone",
                    "insureds.email",
                    "addresses.street",
                    "addresses.city",
                    "addresses.house_number",
                    "addresses.postal_code"};

            //hodnoty dosazené do sloupců
            Object[] params = {
                    name.trim(),
                    surname.trim(),
                    phone.trim(),
                    email.trim(),
                    street.trim(),
                    city.trim(),
                    houseNumber.trim(),
                    postalCode.trim(),
                    id //poslední parametr pro dosazení do requierement
            };

            //spuštění dotazu
            database.update(tables, columns, "insureds.insured_id = ?", params);

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
    }

    /**
     * Metoda vrací všechny záznamy z tabulek insureds a addresses
     * Data pak ukládá do seznamu allInsureds
     */
    public void setAllInsureds() {
        ArrayList<Insured> insuredsList = new ArrayList<>(); // vytvoření prázdného seznamu
        Database database = null;

        try {
            // připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            // Vytvoření join dotazu na databázi (insureds JOIN addresses)
            String joinClause = "insureds JOIN addresses ON insureds.insured_id = addresses.insured_id";

            // Specifikace sloupců
            Object[] columns = {
                    "insureds.insured_id",
                    "insureds.name",
                    "insureds.surname",
                    "insureds.date_of_birth",
                    "insureds.social_security_number",
                    "insureds.phone",
                    "insureds.email",
                    "addresses.street",
                    "addresses.city",
                    "addresses.house_number",
                    "addresses.postal_code"
            };

            // Spuštění dotazu pomocí metody select()
            try (ResultSet rs = database.select(joinClause, columns, null, null)) {
                // naplnění seznamu z ResultSetu
                while (rs.next()) {
                    insuredsList.add(new Insured(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getDate(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            new Address(
                                    rs.getString(8),
                                    rs.getString(9),
                                    rs.getString(10),
                                    rs.getString(11)
                            )
                    ));
                }
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
        this.allInsureds = insuredsList;
    }

    /**
     * Metoda vybere pojištěného z databáze dle ID
     * @param id - id hledaného pojištěného
     * @return instance pojištěného dle zadaných kritérií
     */
    public Insured selectInsuredById(int id) {
        Insured insuredById = null;
        Database database = null;
        ResultSet rs = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            // Vytvoření join dotazu na databázi (insureds JOIN addresses)
            String joinClause = "insureds JOIN addresses ON insureds.insured_id = addresses.insured_id";
            String condition = "insureds.insured_id = ?";

            // Specifikace sloupců
            Object[] columns = {
                    "insureds.insured_id",
                    "insureds.name",
                    "insureds.surname",
                    "insureds.date_of_birth",
                    "insureds.social_security_number",
                    "insureds.phone",
                    "insureds.email",
                    "addresses.street",
                    "addresses.city",
                    "addresses.house_number",
                    "addresses.postal_code"
            };

            // Specifikace parametrů
            Object[] params = {id};

            // Spuštění dotazu pomocí metody selectWithJoin()
            rs = database.select(joinClause, columns, condition, params);

            // vytvoření instance pojištěnce z ResultSetu
            if (rs.next()) {
                insuredById = new Insured(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDate(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        new Address(
                                rs.getString(8),
                                rs.getString(9),
                                rs.getString(10),
                                rs.getString(11)
                        )
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
        return insuredById;
    }

    public void deleteInsuredById(int id) {
        Database database = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            // spuštění delete dotazu na databázi -> DELETE FROM insureds WHERE insured_id = id;
            database.delete("insureds", "insured_id = ?", new Object[]{id});

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // uzavření spojení s databází
            if (database != null) {
                try {
                    database.closeConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Metoda vybere pojištěné dle jména či příjmení(pro hromadné vyhledávání oddělujte mezerou)
     * @param name jméno či příjmení pojištěného
     * @return kolekce záznamů odpovídajících daným kritériím
     */
    public HashSet<Insured> getInsuredsByName(String name) {
        ArrayList<Insured> insuredsList = getAllInsureds();
        HashSet<Insured> insuredsByName = new HashSet<>();//zajistí unikátnost hledaných záznamů
        String[] names = null;
        Boolean recordFound = false;

        if (name.trim().contains(" "))
        {
            names = name.split(" ");
        }

        for (Insured insured : insuredsList) {
            if (
                    insured.getName().equalsIgnoreCase(name.trim()) //shoda jména
                    || insured.getSurname().equalsIgnoreCase(name.trim()) //shoda příjmení
                    || (insured.getName() + " " + insured.getSurname()).equalsIgnoreCase(name.trim()) //shoda jména i příjmení
            ) {
                insuredsByName.add(insured);
                recordFound = true;
            }

            //druhý způsob hledání pokud nebyla nalezena shoda - hromadné vyhledávání(bylo zadáno více jmen)
            if ((!recordFound) && (names != null)){
                for (int i = 0; i < names.length; i++) {
                    for (Insured insured1 : insuredsList) {
                        if (names[i].equalsIgnoreCase(insured1.getName()) || names[i].equalsIgnoreCase(insured1.getSurname())) {
                            insuredsByName.add(insured1);
                        }
                    }
                }
            }
        }
        return insuredsByName;
    }
}
