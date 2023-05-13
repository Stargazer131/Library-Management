/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultility;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 *
 * @author Hao
 */
public class Updater {
    public static void updateOverdueBook() {
        Date today = Date.valueOf(LocalDate.now());
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "update records "
                        + "set status = ? "
                        + "where ? > due_date and return_date is null";    
            pst = con.prepareStatement(sql);
            pst.setString(1, "Overdue");
            pst.setDate(2, today);
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.out.println("Error update");
            }
        } 
        catch(Exception e1) {
            System.out.println(e1);
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
        }
    }
}
