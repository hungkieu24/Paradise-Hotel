package Dal;

import DBcontext.DBContext;
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
    /**
 * Lấy danh sách services của một booking cụ thể
 */
public List<Service> getServicesByBookingId(int bookingId) {
    List<Service> services = new ArrayList<>();
    
    try {
        String sql = "SELECT s.*, bs.quantity as booking_quantity, bs.paid_status " +
                     "FROM BookingService bs " +
                     "JOIN Service s ON bs.service_id = s.id " +
                     "WHERE bs.booking_id = ? AND s.is_deleted = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
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
}