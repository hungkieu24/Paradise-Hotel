/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BranchDAO;
import Dal.CartRoomTypeDAO;
import Dal.RoomTypeDAO;
import Model.Branch;
import Model.CartItem;
import Model.CartRoomType;
import Model.RoomType;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author KTC
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = {"/addToCart"})
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        CartRoomTypeDAO cartRoomTypeDAO = new CartRoomTypeDAO();
        UserAccount user = (UserAccount) session.getAttribute("user");
        List<CartItem> listCart = cartRoomTypeDAO.getCartByUserId(user.getId());
        if (listCart == null) {
            listCart = new ArrayList<>();
        }

        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
        RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeId);
        
      

// Kiểm tra nếu roomType đã có trong giỏ hàng thì tăng số lượng
        boolean found = false;
        for (CartItem item : listCart) {
            if (item.getRoomType().getRoomTypeID() == roomTypeId) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                cartRoomTypeDAO.updateCartItemQuantityWithTime(user.getId(), roomTypeId, item.getQuantity(), new Timestamp(System.currentTimeMillis()));
                break;
            }
        }
        if (!found) {
            listCart.add(new CartItem(roomType, 1));
            cartRoomTypeDAO.addToCart(user.getId(), roomTypeId, 1, new Timestamp(System.currentTimeMillis()));
        }

        session.setAttribute("cart", listCart);

        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"success\",\"message\":\"Added to cart\"}");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
//        int quantity = Integer.parseInt(request.getParameter("quantity"));
//        Integer userId = (Integer) request.getSession().getAttribute("userId");
//        String sessionId = request.getSession().getId();
//
//        CartRoomTypeDAO cartRoomTypeDAO = new CartRoomTypeDAO();
//        cartRoomTypeDAO.addToCart(userId, sessionId, roomTypeId, quantity);
//
//        response.sendRedirect("cart.jsp");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
