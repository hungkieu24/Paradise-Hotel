package Controller.Staff;

import Dal.UserAccountDAO;
import Dal.LoyaltyPointDAO;
import Dal.BookingDAO;
import Model.UserAccount;
import Model.Booking;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name="ViewUserInfoServlet", urlPatterns={"/view-user-info"})
public class ViewUserInfoServlet extends HttpServlet {
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
    private final LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // --- VALIDATION: Check staff session/role ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userRole") == null
                || !"staff".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")))) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Trang bây giờ sẽ nhận 'bookingId' để hiển thị chi tiết cho một booking
        String bookingId = request.getParameter("bookingId");
        if (bookingId != null && !bookingId.trim().isEmpty()) {
           
            // Phương thức này cần trả về một đối tượng Booking đã bao gồm các dịch vụ liên quan.
            Booking booking = bookingDAO.getBookingDetailById(bookingId);
            
            if (booking != null) {
                // Giả định rằng đối tượng Booking có phương thức getUserId() để lấy ID của người dùng
                String userId = booking.getUserId();
                UserAccount user = userAccountDAO.getUserInfoById(userId);

                // Lấy rank của khách hàng
                String rank = null;
                if (user != null) {
                    rank = user.getRank();
                    if (rank == null) {
                        rank = loyaltyPointDAO.getRankByUserId(userId);
                        user.setRank(rank);
                    }
                }
                
                request.setAttribute("user", user);
                request.setAttribute("rank", rank);
                request.setAttribute("booking", booking); // Gửi đối tượng booking duy nhất
            } else {
                // Xử lý trường hợp không tìm thấy booking
                request.setAttribute("user", null);
                request.setAttribute("rank", null);
                request.setAttribute("booking", null);
            }
        } else {
            // Xử lý trường hợp không có bookingId
            request.setAttribute("user", null);
            request.setAttribute("rank", null);
            request.setAttribute("booking", null);
        }
        
        // Chuyển tiếp đến cùng một file JSP, nhưng giờ nó sẽ hiển thị dữ liệu của một booking
        request.getRequestDispatcher("view-user-info.jsp").forward(request, response);
    }
}