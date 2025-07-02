/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

/**
 *
 * @author thien
 */
import DBcontext.DBContext;
import Model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO extends DBContext {

    //author: HieuTT
    // Tính tổng tiền dịch vụ theo bookingId
    public double getTotalServicePriceByBookingId(int bookingId) {
        String sql = "SELECT SUM(price) FROM BookingService WHERE booking_id = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //author: ThienTC
    //Lấy list service cho manager để hiển thị 
    public List<Service> getServicesByBranchId(int branchId, int page, int pageSize) {
        List<Service> services = new ArrayList<>();
        String sql = "select * from Service where branch_id =? and is_deleted = 0"
                + "ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (page - 1) * pageSize;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Service s = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("branch_id"),
                        rs.getString("status"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_deleted")
                );
                services.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    //author: THien
    //lay total service by Branch ID
    public int getTotalServicesByBranchId(int branchId) {
        String sql = "SELECT COUNT(*) FROM Service WHERE branch_id = ? AND is_deleted = 0";
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

    // author : thien
    // lay total service theo search
    public int getTotalServicesBySearch(int branchId, String searchQuery, String status) {
        String sql = "SELECT COUNT(*) FROM Service WHERE branch_id = ? AND is_deleted = 0";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " AND name LIKE ?";
        }
        if (status != null && !status.isEmpty()) {
            sql += " AND status = ?";
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
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // author : thien
    //lay list service theo search
    public List<Service> searchServicesByBranchId(int branchId, String searchQuery, String status, int page, int pageSize) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE branch_id = ? AND is_deleted = 0";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " AND name LIKE ?";
        }
        if (status != null && !status.isEmpty()) {
            sql += " AND status = ?";
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
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Service s = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("branch_id"),
                        rs.getString("status"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_deleted")
                );
                services.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    //author: thien
    // add service
    public int addServiceAndGetId(Service service) {
        String sql = "INSERT INTO Service (name, description, price, branch_id, status, is_deleted) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, service.getName());
            ps.setString(2, service.getDescription());
            ps.setDouble(3, service.getPrice());
            ps.setInt(4, service.getBranchId());
            ps.setString(5, service.getStatus());
            ps.setBoolean(6, service.isDeleted());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated service ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //author : thien
    // update service image
    public boolean updateServiceImage(Service service) {
        String sql = "UPDATE Service SET image_url = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, service.getImageUrl());
            ps.setInt(2, service.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //author: thien
    // get service  by ID
    public Service getServiceById(int serviceId) {
        String sql = "SELECT * FROM Service WHERE id = ? AND is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("branch_id"),
                        rs.getString("status"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_deleted")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //author: thien
    //get service by ID
    public boolean updateService(Service service) {
        String sql = "UPDATE Service SET name = ?, description = ?, price = ?, status = ?, image_url = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, service.getName());
            ps.setString(2, service.getDescription());
            ps.setDouble(3, service.getPrice());
            ps.setString(4, service.getStatus());
            ps.setString(5, service.getImageUrl());
            ps.setInt(6, service.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //author: thien
    // delete Service
    public boolean deleteService(int serviceId) {
        String sql = "UPDATE Service SET is_deleted = 1 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //author: thien
    //
    public boolean isServiceInUse(int serviceId) {
        String sql = "select count(*) from BookingService where service_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //author thien
    public boolean isServiceNameExists(String name, int branchId) {
        String sql = "SELECT COUNT(*) FROM Service WHERE name = ? AND branch_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
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
}
