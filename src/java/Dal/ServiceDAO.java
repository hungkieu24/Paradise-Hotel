package Dal;

import DBcontext.DBContext;
import Model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO extends DBContext {

    /**
     * Tính tổng tiền dịch vụ theo bookingId
     */
    public double getTotalServicePriceByBookingId(int bookingId) {
        try {
            String sql = "SELECT SUM(s.price * bs.quantity) FROM BookingService bs "
                    + "JOIN Service s ON bs.service_id = s.id "
                    + "WHERE bs.booking_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy danh sách services của một booking cụ thể
     */
    public List<Service> getServicesByBookingId(int bookingId) {
        List<Service> services = new ArrayList<>();

        try {
            String sql = "SELECT s.*, bs.quantity as booking_quantity, bs.paid_status "
                    + "FROM BookingService bs "
                    + "JOIN Service s ON bs.service_id = s.id "
                    + "WHERE bs.booking_id = ? AND s.is_deleted = 0";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Service service = mapResultSetToService(rs);
                        service.setQuantity(rs.getInt("booking_quantity")); // Quantity from booking
                        service.setBookingServiceStatus(rs.getString("paid_status"));
                        services.add(service);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }

    /**
     * Lấy danh sách tất cả các dịch vụ hoạt động
     */
    public List<Service> getAllActiveServices() {
        List<Service> services = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Service WHERE status = 'Active' AND is_deleted = 0";

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Service service = mapResultSetToService(rs);
                    services.add(service);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }

    /**
     * Lấy danh sách các dịch vụ hoạt động theo chi nhánh
     */
    public List<Service> getActiveServicesByBranch(int branchId) {
        List<Service> services = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Service "
                    + "WHERE branch_id = ? AND status = 'Active' AND is_deleted = 0";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, branchId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Service service = mapResultSetToService(rs);
                        services.add(service);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }

    /**
     * Lấy thông tin dịch vụ theo ID
     */
    public Service getServiceById(int serviceId) {
        try {
            String sql = "SELECT * FROM Service WHERE id = ? AND is_deleted = 0";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, serviceId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToService(rs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Thêm mới một dịch vụ
     */
    public boolean addService(Service service) {
        try {
            String sql = "INSERT INTO Service (name, description, price, branch_id, status, image_url) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, service.getName());
                ps.setString(2, service.getDescription());
                ps.setDouble(3, service.getPrice());
                ps.setInt(4, service.getBranchId());
                ps.setString(5, service.getStatus());
                ps.setString(6, service.getImageUrl());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            service.setId(generatedKeys.getInt(1));
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật thông tin dịch vụ
     */
    public boolean updateService(Service service) {
        try {
            String sql = "UPDATE Service SET name = ?, description = ?, price = ?, "
                    + "branch_id = ?, status = ?, image_url = ? WHERE id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, service.getName());
                ps.setString(2, service.getDescription());
                ps.setDouble(3, service.getPrice());
                ps.setInt(4, service.getBranchId());
                ps.setString(5, service.getStatus());
                ps.setString(6, service.getImageUrl());
                ps.setInt(7, service.getId());

                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa mềm dịch vụ
     */
    public boolean softDeleteService(int serviceId) {
        try {
            String sql = "UPDATE Service SET is_deleted = 1 WHERE id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, serviceId);

                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Tìm kiếm dịch vụ theo tên hoặc mô tả
     */
    public List<Service> searchServices(String keyword) {
        List<Service> services = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Service WHERE (name LIKE ? OR description LIKE ?) "
                    + "AND is_deleted = 0 ORDER BY name";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                String searchParam = "%" + keyword + "%";
                ps.setString(1, searchParam);
                ps.setString(2, searchParam);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Service service = mapResultSetToService(rs);
                        services.add(service);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }

    /**
     * Map từ ResultSet sang đối tượng Service
     */
    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getDouble("price"));
        service.setBranchId(rs.getInt("branch_id"));
        service.setStatus(rs.getString("status"));
        service.setImageUrl(rs.getString("image_url"));

        // Các trường bổ sung nếu có trong ResultSet
        try {
            service.setDeleted(rs.getBoolean("is_deleted"));
        } catch (SQLException e) {
            // Trường hợp không có cột is_deleted trong kết quả
            service.setDeleted(false);
        }

        return service;
    }
    /**
 * Lấy danh sách services chưa thanh toán của booking
 */
public List<Service> getUnpaidServicesByBookingId(int bookingId) {
    List<Service> services = new ArrayList<>();
    
    try {
        String sql = "SELECT s.id, s.name, s.description, s.price, s.image_url, " +
                     "bs.quantity, bs.paid_status " +
                     "FROM BookingService bs " +
                     "JOIN Service s ON bs.service_id = s.id " +
                     "WHERE bs.booking_id = ? AND bs.paid_status = 'Unpaid' " +
                     "AND s.is_deleted = 0 " +
                     "ORDER BY s.name";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                    service.setImageUrl(rs.getString("image_url"));
                    service.setQuantity(rs.getInt("quantity"));
                    service.setBookingServiceStatus(rs.getString("paid_status"));
                    services.add(service);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return services;
}

/**
 * Lấy tổng tiền services chưa thanh toán
 */
public double getUnpaidServiceTotalByBookingId(int bookingId) {
    double total = 0;
    
    try {
        String sql = "SELECT SUM(s.price * bs.quantity) as total " +
                     "FROM BookingService bs " +
                     "JOIN Service s ON bs.service_id = s.id " +
                     "WHERE bs.booking_id = ? AND bs.paid_status = 'Unpaid' " +
                     "AND s.is_deleted = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return total;
}

/**
 * Lấy tổng tiền services đã thanh toán
 */
public double getPaidServiceTotalByBookingId(int bookingId) {
    double total = 0;
    
    try {
        String sql = "SELECT SUM(s.price * bs.quantity) as total " +
                     "FROM BookingService bs " +
                     "JOIN Service s ON bs.service_id = s.id " +
                     "WHERE bs.booking_id = ? AND bs.paid_status = 'Paid' " +
                     "AND s.is_deleted = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return total;
}

/**
 * Đánh dấu tất cả services của booking là đã thanh toán
 */
public boolean markAllServicesAsPaid(int bookingId) {
    try {
        String sql = "UPDATE BookingService SET paid_status = 'Paid' WHERE booking_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}
