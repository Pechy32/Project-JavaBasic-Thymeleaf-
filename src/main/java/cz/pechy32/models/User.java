package cz.pechy32.models;

public class User {
    private int id;
    private String name;
    private String password;
    private boolean isEnabled;
    private int insuredId;

    public User(int id, String name, String password, int insuredId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.insuredId = insuredId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getInsuredId() {
        return insuredId;
    }

    public void setInsuredId(int insuredId) {
        this.insuredId = insuredId;
    }
}
