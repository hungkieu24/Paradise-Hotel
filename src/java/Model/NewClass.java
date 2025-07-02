/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author KTC
 */
import Dal.CartRoomTypeDAO;
import java.util.List;
 import java.util.*;
public class NewClass {



    public static void main(String[] args) {
        // 🔸 Giả lập user sau khi đăng nhập
        String userId = "U001";
        System.out.println("👤 Đăng nhập với user: " + userId);

        // 🔸 Giả lập session bằng 1 map
        Map<String, Object> session = new HashMap<>();

        // 🔸 Lấy giỏ hàng từ DB
        CartRoomTypeDAO dao = new CartRoomTypeDAO();
        List<CartItem> cartList = dao.getCartByUserId(userId);

        // 🔸 Gán cart vào session (giống như Servlet)
        session.put("cart", cartList);

        // ✅ Bắt chước JSP in cart
        System.out.println("\n🛒 Giỏ hàng hiện tại:");
        List<CartItem> sessionCart = (List<CartItem>) session.get("cart");
        if (sessionCart == null || sessionCart.isEmpty()) {
            System.out.println("⚠️ Cart is empty.");
        } else {
            for (CartItem item : sessionCart) {
                String name = item.getRoomType().getName();
                int quantity = item.getQuantity();
                System.out.println("➡️ " + name + " - Quantity: " + quantity);
            }
        }
    }
}



