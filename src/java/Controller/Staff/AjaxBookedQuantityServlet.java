package Controller.Staff;

import Dal.RoomDAO;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name="AjaxBookedQuantityServlet", urlPatterns={"/ajaxBookedQuantity"})
public class AjaxBookedQuantityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roomTypeIdStr = request.getParameter("roomTypeId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        int bookedQuantity = 0;
        try {
            int roomTypeId = Integer.parseInt(roomTypeIdStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date checkInDate = dateFormat.parse(checkInStr);
            Date checkOutDate = dateFormat.parse(checkOutStr);
            RoomDAO roomDAO = new RoomDAO();
            // Bạn cần lấy branchId từ session hoặc tham số, ví dụ:
            Integer branchId = ((Model.UserAccount)request.getSession().getAttribute("user")).getBranchId();
            bookedQuantity = roomDAO.getBookedQuantityByRoomTypeAndDateRange(
                branchId, roomTypeId,
                new java.sql.Date(checkInDate.getTime()),
                new java.sql.Date(checkOutDate.getTime())
            );
        } catch (Exception e) {
            bookedQuantity = -1; // báo lỗi
        }
        response.setContentType("application/json");
        response.getWriter().write("{\"bookedQuantity\":" + bookedQuantity + "}");
    }
}