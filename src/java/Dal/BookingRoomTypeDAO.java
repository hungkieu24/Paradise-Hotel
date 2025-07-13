package Dal;

import DBcontext.DBContext;
import Model.BookingRoomType;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRoomTypeDAO extends DBContext {
    
    /**
     * Get all BookingRoomType records for a specific booking
     * @param bookingId The booking ID
     * @return List of BookingRoomType objects
     */
    public List<BookingRoomType> getBookingRoomTypesByBookingId(int bookingId) {
        List<BookingRoomType> bookingRoomTypes = new ArrayList<>();
        String sql = """
            SELECT brt.booking_id, brt.room_type_id, brt.quantity, brt.price_per_room,
                   rt.name as room_type_name, rt.description as room_type_description, 
                   rt.image_url as room_type_image_url, hb.name as branch_name, rt.base_price as base_price
            FROM BookingRoomType brt
            INNER JOIN RoomType rt ON brt.room_type_id = rt.id
            INNER JOIN Booking b ON brt.booking_id = b.id
            INNER JOIN HotelBranch hb ON b.branch_id = hb.id
            WHERE brt.booking_id = ?
            ORDER BY brt.room_type_id
        """;
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                BookingRoomType brt = new BookingRoomType();
                brt.setBookingId(rs.getInt("booking_id"));
                brt.setRoomTypeId(rs.getInt("room_type_id"));
                brt.setQuantity(rs.getInt("quantity"));
                brt.setPricePerRoom(rs.getBigDecimal("price_per_room"));
                brt.setRoomTypeName(rs.getString("room_type_name"));
                brt.setRoomTypeDescription(rs.getString("room_type_description"));
                brt.setRoomTypeImageUrl(rs.getString("room_type_image_url"));
                brt.setBranchName(rs.getString("branch_name"));
                brt.setBase_price(rs.getDouble("base_price"));
                bookingRoomTypes.add(brt);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting BookingRoomTypes by booking ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bookingRoomTypes;
    }
    
    /**
     * Get a specific BookingRoomType record
     * @param bookingId The booking ID
     * @param roomTypeId The room type ID
     * @return BookingRoomType object or null if not found
     */
    public BookingRoomType getBookingRoomType(int bookingId, int roomTypeId) {
        String sql = """
            SELECT brt.booking_id, brt.room_type_id, brt.quantity, brt.price_per_room,
                   rt.name as room_type_name, rt.description as room_type_description, 
                   rt.image_url as room_type_image_url
            FROM BookingRoomType brt
            INNER JOIN RoomType rt ON brt.room_type_id = rt.id
            WHERE brt.booking_id = ? AND brt.room_type_id = ?
        """;
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ps.setInt(2, roomTypeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BookingRoomType brt = new BookingRoomType();
                brt.setBookingId(rs.getInt("booking_id"));
                brt.setRoomTypeId(rs.getInt("room_type_id"));
                brt.setQuantity(rs.getInt("quantity"));
                brt.setPricePerRoom(rs.getBigDecimal("price_per_room"));
                brt.setRoomTypeName(rs.getString("room_type_name"));
                brt.setRoomTypeDescription(rs.getString("room_type_description"));
                brt.setRoomTypeImageUrl(rs.getString("room_type_image_url"));
                
                return brt;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting BookingRoomType: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Insert a new BookingRoomType record
     * @param bookingRoomType The BookingRoomType object to insert
     * @return true if successful, false otherwise
     */
    public boolean insertBookingRoomType(BookingRoomType bookingRoomType) {
        String sql = """
            INSERT INTO BookingRoomType (booking_id, room_type_id, quantity, price_per_room)
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingRoomType.getBookingId());
            ps.setInt(2, bookingRoomType.getRoomTypeId());
            ps.setInt(3, bookingRoomType.getQuantity());
            ps.setBigDecimal(4, bookingRoomType.getPricePerRoom());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting BookingRoomType: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing BookingRoomType record
     * @param bookingRoomType The BookingRoomType object with updated values
     * @return true if successful, false otherwise
     */
    public boolean updateBookingRoomType(BookingRoomType bookingRoomType) {
        String sql = """
            UPDATE BookingRoomType 
            SET quantity = ?, price_per_room = ?
            WHERE booking_id = ? AND room_type_id = ?
        """;
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingRoomType.getQuantity());
            ps.setBigDecimal(2, bookingRoomType.getPricePerRoom());
            ps.setInt(3, bookingRoomType.getBookingId());
            ps.setInt(4, bookingRoomType.getRoomTypeId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating BookingRoomType: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a BookingRoomType record
     * @param bookingId The booking ID
     * @param roomTypeId The room type ID
     * @return true if successful, false otherwise
     */
    public boolean deleteBookingRoomType(int bookingId, int roomTypeId) {
        String sql = "DELETE FROM BookingRoomType WHERE booking_id = ? AND room_type_id = ?";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ps.setInt(2, roomTypeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting BookingRoomType: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get total quantity of rooms needed for a booking
     * @param bookingId The booking ID
     * @return Total quantity of rooms
     */
    public int getTotalRoomQuantityByBookingId(int bookingId) {
        String sql = "SELECT SUM(quantity) as total_quantity FROM BookingRoomType WHERE booking_id = ?";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_quantity");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total room quantity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get total price for all room types in a booking
     * @param bookingId The booking ID
     * @return Total price as BigDecimal
     */
    public BigDecimal getTotalPriceByBookingId(int bookingId) {
        String sql = "SELECT SUM(quantity * price_per_room) as total_price FROM BookingRoomType WHERE booking_id = ?";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal totalPrice = rs.getBigDecimal("total_price");
                return totalPrice != null ? totalPrice : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total price: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Check if a booking has room types assigned
     * @param bookingId The booking ID
     * @return true if booking has room types, false otherwise
     */
    public boolean hasRoomTypes(int bookingId) {
        String sql = "SELECT COUNT(*) as count FROM BookingRoomType WHERE booking_id = ?";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking if booking has room types: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get quantity for a specific room type in a booking
     * @param bookingId The booking ID
     * @param roomTypeId The room type ID
     * @return Quantity of rooms needed
     */
    public int getQuantityByBookingAndRoomType(int bookingId, int roomTypeId) {
        String sql = "SELECT quantity FROM BookingRoomType WHERE booking_id = ? AND room_type_id = ?";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            ps.setInt(2, roomTypeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("quantity");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting quantity by booking and room type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Batch insert multiple BookingRoomType records
     * @param bookingRoomTypes List of BookingRoomType objects to insert
     * @return true if all inserts successful, false otherwise
     */
    public boolean insertBatchBookingRoomTypes(List<BookingRoomType> bookingRoomTypes) {
        if (bookingRoomTypes == null || bookingRoomTypes.isEmpty()) {
            return true;
        }
        
        String sql = """
            INSERT INTO BookingRoomType (booking_id, room_type_id, quantity, price_per_room)
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (BookingRoomType brt : bookingRoomTypes) {
                ps.setInt(1, brt.getBookingId());
                ps.setInt(2, brt.getRoomTypeId());
                ps.setInt(3, brt.getQuantity());
                ps.setBigDecimal(4, brt.getPricePerRoom());
                ps.addBatch();
            }
            
            int[] results = ps.executeBatch();
            conn.commit();
            
            // Check if all inserts were successful
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error batch inserting BookingRoomTypes: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public Map<Integer, Integer> getRemainingRoomQuantities(int bookingId) {
    Map<Integer, Integer> remainingMap = new HashMap<>();
    // Câu lệnh SQL đã được điều chỉnh để khớp với schema của bạn
    String sql = "SELECT " +
                 "    brt.room_type_id, " +
                 "    brt.quantity - COALESCE(ra_counts.assigned_count, 0) AS remaining_quantity " +
                 "FROM " +
                 "    BookingRoomType brt " + // Tên bảng đúng
                 "LEFT JOIN " +
                 "    (SELECT " +
                 "         r.room_type_id, " +
                 "         COUNT(ra.room_id) AS assigned_count " + // Đếm trên cột room_id
                 "     FROM " +
                 "         RoomAssignment ra " + // Tên bảng đúng
                 "     JOIN " +
                 "         Room r ON ra.room_id = r.id " + // Tên bảng đúng
                 "     WHERE " +
                 "         ra.booking_id = ? " +
                 "     GROUP BY " +
                 "         r.room_type_id) AS ra_counts " +
                 "ON brt.room_type_id = ra_counts.room_type_id " +
                 "WHERE " +
                 "    brt.booking_id = ?;";

    try (PreparedStatement st = connection.prepareStatement(sql)) {
        st.setInt(1, bookingId);
        st.setInt(2, bookingId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            remainingMap.put(rs.getInt("room_type_id"), rs.getInt("remaining_quantity"));
        }
    } catch (SQLException e) {
        System.out.println("Error in getRemainingRoomQuantities: " + e.getMessage());
        e.printStackTrace();
    }
    return remainingMap;
}
    public Integer getRoomTypeIdByBookingId(int bookingId) {
        Integer roomTypeId = null;
        String sql = "SELECT room_type_Id FROM BookingRoomType WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                roomTypeId = rs.getInt("roomTypeId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomTypeId;
    }
}