package cz.pechy32.models;

public class Address {
    private String street;
    private String city;
    private String houseNumber;
    private String country;
    private String postalCode;
    private int insuredID;

    public Address(String street, String city, String houseNumber, String postalCode) {
        this.street = street;
        this.city = city;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getInsuredID() {
        return insuredID;
    }

    public void setInsuredID(int insuredID) {
        this.insuredID = insuredID;
    }
}
