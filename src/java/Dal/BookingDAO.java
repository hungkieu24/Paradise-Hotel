package Dal;

import java.sql.Date;
import Model.Booking;
import Model.BookingRoomType;
import Model.Room;
import Model.Service;
import Model.UserAccount;
import com.sun.jdi.connect.spi.Connection;
import java.math.BigDecimal;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO extends DBcontext.DBContext {

    // Lấy booking hôm nay theo chi nhánh (kèm full name & rank)
    public List<Booking> getBookingsTodayByBranch(int branchId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, u.fullname, rt.name AS roomTypeName, lp.level AS rank "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN RoomType rt ON b.room_type_id = rt.id "
                + "LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id "
                + "WHERE b.branch_id = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "  OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs);
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lay booking theo userID
    public List<Booking> getBookingByUserId(String userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getString("user_id"));
                booking.setBookingTime(rs.getTimestamp("booking_time"));
                booking.setCheckIn(rs.getTimestamp("check_in"));
                booking.setCheckOut(rs.getTimestamp("check_out"));
                booking.setStatus(rs.getString("status"));
                booking.setTotalPrice(rs.getDouble("total_price"));
                booking.setPaymentStatus(rs.getString("payment_status"));
                booking.setCancelReason(rs.getString("cancel_reason"));
                booking.setCancelTime(rs.getTimestamp("cancel_time"));
                booking.setPromotionId(rs.getInt("promotion_id"));
                // Nếu Booking có các field bổ sung như roomTypes, userName... bạn có thể map thêm nếu cần (hoặc bỏ qua)
                bookings.add(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Lấy danh sách booking theo userId và branch (nếu cần)
    public List<Booking> getBookingsByUserIdAndBranch(String userId, int branchId) throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, "
                + "STRING_AGG(rt.name, ', ') AS roomTypes "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id "
                + "LEFT JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE b.user_id = ? AND b.branch_id = ? "
                + "GROUP BY b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, b.exported_to_revenue, "
                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, "
                + "b.promotion_id, u.username";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setInt(2, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = mapResultSetToBooking(rs);
                b.setRoomTypes(rs.getString("roomTypes"));
                b.setRooms(getRoomsByBookingIdAndBranch(b.getId(), branchId));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // Lấy booking theo bookingId và branchId (chuẩn, tránh lỗi GROUP BY, trả về đúng 1 booking hoặc null)

    public Booking getBookingByIdAndBranch(int bookingId, int branchId) {
        Booking booking = null;
        String sql
                = "SELECT b.*, u.username, u.email, "
                + "  (SELECT STRING_AGG(rt.name, ', ') "
                + "   FROM BookingRoomType brt "
                + "   JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "   WHERE brt.booking_id = b.id) AS roomTypeName "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.id = ? AND b.branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking = mapResultSetToBooking(rs);
                    // Lấy chuỗi số phòng đã gán và danh sách phòng nếu cần
                    booking.setRoomNumbers(getRoomNumbersStringByBookingId(bookingId));
                    booking.setRooms(getRoomsByBookingIdAndBranch(bookingId, branchId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booking;
    }

    /**
     * Lấy chuỗi các loại phòng đã đặt theo booking id (VD: "Standard, Deluxe")
     */
    private String getRoomTypesStringByBookingId(int bookingId) {
        String sql = "SELECT rt.name, brt.quantity "
                + "FROM BookingRoomType brt "
                + "JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE brt.booking_id = ?";

        StringBuilder roomTypesBuilder = new StringBuilder();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int quantity = rs.getInt("quantity");
                    String name = rs.getString("name");

                    if (roomTypesBuilder.length() > 0) {
                        roomTypesBuilder.append(", ");
                    }
                    roomTypesBuilder.append(quantity).append(" x ").append(name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting room types by booking ID: " + e.getMessage());
            e.printStackTrace();
        }

        // Nếu không có loại phòng nào, trả về "N/A"
        return roomTypesBuilder.length() > 0 ? roomTypesBuilder.toString() : "N/A";
    }

    /**
     * Lấy chuỗi các số phòng đã gán cho booking (VD: "101, 102")
     */
    private String getRoomNumbersStringByBookingId(int bookingId) {
        String sql = "SELECT STRING_AGG(r.room_number, ', ') AS roomNumbers "
                + "FROM RoomAssignment ra "
                + "JOIN Room r ON ra.room_id = r.id "
                + "WHERE ra.booking_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("roomNumbers");
            }
        } catch (SQLException e) {
            System.err.println("Error getting room numbers for booking " + bookingId + ": " + e.getMessage());
        }

        return null;
    }

    public List<Room> getRoomsByBookingIdAndBranch(int bookingId, int branchId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name AS roomTypeName "
                + "FROM RoomAssignment ra "
                + "JOIN Room r ON ra.room_id = r.id "
                + "LEFT JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE ra.booking_id = ? AND r.branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setStatus(rs.getString("status"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    // Lấy danh sách roomId theo bookingId và branchId

    /**
     * Lấy danh sách ID phòng đã gán cho booking
     *
     * @param bookingId ID của booking
     * @param branchId ID chi nhánh để validate
     * @return List<Integer> danh sách ID phòng
     */
    public List<Integer> getRoomIdsByBookingAndBranch(int bookingId, int branchId) {
        String sql = "SELECT ra.room_id FROM RoomAssignment ra "
                + "INNER JOIN Rooms r ON ra.room_id = r.id "
                + "WHERE ra.booking_id = ? AND r.branch_id = ?";

        List<Integer> roomIds = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.setInt(2, branchId);

            rs = ps.executeQuery();

            while (rs.next()) {
                roomIds.add(rs.getInt("room_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting room IDs for booking " + bookingId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources in getRoomIdsByBookingAndBranch: " + e.getMessage());
            }
        }

        return roomIds;
    }

    // Cập nhật trạng thái booking
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE Booking SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error in updateBookingStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy danh sách các roomTypeId trong booking
     */
    public List<Integer> getRoomTypeIdsByBookingId(int bookingId) {
        List<Integer> roomTypeIds = new ArrayList<>();
        String sql = "SELECT room_type_id FROM BookingRoomType WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roomTypeIds.add(rs.getInt("room_type_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getRoomTypeIdsByBookingId: " + e.getMessage());
            e.printStackTrace();
        }
        return roomTypeIds;
    }

    // Hủy booking với lý do
    public boolean cancelBooking(int bookingId, String cancelReason) {
        String sql = "UPDATE Booking SET status = 'Cancelled', cancel_reason = ?, cancel_time = ? WHERE id = ? AND status IN ('Pending', 'Confirmed')";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, cancelReason);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // Thời gian hủy
            ps.setInt(3, bookingId);
            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0; // Trả về true nếu cập nhật thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm booking hôm nay theo customer và branch
    public List<Booking> searchBookingsTodayByCustomerAndBranch(String keyword, int branchId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, "
                + "STRING_AGG(rt.name, ', ') AS roomTypes "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id "
                + "LEFT JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "   OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND u.username LIKE ? "
                + "AND b.branch_id = ? "
                + "GROUP BY b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, "
                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, b.exported_to_revenue, "
                + "b.promotion_id, u.username";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs);
                    b.setRoomTypes(rs.getString("roomTypes"));
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ví dụ cho phân trang
    public List<Booking> getBookingsTodayByBranchPaging(int branchId, int page, int pageSize) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, rt.name AS roomTypeName, lp.level AS rank "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN RoomType rt ON b.room_type_id = rt.id "
                + "LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id "
                + "WHERE b.branch_id = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "ORDER BY b.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs);
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số booking hôm nay theo chi nhánh
    public int countBookingsTodayByBranch(int branchId) {
        String sql = "SELECT COUNT(*) AS total FROM Booking b "
                + "WHERE b.branch_id = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Đếm tổng số booking hôm nay theo customer (full name hoặc username) và branch
    public int countBookingsTodayByCustomerAndBranch(String keyword, int branchId) {
        String sql = "SELECT COUNT(*) AS total FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.branch_id = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND (u.fullname LIKE ? OR u.username LIKE ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy thông tin booking theo ID - phiên bản cập nhật để hỗ trợ việc gán
     * phòng
     */
    public Booking getBookingById(int bookingId) {
        Booking booking = null;
        String sql = "SELECT b.*, u.username, u.fullname "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setCancelReason(rs.getString("cancel_reason"));
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));
                    booking.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                    booking.setBranchId(rs.getInt("branch_id"));
                    booking.setUserName(rs.getString("username"));
                    booking.setFullName(rs.getString("fullname"));
                    booking.setNote(rs.getString("note"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getBookingById: " + e.getMessage());
            e.printStackTrace();
        }

        // Lấy thông tin về loại phòng đã đặt
        if (booking != null) {
            String sqlRoomTypes = "SELECT rt.name, brt.room_type_id FROM BookingRoomType brt "
                    + "JOIN RoomType rt ON brt.room_type_id = rt.id "
                    + "WHERE brt.booking_id = ?";
            List<String> roomTypeNames = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlRoomTypes)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        roomTypeNames.add(rs.getString("name"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            booking.setRoomTypeName(String.join(", ", roomTypeNames));

            // Lấy danh sách phòng đã gán cho booking
            booking.setRooms(getAssignedRoomsByBookingId(bookingId));

            // Lấy chuỗi số phòng
            String roomNumbers = getRoomNumbersStringByBookingId(bookingId);
            booking.setRoomNumbers(roomNumbers != null ? roomNumbers : "");
        }

        return booking;
    }

    //hoang
    public Booking getBookingById1(int bookingId) {
        Booking booking = null;
        String sql = "SELECT b.*, u.username, u.fullname, hb.name AS branch_name "
                + "FROM Booking b "
                + "JOIN HotelBranch hb ON b.branch_id = hb.id "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setCancelReason(rs.getString("cancel_reason"));
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));
                    booking.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                    booking.setBranchId(rs.getInt("branch_id"));
                    booking.setUserName(rs.getString("username"));
                    booking.setFullName(rs.getString("fullname"));
                    booking.setNote(rs.getString("note"));
                    booking.setBranchName(rs.getString("branch_name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getBookingById: " + e.getMessage());
            e.printStackTrace();
        }

        // Lấy thông tin về loại phòng đã đặt
        if (booking != null) {
            String sqlRoomTypes = "SELECT rt.name, brt.room_type_id FROM BookingRoomType brt "
                    + "JOIN RoomType rt ON brt.room_type_id = rt.id "
                    + "WHERE brt.booking_id = ?";
            List<String> roomTypeNames = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sqlRoomTypes)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        roomTypeNames.add(rs.getString("name"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            booking.setRoomTypeName(String.join(", ", roomTypeNames));

            // Lấy danh sách phòng đã gán cho booking
            booking.setRooms(getAssignedRoomsByBookingId(bookingId));

            // Lấy chuỗi số phòng
            String roomNumbers = getRoomNumbersStringByBookingId(bookingId);
            booking.setRoomNumbers(roomNumbers != null ? roomNumbers : "");
        }

        return booking;
    }

    /**
     * Lấy danh sách phòng đã được gán cho booking
     */
    public List<Room> getAssignedRoomsByBookingId(int bookingId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name AS roomTypeName "
                + "FROM RoomAssignment ra "
                + "JOIN Room r ON ra.room_id = r.id "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE ra.booking_id = ? AND r.is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setStatus(rs.getString("status"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getAssignedRoomsByBookingId: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    // (Tùy chọn) Lấy tất cả booking của một user
    public List<Booking> getBookingsByUserId(String userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setCancelReason(rs.getString("cancel_reason"));
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));
                    booking.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                    list.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getBookingsByUserId1(String userId) {
        List<Booking> list = new ArrayList<>();
        String sql = """
   SELECT b.*, 
          rt.image_url AS room_type_image, 
          rt.name AS room_type_name,  
          hb.name AS branch_name
   FROM Booking b
   JOIN HotelBranch hb ON b.branch_id = hb.id 
   OUTER APPLY (
       SELECT TOP 1 rt.image_url, rt.name
       FROM Room r
       JOIN RoomType rt ON r.room_type_id = rt.id
       WHERE r.branch_id = b.branch_id
   ) rt
   WHERE b.user_id = ?
     AND b.status NOT IN ('Pending', 'Paid')
   ORDER BY b.booking_time DESC
""";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setCancelReason(rs.getString("cancel_reason"));
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));
                    booking.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                    booking.setBranchId(rs.getInt("branch_id"));
                    booking.setBranchName(rs.getString("branch_name"));
                    booking.setRoomTypeImage(rs.getString("room_type_image"));
                    booking.setRoomTypeName(rs.getString("room_type_name"));
                    list.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // hoang
    public List<Booking> getBookingsByUserId11(String userId) {
        List<Booking> list = new ArrayList<>();
        String sql = """
   SELECT b.*, 
          rt.image_url AS room_type_image, 
          rt.name AS room_type_name,  
          hb.name AS branch_name
   FROM Booking b
   JOIN HotelBranch hb ON b.branch_id = hb.id 
   OUTER APPLY (
       SELECT TOP 1 rt.image_url, rt.name
       FROM Room r
       JOIN RoomType rt ON r.room_type_id = rt.id
       WHERE r.branch_id = b.branch_id
   ) rt
   WHERE b.user_id = ?
     AND b.status IN ('Pending', 'Paid')
   ORDER BY b.booking_time DESC
""";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setCancelReason(rs.getString("cancel_reason"));
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));
                    booking.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                    booking.setBranchId(rs.getInt("branch_id"));
                    booking.setNote(rs.getString("note"));
                    booking.setBranchName(rs.getString("branch_name"));
                    booking.setRoomTypeImage(rs.getString("room_type_image"));
                    booking.setRoomTypeName(rs.getString("room_type_name"));
                    list.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // DAO tạo booking. Chỉ cần hàm đơn giản như sau:
    public boolean createWalkInBookingSimple(String guestId, int roomId, Timestamp checkIn, Timestamp checkOut) {
        String insertBooking = "INSERT INTO Booking (user_id, check_in, check_out, status, total_price, deposit, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertBookingRoom = "INSERT INTO BookingRoom (booking_id, room_id) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false);

            // 1. Insert Booking
            PreparedStatement ps = connection.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, guestId);
            ps.setTimestamp(2, checkIn);
            ps.setTimestamp(3, checkOut);
            ps.setString(4, "Pending");
            ps.setBigDecimal(5, BigDecimal.ZERO);
            ps.setBigDecimal(6, BigDecimal.ZERO);
            ps.setString(7, "Unpaid");
            int rows = ps.executeUpdate();

            if (rows == 0) {
                connection.rollback();
                return false;
            }

            // 2. Lấy booking_id vừa tạo
            ResultSet rs = ps.getGeneratedKeys();
            int bookingId = -1;
            if (rs.next()) {
                bookingId = rs.getInt(1);
            } else {
                connection.rollback();
                return false;
            }

            // 3. Insert BookingRoom
            PreparedStatement ps2 = connection.prepareStatement(insertBookingRoom);
            ps2.setInt(1, bookingId);
            ps2.setInt(2, roomId);
            int rows2 = ps2.executeUpdate();

            if (rows2 == 0) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ex) {
            }
        }
    }

    // Helper để lấy chuỗi room numbers từ booking_id
    private String getRoomNumbersByBookingId(int bookingId) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT r.room_number FROM BookingRoom br "
                + "JOIN Room r ON br.room_id = r.id "
                + "WHERE br.booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(rs.getString("room_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // Lấy booking dùng cho thanh toán (theo bookingId)
    public Booking getBookingByIdPay(int bookingId) {
        String sql = "SELECT * FROM Booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Xác nhận thanh toán: cập nhật tổng tiền, trạng thái thanh toán
    public boolean confirmPayment(int bookingId, double totalPrice, String paymentStatus) {
        String sql = "UPDATE Booking SET total_price = ?, payment_status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, totalPrice);
            ps.setString(2, paymentStatus);
            ps.setInt(3, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy booking hôm nay theo branch và status (có phân trang, kèm full name & rank)
    public List<Booking> getBookingsTodayByBranchStatusPaging(int branchId, String status, int page, int pageSize) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, u.fullname, rt.name AS roomTypeName, lp.level AS rank "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN RoomType rt ON b.room_type_id = rt.id "
                + "LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id "
                + "WHERE b.branch_id = ? "
                + "AND LOWER(b.status) = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "ORDER BY b.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, status.toLowerCase());
            ps.setInt(3, (page - 1) * pageSize);
            ps.setInt(4, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs);
                    list.add(b);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Đếm số booking hôm nay theo branch và status
    public int countBookingsTodayByBranchStatus(int branchId, String status) {
        String sql = "SELECT COUNT(*) FROM Booking b "
                + "WHERE b.branch_id = ? "
                + "AND LOWER(b.status) = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, status.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Tìm kiếm booking hôm nay theo tên khách & branch & status (phân trang, kèm full name & rank)
    public List<Booking> searchBookingsTodayByCustomerAndBranchStatusPaging(
            String customerName, int branchId, String status, int page, int pageSize) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, u.fullname, rt.name AS roomTypeName, lp.level AS rank "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN RoomType rt ON b.room_type_id = rt.id "
                + "LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id "
                + "WHERE b.branch_id = ? "
                + "AND LOWER(b.status) = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND (u.fullname LIKE ? OR u.username LIKE ?) "
                + "ORDER BY b.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, status.toLowerCase());
            ps.setString(3, "%" + customerName + "%");
            ps.setString(4, "%" + customerName + "%");
            ps.setInt(5, (page - 1) * pageSize);
            ps.setInt(6, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs);
                    list.add(b);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Đếm số booking hôm nay theo tên khách & branch & status
    public int countBookingsTodayByCustomerAndBranchStatus(
            String customerName, int branchId, String status) {
        String sql = "SELECT COUNT(*) FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.branch_id = ? "
                + "AND LOWER(b.status) = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND (u.fullname LIKE ? OR u.username LIKE ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, status.toLowerCase());
            ps.setString(3, "%" + customerName + "%");
            ps.setString(4, "%" + customerName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Phân trang và filter nâng cao (keyword, status, fromDate, toDate)
    public List<Booking> searchBookingsByBranchWithFilterPaging(
            Integer branchId, String keyword, String status, String fromDate, String toDate, int page, int pageSize
    ) {
        List<Booking> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT b.*, u.username, u.fullname, "
                + "STRING_AGG(rt.name, ', ') AS roomTypes, lp.level AS rank "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id "
                + "LEFT JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id "
                + "WHERE b.branch_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(branchId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.fullname LIKE ? OR u.username LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(b.status) = ? ");
            params.add(status.trim().toLowerCase());
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND CAST(b.check_in AS date) >= ? ");
            params.add(java.sql.Date.valueOf(fromDate.trim()));
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND CAST(b.check_in AS date) <= ? ");
            params.add(java.sql.Date.valueOf(toDate.trim()));
        }

        sql.append("GROUP BY b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, "
                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, b.exported_to_revenue, "
                + "b.promotion_id, u.username, u.fullname, lp.level ");
        sql.append("ORDER BY b.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); ++i) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs);
                    b.setRoomTypes(rs.getString("roomTypes"));
                    list.add(b);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số bookings theo chi nhánh + filter (keyword, status, fromDate, toDate)
    public int countBookingsByBranchWithFilter(
            Integer branchId, String keyword, String status, String fromDate, String toDate
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.branch_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(branchId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.fullname LIKE ? OR u.username LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(b.status) = ? ");
            params.add(status.trim().toLowerCase());
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND CAST(b.check_in AS date) >= ? ");
            params.add(java.sql.Date.valueOf(fromDate.trim()));
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND CAST(b.check_in AS date) <= ? ");
            params.add(java.sql.Date.valueOf(toDate.trim()));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); ++i) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Sửa hàm mapping để lấy roomTypes đúng field
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getString("user_id"));
        b.setBookingTime(rs.getTimestamp("booking_time"));
        b.setCheckIn(rs.getTimestamp("check_in"));
        b.setCheckOut(rs.getTimestamp("check_out"));
        b.setStatus(rs.getString("status"));
        b.setTotalPrice(rs.getDouble("total_price"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setCancelReason(rs.getString("cancel_reason"));
        b.setCancelTime(rs.getTimestamp("cancel_time"));
        Object promotionIdObj = rs.getObject("promotion_id");
        b.setPromotionId(promotionIdObj != null ? (Integer) promotionIdObj : null);

        try {
            b.setUserName(rs.getString("username"));
        } catch (Exception ignore) {
        }
        try {
            b.setFullName(rs.getString("fullname"));
        } catch (Exception ignore) {
        }
        try {
            b.setRank(rs.getString("rank"));
        } catch (Exception ignore) {
        }
        try {
            b.setRoomTypeName(rs.getString("roomTypeName")); // SỬA tại đây
        } catch (Exception ignore) {
        }
        return b;
    }
    // Cập nhật voucher cho booking (nếu có)

    public void updateBookingVoucher(int bookingId, int voucherId) {
        String sql = "UPDATE Booking SET promotion_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách booking theo branch, có phân trang, KHÔNG filter gì cả
    public List<Booking> getBookingsByBranchWithPaging(Integer branchId, int page, int pageSize) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE branch_id = ? ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        // Nếu bạn dùng MySQL, đổi OFFSET thành LIMIT như sau:
        // String sql = "SELECT * FROM Booking WHERE branch_id = ? ORDER BY id DESC LIMIT ? OFFSET ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            // SQL Server
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            // Nếu MySQL:
            // ps.setInt(2, pageSize);
            // ps.setInt(3, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                // setBookingFromResultSet là hàm bạn tự viết để set các field Booking từ ResultSet
                setBookingFromResultSet(b, rs);
                bookings.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Đếm tổng số booking của branch, không filter gì cả
    public int countBookingsByBranch(Integer branchId) {
        String sql = "SELECT COUNT(*) FROM Booking WHERE branch_id = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Hàm này bạn đã có hoặc tự viết theo cấu trúc project của bạn
    private void setBookingFromResultSet(Booking b, ResultSet rs) throws SQLException {
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getString("user_id"));
        b.setUserName(rs.getString("user_name")); // nếu có
        b.setFullName(rs.getString("fullname")); // nếu có
        b.setRank(rs.getString("rank")); // nếu có
        b.setRoomTypes(rs.getString("room_types")); // nếu có
        b.setCheckIn(rs.getTimestamp("check_in"));
        b.setCheckOut(rs.getTimestamp("check_out"));
        b.setTotalPrice(rs.getDouble("total_price"));
        b.setStatus(rs.getString("status"));
        // và các trường khác nếu cần
    }

    public List<Booking> searchBookingsByBranchWithFilter(
            Integer branchId,
            String keyword,
            String status,
            String fromDate,
            String toDate
    ) {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.*, u.fullname, u.username, lp.level AS rank, ")
                .append("STRING_AGG(rt.name, ', ') AS roomTypes ")
                .append("FROM Booking b ")
                .append("JOIN UserAccount u ON b.user_id = u.id ")
                .append("LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id ")
                .append("LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id ")
                .append("LEFT JOIN RoomType rt ON brt.room_type_id = rt.id ")
                .append("WHERE b.branch_id = ? ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.fullname LIKE ? OR u.username LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND b.status = ? ");
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND b.check_in >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND b.check_out <= ? ");
        }
        // GROUP BY các trường không dùng aggregate
        sql.append("GROUP BY ")
                .append("b.id, b.user_id, b.created_by, b.booking_time, b.check_in, b.check_out, ")
                .append("b.status, b.total_price, b.refund_amount, b.payment_status, b.cancel_reason, ")
                .append("b.cancel_time, b.promotion_id, b.branch_id, b.note, b.is_deleted, ")
                .append("b.exported_to_revenue, ") // ✅ THÊM DÒNG NÀY
                .append("u.fullname, u.username, lp.level ");

        sql.append("ORDER BY b.check_in DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, branchId);

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status);
            }
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                ps.setString(idx++, fromDate);
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                ps.setString(idx++, toDate);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                // ... các trường khác ...
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getString("user_id"));
                b.setBookingTime(rs.getTimestamp("booking_time"));
                b.setCheckIn(rs.getTimestamp("check_in"));
                b.setCheckOut(rs.getTimestamp("check_out"));
                b.setStatus(rs.getString("status"));
                b.setTotalPrice(rs.getDouble("total_price"));
                b.setPaymentStatus(rs.getString("payment_status"));
                b.setCancelReason(rs.getString("cancel_reason"));
                b.setCancelTime(rs.getTimestamp("cancel_time"));
                Object promotionIdObj = rs.getObject("promotion_id");
                b.setPromotionId(promotionIdObj != null ? (Integer) promotionIdObj : null);

                b.setUserName(rs.getString("username"));
                b.setFullName(rs.getString("fullname"));
                b.setRank(rs.getString("rank")); // lấy level
                b.setRoomTypes(rs.getString("roomTypes")); // lấy chuỗi room type
                bookings.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

// Lấy danh sách booking của 1 user, kèm đúng danh sách Service từng booking (bao gồm trạng thái dịch vụ trong booking)
    public List<Booking> getBookingsDetailOfUser(String userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, "
                + "STUFF((SELECT ', ' + rt.name FROM BookingRoomType brt "
                + "JOIN RoomType rt ON brt.room_type_id = rt.id WHERE brt.booking_id = b.id FOR XML PATH('')), 1, 2, '') AS roomTypes "
                + "FROM Booking b WHERE b.user_id = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getString("user_id"));
                b.setBookingTime(rs.getTimestamp("booking_time"));
                b.setCheckIn(rs.getTimestamp("check_in"));
                b.setCheckOut(rs.getTimestamp("check_out"));
                b.setStatus(rs.getString("status"));
                b.setTotalPrice(rs.getDouble("total_price"));
                b.setPaymentStatus(rs.getString("payment_status"));
                b.setCancelReason(rs.getString("cancel_reason"));
                b.setCancelTime(rs.getTimestamp("cancel_time"));
                b.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                b.setNote(rs.getString("note"));
                b.setRoomTypes(rs.getString("roomTypes"));

                // ĐÚNG NGHIỆP VỤ: Lấy service đúng của từng booking
                b.setServices(getServicesOfBooking(b.getId()));

                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy các dịch vụ đi kèm booking, mapping cả bookingServiceStatus (trạng thái dịch vụ trong booking)
    public List<Service> getServicesOfBooking(int bookingId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.description, s.price, s.branch_id, s.status, s.image_url, s.is_deleted, "
                + "bs.quantity, bs.paid_status "
                + "FROM BookingService bs "
                + "JOIN Service s ON bs.service_id = s.id "
                + "WHERE bs.booking_id = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
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
                s.setQuantity(rs.getInt("quantity"));
                s.setBookingServiceStatus(rs.getString("paid_status")); // mapping trạng thái dịch vụ trong booking
                services.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    // Gán thêm phòng cho booking (không dùng transaction)
    public boolean assignRoomsToBooking(int bookingId, String[] roomIds) {
        if (roomIds == null || roomIds.length == 0) {
            System.out.println("No rooms to assign for booking ID: " + bookingId);
            return false;
        }

        String checkExistingSQL = "SELECT COUNT(*) FROM RoomAssignment WHERE booking_id = ? AND room_id = ?";
        String insertSQL = "INSERT INTO RoomAssignment (booking_id, room_id, assigned_at) VALUES (?, ?, GETDATE())";
        String checkRoomStatusSQL = "SELECT status FROM Room WHERE id = ? AND is_deleted = 0";

        int successfulAssignments = 0;
        List<String> errors = new ArrayList<>();

        // Validate và insert từng phòng
        for (String rid : roomIds) {
            try {
                int roomId = Integer.parseInt(rid.trim());

                // 1. Kiểm tra phòng đã được assign cho booking này chưa
                try (PreparedStatement checkExisting = connection.prepareStatement(checkExistingSQL)) {
                    checkExisting.setInt(1, bookingId);
                    checkExisting.setInt(2, roomId);
                    ResultSet rs = checkExisting.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        errors.add("Room ID " + roomId + " already assigned to this booking");
                        continue; // Skip phòng này
                    }
                }

                // 2. Kiểm tra trạng thái phòng
                try (PreparedStatement checkStatus = connection.prepareStatement(checkRoomStatusSQL)) {
                    checkStatus.setInt(1, roomId);
                    ResultSet rs = checkStatus.executeQuery();

                    if (rs.next()) {
                        String roomStatus = rs.getString("status");
                        if (!"Available".equals(roomStatus)) {
                            errors.add("Room ID " + roomId + " is not available (Status: " + roomStatus + ")");
                            continue; // Skip phòng này
                        }
                    } else {
                        errors.add("Room ID " + roomId + " not found or deleted");
                        continue; // Skip phòng này
                    }
                }

                // 3. Insert assignment
                try (PreparedStatement ps = connection.prepareStatement(insertSQL)) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, roomId);
                    int rowsInserted = ps.executeUpdate();

                    if (rowsInserted > 0) {
                        successfulAssignments++;
                        System.out.println("Successfully assigned room " + roomId + " to booking " + bookingId);
                    }
                }

            } catch (NumberFormatException e) {
                errors.add("Invalid room ID format: " + rid);
            } catch (SQLException e) {
                errors.add("Database error for room " + rid + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Log errors nếu có
        if (!errors.isEmpty()) {
            System.out.println("Assignment errors for booking " + bookingId + ":");
            for (String error : errors) {
                System.out.println("- " + error);
            }
        }

        System.out.println("Successfully assigned " + successfulAssignments + " room(s) to booking " + bookingId);
        return successfulAssignments > 0;
    }

    /**
     * Đếm tổng số lượng booking khớp với các tiêu chí lọc.
     *
     */
    public int getBookingCountByBranchWithFilter(Integer branchId, String keyword, String status, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Booking b LEFT JOIN UserAccount u ON b.user_id = u.id WHERE b.branch_id = ?");

        List<Object> params = new ArrayList<>();
        params.add(branchId);

        buildWhereClause(sql, params, keyword, status, fromDate, toDate);

        try (
                PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting booking count with filter: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /*
 * Tìm kiếm và lấy danh sách booking đã được phân trang, khớp với model Booking mới.
     */
    public List<Booking> searchBookingsByBranchWithFilter(Integer branchId, String keyword, String status, String fromDate, String toDate, int currentPage, int pageSize) {
        List<Booking> bookingList = new ArrayList<>();

        // **Thay đổi**: Liệt kê tường minh các cột để khớp với model Booking
        StringBuilder sql = new StringBuilder(
                "SELECT b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, b.total_price, "
                + "b.payment_status, b.cancel_reason, b.cancel_time, b.promotion_id, b.room_types, b.note, b.branch_id, "
                + "u.full_name, u.username, u.rank "
                + "FROM Booking b LEFT JOIN UserAccount u ON b.user_id = u.id WHERE b.branch_id = ?"
        );

        List<Object> params = new ArrayList<>();
        params.add(branchId);

        buildWhereClause(sql, params, keyword, status, fromDate, toDate);

        sql.append(" ORDER BY b.check_in DESC");
        sql.append(" LIMIT ? OFFSET ?");

        int offset = (currentPage - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);

        try (
                PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();

                    // --- Mapping dữ liệu vào model Booking mới ---
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id")); // **Thay đổi**: Kiểu String
                    booking.setBranchId(rs.getInt("branch_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time")); // **Cập nhật**: Dùng booking_time
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setStatus(rs.getString("status"));
                    booking.setRoomTypes(rs.getString("room_types"));
                    booking.setPaymentStatus(rs.getString("payment_status")); // **Mới**
                    booking.setCancelReason(rs.getString("cancel_reason"));   // **Mới**
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));     // **Mới**
                    booking.setNote(rs.getString("note"));                     // **Mới**

                    // **Cập nhật**: Xử lý promotion_id có thể là NULL
                    Integer promotionId = (Integer) rs.getObject("promotion_id");
                    if (promotionId != null) {
                        booking.setPromotionId(promotionId);
                    }

                    // Dữ liệu từ bảng UserAccount
                    booking.setFullName(rs.getString("full_name"));
                    booking.setUserName(rs.getString("username"));
                    booking.setRank(rs.getString("rank"));

                    bookingList.add(booking);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching bookings with filter and pagination: " + e.getMessage());
            e.printStackTrace();
        }

        return bookingList;
    }

    /*
 * Phương thức private helper để xây dựng phần WHERE động của câu lệnh SQL.
     */
    private void buildWhereClause(StringBuilder sql, List<Object> params, String keyword, String status, String fromDate, String toDate) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR u.username LIKE ?)");
            String searchKeyword = "%" + keyword.trim() + "%";
            params.add(searchKeyword);
            params.add(searchKeyword);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND b.status = ?");
            params.add(status.trim());
        }

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append(" AND b.check_in >= ?");
            params.add(fromDate.trim());
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append(" AND b.check_in < DATE_ADD(?, INTERVAL 1 DAY)");
            params.add(toDate.trim());
        }
    }

    /**
     * Lấy danh sách voucher IDs đã được apply cho booking
     */
    public List<Integer> getAppliedVouchersByBookingId(int bookingId) {
        List<Integer> voucherIds = new ArrayList<>();

        try {
            String sql = "SELECT voucher_id FROM BookingVoucher WHERE booking_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        voucherIds.add(rs.getInt("voucher_id"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return voucherIds;
    }

    /**
     * Lấy chi tiết 1 booking cụ thể với services của chính user đó
     */
    public Booking getBookingDetailWithServices(int bookingId, String userId) {
        String sql = "SELECT b.*, "
                + "u.username, u.fullname, "
                + "STRING_AGG(DISTINCT rt.name, ', ') as room_types "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id "
                + "LEFT JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE b.id = ? AND b.user_id = ? AND b.is_deleted = 0 "
                + "GROUP BY b.id, b.user_id, b.created_by, b.booking_time, b.check_in, b.check_out, "
                + "b.status, b.total_price, b.refund_amount, b.payment_status, b.cancel_reason, "
                + "b.cancel_time, b.promotion_id, b.branch_id, b.note, b.is_deleted, b.exported_to_revenue, "
                + "u.username, u.fullname";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));

                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setCancelReason(rs.getString("cancel_reason"));
                    booking.setCancelTime(rs.getTimestamp("cancel_time"));
                    booking.setPromotionId(rs.getInt("promotion_id"));
                    booking.setBranchId(rs.getInt("branch_id"));
                    booking.setNote(rs.getString("note"));

                    // Set user info
                    booking.setUserName(rs.getString("username"));
                    booking.setFullName(rs.getString("fullname"));
                    booking.setRoomTypes(rs.getString("room_types"));

                    // Lấy services của booking này
                    List<Service> services = getServicesByBookingId(bookingId);
                    booking.setServices(services);

                    System.out.println("Found booking " + bookingId + " for user " + userId
                            + " with " + (services != null ? services.size() : 0) + " services");

                    return booking;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting booking detail with services: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy danh sách booking cơ bản của user (không có services detail)
     */
    public List<Booking> getBookingsOfUser(String userId) {
        String sql = "SELECT b.id, b.booking_time, b.check_in, b.check_out, b.status, "
                + "b.total_price, b.payment_status, "
                + "STRING_AGG(DISTINCT rt.name, ', ') as room_types "
                + "FROM Booking b "
                + "LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id "
                + "LEFT JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE b.user_id = ? AND b.is_deleted = 0 "
                + "GROUP BY b.id, b.booking_time, b.check_in, b.check_out, b.status, "
                + "b.total_price, b.payment_status "
                + "ORDER BY b.booking_time DESC";

        List<Booking> bookings = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(userId);
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setRoomTypes(rs.getString("room_types"));

                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings of user: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    /**
     * Lấy services của 1 booking cụ thể
     */
    private List<Service> getServicesByBookingId(int bookingId) {
        String sql = "SELECT s.*, bs.quantity, bs.paid_status as booking_service_status "
                + "FROM Service s "
                + "INNER JOIN BookingService bs ON s.id = bs.service_id "
                + "WHERE bs.booking_id = ? AND s.is_deleted = 0 "
                + "ORDER BY s.name";

        List<Service> services = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getDouble("price"));
                    service.setBranchId(rs.getInt("branch_id"));
                    service.setStatus(rs.getString("status"));
                    service.setImageUrl(rs.getString("image_url"));

                    // Thông tin từ BookingService
                    service.setQuantity(rs.getInt("quantity"));
                    service.setBookingServiceStatus(rs.getString("booking_service_status"));

                    services.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting services by booking ID: " + e.getMessage());
            e.printStackTrace();
        }

        return services;
    }

    public Booking getBookingDetailById(String bookingId) {

        String sql = "SELECT * FROM Booking WHERE id = ?";
        Booking booking = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(bookingId));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking = new Booking();
                    // SỬA LỖI 2: Đọc thông tin từ ResultSet với tên cột chính xác (snake_case)
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getString("user_id"));
                    booking.setBookingTime(rs.getTimestamp("booking_time"));
                    booking.setCheckIn(rs.getTimestamp("check_in"));
                    booking.setCheckOut(rs.getTimestamp("check_out"));
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setNote(rs.getString("note"));

                    String roomTypesString = getRoomTypesStringByBookingId(booking.getId());
                    booking.setRoomTypes(roomTypesString);

                    List<Service> services = getServicesByBookingId(booking.getId());
                    booking.setServices(services);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            // Log lỗi này sẽ giúp bạn tìm ra vấn đề nhanh hơn
            System.err.println("Error in getBookingDetailById: " + e.getMessage());
            e.printStackTrace();
        }

        return booking;
    }

    public boolean areAllRoomsAssigned(int bookingId) {
        // Câu lệnh SQL đã được điều chỉnh để khớp với schema của bạn
        String sql = "SELECT "
                + "    CASE WHEN "
                + "        (SELECT SUM(quantity) FROM BookingRoomType WHERE booking_id = ?) = "
                + // Tên bảng đúng
                "        (SELECT COUNT(room_id) FROM RoomAssignment WHERE booking_id = ?) "
                + // Tên bảng và cột đúng
                "    THEN 1 "
                + "    ELSE 0 "
                + "END AS is_fully_assigned";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, bookingId);
            st.setInt(2, bookingId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_fully_assigned");
            }
        } catch (SQLException e) {
            System.out.println("Error in areAllRoomsAssigned: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Mặc định trả về false nếu có lỗi
    }

    public List<Booking> getPendingBookingsForBranch(int branchId) {
        String sql = "SELECT b.*, u.username as user_name, u.full_name as full_name "
                + "FROM Booking b "
                + "JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.branch_id = ? AND (b.status = 'Pending' OR b.status = 'Paid' OR b.status = 'CheckedIn') "
                + "ORDER BY b.check_in ASC";

        // Thêm debug
        System.out.println("Executing SQL: " + sql.replace("?", String.valueOf(branchId)));

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            List<Booking> bookings = new ArrayList<>();

            while (rs.next()) {
                Booking booking = new Booking();
                // Populate booking...
                bookings.add(booking);
            }

            System.out.println("Found " + bookings.size() + " bookings for branch " + branchId);
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error getting pending bookings: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cập nhật booking sau khi checkout thành công
     */
    public boolean updateBookingAfterCheckout(Booking booking) {
        try {
            String sql = "UPDATE Booking SET status = ?, payment_status = ?, total_price = ? WHERE id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, booking.getStatus()); // "Completed"
                ps.setString(2, booking.getPaymentStatus()); // "Paid"
                ps.setDouble(3, booking.getTotalPrice());
                ps.setInt(4, booking.getId());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tổng tiền phòng từ BookingRoomType
     */
    public double getTotalRoomPriceByBookingId(int bookingId) {
        double total = 0;

        try {
            String sql = "SELECT SUM(quantity * price_per_room) as total "
                    + "FROM BookingRoomType WHERE booking_id = ?";

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
     * Lấy danh sách BookingRoomType theo booking ID với đầy đủ thông tin room
     * type
     */
    public List<BookingRoomType> getBookingRoomTypesByBookingId(int bookingId) {
        List<BookingRoomType> bookingRoomTypes = new ArrayList<>();

        try {
            String sql = "SELECT brt.booking_id, brt.room_type_id, brt.quantity, brt.price_per_room, "
                    + "rt.name as room_type_name, rt.description as room_type_description, "
                    + "rt.image_url as room_type_image_url "
                    + "FROM BookingRoomType brt "
                    + "JOIN RoomType rt ON brt.room_type_id = rt.id "
                    + "WHERE brt.booking_id = ? AND rt.is_deleted = 0 "
                    + "ORDER BY rt.name";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        BookingRoomType brt = new BookingRoomType();
                        brt.setBookingId(rs.getInt("booking_id"));
                        brt.setRoomTypeId(rs.getInt("room_type_id"));
                        brt.setQuantity(rs.getInt("quantity"));
                        brt.setPricePerRoom(rs.getBigDecimal("price_per_room"));
                        brt.setRoomTypeName(rs.getString("room_type_name"));
                        brt.setRoomTypeDescription(rs.getString("room_type_description"));
                        brt.setRoomTypeImageUrl(rs.getString("room_type_image_url"));
                        bookingRoomTypes.add(brt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookingRoomTypes;
    }

    public Integer addBookingReturnId(String userId, Timestamp checkIn, Timestamp checkOut,
            String status, double totalPrice, String paymentStatus, int branchId,
            String note, boolean isDeleted) {

        String sql = "INSERT INTO Booking (user_id, check_in, check_out, status, total_price, "
                + "payment_status, branch_id, note, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, userId);
            ps.setTimestamp(2, checkIn);
            ps.setTimestamp(3, checkOut);
            ps.setString(4, status);
            ps.setDouble(5, totalPrice);
            ps.setString(6, paymentStatus);
            ps.setInt(7, branchId);
            ps.setString(8, note);
            ps.setBoolean(9, isDeleted);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // booking_id vừa insert
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertBookingRoomType(int bookingId, int roomTypeId, int quantity, double pricePerRoom) {
        String sql = "INSERT INTO BookingRoomType (booking_id, room_type_id, quantity, price_per_room) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, roomTypeId);
            ps.setInt(3, quantity);
            ps.setDouble(4, pricePerRoom);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertBookingService(int bookingId, int serviceId, int quantity, String paidStatus) {
        String sql = "INSERT INTO BookingService (booking_id, service_id, quantity, paid_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, serviceId);
            ps.setInt(3, quantity);
            ps.setString(4, paidStatus); // e.g., "Unpaid"
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSpecialRequest(int bookingId, String note) {
        String sql = "UPDATE Booking SET note = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, note);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
             return false;
        }
    }


    // Hung: Lấy số lượng khách theo khoảng thời gian
    public int getTotalGuestsCompletedBookingByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        int totalGuests = 0;
        String sql = "SELECT COUNT(DISTINCT user_id) AS total FROM Booking "
                + "WHERE branch_id = ? "
                + "AND is_deleted = 0 "
                + "AND status = 'Completed' "
                + "AND check_in >= ? AND check_in <= ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);

            // Tính ngày đầu và cuối của khoảng tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            st.setDate(2, java.sql.Date.valueOf(fromDate));
            st.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                totalGuests = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalGuests;
    }

    // Hung: hàm hỗ trợ làm biểu đồ theo status 
    public Map<String, Integer> getBookingStatusCountsByBranchAndMonthYearRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        Map<String, Integer> statusCounts = new LinkedHashMap<>();

        // Khởi tạo các status với 0 để tránh null
        String[] allStatuses = {"Pending", "Paid", "CheckedIn", "CheckedOut", "Completed", "Cancelled", "NoShow"};
        for (String status : allStatuses) {
            statusCounts.put(status, 0);
        }

        LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
        LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

        String sql = "SELECT status, COUNT(*) AS count FROM Booking "
                + "WHERE branch_id = ? "
                + "AND is_deleted = 0 "
                + "AND booking_time BETWEEN ? AND ? "
                + "GROUP BY status";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setDate(2, java.sql.Date.valueOf(fromDate));
            st.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                statusCounts.put(status, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statusCounts;
    }

    public int getTotalBookingByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        int totalBooking = 0;

        LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
        LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

        String sql = "SELECT COUNT(*) AS total FROM Booking "
                + "WHERE branch_id = ? "
                + "AND is_deleted = 0 "
                + "AND booking_time BETWEEN ? AND ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setDate(2, java.sql.Date.valueOf(fromDate));
            st.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                totalBooking = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalBooking;
    }

    public Integer addBooking2(String userId, Timestamp checkIn, Timestamp checkOut,
            String status, double totalPrice, String paymentStatus, int branchId,
            String note, boolean isDeleted) {

        String sql = "INSERT INTO Booking (user_id, check_in, check_out, status, total_price, "
                + "payment_status, branch_id, note, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userId);
            ps.setTimestamp(2, checkIn);
            ps.setTimestamp(3, checkOut);
            ps.setString(4, status);
            ps.setDouble(5, totalPrice);
            ps.setString(6, paymentStatus);
            ps.setInt(7, branchId);
            ps.setString(8, note);
            ps.setBoolean(9, isDeleted);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // return booking_id
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;  // or throw custom exception if you prefer
        }
    }

    public boolean updateBookingPrice(int bookingId, double newTotalPrice) {
        try {
            String sql = "UPDATE Booking SET total_price = ? WHERE id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setDouble(1, newTotalPrice);
                ps.setInt(2, bookingId);

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        BookingDAO bookingDAO = new BookingDAO();
        List<Booking> bookings = bookingDAO.searchBookingsByBranchWithFilter(
                1, "J", "Pending", "2025-05-01", "2025-07-31"
        );
        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }
}
