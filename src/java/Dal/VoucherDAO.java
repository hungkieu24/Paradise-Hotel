/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import DBcontext.DBContext;
import Model.Voucher;
import Model.VoucherRedemptionRule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thien
 */
public class VoucherDAO extends DBContext {

    //atuthor: thien
    // lay total coucher
    public int getTotalVoucherByBranchId(int branchId) {
        String sql = "select count(*) from Voucher where is_deleted = 0 and branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //author: thien
    // list voucher
    public List<Voucher> getVoucherByBranchId(int branchId, int page, int pageSize) {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "select * from Voucher where branch_id = ? and is_deleted = 0"
                + " ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (page - 1) * pageSize;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher v = new Voucher(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("min_price"),
                        rs.getInt("total_quantity"),
                        rs.getInt("used_quantity"),
                        rs.getInt("branch_id"),
                        rs.getDate("valid_from"),
                        rs.getDate("valid_to"),
                        rs.getString("status"),
                        rs.getBoolean("is_deleted")
                );
                vouchers.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vouchers;
    }

    //author: Thien
    // dem tong so voucher khi search
    public int countSearchVoucher(int branchId, String searchQuery, String status, String fromDate, String toDate) {
        String sql = "select count(*) from Voucher where is_deleted = 0 and branch_id = ?";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " and Lower(code) like Lower(?)";
        }
        if (status != null && !status.isEmpty()) {
            sql += " and status = ?";
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql += " and valid_from >= ?";
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql += " and valid_to <= ?";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, branchId);
            if (searchQuery != null && !searchQuery.isEmpty()) {
                ps.setString(paramIndex++, "%" + searchQuery + "%");
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(fromDate));
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(toDate));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //author : Thien
    // list after search voucher
    public List<Voucher> getSearchVoucherList(int branchId, String searchQuery, String status, String fromDate, String toDate, int page, int pageSize) {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "select * from Voucher where is_deleted = 0 and branch_id = ?";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " and Lower(code) like Lower(?)";
        }
        if (status != null && !status.isEmpty()) {
            sql += " and status = ?";
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql += " and valid_from >= ?";
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql += " and valid_to <= ?";
        }
        sql += " ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (page - 1) * pageSize;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, branchId);
            if (searchQuery != null && !searchQuery.isEmpty()) {
                ps.setString(paramIndex++, "%" + searchQuery + "%");
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(fromDate));
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(toDate));
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher v = new Voucher(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("min_price"),
                        rs.getInt("total_quantity"),
                        rs.getInt("used_quantity"),
                        rs.getInt("branch_id"),
                        rs.getDate("valid_from"),
                        rs.getDate("valid_to"),
                        rs.getString("status"),
                        rs.getBoolean("is_deleted")
                );
                vouchers.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vouchers;

    }

    //author: Thien
    //add voucher
    public boolean addVoucher(Voucher v) {
        String sql = "INSERT INTO Voucher (code, description, discount_percent, discount_amount, min_price, "
                + "total_quantity, used_quantity, branch_id, valid_from, valid_to, status, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDescription());
            ps.setDouble(3, v.getDiscount_percent());
            ps.setDouble(4, v.getDiscount_amount());
            ps.setDouble(5, v.getMin_price());
            ps.setInt(6, v.getTotal_quantity());
            ps.setInt(7, v.getUsed_quantity());
            ps.setInt(8, v.getBranchId());
            ps.setDate(9, (Date) v.getValid_from());
            ps.setDate(10, (Date) v.getValid_to());
            ps.setString(11, v.getStatus());
            ps.setBoolean(12, v.isIs_deleted());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //author: Thien
    // edit voucher
    public boolean updateVoucher(Voucher voucher) {
        String sql = "UPDATE Voucher SET code = ?, description = ?, discount_percent = ?, "
                + "discount_amount = ?, min_price = ?, total_quantity = ?, valid_from = ?, valid_to = ? "
                + "WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getDescription());
            ps.setDouble(3, voucher.getDiscount_percent());
            ps.setDouble(4, voucher.getDiscount_amount());
            ps.setDouble(5, voucher.getMin_price());
            ps.setInt(6, voucher.getTotal_quantity());
            ps.setDate(7, voucher.getValid_from());
            ps.setDate(8, voucher.getValid_to());
            ps.setInt(9, voucher.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //author: thien
    //delete voucher
    public boolean softDeleteVoucher(int id) {
        String sql = "UPDATE Voucher SET is_deleted = 1 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //author: thien
    public boolean isVoucherCodeExist(String code, int branchId) {
        String sql = "select count(*) from Voucher where code = ? and branch_id = ? and is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

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

                // Lấy trực tiếp kiểu double, nếu null trong DB thì giá trị trả về là 0.0
                voucher.setDiscount_percent(rs.getDouble("discount_percent"));
                voucher.setDiscount_amount(rs.getDouble("discount_amount"));

                voucher.setMin_price(rs.getDouble("min_price"));
                voucher.setTotal_quantity(rs.getInt("total_quantity"));
                voucher.setUsed_quantity(rs.getInt("used_quantity"));
                voucher.setBranchId(rs.getInt("branch_id"));

                Timestamp validFrom = rs.getTimestamp("valid_from");
                Timestamp validTo = rs.getTimestamp("valid_to");
                voucher.setValid_from(validFrom != null ? new Date(validFrom.getTime()) : null);
                voucher.setValid_to(validTo != null ? new Date(validTo.getTime()) : null);

                voucher.setStatus(rs.getString("status"));
                voucher.setIs_deleted(rs.getBoolean("is_deleted"));

                // Nếu có rule thì set, còn không thì bỏ qua
                int ruleId = rs.getInt("rule_id");
                if (!rs.wasNull()) {
                    VoucherRedemptionRule rule = new VoucherRedemptionRule();
                    rule.setId(ruleId);
                    rule.setVoucherId(voucher.getId());
                    rule.setRequiredPoints(rs.getInt("required_points"));
                    rule.setRequiredTier(rs.getString("required_tier"));
                    rule.setActive(rs.getBoolean("is_active"));

                    voucher.setRedemptionRule(rule);
                }

                vouchers.add(voucher);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vouchers;
    }

    /**
     * Get voucher by ID
     */
    public Voucher getVoucherById(int voucherId) {
        String sql = "SELECT * FROM Voucher WHERE id = ? AND is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setDescription(rs.getString("description"));
                voucher.setDiscount_percent(rs.getDouble("discount_percent"));
                voucher.setDiscount_amount(rs.getDouble("discount_amount"));
                voucher.setMin_price(rs.getDouble("min_price"));
                voucher.setTotal_quantity(rs.getInt("total_quantity"));
                voucher.setUsed_quantity(rs.getInt("used_quantity"));
                voucher.setBranchId(rs.getInt("branch_id"));

                Timestamp validFrom = rs.getTimestamp("valid_from");
                Timestamp validTo = rs.getTimestamp("valid_to");
                voucher.setValid_from(validFrom != null ? new Date(validFrom.getTime()) : null);
                voucher.setValid_to(validTo != null ? new Date(validTo.getTime()) : null);

                voucher.setStatus(rs.getString("status"));
                voucher.setIs_deleted(rs.getBoolean("is_deleted"));

                return voucher;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get vouchers by IDs
     */
    public List<Voucher> getVouchersByIds(List<Integer> voucherIds) {
        List<Voucher> vouchers = new ArrayList<>();


        if (voucherIds == null || voucherIds.isEmpty()) {
            return vouchers;
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM Voucher WHERE id IN (");
        for (int i = 0; i < voucherIds.size(); i++) {
            sql.append("?");
            if (i < voucherIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(") AND is_deleted = 0 AND status = 'Active'");


        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < voucherIds.size(); i++) {
                ps.setInt(i + 1, voucherIds.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setDescription(rs.getString("description"));
                voucher.setDiscount_percent(rs.getDouble("discount_percent"));
                voucher.setDiscount_amount(rs.getDouble("discount_amount"));
                voucher.setMin_price(rs.getDouble("min_price"));
                voucher.setTotal_quantity(rs.getInt("total_quantity"));
                voucher.setUsed_quantity(rs.getInt("used_quantity"));
                voucher.setBranchId(rs.getInt("branch_id"));

                Timestamp validFrom = rs.getTimestamp("valid_from");
                Timestamp validTo = rs.getTimestamp("valid_to");
                voucher.setValid_from(validFrom != null ? new Date(validFrom.getTime()) : null);
                voucher.setValid_to(validTo != null ? new Date(validTo.getTime()) : null);

                voucher.setStatus(rs.getString("status"));
                voucher.setIs_deleted(rs.getBoolean("is_deleted"));


                vouchers.add(voucher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vouchers;
    }
    public void updateStatus(int id, String status){
        String sql="update Voucher set status = ? where id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        
    }

}
