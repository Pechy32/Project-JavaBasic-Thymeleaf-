package cz.pechy32.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Insurance {
    private int id;
    private String name;
    private String policyNumber;
    private Date startDate;
    private Date endDate;
    private BigDecimal amount;
    private int insuredID;

    public Insurance(int id, String name, String policyNumber, Date startDate, Date endDate, BigDecimal amount, int insuredID) {
        this.id = id;
        this.name = name;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.insuredID = insuredID;
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

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getInsuredID() {
        return insuredID;
    }

    public void setInsuredID(int insuredID) {
        this.insuredID = insuredID;
    }
}
