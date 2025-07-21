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

    //hoang
    /**
     * Thêm điểm thưởng cho khách hàng và cập nhật total_spending
     */
    public boolean addPointsWithSpending(String userId, int points, String reason, double spendingAmount) {
        try {
            connection.setAutoCommit(false);

            // Update loyalty points and total_spending
            String updateSql = "UPDATE LoyaltyPoint SET points = points + ?, total_spending = total_spending + ?, "
                    + "lifetime_points = lifetime_points + ?, last_updated = GETDATE() WHERE user_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, points);
                ps.setDouble(2, spendingAmount);
                ps.setInt(3, points);
                ps.setString(4, userId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    // Insert new record if user doesn't exist
                    String insertSql = "INSERT INTO LoyaltyPoint (user_id, points, level, total_spending, lifetime_points, last_updated) "
                            + "VALUES (?, ?, 'Member', ?, ?, GETDATE())";
                    try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                        insertPs.setString(1, userId);
                        insertPs.setInt(2, points);
                        insertPs.setDouble(3, spendingAmount);
                        insertPs.setInt(4, points);
                        insertPs.executeUpdate();
                    }
                }

                // Record transaction
                String transactionSql = "INSERT INTO PointTransaction (user_id, change_type, points_changed, reason, created_at) "
                        + "VALUES (?, 'Earn', ?, ?, GETDATE())";
                try (PreparedStatement transactionPs = connection.prepareStatement(transactionSql)) {
                    transactionPs.setString(1, userId);
                    transactionPs.setInt(2, points);
                    transactionPs.setString(3, reason);
                    transactionPs.executeUpdate();
                }

                connection.commit();
                checkAndUpdateTier(userId);
                return true;
            }
        } catch (Exception e) {
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Trừ điểm khi hoàn tiền và cập nhật total_spending
     */
    /**
     * Trừ điểm khi hoàn tiền và cập nhật total_spending
     */
    /**
     * Trừ điểm khi hoàn tiền - sửa change_type
     */
    public boolean subtractPointsForRefund(String userId, double refundAmount, String reason) {

        try {
            connection.setAutoCommit(false);

            // Kiểm tra user có tồn tại trong LoyaltyPoint không
            if (!userExistsInLoyaltyPoint(userId)) {
                String insertSql = "INSERT INTO LoyaltyPoint (user_id, points, level, total_spending, lifetime_points, last_updated) "
                        + "VALUES (?, 0, 'Member', 0, 0, GETDATE())";
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    ps.setString(1, userId);
                    ps.executeUpdate();
                }
            }

            int pointsToSubtract = (int) (refundAmount / 100000); // 100,000 VND = 1 point

            if (pointsToSubtract <= 0) {
                connection.commit();
                return true;
            }

            // Lấy thông tin hiện tại
            String selectSql = "SELECT points, total_spending FROM LoyaltyPoint WHERE user_id = ?";
            int currentPoints = 0;
            double currentSpending = 0;

            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setString(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    currentPoints = rs.getInt("points");
                    currentSpending = rs.getDouble("total_spending");
                }
            }

            // Tính toán điểm và spending mới
            int finalPointsToSubtract = Math.min(pointsToSubtract, currentPoints);
            double newSpending = Math.max(0, currentSpending - refundAmount);

            // Cập nhật
            String updateSql = "UPDATE LoyaltyPoint SET points = points - ?, total_spending = ?, last_updated = GETDATE() WHERE user_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, finalPointsToSubtract);
                ps.setDouble(2, newSpending);
                ps.setString(3, userId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0 && finalPointsToSubtract > 0) {
                    // Ghi transaction với change_type = 'Adjustment' và points_changed âm
                    String transactionSql = "INSERT INTO PointTransaction (user_id, change_type, points_changed, reason, created_at) "
                            + "VALUES (?, 'Adjustment', ?, ?, GETDATE())";
                    try (PreparedStatement transactionPs = connection.prepareStatement(transactionSql)) {
                        transactionPs.setString(1, userId);
                        transactionPs.setInt(2, -finalPointsToSubtract); // Số âm để biểu thị trừ điểm
                        transactionPs.setString(3, reason);
                        int transactionRows = transactionPs.executeUpdate();
                    }
                }

                connection.commit();
                return true;
            }

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.err.println("❌ Error in subtractPointsForRefund: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Kiểm tra và cập nhật tier dựa trên total_spending (có thể tăng hoặc giảm
     * tier)
     */
    public void checkAndUpdateTier(String userId) {
        try {
            String sql = "SELECT total_spending, level FROM LoyaltyPoint WHERE user_id = ?";
            double totalSpending = 0;
            String currentTier = "Member";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalSpending = rs.getDouble("total_spending");
                    currentTier = rs.getString("level");
                }
            }

            // Xác định tier mới dựa trên total_spending
            String newTier = "Member";
            if (totalSpending >= 20000001) { // 20 triệu
                newTier = "VIP";
            } else if (totalSpending >= 10000001) { // 10 triệu
                newTier = "Gold";
            } else if (totalSpending >= 5000001) { // 5 triệu
                newTier = "Silver";
            }

            // Cập nhật tier nếu có thay đổi
            if (!newTier.equals(currentTier)) {
                String updateSql = "UPDATE LoyaltyPoint SET level = ?, last_tier_check = GETDATE() WHERE user_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                    ps.setString(1, newTier);
                    ps.setString(2, userId);
                    ps.executeUpdate();
                }

                // Ghi log thay đổi tier
                String tierChangeReason = newTier.compareTo(currentTier) > 0
                        ? "Tier upgraded from " + currentTier + " to " + newTier
                        : "Tier downgraded from " + currentTier + " to " + newTier;

                String transactionSql = "INSERT INTO PointTransaction (user_id, change_type, points_changed, reason, created_at) "
                        + "VALUES (?, 'TierChange', 0, ?, GETDATE())";
                try (PreparedStatement ps = connection.prepareStatement(transactionSql)) {
                    ps.setString(1, userId);
                    ps.setString(2, tierChangeReason);
                    ps.executeUpdate();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra user có tồn tại trong LoyaltyPoint table không
     */
    public boolean userExistsInLoyaltyPoint(String userId) {
        String sql = "SELECT COUNT(*) FROM LoyaltyPoint WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //hieu 
    public boolean updateTotalSpending(String userId, double additionalSpending) {
        // Get current data first
        String selectSql = "SELECT total_spending, level FROM LoyaltyPoint WHERE user_id = ?";

        // Update query with tier logic
        String updateSql = "UPDATE LoyaltyPoint SET "
                + "total_spending = total_spending + ?, "
                + "level = ?, "
                + "next_tier_spending_needed = ?, "
                + "last_updated = GETDATE(), "
                + "last_tier_check = GETDATE() "
                + "WHERE user_id = ?";

        try (PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
            selectPs.setString(1, userId);

            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    double currentSpending = rs.getDouble("total_spending");
                    String currentLevel = rs.getString("level");

                    // Calculate new total spending
                    double newTotalSpending = currentSpending + additionalSpending;

                    // TIER LOGIC for hieu1235 at 2025-07-15 17:34:19 UTC
                    String newLevel;
                    double nextTierNeeded;

                    if (newTotalSpending >= 20000001.00) {
                        // VIP tier (max tier)
                        newLevel = "VIP";
                        nextTierNeeded = 0.0;
                    } else if (newTotalSpending >= 10000001.00) {
                        // Gold tier
                        newLevel = "Gold";
                        nextTierNeeded = 20000001.00 - newTotalSpending; // Need for VIP
                    } else if (newTotalSpending >= 5000001.00) {
                        // Silver tier
                        newLevel = "Silver";
                        nextTierNeeded = 10000001.00 - newTotalSpending; // Need for Gold
                    } else {
                        // Member tier
                        newLevel = "Member";
                        nextTierNeeded = 5000001.00 - newTotalSpending; // Need for Silver
                    }

                    // Ensure non-negative value
                    nextTierNeeded = Math.max(0.0, nextTierNeeded);

                    // Execute update
                    try (PreparedStatement updatePs = connection.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, additionalSpending);
                        updatePs.setString(2, newLevel);
                        updatePs.setDouble(3, nextTierNeeded);
                        updatePs.setString(4, userId);

                        int updated = updatePs.executeUpdate();
                        return updated > 0;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }
}
