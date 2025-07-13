/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import DBcontext.DBContext;
import java.sql.*;
import Model.LoyaltyPoint;

/**
 *
 * @author KTC
 */
public class LoyaltyPointDAO extends DBContext {

    //hoang
    public LoyaltyPoint getLoyaltyPointByUserId(String userId) {
        LoyaltyPoint loyaltyPoint = null;

        String sql = "SELECT lp.*, br.discount_percent "
                + "FROM LoyaltyPoint lp "
                + "LEFT JOIN BenefitRank br ON lp.level = br.level AND br.is_deleted = 0 "
                + "WHERE lp.user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Chỉ tạo đối tượng khi có dữ liệu
                    loyaltyPoint = new LoyaltyPoint();

                    loyaltyPoint.setUserId(rs.getString("user_id"));
                    loyaltyPoint.setPoints(rs.getInt("points"));
                    loyaltyPoint.setLevel(rs.getString("level"));

                    loyaltyPoint.setLastUpdated(rs.getTimestamp("last_updated"));
                    loyaltyPoint.setExpiredAt(rs.getTimestamp("expired_at"));

                    loyaltyPoint.setTotalSpending(rs.getBigDecimal("total_spending"));
                    loyaltyPoint.setLifetimePoints(rs.getInt("lifetime_points"));
                    loyaltyPoint.setPointsUsed(rs.getInt("points_used"));
                    loyaltyPoint.setLastTierCheck(rs.getTimestamp("last_tier_check"));
                    loyaltyPoint.setNextTierSpendingNeeded(rs.getBigDecimal("next_tier_spending_needed"));

                    loyaltyPoint.setDiscountPercent(rs.getInt("discount_percent"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loyaltyPoint;
    }

    //hoang
    public int getPointsByUser(String userId) {
        int points = 0;
        String sql = "SELECT points FROM LoyaltyPoint WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    points = rs.getInt("points");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }

    /**
     * Thêm điểm thưởng cho khách hàng
     */
    public boolean addPoints(String userId, int points, String reason) {
        try {
            // Update loyalty points
            String updateSql = "UPDATE LoyaltyPoint SET points = points + ?, last_updated = GETDATE() WHERE user_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, points);
                ps.setString(2, userId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    // Insert new record if user doesn't exist
                    String insertSql = "INSERT INTO LoyaltyPoint (user_id, points, level, last_updated) VALUES (?, ?, 'Member', GETDATE())";
                    try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                        insertPs.setString(1, userId);
                        insertPs.setInt(2, points);
                        insertPs.executeUpdate();
                    }
                }

                // Record transaction
                String transactionSql = "INSERT INTO PointTransaction (user_id, change_type, points_changed, reason, created_at) VALUES (?, 'Earn', ?, ?, GETDATE())";
                try (PreparedStatement transactionPs = connection.prepareStatement(transactionSql)) {
                    transactionPs.setString(1, userId);
                    transactionPs.setInt(2, points);
                    transactionPs.setString(3, reason);
                    transactionPs.executeUpdate();
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Lấy rank của user
     */
    public String getRankByUserId(String userId) {
        try {
            String sql = "SELECT level FROM LoyaltyPoint WHERE user_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("level");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Member";
    }
    //hoang
    public boolean subtractPoints(String userId, int amount) {
        String sql = "UPDATE LoyaltyPoint SET points = points - ?, last_updated = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND points >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, userId);
            ps.setInt(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
