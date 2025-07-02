/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author KTC
 */
public class VoucherRedemptionRule {

    private int id;
    private int voucherId;
    private int requiredPoints;
    private String requiredTier; // Giá trị: null, "Member", "Silver", "Gold", "VIP"
    private boolean isActive;

    // Constructors
    public VoucherRedemptionRule() {
    }

    public VoucherRedemptionRule(int id, int voucherId, int requiredPoints, String requiredTier, boolean isActive) {
        this.id = id;
        this.voucherId = voucherId;
        this.requiredPoints = requiredPoints;
        this.requiredTier = requiredTier;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public String getRequiredTier() {
        return requiredTier;
    }

    public void setRequiredTier(String requiredTier) {
        this.requiredTier = requiredTier;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
