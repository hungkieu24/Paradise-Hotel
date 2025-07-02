/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

/**
 *
 * @author KTC
 */
public class TierUtil {

    public static int tierRank(String tier) {
        return switch (tier) {
            case "VIP" ->
                4;
            case "Gold" ->
                3;
            case "Silver" ->
                2;
            case "Member" ->
                1;
            default ->
                0;
        };
    }

    public static double discountRate(String tier) {
        return switch (tier) {
            case "VIP" ->
                0.30;
            case "Gold" ->
                0.20;
            case "Silver" ->
                0.10;
            default ->
                0.0; // Member hoặc không xác định
        };
    }

}
