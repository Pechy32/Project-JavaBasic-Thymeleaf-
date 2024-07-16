package cz.pechy32.statistics;

/**
 * Třída reprezentuje statistiku pojištění
 */
public class InsurancesStatistics {
    private int totalCount;
    private int propertyCount;
    private int healthCount;
    private int travelCount;
    private double averageSum;

    public InsurancesStatistics(int totalCount, int propertyCount, int healthCount, int travelCount, double averageSum) {
        this.totalCount = totalCount;
        this.propertyCount = propertyCount;
        this.healthCount = healthCount;
        this.travelCount = travelCount;
        this.averageSum = averageSum;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPropertyCount() {
        return propertyCount;
    }

    public void setPropertyCount(int propertyCount) {
        this.propertyCount = propertyCount;
    }

    public int getHealthCount() {
        return healthCount;
    }

    public void setHealthCount(int healthCount) {
        this.healthCount = healthCount;
    }

    public int getTravelCount() {
        return travelCount;
    }

    public void setTravelCount(int travelCount) {
        this.travelCount = travelCount;
    }

    public double getAverageSum() {
        return averageSum;
    }

    public void setAverageSum(double averageSum) {
        this.averageSum = averageSum;
    }
}
