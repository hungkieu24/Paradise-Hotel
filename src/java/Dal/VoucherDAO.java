/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DBcontext.DBContext;
import Model.Voucher;
import Model.VoucherRedemptionRule;

/**
 *
 * @author KTC
 */
public class VoucherDAO extends DBContext {

    public List<Voucher> getAllVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT v.*, r.id AS rule_id, r.required_points, r.required_tier, r.is_active "
                + "FROM Voucher v LEFT JOIN VoucherRedemptionRule r ON v.id = r.voucher_id";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setDescription(rs.getString("description"));
                Object discountPercentObj = rs.getObject("discount_percent");
                voucher.setDiscountPercent(discountPercentObj != null ? rs.getInt("discount_percent") : null);
                Object discountAmountObj = rs.getObject("discount_amount");
                voucher.setDiscountAmount(discountAmountObj != null ? rs.getBigDecimal("discount_amount") : null);
                voucher.setMinPrice(rs.getBigDecimal("min_price"));
                voucher.setTotalQuantity(rs.getInt("total_quantity"));
                voucher.setUsedQuantity(rs.getInt("used_quantity"));
                voucher.setBranchId(rs.getInt("branch_id"));
                Timestamp validFrom = rs.getTimestamp("valid_from");
                Timestamp validTo = rs.getTimestamp("valid_to");
                voucher.setValidFrom(validFrom != null ? validFrom.toLocalDateTime() : null);
                voucher.setValidTo(validTo != null ? validTo.toLocalDateTime() : null);
                voucher.setStatus(rs.getString("status"));
                voucher.setDeleted(rs.getBoolean("is_deleted"));

                // Rule
                VoucherRedemptionRule rule = new VoucherRedemptionRule();
                rule.setId(rs.getInt("rule_id"));
                rule.setVoucherId(voucher.getId());
                rule.setRequiredPoints(rs.getInt("required_points"));
                rule.setRequiredTier(rs.getString("required_tier"));
                rule.setActive(rs.getBoolean("is_active"));

                // Giả sử Voucher có field: private VoucherRedemptionRule redemptionRule;
                voucher.setRedemptionRule(rule);

                vouchers.add(voucher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vouchers;
    }

}
