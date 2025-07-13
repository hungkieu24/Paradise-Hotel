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

import Utility.ChatAIService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

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

        try {
            // Step 1: Generate SQL from user message
            String sql = chatAIService.generateSQL(userMessage);

            System.out.println(sql);
            // Step 2: Validate SQL for safety (only SELECT allowed)
            if (!isSQLSafe(sql)) {
                sendResponse(response, "Xin lỗi, yêu cầu của bạn vượt ngoài giới hạn cho phép.");
                return;
            }

            // Step 3: Execute dynamic SQL safely
            JSONArray resultData = chatAIService.executeDynamicSQL(sql);

            // Step 4: Generate AI answer from the actual data
            String aiAnswer = chatAIService.generateAnswerFromResult(userMessage, resultData);

            sendResponse(response, aiAnswer);

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(response, "Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại sau.");
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

    private void sendResponse(HttpServletResponse response, String answer) throws IOException {
        JSONObject resJson = new JSONObject();
        resJson.put("answer", answer);
        PrintWriter out = response.getWriter();
        out.print(resJson.toString());
        out.flush();
    }

}
