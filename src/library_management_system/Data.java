/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library_management_system;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Hao
 */
public class Data {
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//
//    }
    
    private static void addPayFineData() {
        HashMap<Integer, Date> map = new HashMap<>();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select record_id, return_date "
                        + "from records where record_id in (select record_id from fines)";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()) {
                map.put(rs.getInt("record_id"), new Date(rs.getDate("return_date").getTime()));
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
        
        for(Map.Entry<Integer, Date> entry : map.entrySet()) {
            int recordId = entry.getKey();
            Date fineDate = entry.getValue();
            String status;
            
            Date payDate = randomDate(new GregorianCalendar(2023, Calendar.APRIL, 15), new GregorianCalendar(2023, Calendar.MAY, 15));
            if(payDate.compareTo(fineDate) >= 0) {
                status = "Paid";
            }
            else {
                status = "Unpaid";
                payDate = null;
            }
            
            con = null;
            pst = null;
            rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                String sql = "update fines "
                            + "set status = ?, fine_date = ?, pay_date = ? "
                            + "where record_id = ?";    
                pst = con.prepareStatement(sql);
                pst.setString(1, status);
                pst.setDate(2, convertUtilDateToSqlDate(fineDate));
                pst.setDate(3, convertUtilDateToSqlDate(payDate));
                pst.setInt(4, recordId);
                pst.executeUpdate();
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
    
    private static void addFineData() {
        HashMap<Integer, Integer> map = new HashMap<>();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select record_id, DATEDIFF(return_date, due_date) AS DateDiff \n" +
                        "from records\n" +
                        "where status = 'Late Returned'";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()) {
                map.put(rs.getInt("record_id"), rs.getInt("DateDiff"));
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
        
        for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int recordId = entry.getKey();
            int dateDiff = entry.getValue();
            float amount = dateDiff*0.2F;
            String reason = String.format("Return late %d days", dateDiff); 
            
            con = null;
            pst = null;
            rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                String sql = "insert into fines(record_id, amount, reason) values(?,?,?)";    
                pst = con.prepareStatement(sql);
                pst.setInt(1, recordId);
                pst.setFloat(2, amount);
                pst.setString(3, reason);
                pst.executeUpdate();
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
    
    private static void addRecordData() {
               
        ArrayList<String> bookIds = readBookData();
        ArrayList<String> studentIds = readStudentData();
        HashSet<String> recordIds = readRecordData();

        int Overdue = 0;
        int Pending = 0;
        int Late_Returned = 0;
        int Timely_Returned = 0;
        
        for(int i = 1; i <= 100; i++) {
            String bookId = bookIds.get((int)Math.round(Math.random()*(bookIds.size()-1)));
            String studentId = studentIds.get((int)Math.round(Math.random()*(studentIds.size()-1)));
            String combined = bookId+"|"+studentId;
            if(!recordIds.contains(combined)) {
                recordIds.add(combined);
                System.out.println(combined);
                
                Date today = new Date();
                Date issueDate = randomDate(new GregorianCalendar(2023, Calendar.APRIL, 15), new GregorianCalendar(2023, Calendar.MAY, 15));
                Date dueDate = addDaysToDate(issueDate, 14);
                Date returnDate = randomDate(new GregorianCalendar(2023, Calendar.APRIL, 15), new GregorianCalendar(2023, Calendar.MAY, 15));
                String status;
                
                if(returnDate.compareTo(issueDate) < 0) {
                    if(today.compareTo(dueDate) > 0) {
                        status = "Overdue";
                    }
                    else {
                        status = "Pending";

                    }
                    returnDate = null;
                }
                else {
                    if(returnDate.compareTo(dueDate) > 0) {
                        status = "Late Returned";
                    }
                    else {
                        status = "Timely Returned";
                    }
                }
                
                Connection con = null;
                PreparedStatement pst = null;
                ResultSet rs = null;
                try {
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                    String sql = "insert into records(book_id,student_id,issue_date,due_date,status,return_date) values(?,?,?,?,?,?)";    
                    pst = con.prepareStatement(sql);
                    pst.setString(1, bookId);
                    pst.setString(2, studentId);
                    pst.setDate(3, convertUtilDateToSqlDate(issueDate));
                    pst.setDate(4, convertUtilDateToSqlDate(dueDate));
                    pst.setString(5, status);
                    pst.setDate(6, convertUtilDateToSqlDate(returnDate));
                    int row = pst.executeUpdate();
                } 
                catch(Exception e1) {
                    System.out.println(e1);
                } 
                finally {
                    try { if (rs != null) rs.close(); } catch (Exception e2) {}
                    try { if (pst != null) pst.close(); } catch (Exception e3) {}
                    try { if (con != null) con.close(); } catch (Exception e4) {}
                }
                
//                System.out.println(dateToString(issueDate));
//                System.out.println(dateToString(dueDate));
//                System.out.println(dateToString(returnDate));
            }
        }
        
        System.out.println("Overdue: " + Overdue);
        System.out.println("Pending: " + Pending);
        System.out.println("Late_Returned: " + Late_Returned);
        System.out.println("Timely_Returned: " + Timely_Returned);
        
    }
    
    private static ArrayList<String> readBookData() {
        ArrayList<String> arr = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("D:\\Libray MS Data\\BOOK_ID.json")) {
            Object obj = parser.parse(reader);

            // If the JSON file represents an array
            JSONArray jsonArray = (JSONArray) obj;
            
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                arr.add((String)jsonObject.get("book_id"));
            }

            // Now you can work with the parsed JSON object or array
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    
    private static ArrayList<String> readStudentData() {
        ArrayList<String> arr = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("D:\\Libray MS Data\\STUDENT_ID.json")) {
            Object obj = parser.parse(reader);

            // If the JSON file represents an array
            JSONArray jsonArray = (JSONArray) obj;
            
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                arr.add((String)jsonObject.get("student_id"));
            }

            // Now you can work with the parsed JSON object or array
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    
    private static HashSet<String> readRecordData() {
        HashSet<String> arr = new HashSet<>();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("D:\\Libray MS Data\\RECORD_ID.json")) {
            Object obj = parser.parse(reader);

            // If the JSON file represents an array
            JSONArray jsonArray = (JSONArray) obj;
            
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                String combined = (String)jsonObject.get("book_id")+"|"+(String)jsonObject.get("student_id");
                arr.add(combined);
            }

            // Now you can work with the parsed JSON object or array
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    
    private static Date randomDate(GregorianCalendar gc1, GregorianCalendar gc2) {
        Calendar start = gc1;
        Calendar end = gc2;

        long startTimeMillis = start.getTimeInMillis();
        long endTimeMillis = end.getTimeInMillis();

        Random random = new Random();
        long randomTimeMillis = startTimeMillis + (long) (random.nextDouble() * (endTimeMillis - startTimeMillis));

        Date randomDate = new Date(randomTimeMillis);
        return randomDate;
    }
    
    private static Date addDaysToDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
    
    private static String dateToString(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(date);
        }
        catch(Exception e) {
            return null;
        }
    }
    
    private static java.sql.Date convertUtilDateToSqlDate(Date date) {
        try {
            return new java.sql.Date(date.getTime());
        }
        catch(Exception e) {
            return null;
        }
    }
}
