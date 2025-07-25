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

        // Check if room already in cart
        boolean found = false;
        for (CartItem item : listCart) {
            if (item.getRoomType().getRoomTypeID() == roomTypeId) {
                found = true;
                break;
            }
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (found) {
            out.write("{\"status\":\"exists\",\"message\":\"Room is already in cart\"}");
        } else {
            listCart.add(new CartItem(roomType, 1));
            cartRoomTypeDAO.addToCart(user.getId(), roomTypeId, 1, new Timestamp(System.currentTimeMillis()));
            session.setAttribute("cart", listCart);
            out.write("{\"status\":\"success\",\"message\":\"Added to cart\"}");
        }

        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
