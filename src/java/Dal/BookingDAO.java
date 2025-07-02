package Dal;

import java.sql.Date;
import Model.Booking;
import Model.Room;
import Model.Service;
import Model.UserAccount;
import java.math.BigDecimal;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO extends DBcontext.DBContext {

    // Lấy booking hôm nay theo chi nhánh (kèm full name & rank)
    public List<Booking> getBookingsTodayByBranch(int branchId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, u.full_name, rt.name AS roomTypeName, lp.level AS rank "
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
                + "GROUP BY b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, "
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
    public String getRoomTypesStringByBookingId(int bookingId) {
        String sql
                = "SELECT STRING_AGG(DISTINCT rt.name, ', ') AS roomTypes "
                + "FROM BookingRoomType brt "
                + "JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE brt.booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("roomTypes");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy chuỗi các số phòng đã gán cho booking (VD: "101, 102")
     */
    public String getRoomNumbersStringByBookingId(int bookingId) {
        String sql
                = "SELECT STRING_AGG(r.room_number, ', ') AS roomNumbers "
                + "FROM BookingRoom br "
                + "JOIN Room r ON br.room_id = r.id "
                + "WHERE br.booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("roomNumbers");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                    room.setRoomTypeName(rs.getString("roomTypeName")); // SỬA tại đây nếu model Room có trường này
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    // Lấy danh sách roomId theo bookingId và branchId

    public List<Integer> getRoomIdsByBookingAndBranch(int bookingId, int branchId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT r.id FROM BookingRoom br "
                + "JOIN Room r ON br.room_id = r.id "
                + "WHERE br.booking_id = ? AND r.branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    // Cập nhật trạng thái booking
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE Booking SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
            e.printStackTrace();
        }
        return roomTypeIds;
    }

    // Hủy booking với lý do
    public boolean cancelBooking(int bookingId, String cancelReason) {
        String sql = "UPDATE Booking SET status = 'CANCELLED', cancel_reason = ?, cancel_time = ? WHERE id = ? AND status IN ('Pending', 'Confirmed')";
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

//    public Booking getBookingById(int bookingId) {
//        Booking booking = null;
//        String sql = "SELECT b.*, u.username, u.email, "
//                + "STRING_AGG(rt.name, ', ') AS roomTypes "
//                + "FROM Booking b "
//                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
//                + "LEFT JOIN BookingRoom br ON b.id = br.booking_id "
//                + "LEFT JOIN Room r ON br.room_id = r.id "
//                + "LEFT JOIN RoomType rt ON r.room_type_id = rt.roomTypeID "
//                + "WHERE b.id = ? "
//                + "GROUP BY b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, "
//                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, "
//                + "b.promotion_id, u.username, u.email";
//        try {
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setInt(1, bookingId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                booking = new Booking();
//                booking.setId(rs.getInt("id"));
//                booking.setUserId(rs.getString("user_id"));
//                booking.setBookingTime(rs.getTimestamp("booking_time"));
//                booking.setCheckIn(rs.getTimestamp("check_in"));
//                booking.setCheckOut(rs.getTimestamp("check_out"));
//                booking.setStatus(rs.getString("status"));
//                booking.setTotalPrice(rs.getDouble("total_price"));
//                booking.setDeposit(rs.getDouble("deposit"));
//                booking.setPaymentStatus(rs.getString("payment_status"));
//                booking.setCancelReason(rs.getString("cancel_reason"));
//                booking.setCancelTime(rs.getTimestamp("cancel_time"));
//                booking.setPromotionId(rs.getInt("promotion_id"));
//                booking.setUserName(rs.getString("username"));
//                booking.setRoomTypes(rs.getString("roomTypes"));
//                booking.setRooms(getRoomsByBookingId(bookingId));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return booking;
//    }
//
//    public List<Room> getRoomsByBookingId(int bookingId) {
//        List<Room> rooms = new ArrayList<>();
//        String sql = "SELECT r.*, rt.name AS roomTypeName, hb.name AS hotelName "
//                + "FROM BookingRoom br "
//                + "JOIN Room r ON br.room_id = r.id "
//                + "LEFT JOIN RoomType rt ON r.room_type_id = rt.roomTypeID "
//                + "LEFT JOIN HotelBranch hb ON r.branch_id = hb.id "
//                + "WHERE br.booking_id = ?";
//        try {
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setInt(1, bookingId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Room room = new Room();
//                room.setId(rs.getInt("id"));
//                room.setRoomNumber(rs.getString("room_number"));
//                room.setBranchId(rs.getInt("branch_id"));
//                room.setRoomTypeId(rs.getInt("room_type_id"));
//                room.setStatus(rs.getString("status"));
//                room.setImageUrl(rs.getString("image_url"));
//                room.setRoomTypeName(rs.getString("roomTypeName"));
//                room.setHotelName(rs.getString("hotelName"));
//                rooms.add(room);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return rooms;
//    }
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
                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, "
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
    // Tìm kiếm booking hôm nay theo customer (username) và branch (paging)

    // Tìm kiếm booking hôm nay theo customer (full name hoặc username) và branch (paging)
    public List<Booking> searchBookingsTodayByCustomerAndBranchPaging(String keyword, int branchId, int page, int pageSize) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, u.full_name, "
                + "STRING_AGG(rt.name, ', ') AS roomTypes "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN BookingRoomType brt ON b.id = brt.booking_id "
                + "LEFT JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE b.branch_id = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND (u.full_name LIKE ? OR u.username LIKE ?) "
                + "GROUP BY b.id, b.user_id, b.booking_time, b.check_in, b.check_out, b.status, "
                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, "
                + "b.promotion_id, u.username, u.full_name "
                + "ORDER BY b.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ps.setInt(4, (page - 1) * pageSize);
            ps.setInt(5, pageSize);
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

    // Đếm tổng số booking hôm nay theo customer (full name hoặc username) và branch
    public int countBookingsTodayByCustomerAndBranch(String keyword, int branchId) {
        String sql = "SELECT COUNT(*) AS total FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "WHERE b.branch_id = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND (u.full_name LIKE ? OR u.username LIKE ?)";
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

    // Lấy booking theo booking_id
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM Booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
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
                    booking.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
                    return booking;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        String sql = "SELECT b.*, u.username, u.full_name, rt.name AS roomTypeName, lp.level AS rank "
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
        String sql = "SELECT b.*, u.username, u.full_name, rt.name AS roomTypeName, lp.level AS rank "
                + "FROM Booking b "
                + "LEFT JOIN UserAccount u ON b.user_id = u.id "
                + "LEFT JOIN RoomType rt ON b.room_type_id = rt.id "
                + "LEFT JOIN LoyaltyPoint lp ON b.user_id = lp.user_id "
                + "WHERE b.branch_id = ? "
                + "AND LOWER(b.status) = ? "
                + "AND (CAST(b.check_in AS date) = CAST(GETDATE() AS date) "
                + "     OR CAST(b.check_out AS date) = CAST(GETDATE() AS date)) "
                + "AND (u.full_name LIKE ? OR u.username LIKE ?) "
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
                + "AND (u.full_name LIKE ? OR u.username LIKE ?)";
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
                "SELECT b.*, u.username, u.full_name, "
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
            sql.append("AND (u.full_name LIKE ? OR u.username LIKE ?) ");
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
                + "b.total_price, b.deposit, b.payment_status, b.cancel_reason, b.cancel_time, "
                + "b.promotion_id, u.username, u.full_name, lp.level ");
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
            sql.append("AND (u.full_name LIKE ? OR u.username LIKE ?) ");
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
            b.setFullName(rs.getString("full_name"));
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
        b.setFullName(rs.getString("full_name")); // nếu có
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
//    public List<Booking> getBookingsDetailOfUser(String userId) {
//        List<Booking> list = new ArrayList<>();
//        String sql = "SELECT b.*, "
//                + "STUFF((SELECT ', ' + rt.name FROM BookingRoomType brt "
//                + "JOIN RoomType rt ON brt.room_type_id = rt.id WHERE brt.booking_id = b.id FOR XML PATH('')), 1, 2, '') AS roomTypes "
//                + "FROM Booking b WHERE b.user_id = ?";
//        try (
//                PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, userId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Booking b = new Booking();
//                b.setId(rs.getInt("id"));
//                b.setUserId(rs.getString("user_id"));
//                b.setBookingTime(rs.getTimestamp("booking_time"));
//                b.setCheckIn(rs.getTimestamp("check_in"));
//                b.setCheckOut(rs.getTimestamp("check_out"));
//                b.setStatus(rs.getString("status"));
//                b.setTotalPrice(rs.getDouble("total_price"));
//                b.setPaymentStatus(rs.getString("payment_status"));
//                b.setCancelReason(rs.getString("cancel_reason"));
//                b.setCancelTime(rs.getTimestamp("cancel_time"));
//                b.setPromotionId(rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null);
//                b.setNote(rs.getString("note"));
//                b.setRoomTypes(rs.getString("roomTypes"));
//
//                // ĐÚNG NGHIỆP VỤ: Lấy service đúng của từng booking
//                b.setServices(getServicesOfBooking(b.getId()));
//
//                list.add(b);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
    // Lấy các dịch vụ đi kèm booking, mapping cả bookingServiceStatus (trạng thái dịch vụ trong booking)
//    public List<Service> getServicesOfBooking(int bookingId) {
//        List<Service> services = new ArrayList<>();
//        String sql = "SELECT s.id, s.name, s.description, s.price, s.branch_id, s.status, s.image_url, s.is_deleted, "
//                + "bs.quantity, bs.paid_status "
//                + "FROM BookingService bs "
//                + "JOIN Service s ON bs.service_id = s.id "
//                + "WHERE bs.booking_id = ?";
//        try (
//                PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setInt(1, bookingId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Service s = new Service();
//                s.setId(rs.getInt("id"));
//                s.setName(rs.getString("name"));
//                s.setDescription(rs.getString("description"));
//                s.setPrice(rs.getDouble("price"));
//                s.setBranchId(rs.getInt("branch_id"));
//                s.setStatus(rs.getString("status"));
//                s.setImageUrl(rs.getString("image_url"));
//                s.setDeleted(rs.getBoolean("is_deleted"));
//                s.setQuantity(rs.getInt("quantity"));
//                s.setBookingServiceStatus(rs.getString("paid_status")); // mapping trạng thái dịch vụ trong booking
//                services.add(s);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return services;
//    }
    // Gán danh sách phòng cho booking (xóa phòng cũ, gán mới)
    public void assignRoomsToBooking(int bookingId, String[] roomIds) {
        String deleteSQL = "DELETE FROM BookingRoom WHERE booking_id = ?";
        String insertSQL = "INSERT INTO BookingRoom (booking_id, room_id) VALUES (?, ?)";
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;
        try {
            connection.setAutoCommit(false);

            // Xóa tất cả phòng đã gán trước đó cho booking này
            deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.setInt(1, bookingId);
            deleteStmt.executeUpdate();

            // Gán lại các phòng mới
            insertStmt = connection.prepareStatement(insertSQL);
            for (String rid : roomIds) {
                int roomId = Integer.parseInt(rid.trim());
                insertStmt.setInt(1, bookingId);
                insertStmt.setInt(2, roomId);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Failed to assign rooms to booking", e);
        } finally {
            try {
                if (deleteStmt != null) {
                    deleteStmt.close();
                }
            } catch (Exception ignore) {
            }
            try {
                if (insertStmt != null) {
                    insertStmt.close();
                }
            } catch (Exception ignore) {
            }
            try {
                connection.setAutoCommit(true);
            } catch (Exception ignore) {
            }
        }
    }

    public boolean addBooking(String userId, Timestamp checkIn, Timestamp checkOut,
            String status, double totalPrice, String paymentStatus, int branchId,
            String note, boolean isDeleted) {

        String sql = "INSERT INTO Booking (user_id, check_in, check_out, status, total_price, "
                + "payment_status, branch_id, note, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userId);                // user_id
            ps.setTimestamp(2, checkIn);            // check_in
            ps.setTimestamp(3, checkOut);           // check_out
            ps.setString(4, status);                // status
            ps.setDouble(5, totalPrice);            // total_price
            ps.setString(6, paymentStatus);         // payment_status
            ps.setInt(7, branchId);                 // branch_id
            ps.setString(8, note);                  // note
            ps.setBoolean(9, isDeleted);           // is_deleted

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
