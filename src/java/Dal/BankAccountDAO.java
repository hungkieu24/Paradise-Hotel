/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.BankAccount;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungk
 */
public class BankAccountDAO extends DBcontext.DBContext {

    public List<BankAccount> getBankAccountsByUserId(String userId) {
        List<BankAccount> bankAccounts = new ArrayList<>();
        String sql = "SELECT * FROM BankAccount WHERE UserID = ?";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, userId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                BankAccount account = new BankAccount(
                        rs.getInt("BankAccountID"),
                        rs.getString("UserID"),
                        rs.getString("BankName"),
                        rs.getString("AccountNumber"),
                        rs.getString("AccountHolder"),
                        rs.getBoolean("IsDefault")
                );
                bankAccounts.add(account);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bankAccounts;
    }

    public BankAccount getDefaultBankAccount(String userId) {
        String sql = "SELECT * FROM BankAccount WHERE UserID = ? AND IsDefault = 1";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return new BankAccount(
                        rs.getInt("BankAccountID"),
                        rs.getString("UserID"),
                        rs.getString("BankName"),
                        rs.getString("AccountNumber"),
                        rs.getString("AccountHolder"),
                        rs.getBoolean("IsDefault")
                );
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean setDefaultBankAccount(int bankAccountId, String userId) {
        String resetSql = "UPDATE BankAccount SET IsDefault = 0 WHERE UserID = ?";
        String setSql = "UPDATE BankAccount SET IsDefault = 1 WHERE BankAccountID = ? AND UserID = ?";

        try {
            connection.setAutoCommit(false); // Bắt đầu transaction

            // Bước 1: reset tất cả IsDefault = 0 cho user đó
            try (PreparedStatement resetStmt = connection.prepareStatement(resetSql)) {
                resetStmt.setString(1, userId);
                resetStmt.executeUpdate();
            }

            // Bước 2: đặt tài khoản được chọn thành IsDefault = 1
            try (PreparedStatement setStmt = connection.prepareStatement(setSql)) {
                setStmt.setInt(1, bankAccountId);
                setStmt.setString(2, userId);
                int rowsAffected = setStmt.executeUpdate();

                connection.commit(); // Commit cả 2 bước nếu không lỗi
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback nếu lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Trả lại chế độ mặc định
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean addBankAccount(BankAccount bankAccount) {
        String sql = "INSERT INTO BankAccount (UserID, BankName, AccountNumber, AccountHolder, IsDefault) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, bankAccount.getUserID());
            st.setString(2, bankAccount.getBankName());
            st.setString(3, bankAccount.getAccountNumber());
            st.setString(4, bankAccount.getAccountHolder());
            st.setBoolean(5, bankAccount.isIsDefault());

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteBankAccount(int bankAccountId, String userId) {
        String sql = "DELETE FROM BankAccount WHERE BankAccountID = ? AND UserID = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, bankAccountId);
            st.setString(2, userId);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
