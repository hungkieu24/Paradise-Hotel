/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.AmenityDAO;
import Model.Amenity;
import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author hungk
 */
@WebServlet(name = "AmenityEventHandlerServlet", urlPatterns = {"/manager/amenityEventHandler"})
public class AmenityEventHandlerServlet extends HttpServlet {

    private final AmenityDAO amenityDAO = new AmenityDAO();
    private final String AMENITY_PAGE = "./amenity";
    private static final String COL_AMENITY_NAME = "name";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int amenityID = Integer.parseInt(request.getParameter("amenityID"));

        Amenity amenity = amenityDAO.getAmenityById(amenityID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(amenity));
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String branchIDStr = request.getParameter("branchID");
        String amenityName = request.getParameter("amenityName");
        String description = request.getParameter("description");

        if (action != null) {
            int branchID = 0;
            if (branchIDStr != null) {
                branchID = Integer.parseInt(branchIDStr.trim());
            }
            Amenity amenity = new Amenity(amenityName, description, branchID);
            if (action.equals("add")) {
                handleAddAmenity(request, session, amenity);
                response.sendRedirect(AMENITY_PAGE);
                return;
            }

            if (action.equals("edit")) {
                int amenityID = Integer.parseInt(request.getParameter("amenityID").trim());
                amenity.setId(amenityID);
                handleEditAmenity(request, session, amenity);
                response.sendRedirect(AMENITY_PAGE);
                return;
            }

            if (action.equals("delete")) {
                int amenityID = Integer.parseInt(request.getParameter("IdDelete").trim());
                boolean success = amenityDAO.deleteAmenity(amenityID);
                setSessionMessage(session, success ? "Delete amenity successful!" : "Failure to delete amenity!",
                        success ? "success" : "error");
                response.sendRedirect(AMENITY_PAGE);
                return;
            }
        }
    }

    private void handleAddAmenity(HttpServletRequest request, HttpSession session, Amenity amenity) throws ServletException, IOException {
        boolean isExistAmenityName = amenityDAO.isFieldExists(COL_AMENITY_NAME, amenity.getName(), null);
        if (isExistAmenityName) {
            setSessionMessage(session, "Amenity name already exists", "error");
            return;
        }

        // Insert DB
        boolean success = amenityDAO.insertAmenity(amenity);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Add amenity successful!" : "Failure to add amenity!",
                success ? "success" : "error");
    }

    private void handleEditAmenity(HttpServletRequest request, HttpSession session, Amenity amenity) throws ServletException, IOException {
        boolean isExistAmenityName = amenityDAO.isFieldExists(COL_AMENITY_NAME, amenity.getName(), amenity.getId());
        if (isExistAmenityName) {
            setSessionMessage(session, "Amenity name already exists", "error");
            return;
        }

        // Insert DB
        boolean success = amenityDAO.updateAmenity(amenity);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Update amenity successful!" : "Failure to update amenity!",
                success ? "success" : "error");
    }

}
