/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Dal.HotelBranchDAO;
import Dal.RoomTypeDAO;
import Model.HotelBranch;
import Model.RoomType;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author hungk
 */
@WebServlet(name = "SearchRoomServlet_Home", urlPatterns = {"/searchroom"})
public class SearchRoomServlet_Home extends HttpServlet {

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String adults = request.getParameter("adults");
        String childs = request.getParameter("childs");
        String dates = request.getParameter("dates");
        String branchIDRaw = request.getParameter("branchID");
         int branchId = Integer.parseInt(branchIDRaw);

        if (adults == null || childs == null || dates == null || dates.isEmpty()) {
            setSessionMessage(session, "Please fill in all information to search!", "error");
            response.sendRedirect("./homepage");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");

        // Tách chuỗi theo dấu ">"
        String[] parts = dates.split(">");
        if (parts.length < 2) {
            setSessionMessage(session, "Please select check-in and check-out dates.", "error");
            response.sendRedirect("./homepage");
            return;
        }
        String checkInStr = parts[0].trim();   // "05-26-25"
        String checkOutStr = parts[1].trim();  // "05-29-25"

        // Chuyển sang LocalDate
        LocalDate checkIn = LocalDate.parse(checkInStr, formatter);
        LocalDate checkOut = LocalDate.parse(checkOutStr, formatter);

        // Parst adults, childs -> int 
        int childsNum = Integer.parseInt(childs);
        int adultsNum = Integer.parseInt(adults);
        if (adultsNum == 0) {
            setSessionMessage(session, "You need at least 1 adult!", "error");
            response.sendRedirect("./homepage");
            return;
        }

        int totalPeople = childsNum + adultsNum;
        HotelBranchDAO hotelBranch = new HotelBranchDAO();
        List<HotelBranch> branches = hotelBranch.getAllHotelBranchesSimple();

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        List<RoomType> availableRoomTypes = roomTypeDAO.searchAvailableRoomTypesV2(checkIn, checkOut, totalPeople,branchId);
        request.setAttribute("branchList", branches);
        request.setAttribute("availableRoomTypes", availableRoomTypes);
        request.getRequestDispatcher("./searchRoomResult.jsp").forward(request, response);
    }

}
