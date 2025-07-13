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

}
