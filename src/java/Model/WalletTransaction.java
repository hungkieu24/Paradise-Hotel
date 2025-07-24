/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Timestamp;

/**
 *
 * @author hungk
 */
public class WalletTransaction {
    private int TransactionID;
    private int WalletID;
    private double Amount;
    private String TransactionType;
    private String Description;
    private String BankAccountNumber;
    private int BookingID;
    private int BranchID;
    private String CreatedBy;
    private String Status;
    private Timestamp CreatedAt;

    public WalletTransaction() {
    }

    public WalletTransaction(int TransactionID, int WalletID, double Amount, String TransactionType, String Description, String BankAccountNumber, int BookingID, int BranchID, String CreatedBy, String Status, Timestamp CreatedAt) {
        this.TransactionID = TransactionID;
        this.WalletID = WalletID;
        this.Amount = Amount;
        this.TransactionType = TransactionType;
        this.Description = Description;
        this.BankAccountNumber = BankAccountNumber;
        this.BookingID = BookingID;
        this.BranchID = BranchID;
        this.CreatedBy = CreatedBy;
        this.Status = Status;
        this.CreatedAt = CreatedAt;
    }

    public int getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(int TransactionID) {
        this.TransactionID = TransactionID;
    }

    public int getWalletID() {
        return WalletID;
    }

    public void setWalletID(int WalletID) {
        this.WalletID = WalletID;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double Amount) {
        this.Amount = Amount;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String TransactionType) {
        this.TransactionType = TransactionType;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public int getBookingID() {
        return BookingID;
    }

    public void setBookingID(int BookingID) {
        this.BookingID = BookingID;
    }

    public int getBranchID() {
        return BranchID;
    }

    public void setBranchID(int BranchID) {
        this.BranchID = BranchID;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public String getBankAccountNumber() {
        return BankAccountNumber;
    }

    public void setBankAccountNumber(String BankAccountNumber) {
        this.BankAccountNumber = BankAccountNumber;
    }

    @Override
    public String toString() {
        return "WalletTransaction{" + "TransactionID=" + TransactionID + ", WalletID=" + WalletID + ", Amount=" + Amount + ", TransactionType=" + TransactionType + ", Description=" + Description + ", BankAccountNumber=" + BankAccountNumber + ", BookingID=" + BookingID + ", BranchID=" + BranchID + ", CreatedBy=" + CreatedBy + ", Status=" + Status + ", CreatedAt=" + CreatedAt + '}';
    }
    
    
}
