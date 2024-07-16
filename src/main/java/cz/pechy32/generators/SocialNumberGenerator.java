package cz.pechy32.generators;

import java.time.LocalDate;

/**
 * Třída starající se o generování fiktivních rodných čísel
 * pro účely aplikace není vhodné RČ zadávat
 */
public class SocialNumberGenerator {

    /**
     * Metoda kontroluje dělitelnost RČ číslem 11
     * @param socialNumber - rodné číslo (10 cifer)
     * @return true/false - zda je dělitelné 11
     */
    public boolean isValidSocialNumber(String socialNumber) {
        long number = Long.parseLong(socialNumber);
        return number % 11 == 0;
    }

    /**
     * Metoda vygeneruje náhodné fiktivní rodné číslo na základě data narození a pohlaví + náhodná 4 čísla za lomítkem
     * @param date_of_birth - datum narození
     * @param gender . pohlaví
     * @return náhodné fiktivní rodné číslo ve formátu String ("xxxxxx/xxxx") jehož číselná hodnota je dělitelná 11 beze zbytku
     */
    public String generateSocialNumber(LocalDate date_of_birth, String gender) {

        //získání prvního dvojčíslí značící rok
        String year = String.valueOf(date_of_birth.getYear());
        year = year.substring(2);

        //získání druhého dvojčíslí značící měsíc + ošetření, že dotyčný je žěna
        String month;
        if (gender.equals("male")) {
            month = String.format("%02d", date_of_birth.getMonthValue());
        } else {
            month = String.format("%02d", date_of_birth.getMonthValue() + 50);
        }

        //získání třetího dvojčíslí značící den
        String day = String.format("%02d", date_of_birth.getDayOfMonth());

        //vygenerování 3 náhodných čísel značících pořadí
        String randomThreeNumbers = "";
        for (int i = 0; i < 3; i++) {
            int randomNumber = (int) (Math.random() * 10);
            randomThreeNumbers += String.valueOf(randomNumber);
        }

        String socialNumber = year + month + day + randomThreeNumbers;

        //Přidání poslední číslice RČ + kontrola dělitelnosti 11
        for (int i = 0; i < 10; i++) {
            String candidateSocialNumber = socialNumber + i;
            if (isValidSocialNumber(candidateSocialNumber)) {
                socialNumber += i;
                break;
            }
        }
        return year + month + day + "/" + socialNumber.substring(6);
    }
}
