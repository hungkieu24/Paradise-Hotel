/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.WalletTransaction;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungk
 */
public class WalletTransactionDAO extends DBcontext.DBContext {

    public List<WalletTransaction> getTransactionsByUserId(int WalletID) {
        List<WalletTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM WalletTransaction WHERE WalletID = ? ORDER BY TransactionID DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, WalletID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                WalletTransaction wt = new WalletTransaction();
                wt.setTransactionID(rs.getInt("TransactionID"));
                wt.setWalletID(rs.getInt("WalletID"));
                wt.setAmount(rs.getDouble("Amount"));
                wt.setTransactionType(rs.getString("TransactionType"));
                wt.setDescription(rs.getString("Description"));
                wt.setBookingID(rs.getInt("BookingID"));
                wt.setBranchID(rs.getInt("BranchID"));
                wt.setCreatedBy(rs.getString("CreatedBy"));
                wt.setStatus(rs.getString("Status"));
                wt.setCreatedAt(rs.getTimestamp("CreatedAt"));
                wt.setBankAccountNumber(rs.getString("BankAccountNumber"));
                transactions.add(wt);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // hoặc log lỗi ra log file
        }

        return transactions;
    }

    public boolean addWalletTransaction(WalletTransaction wt) {
        String sql = "INSERT INTO WalletTransaction "
                + "(WalletID, Amount, TransactionType, Description, BookingID, BranchID, CreatedBy, Status, CreatedAt, BankAccountNumber) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, wt.getWalletID());
            ps.setDouble(2, wt.getAmount());
            ps.setString(3, wt.getTransactionType());
            ps.setString(4, wt.getDescription());

            // Nếu BookingID hoặc BranchID là null, dùng setNull
            if (wt.getBookingID() != 0) {
                ps.setInt(5, wt.getBookingID());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            if (wt.getBranchID() != 0) {
                ps.setInt(6, wt.getBranchID());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setString(7, wt.getCreatedBy());
            ps.setString(8, wt.getStatus());
            if (wt.getBankAccountNumber() != null) {
                ps.setString(9, wt.getBankAccountNumber());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
