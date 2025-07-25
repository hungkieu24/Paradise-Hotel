/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Branch;
import Model.RoomType;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DBcontext.DBContext;
import Model.Room;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hungk
 */
public class RoomTypeDAO extends DBContext {

    //hoang create: lay all room type
    public List<RoomType> getAllRoomType() {
        List<RoomType> roomTypeList = new ArrayList<>();
        String sql = "SELECT * FROM RoomType WHERE is_deleted = 0";
        BranchDAO branchDAO = new BranchDAO();
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                RoomType roomtype = new RoomType(
                        rs.getInt("id"), // roomTypeID
                        rs.getString("name"), // name
                        rs.getString("description"), // description
                        rs.getDouble("base_price"), // base_price
                        rs.getInt("capacity_adult"), // capacityAdult
                        rs.getInt("capacity_child"), // capacityChild
                        rs.getString("image_url"), // image_url
                        rs.getInt("branch_id"), // branchId
                        rs.getBoolean("is_deleted") // isDeleted
                );
                Branch branch = branchDAO.getBranchByRoomTypeId(roomtype.getRoomTypeID());
                roomtype.setBranch(branch);
                roomTypeList.add(roomtype);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomTypeList;
    }

    // hoang: lay theo khoang gia
    public List<RoomType> getRoomTypesByPriceRange(double minPrice, double maxPrice) {
        List<RoomType> roomTypeList = new ArrayList<>();
        String sql = "SELECT * FROM RoomType WHERE base_price BETWEEN ? AND ? AND is_deleted = 0";
        BranchDAO branchDAO = new BranchDAO();
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setDouble(1, minPrice);
            st.setDouble(2, maxPrice);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    RoomType roomType = new RoomType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("image_url"),
                            rs.getInt("branch_id"),
                            rs.getBoolean("is_deleted")
                    );
                    Branch branch = branchDAO.getBranchByRoomTypeId(roomType.getRoomTypeID());
                    roomType.setBranch(branch);
                    roomTypeList.add(roomType);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching room types by price range.");
            e.printStackTrace();
        }

        return roomTypeList;
    }

    // hoang: lay theo khoang ngay
    public List<RoomType> getAvailableRoomTypesByDate(LocalDate checkIn, LocalDate checkOut) {
        List<RoomType> availableRoomTypes = new ArrayList<>();
        BranchDAO branchDAO = new BranchDAO();

        // Chuyển LocalDate -> Timestamp (bắt đầu ngày)
        Timestamp checkInTimestamp = Timestamp.valueOf(checkIn.atStartOfDay());
        Timestamp checkOutTimestamp = Timestamp.valueOf(checkOut.atStartOfDay());

        String sql = """
    SELECT DISTINCT rt.*
      FROM RoomType rt
      WHERE rt.is_deleted = 0
        AND EXISTS (
            SELECT 1
            FROM Room r
            WHERE r.room_type_id = rt.id
              AND r.status NOT IN ('Maintenance', 'Booked')
              AND r.id NOT IN (
                  SELECT ra.room_id
                  FROM RoomAssignment ra
                  JOIN Booking b ON ra.booking_id = b.id
                  WHERE b.status NOT IN ('Cancelled', 'Locked')
                    AND b.check_in < ?
                    AND b.check_out > ?
              )
        )
""";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setTimestamp(1, checkOutTimestamp); // b.check_in < checkOut
            st.setTimestamp(2, checkInTimestamp);  // b.check_out > checkIn

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                RoomType roomType = new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("image_url"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                // Lấy thông tin chi nhánh
                Branch branch = branchDAO.getBranchByRoomTypeId(roomType.getRoomTypeID());
                roomType.setBranch(branch);
                availableRoomTypes.add(roomType);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available room types by date.");
            e.printStackTrace();
        }

        return availableRoomTypes;
    }

    // hoang: lay theo khoang gia va ngay
    public List<RoomType> getAvailableRoomTypesByPriceAndDate(double minPrice, double maxPrice, LocalDate checkIn, LocalDate checkOut) {
        List<RoomType> availableRoomTypes = new ArrayList<>();
        BranchDAO branchDAO = new BranchDAO();

        // Chuyển LocalDate -> Timestamp để nhất quán
        Timestamp checkInTimestamp = Timestamp.valueOf(checkIn.atStartOfDay());
        Timestamp checkOutTimestamp = Timestamp.valueOf(checkOut.atStartOfDay());

        String sql = """
    SELECT DISTINCT rt.id, rt.name, rt.description, rt.base_price,
                    rt.capacity_adult, rt.capacity_child,
                    rt.image_url, rt.branch_id, rt.is_deleted
    FROM RoomType rt
    WHERE rt.is_deleted = 0
      AND rt.base_price BETWEEN ? AND ?
      AND EXISTS (
          SELECT 1
          FROM Room r
          WHERE r.room_type_id = rt.id
            AND r.status NOT IN ('Maintenance', 'Booked')
            AND r.id NOT IN (
                SELECT ra.room_id
                FROM RoomAssignment ra
                JOIN Booking b ON ra.booking_id = b.id
                WHERE b.status NOT IN ('Cancelled', 'Locked')
                  AND b.check_in < ?
                  AND b.check_out > ?
            )
      )
""";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setDouble(1, minPrice);
            st.setDouble(2, maxPrice);
            st.setTimestamp(3, checkOutTimestamp); // b.check_in < checkOut
            st.setTimestamp(4, checkInTimestamp);  // b.check_out > checkIn

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                RoomType roomType = new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("image_url"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                Branch branch = branchDAO.getBranchByRoomTypeId(roomType.getRoomTypeID());
                roomType.setBranch(branch);
                availableRoomTypes.add(roomType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableRoomTypes;
    }

    // hoang: lay theo id
    public RoomType getRoomTypeById(int id) {
        RoomType roomType = null;
        String sql = "SELECT * FROM RoomType WHERE id = ? AND is_deleted = 0";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                roomType = new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("image_url"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Hoặc dùng logger nếu có
        }

        return roomType;
    }

    // hoang lay 3 phong gan gia nhat
    public List<RoomType> getSimilarRoomTypes(int targetId) {
        List<RoomType> similarRoomTypes = new ArrayList<>();
        RoomType targetRoom = getRoomTypeById(targetId); // đảm bảo hàm này đã được sửa đúng

        if (targetRoom == null) {
            return similarRoomTypes;
        }

        String sql = "SELECT * FROM RoomType WHERE id != ? AND is_deleted = 0"; // bỏ qua phòng đã bị xóa mềm
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, targetId);
            ResultSet rs = st.executeQuery();

            List<RoomType> allOtherRooms = new ArrayList<>();
            while (rs.next()) {
                RoomType room = new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("image_url"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                allOtherRooms.add(room);
            }

            // Sắp xếp theo khoảng cách giá
            allOtherRooms.sort(Comparator.comparingDouble(r -> Math.abs(r.getBase_price() - targetRoom.getBase_price())));

            // Lấy 3 phòng gần giá nhất
            for (int i = 0; i < Math.min(3, allOtherRooms.size()); i++) {
                similarRoomTypes.add(allOtherRooms.get(i));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return similarRoomTypes;
    }

    public List<RoomType> searchAvailableRoomTypes(LocalDate checkIn, LocalDate checkOut, int guests, int branchId) {
        List<RoomType> availableRoomTypes = new ArrayList<>();

        String sql = """
            SELECT DISTINCT rt.id, rt.name, rt.description, rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url
            FROM RoomType rt
            WHERE rt.capacity >= ?
              AND EXISTS (
                  SELECT 1
                  FROM Room r
                  WHERE r.room_type_id = rt.id
                    AND r.branch_id = ?
                    AND r.status NOT IN ('Maintenance', 'Locked')
                    AND r.id NOT IN (
                        SELECT br.room_id
                        FROM BookingRoom br
                        JOIN Booking b ON br.booking_id = b.id
                        WHERE b.status NOT IN ('Cancelled', 'Locked')
                          AND b.check_in < ?
                          AND b.check_out > ?
                    )
              )
            """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, guests);
            st.setInt(2, branchId);
            st.setDate(3, Date.valueOf(checkOut));
            st.setDate(4, Date.valueOf(checkIn));

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                RoomType roomType = new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("image_url")
                );
                availableRoomTypes.add(roomType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableRoomTypes;
    }

    public Map<Integer, String> getRoomTypeMap() {
        Map<Integer, String> map = new HashMap<>();
        String sql = "SELECT id, name FROM RoomType";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public void updateRoomType(int roomtypeId, double basePrice, int capacity, String description) {
        String sql = "update RoomType set base_price = ? , capacity =?, description =? where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, basePrice);
            ps.setInt(2, capacity);
            ps.setString(3, description);
            ps.setInt(4, roomtypeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Double> getRoomTypePriceMap() {
        Map<Integer, Double> map = new HashMap<>();
        String sql = "SELECT id, base_price FROM RoomType";
        try (
                PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                double price = rs.getDouble("base_price");
                map.put(id, price);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // Hung: lay theo branch id
    public List<RoomType> getRoomTypesByBranchId(int branchId) {
        List<RoomType> roomTypeList = new ArrayList<>();
        String sql = "SELECT * FROM RoomType WHERE branch_id = ? AND is_deleted = 0";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, branchId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                RoomType roomtype = new RoomType(
                        rs.getInt("id"), // roomTypeID
                        rs.getString("name"), // name
                        rs.getString("description"), // description
                        rs.getDouble("base_price"), // base_price
                        rs.getInt("capacity_adult"), // capacityAdult
                        rs.getInt("capacity_child"), // capacityChild
                        rs.getString("image_url"), // image_url
                        rs.getInt("branch_id"), // branchId
                        rs.getBoolean("is_deleted") // isDeleted
                );
                roomTypeList.add(roomtype);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomTypeList;
    }

    // Hung: insert roomtype
    public boolean insertRoomType(RoomType roomType) {
        String sql = "INSERT INTO RoomType (name, description, base_price, capacity_adult, capacity_child, image_url, branch_id, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, roomType.getName());
            st.setString(2, roomType.getDescription());
            st.setDouble(3, roomType.getBase_price());
            st.setInt(4, roomType.getCapacity_adult());
            st.setInt(5, roomType.getCapacity_child());
            st.setString(6, roomType.getImage_url());
            st.setInt(7, roomType.getBranchId());

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Có thể thay bằng ghi log nếu bạn dùng logger
        }

        return false;
    }

    // Hung: update roomtype
    public boolean updateRoomType(RoomType roomType) {
        String sql = "UPDATE RoomType SET "
                + "name = ?, "
                + "description = ?, "
                + "base_price = ?, "
                + "capacity_adult = ?, "
                + "capacity_child = ?, "
                + "image_url = ?, "
                + "branch_id = ? "
                + "WHERE id = ? AND is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, roomType.getName());
            st.setString(2, roomType.getDescription());
            st.setDouble(3, roomType.getBase_price());
            st.setInt(4, roomType.getCapacity_adult());
            st.setInt(5, roomType.getCapacity_child());
            st.setString(6, roomType.getImage_url());
            st.setInt(7, roomType.getBranchId());
            st.setInt(8, roomType.getRoomTypeID()); // WHERE id = ?

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log nếu cần
        }

        return false;
    }

    // Hung: insert roomtype + room Amenity
    public boolean insertRoomTypeWithAmenities(RoomType roomType, List<Integer> amenityIds) {
        String insertRoomTypeSql = "INSERT INTO RoomType (name, description, base_price, capacity_adult, capacity_child, image_url, branch_id, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        String insertAmenitySql = "INSERT INTO RoomAmenity (room_type_id, amenity_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement roomTypeStmt = null;
        PreparedStatement amenityStmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = connection;
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Insert RoomType
            roomTypeStmt = conn.prepareStatement(insertRoomTypeSql, Statement.RETURN_GENERATED_KEYS);
            roomTypeStmt.setString(1, roomType.getName());
            roomTypeStmt.setString(2, roomType.getDescription());
            roomTypeStmt.setDouble(3, roomType.getBase_price());
            roomTypeStmt.setInt(4, roomType.getCapacity_adult());
            roomTypeStmt.setInt(5, roomType.getCapacity_child());
            roomTypeStmt.setString(6, roomType.getImage_url());
            roomTypeStmt.setInt(7, roomType.getBranchId());

            int affectedRows = roomTypeStmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Lấy ID vừa insert
            generatedKeys = roomTypeStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                conn.rollback();
                return false;
            }

            int roomTypeId = generatedKeys.getInt(1);

            // 2. Insert các Amenity liên kết
            amenityStmt = conn.prepareStatement(insertAmenitySql);
            for (Integer amenityId : amenityIds) {
                amenityStmt.setInt(1, roomTypeId);
                amenityStmt.setInt(2, amenityId);
                amenityStmt.addBatch();
            }

            int[] results = amenityStmt.executeBatch();
            for (int result : results) {
                if (result == Statement.EXECUTE_FAILED) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (roomTypeStmt != null) {
                    roomTypeStmt.close();
                }
                if (amenityStmt != null) {
                    amenityStmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset trạng thái
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Hung: update roomtype + room Amenity
    public boolean updateRoomTypeWithAmenities(RoomType roomType, List<Integer> amenityIds) {
        String updateRoomTypeSql = "UPDATE RoomType SET "
                + "name = ?, "
                + "description = ?, "
                + "base_price = ?, "
                + "capacity_adult = ?, "
                + "capacity_child = ?, "
                + "image_url = ?, "
                + "branch_id = ? "
                + "WHERE id = ? AND is_deleted = 0";

        String deleteAmenitiesSql = "DELETE FROM RoomAmenity WHERE room_type_id = ?";
        String insertAmenitySql = "INSERT INTO RoomAmenity (room_type_id, amenity_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            conn = connection;
            conn.setAutoCommit(false); // bắt đầu transaction

            // 1. Cập nhật RoomType
            updateStmt = conn.prepareStatement(updateRoomTypeSql);
            updateStmt.setString(1, roomType.getName());
            updateStmt.setString(2, roomType.getDescription());
            updateStmt.setDouble(3, roomType.getBase_price());
            updateStmt.setInt(4, roomType.getCapacity_adult());
            updateStmt.setInt(5, roomType.getCapacity_child());
            updateStmt.setString(6, roomType.getImage_url());
            updateStmt.setInt(7, roomType.getBranchId());
            updateStmt.setInt(8, roomType.getRoomTypeID());

            int affected = updateStmt.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }

            // 2. Xóa amenity cũ
            deleteStmt = conn.prepareStatement(deleteAmenitiesSql);
            deleteStmt.setInt(1, roomType.getRoomTypeID());
            deleteStmt.executeUpdate();

            // 3. Insert lại các amenity mới
            insertStmt = conn.prepareStatement(insertAmenitySql);
            for (int amenityId : amenityIds) {
                insertStmt.setInt(1, roomType.getRoomTypeID());
                insertStmt.setInt(2, amenityId);
                insertStmt.addBatch();
            }

            int[] insertResults = insertStmt.executeBatch();
            for (int result : insertResults) {
                if (result == Statement.EXECUTE_FAILED) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (updateStmt != null) {
                    updateStmt.close();
                }
                if (deleteStmt != null) {
                    deleteStmt.close();
                }
                if (insertStmt != null) {
                    insertStmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // Hung: Delete roomtype + room Amenity
    public boolean deleteRoomType(int roomTypeId) {
        String deleteRoomAmenitySql = "DELETE FROM RoomAmenity WHERE room_type_id = ?";
        String softDeleteRoomTypeSql = "UPDATE RoomType SET is_deleted = 1 WHERE id = ?";

        Connection conn = null;
        PreparedStatement deleteAmenityStmt = null;
        PreparedStatement deleteRoomTypeStmt = null;

        try {
            conn = connection;
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Xóa bản ghi trong RoomAmenity
            deleteAmenityStmt = conn.prepareStatement(deleteRoomAmenitySql);
            deleteAmenityStmt.setInt(1, roomTypeId);
            deleteAmenityStmt.executeUpdate();

            // 2. Xóa mềm RoomType
            deleteRoomTypeStmt = conn.prepareStatement(softDeleteRoomTypeSql);
            deleteRoomTypeStmt.setInt(1, roomTypeId);
            int rowsAffected = deleteRoomTypeStmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (deleteAmenityStmt != null) {
                    deleteAmenityStmt.close();
                }
                if (deleteRoomTypeStmt != null) {
                    deleteRoomTypeStmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // Hung: Kiem tra 1 truong co ton tai khong
    public boolean isFieldExists(String fieldName, String value, Integer excludeId) {
        String sql = "SELECT 1 FROM RoomType WHERE " + fieldName + " = ?" + (excludeId != null ? " AND id != ?" : "");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Hung: search room type
    public List<RoomType> searchRoomTypes(String keyword, int branchId) {
        List<RoomType> roomTypeList = new ArrayList<>();
        String sql = "SELECT * FROM RoomType WHERE is_deleted = 0 AND branch_id = ? AND ("
                + "name LIKE ? OR "
                + "description LIKE ? OR "
                + "CAST(base_price AS NVARCHAR) LIKE ? OR "
                + "CAST(capacity_adult AS NVARCHAR) LIKE ? OR "
                + "CAST(capacity_child AS NVARCHAR) LIKE ? OR "
                + "image_url LIKE ?"
                + ")";

        BranchDAO branchDAO = new BranchDAO();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String wildcardKeyword = "%" + keyword + "%";
            st.setInt(1, branchId); // Điều kiện branch_id

            for (int i = 2; i <= 7; i++) {
                st.setString(i, wildcardKeyword);
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                RoomType roomtype = new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        rs.getInt("capacity_adult"),
                        rs.getInt("capacity_child"),
                        rs.getString("image_url"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );

                Branch branch = branchDAO.getBranchByRoomTypeId(roomtype.getRoomTypeID());
                roomtype.setBranch(branch);
                roomTypeList.add(roomtype);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // hoặc dùng logger
        }

        return roomTypeList;
    }

    //Hung: hàm phụ để search room ở hoompage theo checkin và check out date
    public int getAvailableRoomQuantity(int roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        int availableRooms = 0;

        String totalRoomSql = "SELECT COUNT(*) FROM Room "
                + "WHERE room_type_id = ? "
                + "AND is_deleted = 0 AND status != 'Maintenance'";

        String bookedRoomSql = "SELECT COALESCE(SUM(brt.quantity), 0) FROM BookingRoomType brt "
                + "JOIN Booking b ON brt.booking_id = b.id "
                + "WHERE brt.room_type_id = ? "
                + "AND b.status NOT IN ('Cancelled', 'Locked') "
                + "AND b.check_in < ? AND b.check_out > ?";

        try {
            // 1. Lấy tổng số phòng
            PreparedStatement ps1 = connection.prepareStatement(totalRoomSql);
            ps1.setInt(1, roomTypeId);

            ResultSet rs1 = ps1.executeQuery();
            int totalRooms = 0;
            if (rs1.next()) {
                totalRooms = rs1.getInt(1);
            }

            // 2. Lấy số phòng đã được book
            PreparedStatement ps2 = connection.prepareStatement(bookedRoomSql);
            ps2.setInt(1, roomTypeId);
            ps2.setDate(2, Date.valueOf(checkOutDate));
            ps2.setDate(3, Date.valueOf(checkInDate));

            ResultSet rs2 = ps2.executeQuery();
            int bookedRooms = 0;
            if (rs2.next()) {
                bookedRooms = rs2.getInt(1);
            }

            // 3. Tính số phòng còn trống
            availableRooms = totalRooms - bookedRooms;
            if (availableRooms < 0) {
                availableRooms = 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableRooms;
    }

    // Hung: hàm thực hiện search ở homepage
    public List<RoomType> searchAvailableRoomTypes(LocalDate checkInDate, LocalDate checkOutDate, int guests) {
        List<RoomType> roomTypeList = new ArrayList<>();

        String sql = "SELECT rt.id, rt.name, rt.description, rt.base_price, rt.capacity_adult, rt.capacity_child, "
                + "rt.image_url, rt.branch_id, hb.name AS branch_name "
                + "FROM RoomType rt "
                + "JOIN HotelBranch hb ON rt.branch_id = hb.id "
                + "WHERE rt.is_deleted = 0 "
                + "AND (rt.capacity_adult + rt.capacity_child) >= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, guests);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int roomTypeId = rs.getInt("id");

                // Gọi hàm kiểm tra số phòng trống
                int availableRooms = getAvailableRoomQuantity(roomTypeId, checkInDate, checkOutDate);

                if (availableRooms > 0) {
                    RoomType roomType = new RoomType(
                            roomTypeId,
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("image_url")
                    );

                    // Set Branch
                    Branch branch = new Branch();
                    branch.setId(rs.getInt("branch_id"));
                    branch.setName(rs.getString("branch_name"));
                    roomType.setBranch(branch);

                    // Lấy Amenity qua RoomAmenity
                    List<String> amenities = getAmenitiesByRoomTypeId(roomTypeId);
                    roomType.setAmenity(amenities);

                    roomTypeList.add(roomType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomTypeList;
    }

    public List<String> getAmenitiesByRoomTypeId(int roomTypeId) {
        List<String> amenities = new ArrayList<>();

        String sql = "SELECT a.name FROM RoomAmenity ra "
                + "JOIN Amenity a ON ra.amenity_id = a.id "
                + "WHERE ra.room_type_id = ? AND a.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                amenities.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amenities;
    }

    public int getTotalAvailableRooms(int roomTypeId, int branchId, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Room> rooms = new ArrayList<>();

        // 1. Lấy toàn bộ phòng theo roomTypeId và branchId
        String sql = "SELECT r.id, r.status FROM Room r "
                + "JOIN RoomType rt ON r.room_type_id = rt.id "
                + "WHERE r.is_deleted = 0 AND rt.is_deleted = 0 "
                + "AND r.branch_id = ? AND r.room_type_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setInt(2, roomTypeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setStatus(rs.getString("status"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        // 2. Xác định phòng đang "Occupied"
        try {
            String occupiedSql = "SELECT ra.room_id "
                    + "FROM RoomAssignment ra "
                    + "JOIN Booking b ON ra.booking_id = b.id "
                    + "WHERE b.status = 'CheckedIn' "
                    + "AND b.check_in <= ? AND b.check_out >= ?";

            try (PreparedStatement ps = connection.prepareStatement(occupiedSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(checkOut));
                ps.setTimestamp(2, Timestamp.valueOf(checkIn));
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

        // 3. Gán trạng thái "Booked"
        try {
            String bookedSql = "SELECT brt.room_type_id, SUM(brt.quantity) AS total_booked "
                    + "FROM Booking b "
                    + "JOIN BookingRoomType brt ON b.id = brt.booking_id "
                    + "WHERE b.status IN ('Pending', 'Paid') AND b.is_deleted = 0 "
                    + "AND b.branch_id = ? AND brt.room_type_id = ? "
                    + "AND b.check_in <= ? AND b.check_out >= ? "
                    + "GROUP BY brt.room_type_id";

            try (PreparedStatement ps = connection.prepareStatement(bookedSql)) {
                ps.setInt(1, branchId);
                ps.setInt(2, roomTypeId);
                ps.setTimestamp(3, Timestamp.valueOf(checkOut));
                ps.setTimestamp(4, Timestamp.valueOf(checkIn));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int totalBooked = rs.getInt("total_booked");
                    int count = 0;
                    for (Room room : rooms) {
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

        // 4. Đếm số phòng không phải Available
        long totalUnavailable = rooms.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Available"))
                .count();

        return (int) totalUnavailable;
    }

    public List<RoomType> searchAvailableRoomTypesV2(LocalDate checkIn, LocalDate checkOut, int guests, int branchId) {
        List<RoomType> availableRoomTypes = new ArrayList<>();

        // 1. Lấy danh sách tất cả room types trong chi nhánh
        String sql = """
        SELECT rt.id, rt.name, rt.description, rt.base_price, rt.capacity_adult, rt.capacity_child, rt.image_url
        FROM RoomType rt
        WHERE rt.is_deleted = 0
    """;
        LocalDateTime in = checkIn.atTime(LocalTime.MIN);
        LocalDateTime out = checkOut.atTime(LocalTime.MAX);

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int rtId = rs.getInt("id");
                int capacity = rs.getInt("capacity_adult") + rs.getInt("capacity_child");

                if (capacity < guests) {
                    continue; // Bỏ qua nếu không đủ sức chứa
                }
                // 2. Đếm số phòng AVAILABLE của loại này tại branch
               int totalRooms = getTotalAvailableRooms(rtId, branchId,in, out);
               

                if (totalRooms > 0) {
                    RoomType roomType = new RoomType(
                            rtId,
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("base_price"),
                            rs.getInt("capacity_adult"),
                            rs.getInt("capacity_child"),
                            rs.getString("image_url")
                    );
                    availableRoomTypes.add(roomType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableRoomTypes;
    }

}
