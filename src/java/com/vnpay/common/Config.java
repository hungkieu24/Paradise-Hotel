package com.vnpay.common;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.crypto.Mac;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * @author CTT VNPAY
 */
public class Config {

    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/ParadiseHotel/payment-result";
    public static String vnp_TmnCode = "4YUP19I4";
    public static String secretKey = "MDUIFDCRAKLNBPOFIAFNEKFRNMFBYEPX";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String vnp_OrderType = "other";

    public static String md5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    public static String Sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    //Util for VNPAY
    //Util for VNPAY - SỬA LẠI ĐỂ DÙNG secretKey
    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    // THÊM METHOD hmacSHA512 nếu chưa có
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static class RefundResult {

        boolean success = false;
        String message = "";
        String responseCode = "";
        String txnRef = "";
        String transactionNo = "";
    }
    // SỬA METHOD hashAllFieldsDebug - DÙNG secretKey THAY VÌ vnp_HashSecret

    public static String hashAllFieldsDebug(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        System.out.println("=== HASH DEBUG INFO ===");
        System.out.println("Fields to hash (sorted): " + fieldNames);

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);

            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName);
                hashData.append('=');

                // THỬ URL ENCODE
                try {
                    String encodedValue = URLEncoder.encode(fieldValue, "UTF-8");
                    hashData.append(encodedValue);
                    System.out.println("Added to hash: " + fieldName + "=" + fieldValue + " (encoded: " + encodedValue + ")");
                } catch (Exception e) {
                    hashData.append(fieldValue);
                    System.out.println("Added to hash: " + fieldName + "=" + fieldValue + " (no encoding)");
                }

                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        System.out.println("Final hash string: " + hashData.toString());
        System.out.println("Secret key: " + secretKey);

        try {
            String result = hmacSHA512(secretKey, hashData.toString());
            System.out.println("Generated hash with encoding: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error creating hash: " + e.getMessage());
            return "";
        }
    }

    public static String hashAllFieldsNoEncoding(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);

            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue); // KHÔNG ENCODE

                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        System.out.println("Hash string (no encoding): " + hashData.toString());

        try {
            String result = hmacSHA512(secretKey, hashData.toString());
            System.out.println("Generated hash (no encoding): " + result);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    private String callVNPayRefundAPI(Map<String, String> params) throws Exception {
        URL url = new URL(Config.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String jsonRequest = new com.google.gson.Gson().toJson(params);
        System.out.println("JSON Request: " + jsonRequest);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonRequest.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }

    private RefundResult parseVNPayRefundResponse(String response) {
        RefundResult result = new RefundResult();

        try {
            System.out.println("VNPay Refund Response: " + response);

            com.google.gson.JsonObject responseJson = com.google.gson.JsonParser.parseString(response).getAsJsonObject();
            String responseCode = responseJson.get("vnp_ResponseCode").getAsString();

            result.responseCode = responseCode;
            result.message = responseJson.has("vnp_Message") ? responseJson.get("vnp_Message").getAsString() : "Unknown";
            result.transactionNo = responseJson.has("vnp_TransactionNo") ? responseJson.get("vnp_TransactionNo").getAsString() : "";

            if ("00".equals(responseCode)) {
                result.success = true;
                System.out.println("✅ VNPay refund successful!");
            } else {
                result.success = false;
                System.out.println("❌ VNPay refund failed: " + responseCode + " - " + result.message);
            }

        } catch (Exception e) {
            result.success = false;
            result.message = "Parse response failed: " + e.getMessage();
            e.printStackTrace();
        }

        return result;
    }
}
