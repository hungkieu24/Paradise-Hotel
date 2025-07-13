/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller.Manager;

import Dal.RevenueDAO;
import java.util.concurrent.*;
import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author hungk
 */
public class SystemRevenueScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        Runnable task = () -> {
            new RevenueDAO().exportSystemRevenueDaily();
            System.out.println("✅ Exported SYSTEM revenue at " + new Date());
        };

        // Tính thời gian delay tới 2:00 AM ngày tiếp theo
        long delay = computeInitialDelay(2, 0); // giờ:phút
        long period = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(task, delay, period, TimeUnit.SECONDS);
    }

    private static long computeInitialDelay(int targetHour, int targetMinute) {
        Calendar now = Calendar.getInstance();
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, targetHour);
        nextRun.set(Calendar.MINUTE, targetMinute);
        nextRun.set(Calendar.SECOND, 0);

        if (now.after(nextRun)) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1);
        }

        return (nextRun.getTimeInMillis() - now.getTimeInMillis()) / 1000;
    }
}
