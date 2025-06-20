/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.RoomTypeDAO;
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
import java.util.List;

/**
 *
 * @author KTC
 */
@WebServlet(name = "ViewRoomTypeListServlet", urlPatterns = {"/viewRoomTypeList"})
public class ViewRoomTypeListServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ViewRoomTypeListServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ViewRoomTypeListServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        List<RoomType> listRoomType;

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
                    listRoomType = roomTypeDAO.getAvailableRoomTypesByPriceAndDate(minPrice, maxPrice, checkIn, checkOut);
                } else {
                    listRoomType = roomTypeDAO.getRoomTypesByPriceRange(minPrice, maxPrice);
                }
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            if (checkIn != null && checkOut != null) {
                listRoomType = roomTypeDAO.getAvailableRoomTypesByDate(checkIn, checkOut);
            } else {
                listRoomType = roomTypeDAO.getAllRoomType();
            }
        }

        request.setAttribute("listRoomType", listRoomType);
        request.getRequestDispatcher("./viewRoomTypeList.jsp").forward(request, response);
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
        processRequest(request, response);
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
