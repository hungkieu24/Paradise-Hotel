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
public class Wallet {
    private int WalletID;
    private String UserID;
    private double Balance;
    private Timestamp UpdatedAt;

    public Wallet() {
    }

    public Wallet(int WalletID, String UserID, double Balance, Timestamp UpdatedAt) {
        this.WalletID = WalletID;
        this.UserID = UserID;
        this.Balance = Balance;
        this.UpdatedAt = UpdatedAt;
    }

    public int getWalletID() {
        return WalletID;
    }

    public void setWalletID(int WalletID) {
        this.WalletID = WalletID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public double getBalance() {
        return Balance;
    }

    public void setBalance(double Balance) {
        this.Balance = Balance;
    }

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp UpdatedAt) {
        this.UpdatedAt = UpdatedAt;
    }

    @Override
    public String toString() {
        return "Wallet{" + "WalletID=" + WalletID + ", UserID=" + UserID + ", Balance=" + Balance + ", UpdatedAt=" + UpdatedAt + '}';
    }
}
