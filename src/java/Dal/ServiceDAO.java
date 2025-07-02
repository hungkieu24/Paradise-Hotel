
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
import Model.Branch;

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

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE status = 'Active' AND is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getDouble("price"));
                service.setBranchId(rs.getInt("branch_id"));
                service.setStatus(rs.getString("status"));
                service.setImageUrl(rs.getString("image_url"));
                service.setDeleted(rs.getBoolean("is_deleted"));

                services.add(service);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return services;
    }

    // hoang: lay ca where unactive
    public List<Service> getAllServices1() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getDouble("price"));
                service.setBranchId(rs.getInt("branch_id"));
                service.setStatus(rs.getString("status"));
                service.setImageUrl(rs.getString("image_url"));
                service.setDeleted(rs.getBoolean("is_deleted"));

                services.add(service);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return services;
    }

    public Service getServiceById(int id) {
        String sql = "SELECT * FROM Service WHERE id = ? AND status = 'Active' AND is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                    service.setBranchId(rs.getInt("branch_id"));
                    service.setStatus(rs.getString("status"));
                    service.setImageUrl(rs.getString("image_url"));
                    service.setDeleted(rs.getBoolean("is_deleted"));
                    return service;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    public Service getServiceById1(int id) {
        String sql = "SELECT * FROM Service WHERE id = ? AND is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                    service.setBranchId(rs.getInt("branch_id"));
                    service.setStatus(rs.getString("status"));
                    service.setImageUrl(rs.getString("image_url"));
                    service.setDeleted(rs.getBoolean("is_deleted"));
                    return service;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    public List<Service> getServicesByBranchId(int branchId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE branch_id = ? AND status = 'Active' AND is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                    service.setBranchId(rs.getInt("branch_id"));
                    service.setStatus(rs.getString("status"));
                    service.setImageUrl(rs.getString("image_url"));
                    service.setDeleted(rs.getBoolean("is_deleted"));

                    services.add(service);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return services;
    }

    //hoang
    // Lấy danh sách chi nhánh
    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT id, name FROM HotelBranch WHERE is_deleted = 0";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Branch branch = new Branch();
                branch.setId(rs.getInt("id"));
                branch.setName(rs.getString("name"));
                branches.add(branch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return branches;
    }

    //hoang
    public List<Service> getServicesByPriceRange(double minPrice, double maxPrice) {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE is_deleted = 0 AND price BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
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
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //hoang
    // Trong ServiceDAO hoặc Servlet, bạn có thể thêm đoạn sau nếu cần
    public List<Service> getServicesByBranchIdExcept(int branchId, int excludeServiceId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE branch_id = ? AND id <> ? AND is_deleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setInt(2, excludeServiceId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setDescription(rs.getString("description"));
                    s.setPrice(rs.getDouble("price"));
                    s.setBranchId(rs.getInt("branch_id"));
                    s.setStatus(rs.getString("status"));
                    s.setImageUrl(rs.getString("image_url"));
                    s.setDeleted(rs.getBoolean("is_deleted"));
                    services.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
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
