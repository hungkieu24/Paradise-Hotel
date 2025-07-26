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
import org.json.JSONException;

public class ChatAIService {

    //phải hidden
    private final String OPENAI_API_KEY = "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";
    private final String OPENAI_ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    // schema mô phỏng để AI hiểu về database  cần được sửa lại cho chính xác
    private final String DB_SCHEMA_CONTEXT = """
    You are the AI assistant for a 5-star hotel. The available tables are:

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
    - Wallet(UserID, Balance, WalletID, UpdatedAt)

    Users can ask requests like:
    - Suggest rooms based on number of guests, price, and amenities
    - Information about services, amenities, or promotions
    - How to book/cancel a room
    - View details of a room type or service
    - View their own booking history or feedback
    - Check their loyalty points
    - Information about vouchers or promotions
    - General greetings or casual questions (respond politely, no database queries needed)
    - You can include links in responses where relevant, such as:
        <a href="http://localhost:8080/ParadiseHotel/viewServiceList">Service</a>
        <a href="http://localhost:8080/ParadiseHotel/myWallet">Wallet</a>
        <a href="http://localhost:8080/ParadiseHotel/about.jsp">About us</a>
        <a href="http://localhost:8080/ParadiseHotel/editProfile">Edit Profile</a>

    Refund Policy:
    - 100% refund is granted for bookings within 24 hours. That means the time from the booking being confirmed (status is 'paid') must be less than 24 hours. 
      In all other cases, no refund is issued due to hotel policy. If you have concerns, please visit the hotel branch where you booked or contact the branch directly.

    Rules:
    - Only generate safe SQL SELECT queries for SQL Server.
    - You may join multiple tables to provide the most accurate response for the customer.
    - Do not include unnecessary attributes to avoid exposing sensitive information.
    - Do not allow users to inquire about others' information. If they do, respond with a restricted access message or an error notification.
    - Never generate DELETE, UPDATE, or INSERT queries.
    - If the question involves personal information (such as Booking, Feedback, LoyaltyPoint), use the current user's user_id.
    - If the question does not relate to the database (such as greetings or casual talk), return 'NON_SQL_QUERY'.
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
        templates.put("hi|hello", "Hello! I'm happy to assist you. Are you interested in rooms, services, or something exciting at our hotel?");
        templates.put("what does the hotel have|what's interesting", "Our hotel features a swimming pool, spa, and 5-star restaurant. Would you like to learn more about a specific service? <a href=\"http://localhost:8080/ParadiseHotel/viewServiceList\">Service</a>");
        templates.put("thanks", "You're welcome! Glad to help. Is there anything else you're curious about?");

        for (Map.Entry<String, String> entry : templates.entrySet()) {
            String pattern = entry.getKey();
            if (lowerMessage.matches(".*(" + pattern + ").*")) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String generateSQL(String userMessage, String userId) throws IOException {
    // Check for restricted queries
    if (isRestrictedQuery(userMessage)) {
        return "RESTRICTED_QUERY";
    }

    // Check for template responses
    String templateResponse = getTemplateResponse(userMessage);
    if (templateResponse != null) {
        return "TEMPLATE_RESPONSE:" + templateResponse;
    }

    // Enhanced context with userId
    String enhancedContext = DB_SCHEMA_CONTEXT
            + "\nThe current user has user_id: " + userId + "."
            + "\nIf the question involves personal information (such as booking history) or any data that belongs solely to the user, only use this user_id."
            + "\nAccessing or responding with information that belongs to another user is not allowed. If violated, return the error: 'RESTRICTED_QUERY'."
            + "\nIf the question is not related to the database (e.g., greetings or casual questions), return: 'NON_SQL_QUERY'.";

    JSONObject requestBody = new JSONObject();
    JSONArray messages = new JSONArray();

    messages.put(new JSONObject()
            .put("role", "system")
            .put("content", enhancedContext));

    messages.put(new JSONObject()
            .put("role", "user")
            .put("content", userMessage));

    // Use a valid model from OpenRouter
    requestBody.put("model", "qwen/qwen3-coder:free"); // Replace with a valid model
    requestBody.put("messages", messages);
    requestBody.put("temperature", 0.6);

    HttpURLConnection conn = null;
    try {
        conn = (HttpURLConnection) new URL(OPENAI_ENDPOINT).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Referer", "http://localhost");
        conn.setDoOutput(true);

        // Send request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int statusCode = conn.getResponseCode();
        String contentType = conn.getHeaderField("Content-Type");

        // Check if response is JSON
        if (contentType == null || !contentType.contains("application/json")) {
            System.out.println("Non-JSON response received. Content-Type: " + contentType);
            return "Error: Non-JSON response received from API. Please check the model or endpoint.";
        }

        InputStream inputStream = (statusCode >= 400)
                ? conn.getErrorStream()
                : conn.getInputStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
        }
        System.out.println("log response: " + response.toString());

        // Handle specific HTTP errors
        if (statusCode == 429) {
            return "You are sending too many requests to the system. Please try again in a few seconds.";
        } else if (statusCode == 401) {
            return "Authentication error: Please check your API Key.";
        } else if (statusCode == 404) {
            return "Model not found. Please check the model name or visit https://openrouter.ai/docs for available models.";
        } else if (statusCode >= 400) {
            return "Error from OpenRouter (HTTP " + statusCode + "): " + response;
        }

        // Parse JSON response
        JSONObject res = new JSONObject(response.toString());
        String fullMessage = res.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // Extract SQL from markdown ```sql``` if present
        if (fullMessage.contains("```sql")) {
            int start = fullMessage.indexOf("```sql");
            int end = fullMessage.indexOf("```", start);
            if (end > start) {
                String extractedSQL = fullMessage.substring(start, end).trim();
                System.out.println("SQL extracted from markdown:\n" + extractedSQL);
                return extractedSQL;
            }
        }

        // Fallback: Extract SELECT statement from plain text
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
                    break;
                }
            }
        }

        String extractedSQL = sqlBuilder.toString().trim();

        // Validate SQL
        if (!extractedSQL.toLowerCase().contains("from")) {
            System.out.println("Invalid SQL (missing FROM):\n" + extractedSQL);
            return "NON_SQL_QUERY";
        }

        System.out.println("SQL extracted from plain text block:\n" + extractedSQL);
        return extractedSQL;

    } catch (JSONException e) {
        e.printStackTrace();
        return "Error: Invalid JSON response from API. Please check the model or endpoint.";
    } catch (IOException e) {
        e.printStackTrace();
        return "A connection error occurred while connecting to the API. Please check your network or try again later.";
    } finally {
        if (conn != null) {
            conn.disconnect();
        }
    }
}

    public JSONArray executeDynamicSQL(String sql, String userId) throws SQLException {
        if (sql.equals("RESTRICTED_QUERY")) {
            JSONArray result = new JSONArray();
            result.put(new JSONObject().put("response", "The question is restricted due to a security policy violation."));
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
                    .put("content", "You are a friendly AI assistant for a 5-star hotel. Respond concisely and naturally, without revealing any sensitive information. "
                            + "If the question is unrelated to the hotel, reply politely and suggest returning to hotel-related topics."));

            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", userMessage));

            requestBody.put("model", "qwen/qwen3-coder:free");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.6);

            HttpURLConnection conn = (HttpURLConnection) new URL(OPENAI_ENDPOINT).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            conn.setRequestProperty("Referer", "http://localhost");
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
                .put("content", "You are an AI assistant. Respond in a friendly and concise manner based on the JSON data I provide. Do not disclose any sensitive information."));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", "My request: " + userMessage + "\nData: " + resultData.toString()));

        requestBody.put("model", "qwen/qwen3-coder:free");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.6);

        HttpURLConnection conn = (HttpURLConnection) new URL(OPENAI_ENDPOINT).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Referer", "http://localhost");
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
