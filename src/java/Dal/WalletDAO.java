/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Wallet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungk
 */
public class WalletDAO extends DBcontext.DBContext {

    public Wallet getWalletByUserId(String userId) {
        Wallet wallet = null;
        String sql = "SELECT * FROM Wallet WHERE UserID = ?";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                wallet = new Wallet(
                        rs.getInt("WalletID"),
                        rs.getString("UserID"),
                        rs.getDouble("Balance"),
                        rs.getTimestamp("UpdatedAt")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wallet;
    }

    public boolean updateWalletBalance(String userId, double amountToAdd) {
        String sql = "UPDATE Wallet SET Balance = Balance + ?, UpdatedAt = GETDATE() WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setDouble(1, amountToAdd);
            st.setString(2, userId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        WalletDAO walletDAO = new WalletDAO();
        Wallet wallet = walletDAO.getWalletByUserId("U001");
        System.out.println(wallet);
    }
}
