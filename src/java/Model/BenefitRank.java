/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author KTC
 */
public class BenefitRank {
    private int id;
    private String level;
    private double pointRate;
    private double discountPercent;
    private String benefit;
    private boolean isDeleted;

    public BenefitRank() {
    }

    public BenefitRank(int id, String level, double pointRate, double discountPercent, String benefit, boolean isDeleted) {
        this.id = id;
        this.level = level;
        this.pointRate = pointRate;
        this.discountPercent = discountPercent;
        this.benefit = benefit;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getPointRate() {
        return pointRate;
    }

    public void setPointRate(double pointRate) {
        this.pointRate = pointRate;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "BenefitRank{" +
                "id=" + id +
                ", level='" + level + '\'' +
                ", pointRate=" + pointRate +
                ", discountPercent=" + discountPercent +
                ", benefit='" + benefit + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
