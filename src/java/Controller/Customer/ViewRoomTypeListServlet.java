/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BranchDAO;
import Dal.RoomDAO;
import Dal.RoomTypeDAO;
import Model.Branch;
import Model.RoomType;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author KTC
 */
@WebServlet(name = "ViewRoomTypeListServlet", urlPatterns = {"/viewRoomTypeList"})
public class ViewRoomTypeListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        List<RoomType> listRoomType;
        BranchDAO branchDAO = new BranchDAO();
        RoomType roomType = new RoomType();

        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String dates = request.getParameter("dates");

        LocalDate checkIn = null;
        LocalDate checkOut = null;

        // Parse check-in, check-out nếu có
        if (dates != null && dates.contains(">")) {
            String[] parts = dates.split(">");
            if (parts.length >= 2) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");
                try {
                    checkIn = LocalDate.parse(parts[0].trim(), formatter);
                    checkOut = LocalDate.parse(parts[1].trim(), formatter);
                } catch (DateTimeParseException e) {
                    System.err.println("LỖI PARSE NGÀY:");
                    System.err.println("start: '" + parts[0] + "'");
                    System.err.println("end:   '" + parts[1] + "'");
                    e.printStackTrace();
                    checkIn = null;
                    checkOut = null;
                }
            }
        }

        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()
                    && maxPriceStr != null && !maxPriceStr.isEmpty()) {

                double minPrice = Double.parseDouble(minPriceStr);
                double maxPrice = Double.parseDouble(maxPriceStr);

                if (checkIn != null && checkOut != null) {
                    listRoomType = roomTypeDAO.searchAvailableRoomTypesV3(minPrice, maxPrice, checkIn, checkOut);
                } else {
                    listRoomType = roomTypeDAO.getRoomTypesByPriceRange(minPrice, maxPrice);
                }
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            if (checkIn != null && checkOut != null) {
                listRoomType = roomTypeDAO.searchAvailableRoomTypesV3(checkIn, checkOut);
            } else {
                listRoomType = roomTypeDAO.getAllRoomType();
            }
        }
        
        RoomDAO roomDAO = new RoomDAO(); // thêm dòng này
        Map<Integer, Integer> availableRoomMap = new HashMap<>();
        for (RoomType rt : listRoomType) {
            int roomTypeId = rt.getRoomTypeID();
            int availableCount = roomDAO.getAvailableRoomCountByRoomType(roomTypeId);
            availableRoomMap.put(roomTypeId, availableCount);
        }
        request.setAttribute("availableRoomMap", availableRoomMap);
        request.setAttribute("listRoomType", listRoomType);
        request.getRequestDispatcher("./viewRoomTypeList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
