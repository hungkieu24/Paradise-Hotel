/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import DBcontext.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author KTC
 */
public class PointRedeemVoucherDAO extends DBContext {

    public boolean redeemVoucher(String userId, int voucherId, int pointsUsed) {
        String sql = "INSERT INTO PointRedeemVoucher (user_id, voucher_id, points_used, redeemed_at, expired_at) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, DATEADD(YEAR, 1, CURRENT_TIMESTAMP))";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, voucherId);
            ps.setInt(3, pointsUsed);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasAlreadyRedeemed(String userId, int voucherId) {
        String sql = "SELECT 1 FROM PointRedeemVoucher WHERE user_id = ? AND voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getRedeemedVoucherIdsByUser(String userId) {
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT voucher_id FROM PointRedeemVoucher WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("voucher_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get available vouchers that user owns (can be used multiple times)
     */
    public List<Integer> getAvailableVoucherIdsByUser(String userId) {
        List<Integer> result = new ArrayList<>();

        // Simply get all vouchers that user has redeemed
        String sql = "SELECT prv.voucher_id FROM PointRedeemVoucher prv WHERE prv.user_id = ?";



        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int voucherId = rs.getInt("voucher_id");
                result.add(voucherId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Use voucher - remove it from PointRedeemVoucher and record usage in BookingVoucher
     */
    public boolean useVoucher(String userId, int voucherId, int bookingId) {
        try {
            // Start transaction
            connection.setAutoCommit(false);

            // 1. Insert into BookingVoucher to record usage
            String insertSQL = "INSERT INTO BookingVoucher (booking_id, voucher_id, used_at) VALUES (?, ?, GETDATE())";
            try (PreparedStatement ps1 = connection.prepareStatement(insertSQL)) {
                ps1.setInt(1, bookingId);
                ps1.setInt(2, voucherId);
                int insertResult = ps1.executeUpdate();

                if (insertResult > 0) {
                    // 2. Remove voucher from PointRedeemVoucher (consume the voucher)
                    String deleteSQL = "DELETE FROM PointRedeemVoucher WHERE user_id = ? AND voucher_id = ?";
                    try (PreparedStatement ps2 = connection.prepareStatement(deleteSQL)) {
                        ps2.setString(1, userId);
                        ps2.setInt(2, voucherId);
                        int deleteResult = ps2.executeUpdate();

                        if (deleteResult > 0) {
                            // Both operations successful
                            connection.commit();
                            return true;
                        } else {
                            // Failed to delete from PointRedeemVoucher
                            connection.rollback();
                            return false;
                        }
                    }
                } else {
                    // Failed to insert into BookingVoucher
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
