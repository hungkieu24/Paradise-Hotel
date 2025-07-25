/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.BankAccount;
import Model.WalletTransaction;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungk
 */
public class WalletTransactionDAO extends DBcontext.DBContext {

    public List<WalletTransaction> getTransactionsByUserId(int walletID) {
        List<WalletTransaction> transactions = new ArrayList<>();
        String sql = "SELECT wt.*, ba.BankAccountID, ba.UserID, ba.BankName, ba.AccountNumber, ba.AccountHolder, ba.IsDefault "
                + "FROM WalletTransaction wt "
                + "LEFT JOIN BankAccount ba ON wt.BankAccountID = ba.BankAccountID "
                + "WHERE wt.WalletID = ? "
                + "ORDER BY wt.TransactionID DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, walletID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                WalletTransaction wt = new WalletTransaction();
                wt.setTransactionID(rs.getInt("TransactionID"));
                wt.setWalletID(rs.getInt("WalletID"));
                wt.setAmount(rs.getDouble("Amount"));
                wt.setTransactionType(rs.getString("TransactionType"));
                wt.setDescription(rs.getString("Description"));
                wt.setBankAccountID(rs.getInt("BankAccountID")); // lưu ID
                wt.setBookingID(rs.getInt("BookingID"));
                wt.setBranchID(rs.getInt("BranchID"));
                wt.setCreatedBy(rs.getString("CreatedBy"));
                wt.setStatus(rs.getString("Status"));
                wt.setCreatedAt(rs.getTimestamp("CreatedAt"));

                // Gắn đối tượng BankAccount nếu có
                int bankAccountID = rs.getInt("BankAccountID");
                if (!rs.wasNull()) {
                    BankAccount ba = new BankAccount(
                            bankAccountID,
                            rs.getString("UserID"),
                            rs.getString("BankName"),
                            rs.getString("AccountNumber"),
                            rs.getString("AccountHolder"),
                            rs.getBoolean("IsDefault")
                    );
                    wt.setBankAccount(ba);
                }

                transactions.add(wt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public WalletTransaction getTransactionById(int transactionId) {
        String sql = "SELECT wt.*, ba.BankAccountID, ba.UserID, ba.BankName, ba.AccountNumber, ba.AccountHolder, ba.IsDefault "
                + "FROM WalletTransaction wt "
                + "LEFT JOIN BankAccount ba ON wt.BankAccountID = ba.BankAccountID "
                + "WHERE wt.TransactionID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                WalletTransaction wt = new WalletTransaction();
                wt.setTransactionID(rs.getInt("TransactionID"));
                wt.setWalletID(rs.getInt("WalletID"));
                wt.setAmount(rs.getDouble("Amount"));
                wt.setTransactionType(rs.getString("TransactionType"));
                wt.setDescription(rs.getString("Description"));
                wt.setBankAccountID(rs.getInt("BankAccountID"));
                wt.setBookingID(rs.getInt("BookingID"));
                wt.setBranchID(rs.getInt("BranchID"));
                wt.setCreatedBy(rs.getString("CreatedBy"));
                wt.setStatus(rs.getString("Status"));
                wt.setCreatedAt(rs.getTimestamp("CreatedAt"));

                int bankAccountID = rs.getInt("BankAccountID");
                if (!rs.wasNull()) {
                    BankAccount ba = new BankAccount(
                            bankAccountID,
                            rs.getString("UserID"),
                            rs.getString("BankName"),
                            rs.getString("AccountNumber"),
                            rs.getString("AccountHolder"),
                            rs.getBoolean("IsDefault")
                    );
                    wt.setBankAccount(ba);
                }

                return wt;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addWalletTransaction(WalletTransaction wt) {
        String sql = "INSERT INTO WalletTransaction "
                + "(WalletID, Amount, TransactionType, Description, BookingID, BranchID, CreatedBy, Status, CreatedAt, BankAccountID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, wt.getWalletID());
            ps.setDouble(2, wt.getAmount());
            ps.setString(3, wt.getTransactionType());
            ps.setString(4, wt.getDescription());

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

            // Sử dụng CreatedAt được set hoặc GETDATE() nếu null
            if (wt.getCreatedAt() != null) {
                ps.setTimestamp(9, wt.getCreatedAt());
            } else {
                ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            if (wt.getBankAccountID() != 0) {
                ps.setInt(10, wt.getBankAccountID());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<WalletTransaction> getWithdrawTransactionsByMonthRangeAndPage(int monthFrom, int yearFrom, int monthTo, int yearTo, int page, int pageSize) {

        List<WalletTransaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM WalletTransaction "
                + "WHERE TransactionType = 'Withdraw' "
                + "AND CreatedAt >= ? AND CreatedAt <= ? "
                + "ORDER BY TransactionID DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Tính ngày đầu tháng và cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));
            ps.setInt(3, (page - 1) * pageSize);
            ps.setInt(4, pageSize);

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
                wt.setBankAccountID(rs.getInt("BankAccountID"));
                transactions.add(wt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public int countWithdrawTransactionsByMonthRange(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        String sql = "SELECT COUNT(*) FROM WalletTransaction "
                + "WHERE TransactionType = 'Withdraw' "
                + "AND CreatedAt >= ? AND CreatedAt <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<WalletTransaction> getWithdrawTransactionsByStatusAndPage(String status, int monthFrom, int yearFrom, int monthTo, int yearTo, int page, int pageSize) {
        List<WalletTransaction> list = new ArrayList<>();

        String sql = "SELECT * FROM WalletTransaction "
                + "WHERE TransactionType = 'Withdraw' "
                + "AND Status = ? "
                + "AND CreatedAt >= ? "
                + "AND CreatedAt <= ? "
                + "ORDER BY CreatedAt DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            int offset = (page - 1) * pageSize;

            ps.setString(1, status);
            ps.setDate(2, java.sql.Date.valueOf(fromDate));
            ps.setDate(3, java.sql.Date.valueOf(toDate));
            ps.setInt(4, offset);
            ps.setInt(5, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WalletTransaction wt = new WalletTransaction(
                            rs.getInt("TransactionID"),
                            rs.getInt("WalletID"),
                            rs.getDouble("Amount"),
                            rs.getString("TransactionType"),
                            rs.getString("Description"),
                            rs.getInt("BankAccountID"),
                            rs.getInt("BookingID"),
                            rs.getInt("BranchID"),
                            rs.getString("CreatedBy"),
                            rs.getString("Status"),
                            rs.getTimestamp("CreatedAt")
                    );
                    list.add(wt);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countWithdrawTransactionsByStatus(String status, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        String sql = "SELECT COUNT(*) FROM WalletTransaction "
                + "WHERE TransactionType = 'Withdraw' "
                + "AND Status = ? "
                + "AND CreatedAt >= ? "
                + "AND CreatedAt <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setString(1, status);
            ps.setDate(2, java.sql.Date.valueOf(fromDate));
            ps.setDate(3, java.sql.Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean updateTransactionStatusAndDescription(int transactionId, String newStatus, String newDescription) {
        String sql = "UPDATE WalletTransaction SET Status = ?, Description = ? WHERE TransactionID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, newDescription);
            ps.setInt(3, transactionId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
