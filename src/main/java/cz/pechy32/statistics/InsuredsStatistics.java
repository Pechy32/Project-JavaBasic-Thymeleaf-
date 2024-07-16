package cz.pechy32.statistics;

/**
 * Třída reprezentující statistiku o pojištěných
 */
public class InsuredsStatistics {
    private int count;
    private int womenCount;
    private int manCount;
    private double averageAge;

    public InsuredsStatistics(int count, int womenCount, int manCount, double averageAge) {
        this.count = count;
        this.womenCount = womenCount;
        this.manCount = manCount;
        this.averageAge = averageAge;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getWomenCount() {
        return womenCount;
    }

    public void setWomenCount(int womenCount) {
        this.womenCount = womenCount;
    }

    public int getManCount() {
        return manCount;
    }

    public void setManCount(int manCount) {
        this.manCount = manCount;
    }

    public double getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(double averageAge) {
        this.averageAge = averageAge;
    }
}
