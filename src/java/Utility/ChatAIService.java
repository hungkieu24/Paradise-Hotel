/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

/**
 *
 * @author thien
 */
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;
import java.net.*;
import java.io.*;
import DBcontext.DBContext;

public class ChatAIService {

    //phải hidden
    private final String OPENAI_API_KEY = "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";
    private final String OPENAI_ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    // schema mô phỏng để AI hiểu về database  cần được sửa lại cho chính xác
    private final String DB_SCHEMA_CONTEXT = """
Bạn là trợ lý AI của khách sạn 5 sao. Các bảng hiện có:

- RoomType(id, name, description, base_price, capacity_adult, capacity_child, branch_id)
- Booking(id, customerId, roomId, checkInDate, checkOutDate, status)
- Customer(id, name, email)

Người dùng có thể hỏi các yêu cầu như:
- Gợi ý phòng theo số người, giá tiền, tiện nghi
- Cách đặt/hủy phòng
- Xem chi tiết một loại phòng
- Link đặt phòng hoặc chi tiết phòng
- Xem lịch sử đặt phòng
- Chào hỏi

Chỉ sinh câu SQL SELECT an toàn theo SQL Server.
Không bao giờ sinh DELETE, UPDATE, INSERT.
Chỉ trả lại câu lệnh SQL trong một dòng hoặc trả lời những câu chào hỏi của khách.
""";

    public String generateSQL(String userMessage) throws IOException {
        JSONObject requestBody = new JSONObject();
        JSONArray messages = new JSONArray();

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", DB_SCHEMA_CONTEXT));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userMessage));

        requestBody.put("model", "mistralai/mistral-7b-instruct");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.2);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(OPENAI_ENDPOINT).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            conn.setRequestProperty("HTTP-Referer", "http://localhost");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();

            InputStream inputStream = (statusCode >= 400)
                    ? conn.getErrorStream()
                    : conn.getInputStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // ✅ Xử lý lỗi cụ thể
            if (statusCode == 429) {
                return "Bạn đang gửi quá nhiều yêu cầu đến hệ thống. Vui lòng thử lại sau vài giây.";
            } else if (statusCode == 401) {
                return "Lỗi xác thực: Vui lòng kiểm tra lại API Key.";
            } else if (statusCode >= 400) {
                return "Lỗi từ OpenAI (HTTP " + statusCode + "): " + response;
            }

            JSONObject res = new JSONObject(response.toString());
            String fullMessage = res.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            // ✅ Trích xuất dòng SQL nằm trong markdown ```sql``` nếu có
            if (fullMessage.contains("```sql")) {
                int start = fullMessage.indexOf("```sql") + 6;
                int end = fullMessage.indexOf("```", start);
                if (end > start) {
                    String extractedSQL = fullMessage.substring(start, end).trim();
//                    if (extractedSQL.endsWith(";")) {
//                        extractedSQL = extractedSQL.substring(0, extractedSQL.length() - 1).trim();
//                    }
                    System.out.println("SQL extracted from markdown:\n" + extractedSQL);
                    return extractedSQL;
                }
            }

            // Nếu không có markdown, tìm dòng đầu tiên bắt đầu bằng SELECT
            for (String line : fullMessage.split("\n")) {
                line = line.trim();
                if (line.toLowerCase().startsWith("select")) {
                    System.out.println("SQL extracted from plain text:\n" + line);
                    return line;
                }
            }

            // Nếu không tìm thấy, báo lỗi
            System.out.println("Không tìm thấy câu lệnh SQL hợp lệ trong phản hồi:\n" + fullMessage);
            return "INVALID_SQL";

        } catch (IOException e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi kết nối đến OpenAI. Vui lòng kiểm tra mạng hoặc thử lại sau.";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public JSONArray executeDynamicSQL(String sql) throws SQLException {
        JSONArray results = new JSONArray();
        DBContext db = new DBContext();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String colName = meta.getColumnLabel(i);
                    Object val = rs.getObject(i);
                    obj.put(colName, val);
                }
                results.put(obj);
            }
        }
        return results;
    }

    public String generateAnswerFromResult(String userMessage, JSONArray resultData) throws IOException {
        JSONObject requestBody = new JSONObject();
        JSONArray messages = new JSONArray();

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "Bạn là trợ lý AI, hãy trả lời thân thiện và ngắn gọn dựa trên dữ liệu JSON mà tôi cung cấp."));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", "Yêu cầu của tôi: " + userMessage + "\nDữ liệu: " + resultData.toString()));

        requestBody.put("model", "mistralai/mistral-7b-instruct");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.4);

        HttpURLConnection conn = (HttpURLConnection) new URL(OPENAI_ENDPOINT).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("HTTP-Referer", "http://localhost");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        JSONObject res = new JSONObject(response.toString());
        return res.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    }
}
