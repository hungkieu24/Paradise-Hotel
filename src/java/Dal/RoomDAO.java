package Dal;

import Model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DBcontext.DBContext;
import Model.Booking;
import Model.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RoomDAO for handling Room database operations.
 */
public class RoomDAO extends DBContext {

    /**
     * Lấy thông tin phòng theo ID
     *
     * @param roomId ID của phòng
     * @return Room object hoặc null nếu không tìm thấy
     */
    public Room getRoomById(int roomId) {
        String sql = "SELECT r.id, r.room_number, r.status, r.branch_id, "
                + "rt.name as room_type_name, rt.id as room_type_id "
                + "FROM Room r "
                + "INNER JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.id = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, roomId);

            rs = ps.executeQuery();

            if (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setStatus(rs.getString("status"));
                room.setBranchId(rs.getInt("branch_id"));
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeId(rs.getInt("room_type_id"));

                return room;
            }

        } catch (SQLException e) {
            System.err.println("Error getting room by ID " + roomId + ": " + e.getMessage());
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
                System.err.println("Error closing resources in getRoomById: " + e.getMessage());
            }
        }

        return null;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Room ORDER BY room_number";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getRoomsByBranchId(int branchId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Room WHERE branch_id = ? ORDER BY room_number";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getRoomsByRoomTypeId(int roomTypeId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Room WHERE room_type_id = ? ORDER BY room_number";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, roomTypeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO Room (room_number, branch_id, room_type_id, status, image_url) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getBranchId());
            stmt.setInt(3, room.getRoomTypeId());
            stmt.setString(4, room.getStatus());
            stmt.setString(5, room.getImageUrl());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createRoom(Room room) throws SQLException {
        String sql = "INSERT INTO Room (room_number, branch_id, room_type_id, status, image_url) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getBranchId());
            stmt.setInt(3, room.getRoomTypeId());
            stmt.setString(4, room.getStatus());
            stmt.setString(5, room.getImageUrl() != null ? room.getImageUrl() : "");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE Room SET room_number=?, branch_id=?, room_type_id=?, status=?, image_url=? WHERE id=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getBranchId());
            stmt.setInt(3, room.getRoomTypeId());
            stmt.setString(4, room.getStatus());
            stmt.setString(5, room.getImageUrl());
            stmt.setInt(6, room.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM Room WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //author: Thien
    // Fix theo database moi
    public List<Room> getRooms(String status, String roomTypeId, String search) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.id, r.room_number, r.branch_id, r.room_type_id, r.status, r.image_url, "
                + "rt.id AS rt_id, rt.name AS rt_name, rt.description AS rt_description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id WHERE 1=1";

        if (status != null && !status.isEmpty()) {
            sql += " AND r.status = ?";
        }
        if (roomTypeId != null && !roomTypeId.isEmpty()) {
            sql += " AND r.room_type_id = ?";
        }
        if (search != null && !search.isEmpty()) {
            sql += " AND r.room_number LIKE ? Or lower(rt.name) like lower(?) OR lower(r.status) like lower(?)";
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            int index = 1;
            if (status != null && !status.isEmpty()) {
                stmt.setString(index++, status);
            }
            if (roomTypeId != null && !roomTypeId.isEmpty()) {
                stmt.setInt(index++, Integer.parseInt(roomTypeId));
            }
            if (search != null && !search.isEmpty()) {
                stmt.setString(index++, "%" + search + "%");
                stmt.setString(index++, "%" + search + "%");
                stmt.setString(index++, "%" + search + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Tạo RoomType
                RoomType rt = new RoomType(
                        rs.getInt("rt_id"),
                        rs.getString("rt_name"),
                        rs.getString("rt_description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("rt_image_url")
                );

                // Tạo Room và gắn RoomType
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                room.setRoomType(rt);

                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // Lấy danh sách room_id theo booking_id 
    public List<Integer> getRoomIdsByBooking(int bookingId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT room_id FROM BookingRoom WHERE booking_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("room_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<Room> searchRoomsByRoomTypeName(String roomTypeNameKeyword) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.* FROM Room r "
                + "JOIN RoomType rt ON r.roomTypeId = rt.id "
                + "WHERE rt.name LIKE ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + roomTypeNameKeyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setId(rs.getInt("id"));
                r.setRoomNumber(rs.getString("roomNumber"));
                r.setBranchId(rs.getInt("branchId"));
                r.setRoomTypeId(rs.getInt("roomTypeId"));
                r.setStatus(rs.getString("status"));
                r.setImageUrl(rs.getString("imageUrl"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
// Phân trang danh sách phòng ()

    public List<Room> pagingRoom(int page, int pageSize) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Room ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

// Đếm tổng số phòng
    public int countAllRooms() {
        String sql = "SELECT COUNT(*) FROM Room";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

// Phân trang danh sách phòng theo loại phòng (tìm kiếm)
    public List<Room> searchRoomsByRoomTypeNamePaging(String roomTypeNameKeyword, int page, int pageSize) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.* FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name LIKE ? "
                + "ORDER BY r.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + roomTypeNameKeyword + "%");
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

// Đếm tổng số phòng theo loại phòng (tìm kiếm)
    public int countRoomsByRoomTypeName(String roomTypeNameKeyword) {
        String sql = "SELECT COUNT(*) FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id WHERE rt.name LIKE ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + roomTypeNameKeyword + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    // Lấy danh sách phòng theo branch, status, phân trang

    public List<Room> pagingRoomByBranch(int branchId, String status, int page, int pageSize) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Room WHERE branch_id = ?"
                + (status != null && !status.isEmpty() ? " AND status = ?" : "")
                + " ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            int idx = 2;
            if (status != null && !status.isEmpty()) {
                ps.setString(idx++, status);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx++, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số phòng theo branch
    public int countRoomsByBranch(int branchId) {
        String sql = "SELECT COUNT(*) FROM Room WHERE branch_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
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

    // Tìm kiếm phòng theo tên loại phòng và branch + phân trang
    public List<Room> searchRoomsByRoomTypeNameAndBranchPaging(String roomTypeNameKeyword, int branchId, int page, int pageSize) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.* FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name LIKE ? AND r.branch_id = ? "
                + "ORDER BY r.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + roomTypeNameKeyword + "%");
            ps.setInt(2, branchId);
            ps.setInt(3, (page - 1) * pageSize);
            ps.setInt(4, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getBranchNameById(String managerId) {
        String name = null;
        String sql = "select name from HotelBranch where manager_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, managerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public int getBranchId(String managerId) {
        int branchId = 0;
        String sql = "select id from HotelBranch where manager_id= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, managerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                branchId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branchId;
    }

    // author : thien
    // Content: get all room by branch id
    // Fix theo database moi
    public List<Room> getAllRoomByBranchId(int branchId, int page, int pageSize, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Room> rooms = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT r.id AS room_id, r.room_number, r.status, r.branch_id, r.room_type_id, r.image_url AS room_image_url, "
                + "rt.id AS roomtype_id, rt.name, rt.description, rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS roomtype_image_url, b.check_in, b.check_out, "
                + "CASE "
                + "     When r.status = 'Maintenance' THEN 'Maintenance' "
                + "     WHEN EXISTS ("
                + "         SELECT 1 FROM RoomAssignment ra "
                + "         JOIN Booking b ON ra.booking_id = b.id "
                + "         WHERE ra.room_id = r.id "
                + "         AND b.status = 'CheckIn' "
                + "         AND b.check_in <= ? AND b.check_out >= ? "
                + "     ) THEN 'Occupied' "
                + "     WHEN EXISTS ("
                + "         SELECT 1 FROM RoomAssignment ra "
                + "         JOIN Booking b ON ra.booking_id = b.id "
                + "         WHERE ra.room_id = r.id "
                + "         AND b.status IN ('Pending', 'Paid') "
                + "         AND b.check_in <= ? AND b.check_out >= ? "
                + "     ) THEN 'Booked' "
                + "     ELSE 'Available' "
                + "END AS caculated_status "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "LEFT JOIN RoomAssignment ra ON ra.room_id = r.id "
                + "LEFT JOIN Booking b ON ra.booking_id = b.id "
                + "    AND b.check_in <= ? AND b.check_out >= ? "
                + "WHERE r.is_deleted = 0 and r.branch_id = ? "
                + "ORDER BY r.id " // Added ORDER BY for deterministic results
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(checkOut));
            ps.setTimestamp(2, Timestamp.valueOf(checkIn));
            ps.setTimestamp(3, Timestamp.valueOf(checkOut));
            ps.setTimestamp(4, Timestamp.valueOf(checkIn));
            ps.setTimestamp(5, Timestamp.valueOf(checkOut));
            ps.setTimestamp(6, Timestamp.valueOf(checkIn));
            ps.setInt(7, branchId);
            ps.setInt(8, offset);
            ps.setInt(9, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomType rt = new RoomType(
                        rs.getInt("roomtype_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("roomtype_image_url")
                );
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("caculated_status"),
                        rs.getString("room_image_url") // Sửa từ "image" thành "room_image_url"
                );
                Timestamp checkInTs = rs.getTimestamp("check_in");
                Timestamp checkOutTs = rs.getTimestamp("check_out");
                if (checkInTs != null) {
                    room.setCheckIn(checkInTs.toLocalDateTime());
                }
                if (checkOutTs != null) {
                    room.setCheckOut(checkOutTs.toLocalDateTime());
                }
                room.setRoomType(rt); // Đảm bảo quan hệ được thiết lập
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean isRoomNumberExist(String roomNumber, int branchId) {
        String sql = "select count(*) from Room where room_number = ? and branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
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

    public List<Room> getRoomsByBranch(int branchId, String status, String roomTypeId, String search,
            int page, int pageSize, LocalDateTime checkIn) {
        List<Room> rooms = new ArrayList<>();
        Map<Integer, List<Room>> roomsByRoomType = new HashMap<>();

        LocalDateTime startOfDay = checkIn.with(LocalTime.MIN);
        LocalDateTime endOfDay = checkIn.with(LocalTime.MAX);

        // 1. Lấy toàn bộ phòng, KHÔNG phân trang trong SQL
        StringBuilder sql = new StringBuilder(
                "SELECT r.id AS room_id, r.room_number, r.status, r.branch_id, r.room_type_id, r.image_url AS room_image_url, "
                + "rt.id AS roomtype_id, rt.name, rt.description, rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS roomtype_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.is_deleted = 0 AND rt.is_deleted = 0 AND r.branch_id = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(branchId);

        if (roomTypeId != null && !roomTypeId.isEmpty()) {
            sql.append("AND r.room_type_id = ? ");
            params.add(Integer.parseInt(roomTypeId));
        }

        if (search != null && !search.isEmpty()) {
            sql.append("AND (r.room_number LIKE ? OR LOWER(rt.name) LIKE LOWER(?) ");
            String like = "%" + search + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else {
                    ps.setString(i + 1, (String) param);
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomType rt = new RoomType(
                        rs.getInt("roomtype_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("roomtype_image_url")
                );
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("room_image_url")
                );
                room.setRoomType(rt);
                rooms.add(room);
                roomsByRoomType.computeIfAbsent(rt.getRoomTypeID(), k -> new ArrayList<>()).add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return rooms;
        }

        // 2. Gán trạng thái Occupied
        try {
            String occupiedSql = "SELECT ra.room_id FROM RoomAssignment ra "
                    + "JOIN Booking b ON ra.booking_id = b.id "
                    + "WHERE b.status = 'CheckedIn' AND b.check_in <= ? AND b.check_out >= ?";
            try (PreparedStatement ps = connection.prepareStatement(occupiedSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(endOfDay));
                ps.setTimestamp(2, Timestamp.valueOf(startOfDay));
                ResultSet rs = ps.executeQuery();
                Set<Integer> occupiedRoomIds = new HashSet<>();
                while (rs.next()) {
                    occupiedRoomIds.add(rs.getInt("room_id"));
                }
                for (Room room : rooms) {
                    if (occupiedRoomIds.contains(room.getId()) && !room.getStatus().equalsIgnoreCase("Maintenance")) {
                        room.setStatus("Occupied");
                    }
                }
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. Gán trạng thái Booked
        try {
            String bookedSql = "SELECT brt.room_type_id, SUM(brt.quantity) AS total_booked "
                    + "FROM Booking b "
                    + "JOIN BookingRoomType brt ON b.id = brt.booking_id "
                    + "WHERE b.status IN ('Pending', 'Paid') AND b.is_deleted = 0 "
                    + "AND b.branch_id = ? "
                    + "AND b.check_in <= ? AND b.check_out >= ? "
                    + "GROUP BY brt.room_type_id";
            try (PreparedStatement ps = connection.prepareStatement(bookedSql)) {
                ps.setInt(1, branchId);
                ps.setTimestamp(2, Timestamp.valueOf(endOfDay));
                ps.setTimestamp(3, Timestamp.valueOf(startOfDay));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int rtId = rs.getInt("room_type_id");
                    int totalBooked = rs.getInt("total_booked");

                    List<Room> roomList = roomsByRoomType.getOrDefault(rtId, new ArrayList<>());
                    int count = 0;
                    for (Room room : roomList) {
                        if (count >= totalBooked) {
                            break;
                        }
                        if (room.getStatus().equalsIgnoreCase("Available")) {
                            room.setStatus("Booked");
                            count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 4. Lọc theo status nếu có
        if (status != null && !status.isEmpty()) {
            rooms = rooms.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        // 5. Phân trang sau khi lọc
        int fromIndex = Math.min((page - 1) * pageSize, rooms.size());
        int toIndex = Math.min(fromIndex + pageSize, rooms.size());
        if (fromIndex > toIndex) {
            return new ArrayList<>();
        }
        return rooms.subList(fromIndex, toIndex);
    }

    public boolean softDeleteRoom(int roomId) {
        String sql = "UPDATE Room set is_deleted = 1 where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đếm tổng số phòng theo tên loại phòng và branch
    public int countRoomsByRoomTypeNameAndBranch(String roomTypeNameKeyword, int branchId) {
        String sql = "SELECT COUNT(*) FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id WHERE rt.name LIKE ? AND r.branch_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + roomTypeNameKeyword + "%");
            ps.setInt(2, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<RoomType> getRoomTypesByBranch(int branchId) {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT DISTINCT rt.* FROM RoomType rt "
                + "JOIN Room r ON rt.id = r.room_type_id WHERE r.branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomType rt = new RoomType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("image_url")
                    );
                    list.add(rt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách phòng còn trống của 1 branch
    public List<Room> getAvailableRoomsByBranch(int branchId) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as roomTypeName FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.branch_id = ? AND r.status = 'available'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));
                    // Mapping thêm các trường khác nếu Room có (vd: tầng, trạng thái, ...)
                    list.add(room);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//// Lấy danh sách phòng còn trống theo branch và loại phòng
//    public List<Room> getAvailableRoomsByBranchAndRoomType(int branchId, int roomTypeId) {
//        List<Room> list = new ArrayList<>();
//        String sql = "SELECT r.*, rt.name as roomTypeName FROM Room r "
//                + "JOIN RoomType rt ON r.room_type_id = rt.id "
//                + "WHERE r.branch_id = ? AND r.room_type_id = ? AND r.status = 'available'";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setInt(1, branchId);
//            ps.setInt(2, roomTypeId);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Room room = new Room();
//                    room.setId(rs.getInt("id"));
//                    room.setRoomNumber(rs.getString("room_number"));
//                    room.setRoomTypeId(rs.getInt("room_type_id"));
//                    room.setRoomTypeName(rs.getString("roomTypeName"));
//                    // Mapping thêm các trường khác nếu Room có
//                    list.add(room);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
    /**
     * Cập nhật trạng thái phòng
     *
     * @param roomId ID của phòng
     * @param status Trạng thái mới ("Available", "Reserved", "Occupied",
     * "Maintenance")
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE Room SET status = ? WHERE id = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, roomId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources in updateRoomStatus: " + e.getMessage());
            }
        }
    }

    public int getRoomIdByRoomNumberAndBranchId(String roomNumber, int branchID) {
        int roomId = -1;
        String sql = "select id from Room where room_number =? and branch_id =?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setInt(2, branchID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                roomId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomId;
    }

    public String getStatusById(int roomId) {
        String sql = "select status from Room where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean[] isRoomnumberExist(String roomNumber, int branchID) {
        boolean[] result = {false, false};
        String sql = "select is_deleted from Room where room_number = ? and branch_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setInt(2, branchID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int isDeleted = rs.getInt("is_deleted");
                if (isDeleted == 0) {
                    result[0] = true;
                } else if (isDeleted == 1) {
                    result[1] = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getTotalRoomsByBranchId(int branchId) {
        String sql = "SELECT COUNT(*) as total FROM Room WHERE branch_id = ? AND is_deleted = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, branchId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalRoomsByBranch(int branchId, String status, String roomTypeId, String search, LocalDateTime checkIn) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.id AS room_id, r.status, r.room_type_id "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.is_deleted = 0 AND rt.is_deleted = 0 AND r.branch_id = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(branchId);

        if (roomTypeId != null && !roomTypeId.isEmpty()) {
            sql.append("AND r.room_type_id = ? ");
            params.add(Integer.parseInt(roomTypeId));
        }

        if (search != null && !search.isEmpty()) {
            sql.append("AND (r.room_number LIKE ? OR LOWER(rt.name) LIKE LOWER(?) OR LOWER(r.status) LIKE LOWER(?)) ");
            String like = "%" + search + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        List<Room> rooms = new ArrayList<>();
        Map<Integer, List<Room>> roomsByRoomType = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else {
                    stmt.setString(i + 1, (String) param);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setStatus(rs.getString("status"));
                room.setRoomTypeId(rs.getInt("room_type_id"));
                rooms.add(room);

                roomsByRoomType
                        .computeIfAbsent(room.getRoomTypeId(), k -> new ArrayList<>())
                        .add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        // Tính trạng thái Occupied
        LocalDateTime startOfDay = checkIn.with(LocalTime.MIN);
        LocalDateTime endOfDay = checkIn.with(LocalTime.MAX);
        try {
            String occupiedSql = "SELECT ra.room_id "
                    + "FROM RoomAssignment ra "
                    + "JOIN Booking b ON ra.booking_id = b.id "
                    + "WHERE b.status = 'CheckedIn' "
                    + "AND b.check_in <= ? AND b.check_out >= ?";
            try (PreparedStatement ps = connection.prepareStatement(occupiedSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(endOfDay));
                ps.setTimestamp(2, Timestamp.valueOf(startOfDay));
                ResultSet rs = ps.executeQuery();
                Set<Integer> occupiedRoomIds = new HashSet<>();
                while (rs.next()) {
                    occupiedRoomIds.add(rs.getInt("room_id"));
                }
                for (Room room : rooms) {
                    if (occupiedRoomIds.contains(room.getId()) && !room.getStatus().equalsIgnoreCase("Maintenance")) {
                        room.setStatus("Occupied");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Tính trạng thái Booked
        try {
            String bookedSql = "SELECT brt.room_type_id, SUM(brt.quantity) AS total_booked "
                    + "FROM Booking b "
                    + "JOIN BookingRoomType brt ON b.id = brt.booking_id "
                    + "WHERE b.status IN ('Pending', 'Paid') AND b.is_deleted = 0 "
                    + "AND b.branch_id = ? "
                    + "AND b.check_in <= ? AND b.check_out >= ? "
                    + "GROUP BY brt.room_type_id";
            try (PreparedStatement ps = connection.prepareStatement(bookedSql)) {
                ps.setInt(1, branchId);
                ps.setTimestamp(2, Timestamp.valueOf(endOfDay));
                ps.setTimestamp(3, Timestamp.valueOf(startOfDay));
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int rtId = rs.getInt("room_type_id");
                    int totalBooked = rs.getInt("total_booked");

                    List<Room> roomList = roomsByRoomType.getOrDefault(rtId, new ArrayList<>());
                    int count = 0;
                    for (Room room : roomList) {
                        if (count >= totalBooked) {
                            break;
                        }
                        if (room.getStatus().equalsIgnoreCase("Available")) {
                            room.setStatus("Booked");
                            count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Lọc theo status nếu cần
        if (status != null && !status.isEmpty()) {
            rooms = rooms.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        System.out.println("Available Rooms:");
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getId() + ", Status: " + room.getStatus());
        }

        return rooms.size();
    }

    public static void main(String[] args) {
        RoomDAO roomDAO = new RoomDAO();
        int size = roomDAO.getTotalRoomsByBranch(1, "Available", "", "", LocalDateTime.now());
        System.out.println(size);
        List<Room> rooms = roomDAO.getRoomsByBranch(1, "", "", "", 1, 5, LocalDateTime.of(2025, 8, 11, 0, 0));
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getId() + ", Status: " + room.getStatus());
        }
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

    /**
     * Lấy danh sách phòng đã được gán cho một booking
     *
     * @return List<Room> danh sách phòng đã gán
     */
    public List<Room> getAssignedRoomsByBookingId(int bookingId) {
        String sql = "SELECT r.id, r.room_number, r.status, r.branch_id, "
                + "rt.name as room_type_name, rt.id as room_type_id "
                + "FROM Room r "
                + "INNER JOIN RoomType rt ON r.room_type_id = rt.id "
                + "INNER JOIN RoomAssignment ra ON r.id = ra.room_id "
                + "WHERE ra.booking_id = ? "
                + "ORDER BY r.room_number";

        List<Room> rooms = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);

            rs = ps.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setStatus(rs.getString("status"));
                room.setBranchId(rs.getInt("branch_id"));
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeId(rs.getInt("room_type_id"));

                rooms.add(room);
            }


        } catch (SQLException e) {
            System.err.println("Error getting assigned rooms for booking " + bookingId + ": " + e.getMessage());
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
                System.err.println("Error closing resources in getAssignedRoomsByBookingId: " + e.getMessage());
            }
        }

        return rooms;
    }

    public List<Room> getSimpleAvailableRoomsByType(int roomTypeId, int branchId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name AS roomTypeName "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.room_type_id = ? "
                + "AND r.branch_id = ? "
                + "AND (r.status = 'Available' OR r.status = 'available' OR r.status = 'AVAILABLE' OR r.status = 'Vacant') "
                + "AND r.is_deleted = 0";

        // Debug - In ra truy vấn SQL và tham số

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            ps.setInt(2, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                // Debug - In ra kết quả truy vấn
                int count = 0;

                while (rs.next()) {
                    count++;
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setStatus(rs.getString("status"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));


                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> getAvailableRoomsForAssignment(int roomTypeId, int branchId, int bookingId) {
        List<Room> rooms = new ArrayList<>();

        // Đầu tiên lấy thông tin thời gian của booking hiện tại
        String getBookingTimeSql = "SELECT check_in, check_out FROM Booking WHERE id = ?";
        java.sql.Timestamp bookingCheckIn = null;
        java.sql.Timestamp bookingCheckOut = null;

        try (PreparedStatement psTime = connection.prepareStatement(getBookingTimeSql)) {
            psTime.setInt(1, bookingId);
            try (ResultSet rsTime = psTime.executeQuery()) {
                if (rsTime.next()) {
                    bookingCheckIn = rsTime.getTimestamp("check_in");
                    bookingCheckOut = rsTime.getTimestamp("check_out");
                } else {
                    return rooms; // Return empty list
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return rooms;
        }

        // Truy vấn chính lấy phòng khả dụng
        String sql = "SELECT r.*, rt.id AS rt_id, rt.name AS roomTypeName, rt.description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.room_type_id = ? "
                + "AND r.branch_id = ? "
                + "AND r.status = 'Available' "
                + "AND r.is_deleted = 0 "
                + "AND r.id NOT IN ( "
                + "    SELECT ra.room_id FROM RoomAssignment ra "
                + "    JOIN Booking b ON ra.booking_id = b.id "
                + "    WHERE b.id != ? " // Không phải booking hiện tại
                + "    AND b.status NOT IN ('Cancelled', 'Completed', 'NoShow') " // Không phải booking đã hủy/hoàn thành
                + "    AND (( " // Kiểm tra xem thời gian có trùng không
                + "        b.check_in < ? AND b.check_out > ? " // Booking khác check-in trước và check-out sau khi booking hiện tại check-in
                + "    ) OR ( "
                + "        b.check_in < ? AND b.check_out > ? " // Booking khác check-in trước và check-out sau khi booking hiện tại check-out
                + "    ) OR ( "
                + "        b.check_in >= ? AND b.check_out <= ? " // Booking khác nằm hoàn toàn trong khoảng thời gian của booking hiện tại
                + "    )) "
                + ") "
                + "ORDER BY r.room_number";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            ps.setInt(2, branchId);
            ps.setInt(3, bookingId);
            ps.setTimestamp(4, bookingCheckIn);
            ps.setTimestamp(5, bookingCheckIn);
            ps.setTimestamp(6, bookingCheckOut);
            ps.setTimestamp(7, bookingCheckOut);
            ps.setTimestamp(8, bookingCheckIn);
            ps.setTimestamp(9, bookingCheckOut);



            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setStatus(rs.getString("status"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));

                    // Create RoomType object according to your model
                    RoomType rt = new RoomType(
                            rs.getInt("rt_id"),
                            rs.getString("roomTypeName"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("rt_image_url")
                    );
                    room.setRoomType(rt);

                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Kiểm tra thêm các phòng đã được gán cho booking này
        String alreadyAssignedSql = "SELECT r.*, rt.id AS rt_id, rt.name AS roomTypeName, rt.description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM RoomAssignment ra "
                + "JOIN Room r ON ra.room_id = r.id "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE ra.booking_id = ? AND r.room_type_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(alreadyAssignedSql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, roomTypeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Nếu phòng đã được gán cho booking này, kiểm tra xem nó có trong danh sách kết quả chưa
                    int roomId = rs.getInt("id");
                    boolean alreadyInList = false;

                    for (Room existingRoom : rooms) {
                        if (existingRoom.getId() == roomId) {
                            alreadyInList = true;
                            break;
                        }
                    }

                    // Nếu chưa có trong danh sách, thêm vào
                    if (!alreadyInList) {
                        Room room = new Room();
                        room.setId(roomId);
                        room.setRoomNumber(rs.getString("room_number"));
                        room.setRoomTypeId(rs.getInt("room_type_id"));
                        room.setBranchId(rs.getInt("branch_id"));
                        room.setStatus("Assigned"); // Đánh dấu là đã được gán
                        room.setImageUrl(rs.getString("image_url"));
                        room.setRoomTypeName(rs.getString("roomTypeName"));

                        RoomType rt = new RoomType(
                                rs.getInt("rt_id"),
                                rs.getString("roomTypeName"),
                                rs.getString("description"),
                                rs.getDouble("base_price"),
                                rs.getInt("capacity_adult"),
                                rs.getInt("capacity_child"),
                                rs.getString("rt_image_url")
                        );
                        room.setRoomType(rt);

                        rooms.add(room);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getAvailableRoomsByRoomTypeName(List<String> roomTypeNames, int branchId) {
        List<Room> rooms = new ArrayList<>();

        if (roomTypeNames == null || roomTypeNames.isEmpty()) {
            return rooms;
        }

        // Xây dựng tham số IN cho câu truy vấn SQL
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < roomTypeNames.size(); i++) {
            placeholders.append("?");
            if (i < roomTypeNames.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT r.*, rt.name AS roomTypeName, rt.description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.status = 'Available' "
                + "AND r.branch_id = ? "
                + "AND r.is_deleted = 0 "
                + "AND rt.name IN (" + placeholders.toString() + ") "
                + "ORDER BY rt.name, r.room_number";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            // Thiết lập các tham số tên loại phòng
            for (int i = 0; i < roomTypeNames.size(); i++) {
                ps.setString(i + 2, roomTypeNames.get(i));
            }

            // Debug log

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setStatus(rs.getString("status"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));

                    // Tạo đối tượng RoomType
                    RoomType roomType = new RoomType(
                            rs.getInt("room_type_id"),
                            rs.getString("roomTypeName"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("rt_image_url")
                    );
                    room.setRoomType(roomType);

                    rooms.add(room);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<String> getRoomTypeNamesByBookingId(int bookingId) {
        List<String> roomTypeNames = new ArrayList<>();

        String sql = "SELECT rt.name "
                + "FROM BookingRoomType brt "
                + "JOIN RoomType rt ON brt.room_type_id = rt.id "
                + "WHERE brt.booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roomTypeNames.add(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomTypeNames;
    }

    /**
     * Lấy tất cả phòng theo loại và chi nhánh, không quan tâm trạng thái Dùng
     * cho mục đích debug
     */
    public List<Room> getAllRoomsByTypeAndBranch(int roomTypeId, int branchId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name AS roomTypeName, rt.description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.room_type_id = ? "
                + "AND r.branch_id = ? "
                + "AND r.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            ps.setInt(2, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setStatus(rs.getString("status"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));

                    // Tạo đối tượng RoomType phù hợp với model của bạn
                    RoomType roomType = new RoomType(
                            rs.getInt("room_type_id"),
                            rs.getString("roomTypeName"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("rt_image_url")
                    );
                    room.setRoomType(roomType);

                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Lấy danh sách phòng theo loại, chi nhánh và danh sách các trạng thái có
     * thể Giúp tìm kiếm phòng với nhiều trạng thái khác nhau (ví dụ:
     * "Available", "AVAILABLE", "available", v.v.)
     */
    public List<Room> getRoomsByTypeAndStatuses(int roomTypeId, int branchId, List<String> statuses) {
        List<Room> rooms = new ArrayList<>();

        if (statuses == null || statuses.isEmpty()) {
            return rooms;
        }

        // Tạo placeholders cho IN clause
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < statuses.size(); i++) {
            placeholders.append("?");
            if (i < statuses.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT r.*, rt.name AS roomTypeName, rt.description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.room_type_id = ? "
                + "AND r.branch_id = ? "
                + "AND r.status IN (" + placeholders.toString() + ") "
                + "AND r.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            ps.setInt(2, branchId);

            // Thiết lập các giá trị cho status
            for (int i = 0; i < statuses.size(); i++) {
                ps.setString(i + 3, statuses.get(i));
            }


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setStatus(rs.getString("status"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));

                    // Tạo đối tượng RoomType phù hợp với model của bạn
                    RoomType roomType = new RoomType(
                            rs.getInt("room_type_id"),
                            rs.getString("roomTypeName"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("rt_image_url")
                    );
                    room.setRoomType(roomType);

                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Lấy phòng theo tên loại phòng
     */
    public List<Room> getAvailableRoomsByTypeName(String roomTypeName, int branchId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name AS roomTypeName, rt.description, "
                + "rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS rt_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name = ? "
                + "AND r.branch_id = ? "
                + "AND LOWER(r.status) = 'available' "
                + "AND r.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomTypeName);
            ps.setInt(2, branchId);


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    room.setStatus(rs.getString("status"));
                    room.setImageUrl(rs.getString("image_url"));
                    room.setRoomTypeName(rs.getString("roomTypeName"));

                    // Tạo đối tượng RoomType phù hợp với model của bạn
                    RoomType roomType = new RoomType(
                            rs.getInt("room_type_id"),
                            rs.getString("roomTypeName"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("rt_image_url")
                    );
                    room.setRoomType(roomType);

                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public int getRoomTypeIdByName(String roomTypeName) {
        String sql = "SELECT id FROM RoomType WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomTypeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
        }
        return -1;
    }

    /**
     * Lấy phòng có sẵn theo loại phòng dựa trên tên
     */
    public List<Room> getAvailableRoomsByTypeNames(List<String> roomTypeNames, int branchId) {
        List<Room> rooms = new ArrayList<>();

        if (roomTypeNames == null || roomTypeNames.isEmpty()) {
            return rooms;
        }

        // Tạo danh sách tên loại phòng để dùng trong IN clause
        List<String> quotedTypeNames = new ArrayList<>();
        for (String typeName : roomTypeNames) {
            quotedTypeNames.add("'" + typeName.trim() + "'");
        }

        String typeNamesClause = String.join(",", quotedTypeNames);

        String sql = "SELECT r.*, rt.name AS roomTypeName "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name IN (" + typeNamesClause + ") "
                + "AND r.branch_id = ? "
                + "AND (LOWER(r.status) = 'available' OR LOWER(r.status) = 'vacant') "
                + "AND r.is_deleted = 0";


        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
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

    /**
     * Lấy tất cả phòng theo loại phòng dựa trên tên (bất kể trạng thái) Phương
     * pháp dự phòng khi không tìm thấy phòng nào có sẵn
     */
    public List<Room> getAllRoomsByTypeNames(List<String> roomTypeNames, int branchId) {
        List<Room> rooms = new ArrayList<>();

        if (roomTypeNames == null || roomTypeNames.isEmpty()) {
            return rooms;
        }

        // Tạo danh sách tên loại phòng để dùng trong IN clause
        List<String> quotedTypeNames = new ArrayList<>();
        for (String typeName : roomTypeNames) {
            quotedTypeNames.add("'" + typeName.trim() + "'");
        }

        String typeNamesClause = String.join(",", quotedTypeNames);

        String sql = "SELECT r.*, rt.name AS roomTypeName "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name IN (" + typeNamesClause + ") "
                + "AND r.branch_id = ? "
                + "AND r.is_deleted = 0";


        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setBranchId(rs.getInt("branch_id"));
                    // Ghi đè trạng thái thành "Available" để hiển thị
                    room.setStatus("Available");
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

    /**
     * Lấy danh sách tên loại phòng từ chuỗi phân tách bởi dấu phẩy
     */
    public List<String> parseRoomTypeNames(String roomTypeNamesStr) {
        List<String> roomTypeNames = new ArrayList<>();
        if (roomTypeNamesStr != null && !roomTypeNamesStr.isEmpty()) {
            String[] typeArray = roomTypeNamesStr.split(",");
            for (String type : typeArray) {
                roomTypeNames.add(type.trim());
            }
        }
        return roomTypeNames;
    }

    /**
     * Lấy roomTypeIds từ danh sách tên loại phòng
     */
    public List<Integer> getRoomTypeIdsByNames(List<String> roomTypeNames) {
        List<Integer> roomTypeIds = new ArrayList<>();

        if (roomTypeNames != null && !roomTypeNames.isEmpty()) {
            for (String roomTypeName : roomTypeNames) {
                int roomTypeId = getRoomTypeIdByName(roomTypeName);
                if (roomTypeId > 0) {
                    roomTypeIds.add(roomTypeId);
                }
            }
        }

        return roomTypeIds;
    }

    public List<Room> getAvailableRoomsForBooking(Booking booking) {
        List<Room> availableRooms = new ArrayList<>();

        if (booking == null) {
            return availableRooms;
        }
        // Thử lấy phòng từ roomTypeName trong booking
        if (booking.getRoomTypeName() != null && !booking.getRoomTypeName().isEmpty()) {
            List<String> roomTypeNames = parseRoomTypeNames(booking.getRoomTypeName());

            // Cách 1: Lấy từng loại phòng và gộp vào
            for (String typeName : roomTypeNames) {
                int roomTypeId = getRoomTypeIdByName(typeName);
                if (roomTypeId > 0) {
                    List<Room> roomsOfType = getSimpleAvailableRoomsByType(roomTypeId, booking.getBranchId());
                    availableRooms.addAll(roomsOfType);
                }
            }
            // Cách 2: Nếu không tìm thấy phòng, thử dùng câu truy vấn IN
            if (availableRooms.isEmpty()) {
                availableRooms = getAvailableRoomsByTypeNames(roomTypeNames, booking.getBranchId());
            }
            // Phương án dự phòng: Lấy tất cả phòng bất kể trạng thái
            if (availableRooms.isEmpty()) {
                availableRooms = getAllRoomsByTypeNames(roomTypeNames, booking.getBranchId());
            }
        } else {
            // Phương án dự phòng: Lấy theo roomTypeIds từ BookingRoomType
            List<Integer> roomTypeIds = getRoomTypeIdsByBookingId(booking.getId());

            if (!roomTypeIds.isEmpty()) {
                for (Integer roomTypeId : roomTypeIds) {
                    List<Room> roomsOfType = getSimpleAvailableRoomsByType(roomTypeId, booking.getBranchId());
                    availableRooms.addAll(roomsOfType);
                }
            }
        }
        return availableRooms;
    }

    /**
     * PHƯƠNG PHÁP KHẨN CẤP: Lấy tất cả phòng cho roomType mà không quan tâm đến
     * trạng thái Sử dụng khi không tìm thấy phòng nào khác
     */
    public List<Room> getAllRoomsForBookingEmergency(Booking booking) {
        List<Room> rooms = new ArrayList<>();

        if (booking == null) {
            return rooms;
        }


        try {
            // Truy vấn cực kỳ rộng để lấy TẤT CẢ phòng có trong hệ thống
            String sql = "SELECT r.*, rt.name as roomTypeName "
                    + "FROM Room r "
                    + "JOIN RoomType rt ON r.room_type_id = rt.id "
                    + "WHERE r.branch_id = ? AND r.is_deleted = 0";

            // Khi chạy phương pháp khẩn cấp, không lọc theo room_type_id để đảm bảo có kết quả
            // Tuy nhiên, chúng ta sẽ ưu tiên phòng phù hợp sau khi lấy kết quả
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, booking.getBranchId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Room room = new Room();
                        room.setId(rs.getInt("id"));
                        room.setRoomNumber(rs.getString("room_number"));
                        room.setRoomTypeId(rs.getInt("room_type_id"));
                        room.setBranchId(rs.getInt("branch_id"));
                        room.setStatus("Available"); // Ghi đè thành Available để hiển thị
                        room.setImageUrl(rs.getString("image_url"));
                        room.setRoomTypeName(rs.getString("roomTypeName"));

                        rooms.add(room);
                    }
                }
            }


            // Sắp xếp phòng theo ưu tiên:
            // 1. Phòng có roomTypeName khớp với booking
            if (booking.getRoomTypeName() != null && !booking.getRoomTypeName().isEmpty()) {
                List<String> roomTypeNames = parseRoomTypeNames(booking.getRoomTypeName());
                rooms.sort((r1, r2) -> {
                    boolean r1Matches = roomTypeNames.contains(r1.getRoomTypeName());
                    boolean r2Matches = roomTypeNames.contains(r2.getRoomTypeName());

                    if (r1Matches && !r2Matches) {
                        return -1;  // r1 lên đầu
                    }
                    if (!r1Matches && r2Matches) {
                        return 1;   // r2 lên đầu
                    }
                    return 0;  // giữ nguyên
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getRoomsByIds(List<Integer> roomIds) {
        if (roomIds == null || roomIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Tạo câu SQL với placeholder cho IN clause
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.id, r.room_number, r.status, r.branch_id, ")
                .append("rt.name as room_type_name, rt.id as room_type_id ")
                .append("FROM Room r ")
                .append("INNER JOIN RoomType rt ON r.room_type_id = rt.id ")
                .append("WHERE r.id IN (");

        // Thêm placeholders
        for (int i = 0; i < roomIds.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(") ORDER BY r.room_number");

        List<Room> rooms = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql.toString());

            // Set parameters
            for (int i = 0; i < roomIds.size(); i++) {
                ps.setInt(i + 1, roomIds.get(i));
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setStatus(rs.getString("status"));
                room.setBranchId(rs.getInt("branch_id"));
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeId(rs.getInt("room_type_id"));

                rooms.add(room);
            }


        } catch (SQLException e) {
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
                System.err.println("Error closing resources in getRoomsByIds: " + e.getMessage());
            }
        }

        return rooms;
    }

    /**
     * Phiên bản overload cho array String[]
     *
     */
    public List<Room> getRoomsByIds(String[] roomIdStrings) {
        if (roomIdStrings == null || roomIdStrings.length == 0) {
            return new ArrayList<>();
        }

        try {
            List<Integer> roomIds = new ArrayList<>();
            for (String roomIdStr : roomIdStrings) {
                roomIds.add(Integer.parseInt(roomIdStr.trim()));
            }
            return getRoomsByIds(roomIds);
        } catch (NumberFormatException e) {
            System.err.println("Invalid room ID format in array: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean removeRoomAssignment(int bookingId, int roomId) {
        String sql = "DELETE FROM RoomAssignment WHERE booking_id = ? AND room_id = ?";

        try {
            // Start transaction
            connection.setAutoCommit(false);

            // Check if the assignment exists
            String checkSql = "SELECT COUNT(*) FROM RoomAssignment WHERE booking_id = ? AND room_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, bookingId);
            checkStmt.setInt(2, roomId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                connection.rollback();
                return false; // Assignment doesn't exist
            }

            // Delete assignment from RoomAssignment table
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.setInt(2, roomId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                connection.rollback();
                return false;
            }

            // Update room status back to Available
            String updateRoomSql = "UPDATE Room SET status = 'Available' WHERE id = ?";
            PreparedStatement updatePs = connection.prepareStatement(updateRoomSql);
            updatePs.setInt(1, roomId);
            updatePs.executeUpdate();

            connection.commit();
            System.out.println("Successfully removed room " + roomId + " assignment from booking " + bookingId);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
            }
            System.err.println("Error removing room assignment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }

    }

    /**
     * Đếm số lượng phòng available theo room type
     */
    public int getAvailableRoomCountByRoomType(int roomTypeId) {
        String sql = "SELECT COUNT(*) FROM Room WHERE room_type_id = ? AND status = 'Available' AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting available rooms: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    /**
     * Cập nhật trạng thái phòng sau checkout
     */
    public boolean updateRoomStatusAfterCheckout(int bookingId, String newStatus) {
        try {
            String sql = "UPDATE Room SET status = ? WHERE id IN "
                    + "(SELECT room_id FROM RoomAssignment WHERE booking_id = ?)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, newStatus); // "Available"
                ps.setInt(2, bookingId);

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getOccupancyRate(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double occupancyRate = 0;

        String sqlRoomCount = "SELECT COUNT(*) AS room_count FROM Room "
                + "WHERE branch_id = ? AND status != 'Maintenance' AND is_deleted = 0";

        String sqlBookingRoomType = "SELECT brt.quantity, b.check_in, b.check_out "
                + "FROM BookingRoomType brt "
                + "JOIN Booking b ON brt.booking_id = b.id "
                + "WHERE b.branch_id = ? "
                + "AND b.is_deleted = 0 "
                + "AND b.status IN ('Paid', 'CheckedIn', 'CheckedOut', 'Completed') "
                + "AND (b.check_out >= ? AND b.check_in <= ?)";

        String sqlRoomAssignment = "SELECT ra.room_id, b.check_in, b.check_out "
                + "FROM RoomAssignment ra "
                + "JOIN Booking b ON ra.booking_id = b.id "
                + "WHERE b.branch_id = ? "
                + "AND b.is_deleted = 0 "
                + "AND b.status IN ('Paid', 'CheckedIn', 'CheckedOut', 'Completed') "
                + "AND (b.check_out >= ? AND b.check_in <= ?)";

        try {
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            // 1. Lấy số phòng khả dụng
            int roomCount = 0;
            try (PreparedStatement st = connection.prepareStatement(sqlRoomCount)) {
                st.setInt(1, branchId);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    roomCount = rs.getInt("room_count");
                }
            }

            if (roomCount == 0) {
                return 0; // Không có phòng, Occupancy là 0%
            }

            long daysInPeriod = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
            long totalNightsAvailable = roomCount * daysInPeriod;

            long totalNightsSold = 0;

            // 2. Tính số đêm từ BookingRoomType (khách đặt trước)
            try (PreparedStatement st = connection.prepareStatement(sqlBookingRoomType)) {
                st.setInt(1, branchId);
                st.setDate(2, java.sql.Date.valueOf(fromDate));
                st.setDate(3, java.sql.Date.valueOf(toDate));

                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    int quantity = rs.getInt("quantity");
                    LocalDate checkIn = rs.getTimestamp("check_in").toLocalDateTime().toLocalDate();
                    LocalDate checkOut = rs.getTimestamp("check_out").toLocalDateTime().toLocalDate();

                    LocalDate actualStart = checkIn.isBefore(fromDate) ? fromDate : checkIn;
                    LocalDate actualEnd = checkOut.isAfter(toDate) ? toDate : checkOut;

                    if (!actualStart.isAfter(actualEnd)) {
                        long nights = ChronoUnit.DAYS.between(actualStart, actualEnd);
                        totalNightsSold += nights * quantity;
                    }
                }
            }

            // 3. Tính số đêm từ RoomAssignment (khách walk-in)
            try (PreparedStatement st = connection.prepareStatement(sqlRoomAssignment)) {
                st.setInt(1, branchId);
                st.setDate(2, java.sql.Date.valueOf(fromDate));
                st.setDate(3, java.sql.Date.valueOf(toDate));

                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    LocalDate checkIn = rs.getTimestamp("check_in").toLocalDateTime().toLocalDate();
                    LocalDate checkOut = rs.getTimestamp("check_out").toLocalDateTime().toLocalDate();

                    LocalDate actualStart = checkIn.isBefore(fromDate) ? fromDate : checkIn;
                    LocalDate actualEnd = checkOut.isAfter(toDate) ? toDate : checkOut;

                    if (!actualStart.isAfter(actualEnd)) {
                        long nights = ChronoUnit.DAYS.between(actualStart, actualEnd);
                        totalNightsSold += nights; // walk-in thì tính từng phòng, không cần nhân quantity
                    }
                }
            }

            // 4. Tính occupancy rate
            occupancyRate = (double) totalNightsSold / totalNightsAvailable * 100;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return occupancyRate;
    }
// Thêm các phương thức sau vào class RoomDAO

    /**
     * Phân trang phòng theo branch với filter status
     */
    public List<Room> pagingRoomByBranchWithStatus(int branchId, String status, int page, int pageSize) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Room WHERE branch_id = ?";

        if (status != null && !status.trim().isEmpty()) {
            sql += " AND status = ?";
        }

        sql += " ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int paramIndex = 1;
            ps.setInt(paramIndex++, branchId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }

            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số phòng theo branch với filter status
     */
    public int countRoomsByBranchWithStatus(int branchId, String status) {
        String sql = "SELECT COUNT(*) FROM Room WHERE branch_id = ?";

        if (status != null && !status.trim().isEmpty()) {
            sql += " AND status = ?";
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int paramIndex = 1;
            ps.setInt(paramIndex++, branchId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tìm kiếm phòng theo roomtype name, branch với filter status và phân trang
     */
    public List<Room> searchRoomsByRoomTypeNameAndBranchWithStatus(String roomTypeNameKeyword, int branchId, String status, int page, int pageSize) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.* FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name LIKE ? AND r.branch_id = ?";

        if (status != null && !status.trim().isEmpty()) {
            sql += " AND r.status = ?";
        }

        sql += " ORDER BY r.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int paramIndex = 1;
            ps.setString(paramIndex++, "%" + roomTypeNameKeyword + "%");
            ps.setInt(paramIndex++, branchId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }

            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng phòng theo roomtype name, branch với filter status
     */
    public int countRoomsByRoomTypeNameAndBranchWithStatus(String roomTypeNameKeyword, int branchId, String status) {
        String sql = "SELECT COUNT(*) FROM Room r JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE rt.name LIKE ? AND r.branch_id = ?";

        if (status != null && !status.trim().isEmpty()) {
            sql += " AND r.status = ?";
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int paramIndex = 1;
            ps.setString(paramIndex++, "%" + roomTypeNameKeyword + "%");
            ps.setInt(paramIndex++, branchId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm số lượng phòng theo từng room type và status
     */
    public Map<Integer, Integer> getRoomTypeCountByBranchAndStatus(int branchId, String status) {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT room_type_id, COUNT(*) as cnt FROM Room WHERE branch_id = ?";

        if (status != null && !status.trim().isEmpty()) {
            sql += " AND status = ?";
        }

        sql += " GROUP BY room_type_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, branchId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt("room_type_id"), rs.getInt("cnt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Room> getAllRoomStatusForManager(int branchId, int page, int pageSize, LocalDateTime date) {
        List<Room> rooms = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        Map<Integer, List<Room>> roomsByRoomType = new HashMap<>();

        String sql = "SELECT r.id AS room_id, r.room_number, r.status, r.branch_id, r.room_type_id, r.image_url AS room_image_url, "
                + "rt.id AS roomtype_id, rt.name, rt.description, rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url AS roomtype_image_url "
                + "FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.is_deleted = 0 AND rt.is_deleted = 0 AND r.branch_id = ? "
                + "ORDER BY r.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RoomType rt = new RoomType(
                        rs.getInt("roomtype_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("roomtype_image_url")
                );
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"), // tạm thời giữ nguyên, tí nữa sẽ update lại
                        rs.getString("room_image_url")
                );
                room.setRoomType(rt);

                rooms.add(room);

                // Phân loại theo roomtype
                roomsByRoomType.computeIfAbsent(rt.getRoomTypeID(), k -> new ArrayList<>()).add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in getAllRoomStatusForManager: " + e.getMessage());
            return rooms; // return empty nếu lỗi
        }

        LocalDateTime startOfDay = date;
        LocalDateTime endOfDay = date.with(LocalTime.MAX);

        // Map phòng đang Occupied theo RoomAssignment
        try {
            String assignmentSql = "SELECT ra.room_id "
                    + "FROM RoomAssignment ra "
                    + "JOIN Booking b ON ra.booking_id = b.id "
                    + "WHERE b.status = 'CheckedIn' "
                    + "AND b.check_in <= ? AND b.check_out >= ?";

            try (PreparedStatement ps = connection.prepareStatement(assignmentSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(endOfDay));
                ps.setTimestamp(2, Timestamp.valueOf(startOfDay));
                ResultSet rs = ps.executeQuery();
                Set<Integer> occupiedRoomIds = new HashSet<>();
                while (rs.next()) {
                    occupiedRoomIds.add(rs.getInt("room_id"));
                }

                // Cập nhật trạng thái Occupied trước
                for (Room room : rooms) {
                    if (occupiedRoomIds.contains(room.getId()) && !room.getStatus().equals("Maintenance")) {
                        room.setStatus("Occupied");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Map các phòng cần set "Booked" tạm thời
        try {
            String bookingSql = "SELECT brt.room_type_id, SUM(brt.quantity) AS total_booked "
                    + "FROM Booking b "
                    + "JOIN BookingRoomType brt ON b.id = brt.booking_id "
                    + "WHERE b.status = 'Paid' AND b.is_deleted = 0 "
                    + "AND b.branch_id = ? "
                    + "AND b.check_in <= ? AND b.check_out >= ? "
                    + "GROUP BY brt.room_type_id";

            try (PreparedStatement ps = connection.prepareStatement(bookingSql)) {
                ps.setInt(1, branchId);
                ps.setTimestamp(2, Timestamp.valueOf(endOfDay));
                ps.setTimestamp(3, Timestamp.valueOf(startOfDay));
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int roomTypeId = rs.getInt("room_type_id");
                    int totalBooked = rs.getInt("total_booked");

                    List<Room> roomList = roomsByRoomType.getOrDefault(roomTypeId, new ArrayList<>());

                    int count = 0;
                    for (Room room : roomList) {
                        if (count >= totalBooked) {
                            break;
                        }

                        if (room.getStatus().equals("Available")) { // chỉ gán nếu đang available
                            room.setStatus("Booked");
                            count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public int getTotalRoomForManager(int branchId) {
        String sql = "SELECT COUNT(*) AS total FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.is_deleted = 0 AND rt.is_deleted = 0 AND r.branch_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in getTotalRoomForManager: " + e.getMessage());
        }

        return 0; // Trả về 0 nếu có lỗi
    }

    // Đếm tổng số phòng theo RoomType và Branch (không tính phòng đã xóa)
    public int getTotalRoomsByRoomTypeAndBranch(int branchId, int roomTypeId) {
        String sql = "SELECT COUNT(*) AS total FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.is_deleted = 0 AND rt.is_deleted = 0 "
                + "AND r.branch_id = ? AND r.room_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, roomTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

// Đếm số phòng đã được đặt (booked) cho RoomType trong khoảng ngày (dùng Timestamp)
   public int getBookedQuantityByRoomTypeAndDateRange(int branchId, int roomTypeId, java.sql.Date checkInDate, java.sql.Date checkOutDate) {
    String sql = "SELECT COUNT(*) AS booked_count FROM RoomAssignment ra "
            + "JOIN Room r ON ra.room_id = r.id "
            + "JOIN Booking b ON ra.booking_id = b.id "
            + "WHERE r.branch_id = ? AND r.room_type_id = ? "
            + "AND b.status IN ('Paid', 'CheckedIn', 'CheckedOut', 'Completed') "
            + "AND NOT (b.check_out <= ? OR b.check_in >= ?)";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, branchId);
        ps.setInt(2, roomTypeId);
        ps.setDate(3, checkInDate);   // <-- chỉ truyền ngày
        ps.setDate(4, checkOutDate);  // <-- chỉ truyền ngày
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("booked_count");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

// Đếm số phòng đang trạng thái CheckedIn cho RoomType (nếu không chọn ngày)
    public int getActiveBookedQuantityByRoomType(int branchId, int roomTypeId) {
        String sql = "SELECT COUNT(*) AS booked_count FROM RoomAssignment ra "
                + "JOIN Room r ON ra.room_id = r.id "
                + "JOIN Booking b ON ra.booking_id = b.id "
                + "WHERE r.branch_id = ? AND r.room_type_id = ? "
                + "AND b.status IN ('CheckedIn')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, roomTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("booked_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

// Lấy danh sách phòng available theo branch, roomType và khoảng ngày (dùng Timestamp)
  public List<Room> getAvailableRoomsByBranchRoomTypeAndDateRange(int branchId, int roomTypeId, java.sql.Date checkInDate, java.sql.Date checkOutDate) {
    List<Room> availableRooms = new ArrayList<>();
    String sql = "SELECT r.* FROM Room r "
            + "WHERE r.branch_id = ? AND r.room_type_id = ? AND r.status = 'Available' AND r.is_deleted = 0 "
            + "AND r.id NOT IN ( "
            + "    SELECT ra.room_id FROM RoomAssignment ra "
            + "    JOIN Booking b ON ra.booking_id = b.id "
            + "    WHERE b.branch_id = ? "
            + "    AND b.status IN ('Paid', 'CheckedIn', 'CheckedOut', 'Completed') "
            + "    AND NOT (b.check_out <= ? OR b.check_in >= ?) "
            + ")";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, branchId);
        ps.setInt(2, roomTypeId);
        ps.setInt(3, branchId);
        ps.setDate(4, checkInDate);   // chỉ truyền ngày
        ps.setDate(5, checkOutDate);  // chỉ truyền ngày
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Room room = new Room(
                    rs.getInt("id"),
                    rs.getString("room_number"),
                    rs.getInt("branch_id"),
                    rs.getInt("room_type_id"),
                    rs.getString("status"),
                    rs.getString("image_url")
            );
            availableRooms.add(room);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return availableRooms;
}
// Lấy danh sách phòng available theo branch & roomType (không quan tâm ngày)
    public List<Room> getAvailableRoomsByBranchAndRoomType(int branchId, int roomTypeId) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Room WHERE branch_id = ? AND room_type_id = ? AND status = 'Available' AND is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, roomTypeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("branch_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("image_url")
                );
                list.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
