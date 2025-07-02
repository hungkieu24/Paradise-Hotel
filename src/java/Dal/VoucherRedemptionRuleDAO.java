/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import DBcontext.DBContext;
import Model.VoucherRedemptionRule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author KTC
 */
public class VoucherRedemptionRuleDAO extends DBContext {

    public VoucherRedemptionRule getRuleByVoucherId(int voucherId) {
        String sql = "SELECT * FROM VoucherRedemptionRule WHERE voucher_id = ? AND is_active = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                VoucherRedemptionRule rule = new VoucherRedemptionRule();
                rule.setId(rs.getInt("id"));
                rule.setVoucherId(rs.getInt("voucher_id"));
                rule.setRequiredPoints(rs.getInt("required_points"));
                rule.setRequiredTier(rs.getString("required_tier"));
                rule.setActive(rs.getBoolean("is_active"));
                return rule;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
