/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Branch;
import Model.RoomType;
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

/**
 *
 * @author hoang
 */
public class BranchDAO extends DBcontext.DBContext {

    public Branch getBranchByRoomTypeId(int roomTypeId) {
        String sql = "SELECT b.* "
                + "FROM HotelBranch b "
                + "JOIN RoomType rt ON b.id = rt.branch_id "
                + "WHERE rt.id = ? AND rt.is_deleted = 0 AND b.is_deleted = 0";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, roomTypeId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Branch branch = new Branch();
                    branch.setId(rs.getInt("id"));
                    branch.setName(rs.getString("name"));
                    branch.setAddress(rs.getString("address"));
                    branch.setPhone(rs.getString("phone"));
                    branch.setEmail(rs.getString("email"));
                    branch.setImageUrl(rs.getString("image_url"));
                    branch.setOwnerId(rs.getString("owner_id"));
                    branch.setManagerId(rs.getString("manager_id"));
                    branch.setDeleted(rs.getBoolean("is_deleted"));
                    return branch;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // hoặc logger.warn(...)
        }

        return null;
    }

    public Branch getBranchById(int branchId) {
        String sql = "SELECT * FROM HotelBranch WHERE id = ? AND is_deleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Branch branch = new Branch();
                    branch.setId(rs.getInt("id"));
                    branch.setName(rs.getString("name"));
                    branch.setAddress(rs.getString("address"));
                    branch.setPhone(rs.getString("phone"));
                    branch.setEmail(rs.getString("email"));
                    branch.setImageUrl(rs.getString("image_url"));
                    branch.setOwnerId(rs.getString("owner_id"));
                    branch.setManagerId(rs.getString("manager_id"));
                    branch.setDeleted(rs.getBoolean("is_deleted"));
                    return branch;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
