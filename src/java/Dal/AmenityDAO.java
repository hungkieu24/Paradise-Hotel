/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Amenity;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungk
 */
public class AmenityDAO extends DBcontext.DBContext {

    public List<Amenity> getAllAmenity() {
        List<Amenity> amenityList = new ArrayList<>();
        String sql = "SELECT * FROM Amenity WHERE is_deleted = 0";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Amenity amenity = new Amenity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                amenityList.add(amenity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amenityList;
    }

    public List<Amenity> getAllAmenityByBranchId(int branchId) {
        List<Amenity> amenityList = new ArrayList<>();
        String sql = "SELECT * FROM Amenity WHERE is_deleted = 0 AND branch_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Amenity amenity = new Amenity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                amenityList.add(amenity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amenityList;
    }

    public List<Integer> getAmenityIdsByRoomTypeId(int roomTypeId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT amenity_id FROM RoomAmenity WHERE room_type_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("amenity_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public Amenity getAmenityById(int id) {
        Amenity amenity = null;
        String sql = "SELECT * FROM Amenity WHERE id = ? AND is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                amenity = new Amenity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amenity;
    }

    public boolean insertAmenity(Amenity amenity) {
        String sql = "INSERT INTO Amenity (name, description, branch_id, is_deleted) VALUES (?, ?, ?, 0)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, amenity.getName());
            st.setString(2, amenity.getDescription());
            st.setInt(3, amenity.getBranchId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAmenity(Amenity amenity) {
        String sql = "UPDATE Amenity SET name = ?, description = ?, branch_id = ? WHERE id = ? AND is_deleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, amenity.getName());
            st.setString(2, amenity.getDescription());
            st.setInt(3, amenity.getBranchId());
            st.setInt(4, amenity.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAmenity(int amenityId) {
        String sql = "UPDATE Amenity SET is_deleted = 1 WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, amenityId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Amenity> searchAmenity(String keyword, int branchId) {
        List<Amenity> amenityList = new ArrayList<>();
        String sql = "SELECT * FROM Amenity "
                + "WHERE is_deleted = 0 "
                + "AND branch_id = ? "
                + "AND (name LIKE ? OR description LIKE ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            String searchPattern = "%" + keyword + "%";
            st.setString(2, searchPattern);
            st.setString(3, searchPattern);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Amenity amenity = new Amenity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                amenityList.add(amenity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amenityList;
    }

    public boolean isFieldExists(String fieldName, String value, Integer excludeId) {
        String sql = "SELECT 1 FROM Amenity WHERE " + fieldName + " = ?" + (excludeId != null ? " AND id != ?" : "");
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
}
