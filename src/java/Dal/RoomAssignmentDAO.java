package Dal;

import DBcontext.DBContext;
import Model.RoomAssignmentView;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RoomAssignmentDAO extends DBContext {
    
    /**
     * Get room assignments for a specific branch with filters
     */
    public List<RoomAssignmentView> getRoomAssignmentsByBranch(int branchId, Map<String, Object> filters, int page, int pageSize) {
        List<RoomAssignmentView> assignments = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ra.booking_id, ra.room_id, ra.assigned_at, ");
        sql.append("r.room_number, rt.name as room_type_name, ");
        sql.append("b.check_in, b.check_out, b.status as booking_status, ");
        sql.append("b.payment_status, b.total_price, b.note, ");
        sql.append("u.fullname as customer_name, u.email as customer_email, u.phonenumber as customer_phone, ");
        sql.append("hb.name as branch_name, hb.id as branch_id, ");
        sql.append("DATEDIFF(day, b.check_in, b.check_out) as nights, ");
        sql.append("ISNULL(lp.level, 'Member') as membership_level, ");
        sql.append("cb.fullname as created_by_name ");
        sql.append("FROM RoomAssignment ra ");
        sql.append("JOIN Room r ON ra.room_id = r.id ");
        sql.append("JOIN RoomType rt ON r.room_type_id = rt.id ");
        sql.append("JOIN Booking b ON ra.booking_id = b.id ");
        sql.append("JOIN UserAccount u ON b.user_id = u.id ");
        sql.append("JOIN HotelBranch hb ON b.branch_id = hb.id ");
        sql.append("LEFT JOIN UserAccount cb ON b.created_by = cb.id ");
        sql.append("LEFT JOIN LoyaltyPoint lp ON u.id = lp.user_id ");
        sql.append("WHERE b.branch_id = ? AND b.is_deleted = 0 AND r.is_deleted = 0 ");
        
        List<Object> params = new ArrayList<>();
        params.add(branchId);
        
        // Apply filters
        if (filters.get("status") != null && !((String)filters.get("status")).isEmpty()) {
            sql.append("AND b.status = ? ");
            params.add(filters.get("status"));
        }
        
        if (filters.get("date") != null && !((String)filters.get("date")).isEmpty()) {
            sql.append("AND CAST(b.check_in AS DATE) = ? ");
            params.add(filters.get("date"));
        }
        
        if (filters.get("search") != null && !((String)filters.get("search")).isEmpty()) {
            sql.append("AND (u.fullname LIKE ? OR r.room_number LIKE ? OR u.email LIKE ? OR CAST(b.id AS VARCHAR) LIKE ?) ");
            String searchPattern = "%" + filters.get("search") + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        sql.append("ORDER BY ra.assigned_at DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomAssignmentView assignment = new RoomAssignmentView();
                    assignment.setBookingId(rs.getInt("booking_id"));
                    assignment.setRoomId(rs.getInt("room_id"));
                    assignment.setRoomNumber(rs.getString("room_number"));
                    assignment.setRoomTypeName(rs.getString("room_type_name"));
                    assignment.setCustomerName(rs.getString("customer_name"));
                    assignment.setCustomerEmail(rs.getString("customer_email"));
                    assignment.setCustomerPhone(rs.getString("customer_phone"));
                    assignment.setCheckIn(rs.getTimestamp("check_in"));
                    assignment.setCheckOut(rs.getTimestamp("check_out"));
                    assignment.setAssignedAt(rs.getTimestamp("assigned_at"));
                    assignment.setBookingStatus(rs.getString("booking_status"));
                    assignment.setPaymentStatus(rs.getString("payment_status"));
                    assignment.setTotalPrice(rs.getBigDecimal("total_price"));
                    assignment.setBranchName(rs.getString("branch_name"));
                    assignment.setBranchId(rs.getInt("branch_id"));
                    assignment.setNights(rs.getInt("nights"));
                    assignment.setMembershipLevel(rs.getString("membership_level"));
                    assignment.setAssignedBy(rs.getString("created_by_name"));
                    
                    assignments.add(assignment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return assignments;
    }
    
    /**
     * Get count of room assignments for a specific branch with filters
     */
    public int getRoomAssignmentCountByBranch(int branchId, Map<String, Object> filters) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM RoomAssignment ra ");
        sql.append("JOIN Room r ON ra.room_id = r.id ");
        sql.append("JOIN Booking b ON ra.booking_id = b.id ");
        sql.append("JOIN UserAccount u ON b.user_id = u.id ");
        sql.append("WHERE b.branch_id = ? AND b.is_deleted = 0 AND r.is_deleted = 0 ");
        
        List<Object> params = new ArrayList<>();
        params.add(branchId);
        
        // Apply same filters as main query
        if (filters.get("status") != null && !((String)filters.get("status")).isEmpty()) {
            sql.append("AND b.status = ? ");
            params.add(filters.get("status"));
        }
        
        if (filters.get("date") != null && !((String)filters.get("date")).isEmpty()) {
            sql.append("AND CAST(b.check_in AS DATE) = ? ");
            params.add(filters.get("date"));
        }
        
        if (filters.get("search") != null && !((String)filters.get("search")).isEmpty()) {
            sql.append("AND (u.fullname LIKE ? OR r.room_number LIKE ? OR u.email LIKE ? OR CAST(b.id AS VARCHAR) LIKE ?) ");
            String searchPattern = "%" + filters.get("search") + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get statistics for a specific branch
     */
    public Map<String, Integer> getRoomAssignmentStatistics(int branchId) {
        Map<String, Integer> stats = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_assignments, " +
                    "SUM(CASE WHEN b.status = 'CheckedIn' THEN 1 ELSE 0 END) as checked_in, " +
                    "SUM(CASE WHEN b.status = 'CheckedOut' THEN 1 ELSE 0 END) as checked_out, " +
                    "SUM(CASE WHEN b.status = 'Pending' THEN 1 ELSE 0 END) as pending, " +
                    "SUM(CASE WHEN b.status = 'Paid' THEN 1 ELSE 0 END) as paid, " +
                    "SUM(CASE WHEN b.status = 'Completed' THEN 1 ELSE 0 END) as completed " +
                    "FROM RoomAssignment ra " +
                    "JOIN Booking b ON ra.booking_id = b.id " +
                    "WHERE b.branch_id = ? AND b.is_deleted = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total", rs.getInt("total_assignments"));
                    stats.put("checkedIn", rs.getInt("checked_in"));
                    stats.put("checkedOut", rs.getInt("checked_out"));
                    stats.put("pending", rs.getInt("pending"));
                    stats.put("paid", rs.getInt("paid"));
                    stats.put("completed", rs.getInt("completed"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Get branch name by ID
     */
    public String getBranchName(int branchId) {
        String sql = "SELECT name FROM HotelBranch WHERE id = ? AND is_deleted = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "Unknown Branch";
    }
}