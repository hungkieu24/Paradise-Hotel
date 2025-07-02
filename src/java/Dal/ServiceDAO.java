package Dal;

import DBcontext.DBContext;
import Model.Branch;
import Model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO extends DBContext {

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

}
