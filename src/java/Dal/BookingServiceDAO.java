package Dal;

import DBcontext.DBContext;
import Model.BookingService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceDAO extends DBContext {
    
    /**
     * Lấy danh sách dịch vụ đã đặt theo booking ID
     */
    public List<BookingService> getBookingServicesByBookingId(int bookingId) {
        List<BookingService> bookingServices = new ArrayList<>();
        
        try {
            String sql = "SELECT bs.booking_id, bs.service_id, bs.quantity, bs.paid_status, " +
                         "s.name AS service_name, s.price AS service_price " +
                         "FROM BookingService bs " +
                         "JOIN Service s ON bs.service_id = s.id " +
                         "WHERE bs.booking_id = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        BookingService bookingService = new BookingService();
                        bookingService.setBookingId(rs.getInt("booking_id"));
                        bookingService.setServiceId(rs.getInt("service_id"));
                        bookingService.setQuantity(rs.getInt("quantity"));
                        bookingService.setPaidStatus(rs.getString("paid_status"));
                        bookingService.setServiceName(rs.getString("service_name"));
                        bookingService.setServicePrice(rs.getBigDecimal("service_price"));
                        bookingServices.add(bookingService);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return bookingServices;
    }
    
    /**
     * Thêm hoặc cập nhật dịch vụ vào booking
     */
    public boolean addOrUpdateServiceToBooking(BookingService bookingService) {
        try {
            // Kiểm tra xem dịch vụ đã tồn tại trong booking chưa
            if (checkServiceExists(bookingService.getBookingId(), bookingService.getServiceId())) {
                return updateExistingService(bookingService);
            } else {
                return insertNewService(bookingService);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra xem dịch vụ đã tồn tại trong booking chưa
     */
    public boolean checkServiceExists(int bookingId, int serviceId) {
        try {
            String checkSql = "SELECT COUNT(*) as count FROM BookingService WHERE booking_id = ? AND service_id = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, serviceId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count") > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật dịch vụ đã tồn tại
     */
    private boolean updateExistingService(BookingService bookingService) {
        try {
            String updateSql = "UPDATE BookingService SET quantity = ?, paid_status = ? " +
                              "WHERE booking_id = ? AND service_id = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, bookingService.getQuantity());
                ps.setString(2, bookingService.getPaidStatus());
                ps.setInt(3, bookingService.getBookingId());
                ps.setInt(4, bookingService.getServiceId());
                
                int rowsAffected = ps.executeUpdate();
                
                // Cập nhật tổng giá booking
                if (rowsAffected > 0) {
                    updateBookingTotalPrice(bookingService.getBookingId());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Thêm dịch vụ mới vào booking
     */
    private boolean insertNewService(BookingService bookingService) {
        try {
            String insertSql = "INSERT INTO BookingService (booking_id, service_id, quantity, paid_status) " +
                              "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setInt(1, bookingService.getBookingId());
                ps.setInt(2, bookingService.getServiceId());
                ps.setInt(3, bookingService.getQuantity());
                ps.setString(4, bookingService.getPaidStatus());
                
                int rowsAffected = ps.executeUpdate();
                
                // Cập nhật tổng giá booking
                if (rowsAffected > 0) {
                    updateBookingTotalPrice(bookingService.getBookingId());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
      /**
     * Xóa dịch vụ khỏi booking
     */
    public boolean removeServiceFromBooking(int bookingId, int serviceId) {
        try {
            String deleteSql = "DELETE FROM BookingService WHERE booking_id = ? AND service_id = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, serviceId);
                
                int rowsAffected = ps.executeUpdate();
                
                // Cập nhật tổng giá booking
                if (rowsAffected > 0) {
                    updateBookingTotalPrice(bookingId);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật tổng giá booking sau khi thêm/sửa/xóa dịch vụ
     */
    private void updateBookingTotalPrice(int bookingId) {
        try {
            String updateSql = 
                "UPDATE Booking SET total_price = (" +
                "  SELECT ISNULL(SUM(brt.price_per_room * brt.quantity), 0) + " +
                "         ISNULL(SUM(s.price * bs.quantity), 0) " +
                "  FROM Booking b " +
                "  LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id " +
                "  LEFT JOIN BookingService bs ON b.id = bs.booking_id " +
                "  LEFT JOIN Service s ON bs.service_id = s.id " +
                "  WHERE b.id = ?" +
                ") WHERE id = ?";
                
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, bookingId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Tính tổng tiền dịch vụ theo bookingId
     */
    public double getTotalServicePriceByBookingId(int bookingId) {
        try {
            String sql = "SELECT SUM(s.price * bs.quantity) FROM BookingService bs " +
                         "JOIN Service s ON bs.service_id = s.id " +
                         "WHERE bs.booking_id = ?";
                         
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
     * Cập nhật trạng thái thanh toán của tất cả dịch vụ trong booking
     */
    public boolean updateAllBookingServiceStatus(int bookingId, String paidStatus) {
        try {
            String updateSql = "UPDATE BookingService SET paid_status = ? WHERE booking_id = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, paidStatus);
                ps.setInt(2, bookingId);
                
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return false;
    }

}