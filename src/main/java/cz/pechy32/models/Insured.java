package cz.pechy32.models;

import cz.pechy32.models.Address;

import java.util.Date;

public class Insured {
    private int id;
    private String name;
    private String surname;
    private Date dateOfBirth;
    private String socialNumber;
    private String phone;
    private String email;
    private Address address;
    private String gender;
    private String defaultPhotoUrl;//proměnná uchovávající cestu k profilovému obrázku

    public String getDefaultPhotoUrl() {
        return defaultPhotoUrl;
    }


    public Insured(int id, String name, String surname, Date dateOfBirth, String socialNumber, String phone, String email, Address address) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.socialNumber = socialNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        setGender();//automaticky se nastaví pohlaví instance dle RČ
        setDefaultPhotoUrl();//nastaví URL k profilovému obrázku
    }

    public String getGender() {
        return gender;
    }

    /**
     * nastavení pohlaví dle RČ
     */
    public void setGender() {
        //pokud je druhé dvojčíslí v RČ > 12, jedná se o ženu
        if(Integer.parseInt(socialNumber.substring(2, 4)) > 12){
            this.gender = "female";
        }
        else{
            this.gender = "male";
        }
    }

    /**
     * Metoda nastaví URL k defaultnímu profilovému obrázku
     */
    public void setDefaultPhotoUrl() {
        if(gender.equals("female")){
            this.defaultPhotoUrl = "/woman_siluete.png";
        }
        else{
            this.defaultPhotoUrl = "/man_siluete.png";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSocialNumber() {
        return socialNumber;
    }

    public void setSocialNumber(String socialNumber) {
        this.socialNumber = socialNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
