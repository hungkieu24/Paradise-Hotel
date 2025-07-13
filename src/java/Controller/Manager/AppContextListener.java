/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller.Manager;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
/**
 *
 * @author hungk
 */
@WebListener
public class AppContextListener implements ServletContextListener{
     @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🔄 AppContextListener started: Initializing scheduled tasks...");
        SystemRevenueScheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 AppContextListener shutting down...");
        // Bạn có thể dừng scheduler ở đây nếu muốn
    }
}
