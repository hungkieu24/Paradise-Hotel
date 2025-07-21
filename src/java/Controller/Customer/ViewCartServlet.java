/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller.Customer;

import Dal.CartRoomTypeDAO;
import Dal.RoomDAO;
import Model.CartItem;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author KTC
 */
@WebServlet(name="ViewCartServlet", urlPatterns={"/viewCart"})
public class ViewCartServlet extends HttpServlet {
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user != null) {
            CartRoomTypeDAO cartDAO = new CartRoomTypeDAO();
            List<CartItem> listCart = cartDAO.getCartByUserId(user.getId());
              Map<Integer, Integer> maxQuantityMap = new HashMap<>();
              RoomDAO roomDAO = new RoomDAO();
            for (CartItem item : listCart) {
                int roomTypeId = item.getRoomType().getRoomTypeID();
                int maxQuantity = roomDAO.getAvailableRoomCountByRoomType(roomTypeId);
                maxQuantityMap.put(roomTypeId, maxQuantity);
            }
            session.setAttribute("cart", listCart); // Gán lại vào session
            session.setAttribute("maxQuantityMap", maxQuantityMap);
        }

        request.getRequestDispatcher("./viewCart.jsp").forward(request, response);
    }
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    }  

}
