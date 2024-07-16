package cz.pechy32.services;

import cz.pechy32.DB.Database;
import cz.pechy32.generators.PolicyNumberGenerator;
import cz.pechy32.models.Insurance;
import cz.pechy32.models.InsuranceDTO;
import cz.pechy32.models.Insured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída slouží ke správě pojištění
 */
@Service
public class InsurancesService {
    ArrayList<Insurance> insurancesList;


    public ArrayList<Insurance> getInsurancesList() {
        return insurancesList;
    }


    /**
     * Jednoduchá metoda pro přeložení názvu pojištění do cz
     * @param name název anglický název pojištění(viz DB -> property, health, travel)
     * @return česká název pojištění
     */
    public String translateInsuranceName(String name){
        String czInsuranceName = "";
        if (name.equals("property")) {
            czInsuranceName = "Majetkové";
        }
        else if(name.equals("health")){
            czInsuranceName = "Zdravotní";
        }
        else if(name.equals("travel")){
            czInsuranceName = "Cestovní";
        }
        else{
            czInsuranceName = "Pojištění";
        }
        return  czInsuranceName;
    }

    /**
     * Metoda provede dotaz na databázi a vybere všechna pojištění, která uloží do kolekce pro další zpracování
     */
    public void setInsurancesList(){
        ArrayList<Insurance> list = new ArrayList<>(); // vytvoření prázdného seznamu
        Database database = null;

        try {
            // připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            // Spuštění dotazu pomocí metody select() -> SELECT * FROM insurances
            try (ResultSet rs = database.select("insurances", null, null, null)) {
                // naplnění seznamu z ResultSetu
                while (rs.next()) {
                    list.add(
                            new Insurance(
                                    rs.getInt(1),//id
                                    rs.getString(2),//název
                                    rs.getString(3),//číslo smlouvy
                                    rs.getDate(4),//datum od
                                    rs.getDate(5),//datum do
                                    rs.getBigDecimal(6),//částka
                                    rs.getInt(7))//insured_id - FK
                    );
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
        //nastavení kolekce pojištění
        this.insurancesList = list;
    }

    /**
     * Vybere záznamy na základě přihlášeného uživatele
     * @param principal - aktuálně přihlášený uživatel
     * @return kolekci pojištění dle zadaných kritérií
     */
    public ArrayList<Insurance> getInsurancesByUser(Principal principal){

        String loggedInUsername = principal.getName();//název uživatele
        List<String> userRoles = new ArrayList<>();//role uživatele

        // Získání instance aktuálního uživatele z SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kontrola, zda je uživatel přihlášen
        if (authentication != null) {
            // Získání přihlášeného uživatele (Principal)
            Object loggedUser = authentication.getPrincipal();

            UserDetails userDetails = (UserDetails)loggedUser;

            //naplnění Listu<String> rolemi daného uživatele
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                userRoles.add(authority.getAuthority());}
        }

        ArrayList<Insurance> allInsurances = getInsurancesList();
        ArrayList<Insurance> filteredInsurances = new ArrayList<>();

        //samotné filtrování obsahu
        if (!userRoles.contains("ROLE_ADMIN")){

            //získání id uživatele pomocí dotazu na databázi
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

                //naplnění kolekce filteredInsurances podle id_uživatele
                for (Insurance insurance : getInsurancesList()){
                    if (insurance.getInsuredID() == insuredId){
                        filteredInsurances.add(insurance);
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
            return filteredInsurances;
        }
        else {
            return allInsurances;
        }
    }

    /**
     * Metoda přidá pojištní do databáze
     * @param insuredID ID pojištěného - FK
     * @param insuranceDTO data z formuláře
     */
    public void addInsuranceToDatabase(int insuredID, @ModelAttribute InsuranceDTO insuranceDTO){
        PolicyNumberGenerator policyNumberGenerator = new PolicyNumberGenerator();
        InsuredsService insuredsService = new InsuredsService();
        Database database = null;

        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //získání instance pojištěného pro kterého vytvářím pojištění
            Insured insured = insuredsService.selectInsuredById(insuredID);

            //získání RČ pro vygenerování čísla smlouvy
            String socialNumber = insured.getSocialNumber();

            //do pole objektů params si uložím data získaná z formuláře + vygeneruji číslo smlouvy
            Object[] insuranceParams = {
                    null,//id
                    insuranceDTO.getName(),
                    policyNumberGenerator.generatePolicyNumber(insuranceDTO.getName(), socialNumber),//vygenerování č. smlouvy
                    insuranceDTO.getStartDate(),
                    insuranceDTO.getEndDate(),
                    insuranceDTO.getAmount(),
                    insuredID // FK
            };

            //vložení parametrů do tabulky insurances pomocí dotazu na databázi
            database.insert("insurances", insuranceParams);

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

    /**
     * Metoda vybere z databáze pojištění dle id pojištěného
     * @param id id pojištěného
     * @return kolekce pojištění daného pojištěného
     */
    public ArrayList<Insurance> selectInsurancesById(int id) {
        ArrayList<Insurance> insurancesById = new ArrayList<>();
        Database database = null;
        ResultSet rs = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            // Spuštění dotazu pomocí metody select() -> SELECT * FROM insurances WHERE insured_id = ?
            rs = database.select("insurances", null, "insured_id = ?", new Object[]{id});

            // naplnění kolekce pojištění z ResultSetu
            while (rs.next()) {
                insurancesById.add(new Insurance(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDate(4),
                        rs.getDate(5),
                        rs.getBigDecimal(6),
                        rs.getInt(7)
                ));
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
        return insurancesById;
    }

    /**
     * Metoda vybere z databáze pojištění dle jeho id
     * @param id id pojištění
     * @return instanci pojištění dle zadaných kritérií
     */
    public Insurance selectInsuranceById(int id) {
        Insurance insuranceById = null;
        Database database = null;
        ResultSet rs = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            // Spuštění dotazu pomocí metody select() -> SELECT * FROM insurances WHERE insurance_id = ?
            rs = database.select("insurances", null, "insurance_id = ?", new Object[]{id});

            // naplnění kolekce pojištění z ResultSetu
            while (rs.next()) {
                insuranceById = new Insurance(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDate(4),
                        rs.getDate(5),
                        rs.getBigDecimal(6),
                        rs.getInt(7)
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
        return insuranceById;
    }

    /**
     * Metoda vyselektuje pojištění dle jeho názvu
     * @param insuranceName - název pojištění, které chvi vybrat
     * @param listToSelect - kolekce s kterou pracuji
     * @return kolekce dle zadaných parametrů
     */
    public ArrayList<Insurance> selectInsurancesByName(String insuranceName, ArrayList<Insurance> listToSelect) {
        ArrayList<Insurance> insurancesByName = new ArrayList<>();

        for (Insurance insurance : listToSelect){
            if(insurance.getName().equals(insuranceName)){
                insurancesByName.add(insurance);
            }
        }
        return insurancesByName;
    }

    /**
     * Metoda aktualizuje záznam o pojištění v databázi
     * @param id id pojištění
     * @param endDate datum ukončení pojištění
     * @param amount částka
     */
    public void updateInsurance(int id, LocalDate endDate, BigDecimal amount){
        Database database = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //tabulka
            String table = "insurances";
            String[] columns;
            Object[] params;

            //sloupce
            columns = new String[]{"end_date", "insured_amount"};

            //parametry
            if(endDate != null) {
                params = new Object[]{endDate, amount, id};
            }
            else{
                params = new Object[]{null, amount, id};
            }
            //spuštění SQL dotazu
            database.update(table, columns, "insurance_id = ?", params);

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
     * Metoda odstratní záznam o pojištění z databáze
     * @param id id pojištění
     */
    public void deleteInsurance(int id){
        Database database = null;
        try {
            //připojení k databázi
            database = new Database("insurance-app_db", "root", "");

            //tabulka
            String table = "insurances";

            //defincie parametrů
            Object[] params = {id};

            //spuštění SQL dotazu
            database.delete(table, "insurance_id = ?", params);

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

    // Metoda pro formátování data pojištění
    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. M. yyyy");
        return date.format(formatter);
    }
}
