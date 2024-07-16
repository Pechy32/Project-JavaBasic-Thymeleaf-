package cz.pechy32.statistics;

/**
 * Třída reprezentující statistiku uživatelských účtů
 */
public class UsersStatistics {
    int accountsCount;

    public UsersStatistics(int accountsCount) {
        this.accountsCount = accountsCount;
    }

    public int getAccountsCount() {
        return accountsCount;
    }

    public void setAccountsCount(int accountsCount) {
        this.accountsCount = accountsCount;
    }
}
