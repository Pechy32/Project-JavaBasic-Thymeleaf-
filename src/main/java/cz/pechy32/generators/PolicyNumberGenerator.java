package cz.pechy32.generators;

import cz.pechy32.DB.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Třída se stará o generování pojistných smluv
 */
public class PolicyNumberGenerator {
    /**
     * Metoda vygeneruje číslo smlouvy na základě parametrů
     * @param insuranceName - typ pojištění
     * @param socialNumber - rodné číslo pojištěného
     * @return číslo smlouvy ve formátu String
     */
    public String generatePolicyNumber(String insuranceName, String socialNumber){
        String policyNumber = "";
        String insuranceCode = "";

        //Každému typu pojištění náleží kód
        //110 - zdraví
        //210 - majetek
        //310 - cestovní
        if (insuranceName.equals("health")){
            insuranceCode = "110";
        }
        else if(insuranceName.equals("property")){
            insuranceCode = "210";
        }
        else{
            insuranceCode = "310";
        }

        //odstranění lomítka z RČ (123456/1234 -> 1234561234)
        String socialNumberPart1 = socialNumber.substring(0, 6);
        String socialNumberPart2 = socialNumber.substring(7);
        socialNumber = socialNumberPart1 + socialNumberPart2;

        //získání ID posledního pojištění - použije se jako poslední číslice čísla smlouvy (zajistí unikátnost)
        //SELECT insurance_id FROM insurances ORDER BY id DESC LIMIT 1;
        int id = 0;
        try {
            Database database = new Database("insurance-app_db", "root", "");
            //spuštění dotazu na databázi a naplnění ResultSetu
            ResultSet rs = database.select("insurances ORDER BY insured_id DESC LIMIT 1", new Object[]{"insurance_id"}, null, null);
            //zpracování RS
            while (rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        id++;//inkrementace id - v případě, že je tabulka prázdná id bude 1

        //vygenerování čísla smlouvy
        policyNumber = insuranceCode + socialNumber + id;
        return policyNumber;
    }
}
