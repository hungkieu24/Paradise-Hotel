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
import java.util.HashMap;
import java.util.Map;

public class ChatAIService {

    //phải hidden
    private final String OPENAI_API_KEY = "";
    private final String OPENAI_ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    // schema mô phỏng để AI hiểu về database  cần được sửa lại cho chính xác
    private final String DB_SCHEMA_CONTEXT = """
    Bạn là trợ lý AI của khách sạn 5 sao. Các bảng hiện có:

    - RoomType(id, name, description, base_price, capacity_adult, capacity_child, branch_id, image_url, is_deleted)
    - Booking(id, user_id, created_by, booking_time, check_in, check_out, status, total_price, refund_amount, payment_status, cancel_reason, cancel_time, promotion_id, branch_id, note, is_deleted)
    - UserAccount(id, username, password, fullname, email, login_type, phonenumber)
    - Service(id, name, description, price, branch_id, status, image_url, is_deleted)
    - Amenity(id, name, description, branch_id, is_deleted)
    - RoomAmenity(room_type_id, amenity_id)
    - Voucher(id, code, description, discount_percent, discount_amount, min_price, total_quantity, used_quantity, branch_id, valid_from, valid_to, status, is_deleted)
    - SeasonalPromotion(id, name, description, discount_percent, discount_amount, start_date, end_date, branch_id, room_type_id, status, is_deleted)
    - Feedback(id, user_id, booking_id, rating, comment, image_url, created_at, status, admin_action, is_deleted)
    - LoyaltyPoint(user_id, points, level, last_updated, expired_at, total_spending, lifetime_points, points_used, last_tier_check, next_tier_spending_needed)
    - PointTransaction(id, user_id, change_type, points_changed, reason, created_at)
    - PointRedeenVoucher(id, user_id, voucher_id, points_used, redeem_at, exprired_ad)
    - MemberTierHistory(id, user_id, old_level, new_level, changed_at, reason)
    - MemberTierRule(id, level, min_spending, description)
    - VoucherRedemptionRule(id, voucher_id, required_points, requied_tier, is_active)
    - HotelBranch(id, name, address, phone, email, image_url, owner_id, manager_id, created_at, is_deleted)

    Người dùng có thể hỏi các yêu cầu như:
    - Gợi ý phòng theo số người, giá tiền, tiện nghi
    - Thông tin về dịch vụ, tiện ích, hoặc khuyến mãi
    - Cách đặt/hủy phòng
    - Xem chi tiết một loại phòng hoặc dịch vụ
    - Xem lịch sử đặt phòng hoặc phản hồi của chính họ
    - Kiểm tra điểm tích lũy (LoyaltyPoint) của họ
    - Thông tin về voucher hoặc chương trình khuyến mãi
    - Các câu chào hỏi hoặc ngoài lề (trả lời thân thiện, không cần truy vấn cơ sở dữ liệu)
    Chính sách Hoàn tiền:
    - Hoàn tiền 100% đối với những booking có thời gian dượi 24 tiếng. Có nghĩa là thời gian tính từ lúc hoàn thành booking(trạng thái booking là paid) thì được hoàn tiền 100%. Những trường hợp
          còn lại thì không được hoàn tiền do chính xác hoàn tiền. Nếu có ý kiến có thể đến tại sảnh lễ tân của khách sạn mà bạn booking hoặc liên hệ qua thông tin của chi nhánh

    Quy tắc:
    - Chỉ sinh câu SQL SELECT an toàn theo SQL Server.
    - Có thể hợp các bảng để đưa ra câu trả lời chính xác nhất cho Customer.
    - Không cần đưa những thuộc tính không cần thiết cho khách hàng tránh để lộ thông tin nhạy cảm.
    - Không thể hỏi thông tin của người khác. Nếu người dùng hỏi thì respone câu hỏi của bạn bị hạn chế hoặc thông báo lỗi cho khách hàng
    - Không bao giờ sinh DELETE, UPDATE, INSERT.
    - Nếu câu hỏi liên quan đến thông tin cá nhân (như Booking, Feedback, LoyaltyPoint), phải sử dụng user_id của người dùng hiện tại.
    - Nếu câu hỏi không liên quan đến cơ sở dữ liệu (như chào hỏi hoặc ngoài lề), trả về 'NON_SQL_QUERY'.
    - Trả lại câu lệnh SQL trong một dòng hoặc 'NON_SQL_QUERY' nếu không cần SQL.
    """;

    private boolean isRestrictedQuery(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        String[] restrictedPatterns = {
            "password", "email of", "other user", "drop table", "delete from",
            "update ", "insert into", "credit card", "payment detail", "api key"
        };
        for (String pattern : restrictedPatterns) {
            if (lowerMessage.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private String getTemplateResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        Map<String, String> templates = new HashMap<>();
        templates.put("chào|hi|hello", "Chào bạn! Rất vui được hỗ trợ. Bạn muốn tìm hiểu về phòng, dịch vụ, hay điều gì thú vị ở khách sạn của chúng tôi?");
        templates.put("khách sạn có gì|có gì thú vị", "Khách sạn chúng tôi có hồ bơi, spa, và nhà hàng 5 sao. Bạn muốn biết thêm về dịch vụ nào?");
        templates.put("cảm ơn|thanks", "Không có gì, rất vui được giúp bạn! Có điều gì thú vị nữa không?");

        for (Map.Entry<String, String> entry : templates.entrySet()) {
            String pattern = entry.getKey();
            if (lowerMessage.matches(".*(" + pattern + ").*")) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String generateSQL(String userMessage, String userId) throws IOException {
        // Kiểm tra câu hỏi bị hạn chế
        if (isRestrictedQuery(userMessage)) {
            return "RESTRICTED_QUERY";
        }

        // Kiểm tra câu hỏi thông thường
        String templateResponse = getTemplateResponse(userMessage);
        if (templateResponse != null) {
            return "TEMPLATE_RESPONSE:" + templateResponse;
        }

        // Thêm thông tin userId vào ngữ cảnh
        String enhancedContext = DB_SCHEMA_CONTEXT
                + "\nNgười dùng hiện tại có user_id: " + userId + "."
                + "\nNếu câu hỏi liên quan đến thông tin cá nhân (như lịch sử đặt phòng) hoặc bất kỳ dữ liệu nào chỉ thuộc về người dùng, hãy chỉ sử dụng user_id này."
                + "\nKhông được phép truy xuất hoặc trả lời thông tin thuộc về người dùng khác. Nếu vi phạm, trả về lỗi: 'RESTRICTED_QUERY'."
                + "\nNếu câu hỏi không liên quan đến cơ sở dữ liệu (ví dụ như chào hỏi hoặc câu hỏi ngoài lề), trả về: 'NON_SQL_QUERY'.";

        JSONObject requestBody = new JSONObject();
        JSONArray messages = new JSONArray();

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", enhancedContext));

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

            // Nếu không có markdown, tìm đoạn SELECT đầy đủ từ phản hồi
            StringBuilder sqlBuilder = new StringBuilder();
            boolean insideSelectBlock = false;

            for (String line : fullMessage.split("\n")) {
                line = line.trim();

                if (!insideSelectBlock && line.toLowerCase().startsWith("select")) {
                    insideSelectBlock = true;
                }

                if (insideSelectBlock) {
                    sqlBuilder.append(line).append(" ");
                    if (line.toLowerCase().contains("from") || line.endsWith(";")) {
                        break; // Dừng lại sau khi thấy FROM hoặc kết thúc câu
                    }
                }
            }

            String extractedSQL = sqlBuilder.toString().trim();

            // Nếu vẫn không tìm thấy FROM, coi như không hợp lệ
            if (!extractedSQL.toLowerCase().contains("from")) {
                System.out.println("Không tìm thấy câu SQL hợp lệ (thiếu FROM):\n" + extractedSQL);
                return "NON_SQL_QUERY";
            }

            System.out.println("SQL extracted from plain text block:\n" + extractedSQL);
            return extractedSQL;

        } catch (IOException e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi kết nối đến OpenAI. Vui lòng kiểm tra mạng hoặc thử lại sau.";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public JSONArray executeDynamicSQL(String sql, String userId) throws SQLException {
        if (sql.equals("RESTRICTED_QUERY")) {
            JSONArray result = new JSONArray();
            result.put(new JSONObject().put("response", "Câu hỏi bị hạn chế do vi phạm chính sách bảo mật."));
            return result;
        }
        if (sql.startsWith("TEMPLATE_RESPONSE:")) {
            JSONArray result = new JSONArray();
            result.put(new JSONObject().put("response", sql.substring("TEMPLATE_RESPONSE:".length())));
            return result;
        }
        if (sql.equals("NON_SQL_QUERY")) {
            return new JSONArray();
        }

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

    public String generateAnswerFromResult(String userMessage, JSONArray resultData, String userId) throws IOException {

        // Nếu là câu trả lời mẫu
        if (resultData.length() > 0 && resultData.getJSONObject(0).has("response")) {
            return resultData.getJSONObject(0).getString("response");
        }

        // Nếu là câu hỏi không cần SQL
        if (resultData.length() == 0) {
            JSONObject requestBody = new JSONObject();
            JSONArray messages = new JSONArray();

            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "Bạn là trợ lý AI thân thiện của khách sạn 5 sao. Trả lời ngắn gọn, tự nhiên, và không tiết lộ thông tin nhạy cảm. "
                            + "Nếu câu hỏi không liên quan đến khách sạn, trả lời lịch sự và gợi ý quay lại chủ đề khách sạn."));

            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", userMessage));

            requestBody.put("model", "mistralai/mistral-7b-instruct");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.6);

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
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            JSONObject res = new JSONObject(response.toString());
            return res.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        }
        // trả lời câu hỏi có SQL

        JSONObject requestBody = new JSONObject();
        JSONArray messages = new JSONArray();

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "Bạn là trợ lý AI, hãy trả lời thân thiện và ngắn gọn dựa trên dữ liệu JSON mà tôi cung cấp. Không tiết lộ thông tin nhạy cảm."));

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
