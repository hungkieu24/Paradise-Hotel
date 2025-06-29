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
public class LoyaltyPointDAO extends DBContext{
    public LoyaltyPoint getLoyaltyPointByUserId(String userId) {
        String sql = "SELECT * FROM LoyaltyPoint WHERE user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new LoyaltyPoint(
                        rs.getString("user_id"),
                        rs.getInt("points"),
                        rs.getString("level"),
                        rs.getTimestamp("last_updated"), // Use getTimestamp for Date fields
                        rs.getTimestamp("expired_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
}
