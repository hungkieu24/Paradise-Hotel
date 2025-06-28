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
    
   public String getRankByUserId(String userId) {
        String sql = "SELECT level FROM LoyaltyPoint WHERE user_id = ?";
        try (
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("level");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
