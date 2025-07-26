/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DBcontext.DBContext;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Utility.ChatAIService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thien
 */
@WebServlet(name = "ChatAIServlet", urlPatterns = {"/ChatServlet"})
public class ChatAIServlet extends HttpServlet {

    ChatAIService chatAIService = new ChatAIService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        BufferedReader reader = request.getReader();// lấy reader để đọc nội dung của POST
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {// đọc từng dòng của JSON
            sb.append(line); // ghép lại thành chuỗi hoàn chỉnh
        }

        JSONObject reqJson = new JSONObject(sb.toString());// chuyển thành đối tượng JSON
        String userMessage = reqJson.getString("message");// lấy nội dung của message
        String userId = reqJson.optString("userId", "");
        if (userId.isEmpty()) {
            sendResponse(response, "Miss userId", HttpServletResponse.SC_BAD_REQUEST, "");
            System.out.println("lay tu Json" + userId);
            return;
        }

        try {

            // Step 1: Generate SQL from user message
            String sql = chatAIService.generateSQL(userMessage, userId);

            System.out.println(sql);
            if (sql == null || sql.contains("too many requests") || sql.contains("You are sending too many requests")) {
                sendResponse(response, "The system is currently overloaded. Please try again later.", 429, userId);
                return;
            }

            // Step 2: Validate SQL for safety (only SELECT allowed)
            if (!isSQLSafe(sql) && !sql.equals("RESTRICTED_QUERY")
                    && !sql.startsWith("TEMPLATE_RESPONSE:") && !sql.equals("NON_SQL_QUERY")) {
                saveChatHistoryViolation(userId, userMessage, "Invalid SQL");
                sendResponse(response, "Invalid request. Only SELECT queries are allowed.", HttpServletResponse.SC_BAD_REQUEST, userId);
                System.out.println("Is SQL :" + userId);
                return;
            }

            // Step 3: Execute dynamic SQL safely
            JSONArray resultData = chatAIService.executeDynamicSQL(sql, userId);

            // Step 4: Generate AI answer from the actual data
            String aiAnswer = chatAIService.generateAnswerFromResult(userMessage, resultData, userId);
            saveChatHistory(userId, userMessage, aiAnswer);

            // Ghi nhận vi phạm nếu là RESTRICTED_QUERY
            if (sql.equals("RESTRICTED_QUERY")) {
                saveChatHistoryViolation(userId, userMessage, "Restricted query");
            }

            sendResponse(response, aiAnswer, HttpServletResponse.SC_OK, userId);

        } catch (Exception e) {
            try {
                e.printStackTrace();
                saveChatHistoryViolation(userId, userMessage, "Error: " + e.getMessage());
                sendResponse(response, "An error occurred during processing. Please try again later.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, userId);
            } catch (SQLException ex) {
                Logger.getLogger(ChatAIServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isSQLSafe(String sql) {
        String lowerSql = sql.trim().toLowerCase();

        // Cho phép dấu ; nếu nằm ở cuối
        if (lowerSql.endsWith(";")) {
            lowerSql = lowerSql.substring(0, lowerSql.length() - 1).trim();
        }

        return lowerSql.startsWith("select")
                && !lowerSql.contains("delete")
                && !lowerSql.contains("drop")
                && !lowerSql.contains("update")
                && !lowerSql.contains("insert");
    }

    private void sendResponse(HttpServletResponse response, String answer, int statusCode, String userId) throws IOException {
        JSONObject resJson = new JSONObject();
        resJson.put("answer", answer);
        resJson.put("userId", userId);
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(resJson.toString());
        out.flush();
    }

    private void saveChatHistory(String userId, String message, String aiAnswer) throws SQLException {
        Connection conn = new DBContext().getConnection();
        String sql = "INSERT INTO ChatAIHistory (user_id, message, response, created_at) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, message);
            stmt.setString(3, aiAnswer);
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    private void saveChatHistoryViolation(String userId, String message, String violation) throws SQLException {
        Connection conn = new DBContext().getConnection();
        String sql = "UPDATE ChatAIHistory SET violation = ? WHERE user_id = ? AND message = ? AND created_at = (SELECT MAX(created_at) FROM ChatAIHistory WHERE user_id = ? AND message = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, violation);
            stmt.setString(2, userId);
            stmt.setString(3, message);
            stmt.setString(4, userId);
            stmt.setString(5, message);
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

}
