/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Timestamp;
import java.sql.Date;

/**
 *
 * @author hungk
 */
public class BranchMonthlyReport {
    private int Id;
    private int BranchId;
    private Date ReportMonth;
    private double Revenue;
    private double Expenses;
    private double Profit;
    private double ProfitRate;
    
    private HotelBranch hotelBranch;

    public BranchMonthlyReport() {
    }

    public BranchMonthlyReport(int Id, int BranchId, Date ReportMonth, double Revenue, double Expenses, double Profit, double ProfitRate) {
        this.Id = Id;
        this.BranchId = BranchId;
        this.ReportMonth = ReportMonth;
        this.Revenue = Revenue;
        this.Expenses = Expenses;
        this.Profit = Profit;
        this.ProfitRate = ProfitRate;
    }

    public BranchMonthlyReport(int Id, Date ReportMonth, double Revenue, double Expenses, double Profit, double ProfitRate, HotelBranch hotelBranch) {
        this.Id = Id;
        this.ReportMonth = ReportMonth;
        this.Revenue = Revenue;
        this.Expenses = Expenses;
        this.Profit = Profit;
        this.ProfitRate = ProfitRate;
        this.hotelBranch = hotelBranch;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getBranchId() {
        return BranchId;
    }

    public void setBranchId(int BranchId) {
        this.BranchId = BranchId;
    }

    public Date getReportMonth() {
        return ReportMonth;
    }

    public void setReportMonth(Date ReportMonth) {
        this.ReportMonth = ReportMonth;
    }

    public double getRevenue() {
        return Revenue;
    }

    public void setRevenue(double Revenue) {
        this.Revenue = Revenue;
    }

    public double getExpenses() {
        return Expenses;
    }

    public void setExpenses(double Expenses) {
        this.Expenses = Expenses;
    }

    public double getProfit() {
        return Profit;
    }

    public void setProfit(double Profit) {
        this.Profit = Profit;
    }

    public double getProfitRate() {
        return ProfitRate;
    }

    public void setProfitRate(double ProfitRate) {
        this.ProfitRate = ProfitRate;
    }

    public HotelBranch getHotelBranch() {
        return hotelBranch;
    }

    public void setHotelBranch(HotelBranch hotelBranch) {
        this.hotelBranch = hotelBranch;
    }

    @Override
    public String toString() {
        return "BranchMonthlyReport{" + "Id=" + Id + ", BranchId=" + BranchId + ", ReportMonth=" + ReportMonth + ", Revenue=" + Revenue + ", Expenses=" + Expenses + ", Profit=" + Profit + ", ProfitRate=" + ProfitRate + '}';
    }
}
