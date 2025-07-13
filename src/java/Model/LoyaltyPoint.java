package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class LoyaltyPoint {

    private String userId;
    private int points;
    private String level;
    private Timestamp lastUpdated;
    private Timestamp expiredAt;
    private BigDecimal totalSpending;
    private int lifetimePoints;
    private int pointsUsed;
    private Timestamp lastTierCheck;
    private BigDecimal nextTierSpendingNeeded;
    private int discountPercent;
    
    public LoyaltyPoint() {
    }
    
    public int getDiscountPercent() { return discountPercent; }

    // Getters and Setters
    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) { this.userId = userId; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    public Timestamp getExpiredAt() { return expiredAt; }
    public void setExpiredAt(Timestamp expiredAt) { this.expiredAt = expiredAt; }

    public BigDecimal getTotalSpending() { return totalSpending; }
    public void setTotalSpending(BigDecimal totalSpending) { this.totalSpending = totalSpending; }

    public int getLifetimePoints() { return lifetimePoints; }
    public void setLifetimePoints(int lifetimePoints) { this.lifetimePoints = lifetimePoints; }

    public int getPointsUsed() { return pointsUsed; }
    public void setPointsUsed(int pointsUsed) { this.pointsUsed = pointsUsed; }

    public Timestamp getLastTierCheck() { return lastTierCheck; }
    public void setLastTierCheck(Timestamp lastTierCheck) { this.lastTierCheck = lastTierCheck; }

    public BigDecimal getNextTierSpendingNeeded() { return nextTierSpendingNeeded; }
    public void setNextTierSpendingNeeded(BigDecimal nextTierSpendingNeeded) { this.nextTierSpendingNeeded = nextTierSpendingNeeded; }
}