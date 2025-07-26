/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;
import DBcontext.DBContext;

/**
 *
 * @author thien
 */
@WebServlet(name = "ChatAiHistoryServlet", urlPatterns = {"/ChatHistoryServlet"})
public class ChatAiHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String userId = request.getParameter("userId");

        if (userId == null || userId.isEmpty()) {
            sendErrorResponse(response, "Vui lòng cung cấp userId.");
            return;
        }

        try {
            JSONArray conversations = getChatHistory(userId);
            JSONObject resJson = new JSONObject();
            resJson.put("conversations", conversations);
            PrintWriter out = response.getWriter();
            out.print(resJson.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, "Lỗi khi tải lịch sử trò chuyện: " + e.getMessage());
        }
    }

    private JSONArray getChatHistory(String userId) throws SQLException {
        JSONArray conversations = new JSONArray();
        Connection conn = new DBContext().getConnection();
        String sql = "SELECT message, response, created_at, violation FROM ChatAIHistory WHERE user_id = ? ORDER BY created_at ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            JSONArray currentConv = new JSONArray();
            while (rs.next()) {
                String message = rs.getString("message");
                String timestamp = rs.getTimestamp("created_at").toString();
                String violation = rs.getString("violation");
                String responseText = rs.getString("response");
                
                if (responseText != null) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("role", "ai");
                    responseObj.put("text", responseText);
                    responseObj.put("timestamp", timestamp);
                    currentConv.put(responseObj);
                }

                JSONObject msgObj = new JSONObject();
                msgObj.put("role", "user");
                msgObj.put("text", message);
                msgObj.put("timestamp", timestamp);
                currentConv.put(msgObj);

                if (violation != null) {
                    JSONObject violationObj = new JSONObject();
                    violationObj.put("role", "ai");
                    violationObj.put("text", violation.equals("Invalid SQL")
                            ? "Yêu cầu không hợp lệ. Chỉ các truy vấn SELECT được phép."
                            : "Xin lỗi, câu hỏi này bị hạn chế do vi phạm chính sách bảo mật.");
                    violationObj.put("timestamp", timestamp);
                    currentConv.put(violationObj);
                }
            }
            if (currentConv.length() > 0) {
                conversations.put(currentConv);
            }
        } finally {
            conn.close();
        }
        return conversations;
    }

    private void sendErrorResponse(HttpServletResponse response, String error) throws IOException {
        JSONObject resJson = new JSONObject();
        resJson.put("error", error);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter out = response.getWriter();
        out.print(resJson.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
