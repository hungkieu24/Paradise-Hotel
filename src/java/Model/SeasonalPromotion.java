/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author thien
 */
public class SeasonalPromotion {
    private int id;
    private String name;
    private String description;
    private double discount_percent;
    private double discount_amount;
    private Date startDate;
    private Date endDate;
    private int branchId;
    private String status;
    private boolean is_deleted;

    public SeasonalPromotion() {
    }

    public SeasonalPromotion(int id, String name, String description, double discount_percent, double discount_amount, Date startDate, Date endDate, int branchId, String status, boolean is_deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discount_percent = discount_percent;
        this.discount_amount = discount_amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.branchId = branchId;
        this.status = status;
        this.is_deleted = is_deleted;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(double discount_percent) {
        this.discount_percent = discount_percent;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
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

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
    
}
