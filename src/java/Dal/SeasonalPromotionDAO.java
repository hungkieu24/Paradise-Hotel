/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import DBcontext.DBContext;
import Model.SeasonalPromotion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thien
 */
public class SeasonalPromotionDAO extends DBContext {

    //author: THien
    // dem tat ca promotion theo brach id
    public int countPromotionByBranch(int branchId) {
        String sql = "select count(*) from SeasonalPromotion where is_deleted = 0 and branch_id = ?";
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

    //author: Thien
    // lay list tat ca promotion
    public List<SeasonalPromotion> getPromotionsByBranchId(int branchId, int page, int pageSize) {
        List<SeasonalPromotion> promotions = new ArrayList<>();
        String sql = "select * from SeasonalPromotion where branch_id = ? and is_deleted = 0"
                + "ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (page - 1) * pageSize;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SeasonalPromotion p = new SeasonalPromotion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("discount_amount"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getInt("branch_id"),
                        rs.getString("status"),
                        rs.getBoolean("is_deleted")
                );
                promotions.add(p);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    //author: Thien
    // dem tong so promotion theo search
    public int countSearchPromotion(int branchId, String searchQuery, String status, String startDate, String endDate) {
        String sql = "select count(*) from SeasonalPromotion where is_deleted = 0 and branch_id =?";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " and Lower(name) LIKE Lower(?)";
        }
        if (status != null && !status.isEmpty()) {
            sql += " and status = ?";
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql += " and start_date >= ?";

        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql += " and end_date <= ?";
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
            if (startDate != null && !startDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(startDate));
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(endDate));
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

    //author: Thien
    //lay list danh sach protion search
    public List<SeasonalPromotion> getSearchPromotionByBranchId(int branchId, String searchQuery, String status, String startDate, String endDate, int page, int pageSize) {
        List<SeasonalPromotion> promotions = new ArrayList<>();
        String sql = "select * from SeasonalPromotion where is_deleted = 0 and branch_id = ?";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " and Lower(name) LIKE Lower(?)";
        }
        if (status != null && !status.isEmpty()) {
            sql += " and status = ?";
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql += " and start_date >= ?";

        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql += " and end_date <= ?";
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
            if (startDate != null && !startDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(startDate));
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(endDate));
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SeasonalPromotion p = new SeasonalPromotion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("discount_amount"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getInt("branch_id"),
                        rs.getString("status"),
                        rs.getBoolean("is_deleted")
                );
                promotions.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    //author: Thien
    // add promotion
    public void addPromotion(SeasonalPromotion promotion) {
        String sql = "insert into SeasonalPromotion (name, description , discount_percent, discount_amount, start_date, end_date, branch_id, status, is_deleted)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, promotion.getName());
            ps.setString(2, promotion.getDescription());
            ps.setDouble(3, promotion.getDiscount_percent());
            ps.setDouble(4, promotion.getDiscount_amount());
            ps.setDate(5, (Date) promotion.getStartDate());
            ps.setDate(6, (Date) promotion.getEndDate());
            ps.setInt(7, promotion.getBranchId());
            ps.setString(8, promotion.getStatus());
            ps.setBoolean(9, promotion.isIs_deleted());
            int rows = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //author: Thien
    // upadate promotion
    public boolean updatePromotion(SeasonalPromotion promotion) {
        String sql = " update SeasonalPromotion set name = ?, description =?, discount_percent = ?, discount_amount = ?, start_date = ?, end_date = ?, branch_id = ?, status = ?, is_deleted =? where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, promotion.getName());
            ps.setString(2, promotion.getDescription());
            ps.setDouble(3, promotion.getDiscount_percent());
            ps.setDouble(4, promotion.getDiscount_amount());
            ps.setDate(5, (Date) promotion.getStartDate());
            ps.setDate(6, (Date) promotion.getEndDate());
            ps.setInt(7, promotion.getBranchId());
            ps.setString(8, promotion.getStatus());
            ps.setBoolean(9, promotion.isIs_deleted());
            ps.setInt(10, promotion.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //author: thien
    // soft delete promotion
    public boolean deletePromotion(int id) {
        String sql = "Update SeasonalPromotion set is_deleted = 1 where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //author thien
    public boolean isPromotionNameExist(String promotionName, int branchId) {
        String sql = "select count(*) from SeasonalPromotion where name = ? and branch_id = ? and is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, promotionName);
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

    //author: thien
    public void updateStatus(int id, String status) {
        String sql = "update SeasonalPromotion set status = ? when id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(id, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SeasonalPromotion getPromotionById(int id) {
        String sql = "Select * from SeasonalPromotion where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SeasonalPromotion promotion = new SeasonalPromotion();
                    promotion.setId(rs.getInt("id"));
                    promotion.setName(rs.getString("name"));
                    promotion.setDescription(rs.getString("description"));
                    promotion.setDiscount_percent(rs.getDouble("discount_percent"));
                    promotion.setDiscount_amount(rs.getDouble("discount_amount"));
                    promotion.setStartDate(rs.getDate("start_date"));
                    promotion.setEndDate(rs.getDate("end_date"));
                    promotion.setBranchId(rs.getInt("branch_id"));
                    promotion.setStatus(rs.getString("status"));
                    promotion.setIs_deleted(rs.getBoolean("is_deleted"));
                    return promotion;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
