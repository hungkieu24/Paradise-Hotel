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
        LoyaltyPoint loyaltyPoint = new LoyaltyPoint();

        String sql = "SELECT lp.user_id, lp.points, lp.level, lp.last_updated, lp.expired_at, br.discount_percent "
                + "FROM LoyaltyPoint lp "
                + "JOIN BenefitRank br ON lp.level = br.level "
                + "WHERE lp.user_id = ? AND br.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loyaltyPoint.setUser_id(rs.getString("user_id"));
                    loyaltyPoint.setPoints(rs.getInt("points"));
                    loyaltyPoint.setLevel(rs.getString("level"));
                    loyaltyPoint.setLast_updated(rs.getDate("last_updated"));
                    loyaltyPoint.setExpired_at(rs.getDate("expired_at"));
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
