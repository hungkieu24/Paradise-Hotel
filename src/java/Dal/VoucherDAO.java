package Dal;

import DBcontext.DBContext;
import Model.Voucher;
import java.sql.*;

public class VoucherDAO extends DBContext {

    // Tìm voucher theo id
    public Voucher findVoucherById(int voucherId) {
        String sql = "SELECT * FROM Voucher WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getInt("id"));
                    v.setCode(rs.getString("code"));
                    v.setDescription(rs.getString("description"));
                    v.setDiscountPercent(rs.getInt("discount_percent"));
                    v.setDiscountAmount(rs.getDouble("discount_amount"));
                    v.setMinPrice(rs.getDouble("min_price"));
                    v.setTotalQuantity(rs.getInt("total_quantity"));
                    v.setUsedQuantity(rs.getInt("used_quantity"));
                    v.setBranchId(rs.getInt("branch_id"));
                    v.setValidFrom(rs.getTimestamp("valid_from"));
                    v.setValidTo(rs.getTimestamp("valid_to"));
                    v.setStatus(rs.getString("status"));
                    v.setDeleted(rs.getBoolean("is_deleted"));
                    return v;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm voucher theo code
    public Voucher findVoucherByCode(String code) {
        String sql = "SELECT * FROM Voucher WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getInt("id"));
                    v.setCode(rs.getString("code"));
                    v.setDescription(rs.getString("description"));
                    v.setDiscountPercent(rs.getInt("discount_percent"));
                    v.setDiscountAmount(rs.getDouble("discount_amount"));
                    v.setMinPrice(rs.getDouble("min_price"));
                    v.setTotalQuantity(rs.getInt("total_quantity"));
                    v.setUsedQuantity(rs.getInt("used_quantity"));
                    v.setBranchId(rs.getInt("branch_id"));
                    v.setValidFrom(rs.getTimestamp("valid_from"));
                    v.setValidTo(rs.getTimestamp("valid_to"));
                    v.setStatus(rs.getString("status"));
                    v.setDeleted(rs.getBoolean("is_deleted"));
                    return v;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật voucher đã sử dụng (tăng used_quantity)
    public boolean setVoucherUsed(int voucherId) {
        String sql = "UPDATE Voucher SET used_quantity = used_quantity + 1 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}