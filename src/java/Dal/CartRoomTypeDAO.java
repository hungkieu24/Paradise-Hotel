/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

/**
 *
 * @author KTC
 */
import Controller.Customer.UpdateCartServlet;
import Model.Branch;
import Model.CartItem;
import Model.CartRoomType;
import Model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartRoomTypeDAO extends DBcontext.DBContext {

    public void addToCart(Integer userId, String sessionId, int roomTypeId, int quantity) {
        String sql = """
            MERGE CartRoomType AS target
            USING (SELECT ? AS user_id, ? AS session_id, ? AS room_type_id) AS source
            ON (
                (target.user_id = source.user_id AND source.user_id IS NOT NULL)
                OR
                (target.session_id = source.session_id AND source.user_id IS NULL)
            )
            AND target.room_type_id = source.room_type_id
            WHEN MATCHED THEN
                UPDATE SET quantity = quantity + ?, updated_at = GETDATE()
            WHEN NOT MATCHED THEN
                INSERT (user_id, session_id, room_type_id, quantity)
                VALUES (?, ?, ?, ?);
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setString(2, sessionId);
            ps.setInt(3, roomTypeId);
            ps.setInt(4, quantity);
            ps.setObject(5, userId);
            ps.setString(6, sessionId);
            ps.setInt(7, roomTypeId);
            ps.setInt(8, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CartItem> getCartByUserId(String userId) {
        List<CartItem> cartItems = new ArrayList<>();

        String sql = "SELECT room_type_id, quantity FROM CartRoomType WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            RoomTypeDAO roomTypeDAO = new RoomTypeDAO(); // để lấy thông tin room_type

            while (rs.next()) {
                int roomTypeId = rs.getInt("room_type_id");
                int quantity = rs.getInt("quantity");

                RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeId);
                BranchDAO branchDAO = new BranchDAO();
                Branch branch = branchDAO.getBranchByRoomTypeId(roomTypeId);
                roomType.setBranch(branch);
                if (roomType != null) {
                    cartItems.add(new CartItem(roomType, quantity));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error in getCartByUserId: " + e.getMessage());
        }

        return cartItems;
    }

    public void removeCartItem(int id) {
        String sql = "DELETE FROM CartRoomType WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearCart(Integer userId, String sessionId) {
        String sql = "DELETE FROM CartRoomType WHERE (user_id = ? AND ? IS NOT NULL) OR (session_id = ? AND ? IS NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, userId);
            ps.setString(3, sessionId);
            ps.setObject(4, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateCartItemQuantity(String userId, int roomTypeId, int newQuantity) {
        String sql = "UPDATE CartRoomType SET quantity = ?, added_at = ? WHERE user_id = ? AND room_type_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // thời gian hiện tại
            ps.setString(3, userId);
            ps.setInt(4, roomTypeId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCartItemQuantityWithTime(String userId, int roomTypeId, int newQuantity, Timestamp updatedAt) {
        String sql = "UPDATE CartRoomType SET quantity = ?, added_at = ? WHERE user_id = ? AND room_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setTimestamp(2, updatedAt);
            ps.setString(3, userId);
            ps.setInt(4, roomTypeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRoomInCart(String userId, int roomTypeId) {
        String sql = "SELECT 1 FROM CartRoomType WHERE user_id = ? AND room_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, roomTypeId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true nếu tồn tại
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToCart(String userId, int roomTypeId, int quantity, Timestamp addedAt) {
        String sql = "INSERT INTO CartRoomType (user_id, room_type_id, quantity, added_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setInt(2, roomTypeId);
            ps.setInt(3, quantity);
            ps.setTimestamp(4, addedAt);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCartItem(String userId, int roomTypeId) {
        String sql = "DELETE FROM CartRoomType WHERE user_id = ? AND room_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, roomTypeId);
            int result = ps.executeUpdate();
            System.out.println("➡️ Deleted from DB: " + result + " row(s).");
            return result > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting cart item: " + e.getMessage());
            return false;
        }
    }

    public CartRoomTypeDAO() {
        // Initialize connection (e.g., via a connection pool or DataSource)
        // Example: connection = YourConnectionPool.getConnection();
    }

    public boolean clearCartByUserId(String userId) {
        String sql = "DELETE FROM CartRoomType WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CartItem> getSelectedCartItems(String userId, String[] selectedIds) {
        List<CartItem> selectedItems = new ArrayList<>();
        List<CartItem> allCartItems = getCartByUserId(userId);

        if (selectedIds != null && allCartItems != null) {
            for (String idStr : selectedIds) {
                try {
                    int id = Integer.parseInt(idStr);

                    for (CartItem item : allCartItems) {
                        if (item.getRoomType().getRoomTypeID() == id) {
                            selectedItems.add(item); // giữ nguyên quantity đã lưu
                            break;
                        }
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace(); // hoặc log lỗi
                }
            }
        }

        return selectedItems;
    }
    public static void main(String[] args) {
        CartRoomTypeDAO cartRoomTypeDAO = new CartRoomTypeDAO();
        
    }
}
