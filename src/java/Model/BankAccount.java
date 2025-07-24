/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author hungk
 */
public class BankAccount {
    private int BankAccountID;
    private String UserID;
    private String BankName;
    private String AccountNumber;
    private String AccountHolder;
    private boolean IsDefault;

    public BankAccount() {
    }

    public BankAccount(int BankAccountID, String UserID, String BankName, String AccountNumber, String AccountHolder, boolean IsDefault) {
        this.BankAccountID = BankAccountID;
        this.UserID = UserID;
        this.BankName = BankName;
        this.AccountNumber = AccountNumber;
        this.AccountHolder = AccountHolder;
        this.IsDefault = IsDefault;
    }

    public int getBankAccountID() {
        return BankAccountID;
    }

    public void setBankAccountID(int BankAccountID) {
        this.BankAccountID = BankAccountID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String BankName) {
        this.BankName = BankName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public String getAccountHolder() {
        return AccountHolder;
    }

    public void setAccountHolder(String AccountHolder) {
        this.AccountHolder = AccountHolder;
    }

    public boolean isIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(boolean IsDefault) {
        this.IsDefault = IsDefault;
    }

    @Override
    public String toString() {
        return "BankAccount{" + "BankAccountID=" + BankAccountID + ", UserID=" + UserID + ", BankName=" + BankName + ", AccountNumber=" + AccountNumber + ", AccountHolder=" + AccountHolder + ", IsDefault=" + IsDefault + '}';
    }
}
