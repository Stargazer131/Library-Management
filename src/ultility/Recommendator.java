/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Hao
 */
public class Recommendator {
    private static ArrayList<String> listOfGenre;
    private static HashMap<String, Integer> mapOfGenre = getMapAndListOfGenre();
    
    private static ArrayList<String> listOfBook;
    private static HashMap<String, Integer> mapOfBook = getMapAndListOfBook();
    
    private static int[][] bookMatrix = generateBookMatrix();
    private static double[][] cosineSimilarityMatrix = generateCosineSimilarityMatrix();
    
    public static void updateData() {
        mapOfGenre = getMapAndListOfGenre();
        mapOfBook = getMapAndListOfBook();
        bookMatrix = generateBookMatrix();
        cosineSimilarityMatrix = generateCosineSimilarityMatrix();
    }
    
    public static String getIdOfRecommendBook(String studentId) {
        try {
            String bookId = getIdOfLastestBorrowBook(studentId);
            HashSet<String> unBorrowedBooks = getListOfUnBorrowedBook(studentId);
            int bookIdIndex = mapOfBook.get(bookId);
            int max = -1;
            for(int i = 0; i < listOfBook.size(); i++) {
                if(unBorrowedBooks.contains(listOfBook.get(i))) {
                    if(max == -1) {
                        max = i;
                    }
                    else {
                        if(cosineSimilarityMatrix[bookIdIndex][i] > cosineSimilarityMatrix[bookIdIndex][max]) {
                            max = i;
                        }
                    }
                }
            }
            return (max != -1) ? listOfBook.get(max) : null;
        }
        catch(Exception e) {
            return null;
        }
    }
    
    // get all the genres of a book
    private static ArrayList<String> getAllGenreOfBook(String bookId) {
        ArrayList<String> genres = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select name from "
                        + "books natural join book_genres natural join genres "
                        + "where book_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            rs = pst.executeQuery();
            while(rs.next()) {
                genres.add(rs.getString("name"));
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
        return genres;
    }
    
    // get list of unborrowed book from a student
    private static HashSet<String> getListOfUnBorrowedBook(String studentId) {
        HashSet<String> books = new HashSet<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select book_id from books "
                        + "where book_id not in "
                        + "(select distinct(book_id) "
                        + "from books natural join records natural join students "
                        + "where student_id = ?)";    
            pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
            rs = pst.executeQuery();
            while(rs.next()) {
                books.add(rs.getString("book_id"));
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
        return books;
    }
        
    // get the book_id of the lastest book that student borrow 
    private static String getIdOfLastestBorrowBook(String studentId) {
        String bookId = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select book_id from "
                        + "books natural join records natural join students "
                        + "where student_id = ? "
                        + "order by record_id desc "
                        + "limit 1";    
            pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
            rs = pst.executeQuery();
            while(rs.next()) {
                bookId = rs.getString("book_id");
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
        return bookId;
    }

    // get all the genres from database
    private static HashMap<String, Integer> getMapAndListOfGenre() {
        HashMap<String, Integer> genre = new HashMap<>();
        listOfGenre = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select name from genres";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            int count = 0;
            while(rs.next()) {
                String name = rs.getString("name");
                genre.put(name, count++);
                listOfGenre.add(name);
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
        return genre;
    }
    
    // get all the books from database
    private static HashMap<String, Integer> getMapAndListOfBook() {
        HashMap<String, Integer> books = new HashMap<>();
        listOfBook = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select book_id from books";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            int count = 0;
            while(rs.next()) {
                String bookId = rs.getString("book_id");
                books.put(bookId, count++);
                listOfBook.add(bookId);
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
        return books;
    }
    
    // create TF-IDF book matrix
    private static int[][] generateBookMatrix() {
        int[][] matrix = new int[listOfBook.size()][listOfGenre.size()];
        for(int i = 0; i < listOfBook.size(); i++) {
            ArrayList<String> genres = getAllGenreOfBook(listOfBook.get(i));
            for(String genre : genres) {
                matrix[i][mapOfGenre.get(genre)] = 1;
            }
        }
        return matrix;
    }
    
    // create cosine similarity book matrix
    private static double[][] generateCosineSimilarityMatrix() {
        int length = listOfBook.size();
        double[][] matrix = new double[length][length];
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++) {
                if(i == j) {
                    matrix[i][j] = 1.0;
                }
                else if(j > i) {
                    matrix[i][j] = cosineSimilarity(bookMatrix[i], bookMatrix[j]);
                }
                else {
                    matrix[i][j] = matrix[j][i];
                }
            }
        }
        return matrix;
    }
    
    // caculate the cosine similarity 
    private static double cosineSimilarity(int[] vectorA, int[] vectorB) {
        double dotProduct = 0.0; // tich vo huong
        double normA = 0.0; // do dai vector
        double normB = 0.0;

        for(int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return similarity;
    }
}
