package cz.pechy32.statistics;

import cz.pechy32.models.Insurance;
import cz.pechy32.models.Insured;
import cz.pechy32.models.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Třída se stará o generování statistik
 */
@Service
public class StatisticGenerator {
    /**
     * Metoda vygeneruje statistiku o pojištěných
     * @param insuredsCollection - kolekce pojištěných
     * @return instance statistiky o pojištěných
     */
    public InsuredsStatistics createInsuredStatistics(ArrayList<Insured> insuredsCollection){

        InsuredsStatistics insuredsStatistics = null;

         int womenCount = 0;
         int manCount = 0;

         //získání celkového počtu pojištěných
        int insuredsCount = insuredsCollection.size();

        //získání průměrného věku + počtu mužů a žen
        double averageAge = 0;
        int ageSummary = 0;

        for (Insured insured : insuredsCollection) {
            int age = LocalDate.now().getYear() - (insured.getDateOfBirth().getYear() + 1900);
            ageSummary += age;
            if (insured.getGender().equals("female")){
                womenCount++;
            }
            else{
                manCount++;
            }
        }

        if (insuredsCount > 0) {
            averageAge = (double) ageSummary / insuredsCount;
        } else {
            averageAge = 0;
        }

        //nastavení objektu insuredsStatistics
        insuredsStatistics = new InsuredsStatistics(insuredsCount, womenCount, manCount, averageAge);

        return insuredsStatistics;
    }

    /**
     * Metoda vytvoří statistiku o pojištěních
     * @param insurancesCollection kolekce pojištění
     * @return instance statistiky o pojištěních
     */
    public InsurancesStatistics createInsuranceStatistics(ArrayList<Insurance> insurancesCollection){
        int insurancesCount = insurancesCollection.size();

        //získání počtu jednodlivých pojištění (majetek, zdraví, cestování) + průměrné částky
        int healthInsuranceCount = 0;
        int travelInsuranceCount = 0;
        int propertyInsuranceCount = 0;

        double sum = 0;
        double averageSum = 0;

        for (Insurance insurance : insurancesCollection){
            sum+=insurance.getAmount().doubleValue();
            if (insurance.getName().equals("health")){
                healthInsuranceCount++;
            }
            else if (insurance.getName().equals("travel")) {
                travelInsuranceCount++;
            }
            else {
                propertyInsuranceCount++;
            }
        }
        averageSum = sum / insurancesCount;
        averageSum = Math.round(averageSum * 100.0) / 100.0; //na 2 des. místa

        //nastavení objektu insurancesStatistics
        InsurancesStatistics insurancesStatistics = new InsurancesStatistics(insurancesCount, propertyInsuranceCount, healthInsuranceCount, travelInsuranceCount, averageSum);

        return insurancesStatistics;
    }

    /**
     * Metoda vygeneruje statistiku o uživatelích
     * @param usersCollection kolekce uživatelů
     * @return instance statistiky o uživatelích
     */
    public UsersStatistics createUsersStatistics(ArrayList<User> usersCollection){
        int usersCount = usersCollection.size();

        UsersStatistics usersStatistics = new UsersStatistics(usersCount);

        return usersStatistics;
    }
}
