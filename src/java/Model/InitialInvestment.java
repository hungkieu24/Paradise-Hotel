/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Date;

/**
 *
 * @author hungk
 */
public class InitialInvestment {
    private int id;
    private int BranchId;
    private double Capital;
    private Date InvestedDate;
    
    private HotelBranch branch;

    public InitialInvestment() {
    }

    public InitialInvestment(int BranchId, double Capital, Date InvestedDate) {
        this.BranchId = BranchId;
        this.Capital = Capital;
        this.InvestedDate = InvestedDate;
    }

    public InitialInvestment(double Capital, Date InvestedDate, HotelBranch branch) {
        this.Capital = Capital;
        this.InvestedDate = InvestedDate;
        this.branch = branch;
    }

    public InitialInvestment(int id, double Capital, Date InvestedDate, HotelBranch branch) {
        this.id = id;
        this.Capital = Capital;
        this.InvestedDate = InvestedDate;
        this.branch = branch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HotelBranch getBranch() {
        return branch;
    }

    public void setBranch(HotelBranch branch) {
        this.branch = branch;
    }
    
    public int getBranchId() {
        return BranchId;
    }

    public void setBranchId(int BranchId) {
        this.BranchId = BranchId;
    }

    public double getCapital() {
        return Capital;
    }

    public void setCapital(double Capital) {
        this.Capital = Capital;
    }

    public Date getInvestedDate() {
        return InvestedDate;
    }

    public void setInvestedDate(Date InvestedDate) {
        this.InvestedDate = InvestedDate;
    }

    @Override
    public String toString() {
        return "InitialInvestment{" + "BranchId=" + BranchId + ", Capital=" + Capital + ", InvestedDate=" + InvestedDate + '}';
    }
}
