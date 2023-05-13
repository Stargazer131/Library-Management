/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library_management_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.TreeSet;

/**
 *
 * @author Hao
 */
public class Test {
//    public static void main(String[] args) {
//   String data = "Romance\n" +
//"Mystery\n" +
//"Thriller\n" +
//"Science Fiction\n" +
//"Fantasy\n" +
//"Adult\n" +
//"Horror\n" +
//"Crime\n" +
//"Children\n" +
//"Adventure\n" +
//"Nonfiction\n" +
//"Biography\n" +
//"Self-help\n" +
//"Business\n" +
//"History\n" +
//"Memoir\n" +
//"Religion\n" +
//"Travel\n" +
//"Art\n" +
//"Poetry\n" +
//"Psychology\n" +
//"Philosophy\n" +
//"Education\n" +
//"Health\n" +
//"Family\n" +
//"Sports\n" +
//"Politics\n" +
//"Crime\n" +
//"Comedy\n" +
//"Erotica\n" +
//"Military\n" +
//"Comics\n" +
//"Drama\n" +
//"Music\n" +
//"LGBTQ+\n" +
//"Medical\n" +
//"Fantasy\n" +
//"Classics\n" +
//"Cultural\n" +
//"Fashion\n" +
//"Food\n" +
//"Nature\n" +
//"Pets\n" +
//"Technology\n" +
//"Journalism\n" +
//"Design\n" +
//"Ecology\n" +
//"Film\n" +
//"Health\n" +
//"Mathematics\n";
//            
//            String[] genress = data.split("\\n");
//            TreeSet<String> genres = new TreeSet<>();
//            genres.addAll(Arrays.asList(genress));
//            
//            for(String genre : genres) {
//                Connection con1 = null;
//                PreparedStatement pst1 = null;
//                ResultSet rs1 = null;
//                try {
//                    con1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
//                    String sql1 = "insert into genres(name) value(?)";    
//                    pst1 = con1.prepareStatement(sql1);
//                    pst1.setString(1, genre);
//                    pst1.executeUpdate();
//                } 
//                catch(Exception e1) {
//                    System.out.println(e1);
//                } 
//                finally {
//                    try { if (rs1 != null) rs1.close(); } catch (Exception e2) {}
//                    try { if (pst1 != null) pst1.close(); } catch (Exception e3) {}
//                    try { if (con1 != null) con1.close(); } catch (Exception e4) {}
//                }
//            }
//    }
}
