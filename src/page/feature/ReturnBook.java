/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package page.feature;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.sql.Date;
import javax.swing.JOptionPane;
import page.main.HomePage;
import ultility.Formatter;
import ultility.Resizer;
import javax.swing.ImageIcon;
import ultility.Updater;

/**
 *
 * @author Hao
 */
public class ReturnBook extends javax.swing.JFrame {

    /**
     * Creates new form IssueBook
     */
    public ReturnBook() {
        initComponents();
        setOverdueBook();
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
    }
    
    // valdation
    private boolean validateData() {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        
        if(bookId.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Book Id");
            return false;
        }
        
        if(studentId.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Student Id");
            return false;
        } 
        return true;
    }
    
    // check if the record is already existed
    private int chechExistedRecord() {
        // -1 -> can't find the record 
        // 0 -> find a record but the book is already returned
        // 1 -> find a record which is pending or overdue
        
        int exist = -1; 
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select status from records "
                        + "where book_id = ? and student_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, studentId);
            rs = pst.executeQuery();
            while(rs.next()) {
                exist = 0;
                String status = rs.getString("status");
                if(status.equals("Pending") || status.equals("Overdue")) {
                    exist = 1;
                }
            }
            return exist;
        } 
        catch(Exception e1) {
            System.out.println(e1);
            return exist;
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
        }
    }
    
    // get and display issue detail
    private void getIssueDetail() {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select title, name, issue_date, due_date "
                        + "from books natural join records natural join students "
                        + "where book_id = ? and student_id = ? and (status = ? or status = ?)";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, studentId);
            pst.setString(3, "Pending");
            pst.setString(4, "Overdue");

            rs = pst.executeQuery();
            rs.next();
            
            lblBookName.setText(rs.getString("title"));
            lblStudentName.setText(rs.getString("name"));
            Date issueDate = rs.getDate("issue_date");
            Date dueDate = rs.getDate("due_date");
            lblIssueDate.setText(Formatter.dateToString(issueDate));
            lblDueDate.setText(Formatter.dateToString(dueDate));
            
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
    
    // update issue detail
    private void updateIssueDetail(String status) {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        Date today = Date.valueOf(LocalDate.now());
        String returnStatus = "Timely Returned";
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "update records "
                        + "set status = ?, return_date = ?"
                        + "where book_id = ? and student_id = ? and status = ?";    
            pst = con.prepareStatement(sql);
            if(status.equals("Overdue")) {
                returnStatus = "Late Returned";
            }
            
            pst.setString(1, returnStatus);
            pst.setDate(2, today);
            pst.setString(3, bookId);
            pst.setString(4, studentId);
            pst.setString(5, status);      
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.err.println("Update error");
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
    
    // check if the book is overdue
    private boolean isBookOverdue() {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        boolean isOverDue = false;
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select status from records "
                        + "where book_id = ? and student_id = ? and status = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, studentId);
            pst.setString(3, "Overdue");              
            rs = pst.executeQuery();
            if(rs.next()) {
                isOverDue = true;
            }
            return isOverDue;
        } 
        catch(Exception e1) {
            System.out.println(e1);
            return isOverDue;
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
        }
    }
    
    // update all the overdue book
    private void setOverdueBook() {
        Updater.updateOverdueBook();
    }
    
    // increase the quantity of book in database
    private void increaseBookQuantity(int quantity) {
        String bookId = txtBookId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "update books "
                        + "set quantity = quantity+? "
                        + " where book_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setInt(1, quantity);
            pst.setString(2, bookId);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.out.println("Error updating");
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
    
    // 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblBookName = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblStudentName = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblIssueDate = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblDueDate = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtStudentId = new app.bolivia.swing.JCTextField();
        jLabel23 = new javax.swing.JLabel();
        txtBookId = new app.bolivia.swing.JCTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 25)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/borrow.png")), 100, 100));
        jLabel1.setText("Issue Details");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 350, 100));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/name.png")), 45, 45));
        jLabel5.setText("Title:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 320, 50));

        lblBookName.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblBookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 450, 60));

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/name.png")), 45, 45));
        jLabel8.setText("Name:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 320, 50));

        lblStudentName.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblStudentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 450, 60));

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/calender.png")), 45, 45));
        jLabel10.setText("Issue Date:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, 320, 50));

        lblIssueDate.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblIssueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 540, 450, 60));

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/calender.png")), 45, 45));
        jLabel11.setText("Due Date:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, 320, 50));

        lblDueDate.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblDueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 680, 450, 60));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 220, 50, 50));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 0, 480, 1000));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/return.png")), 50, 50));
        jLabel21.setText("Return Books");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(1380, 90, 310, 50));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 51, 51));
        jLabel22.setText("Student ID");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(1620, 200, 200, 30));

        txtStudentId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 51, 51)));
        txtStudentId.setPlaceholder("Enter Student Id ...");
        txtStudentId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtStudentIdFocusLost(evt);
            }
        });
        txtStudentId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStudentIdActionPerformed(evt);
            }
        });
        jPanel1.add(txtStudentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1620, 240, 240, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 51, 51));
        jLabel23.setText("Book ID");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 200, 200, 30));

        txtBookId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 51, 51)));
        txtBookId.setPlaceholder("Enter Book Id ...");
        txtBookId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBookIdFocusLost(evt);
            }
        });
        txtBookId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBookIdActionPerformed(evt);
            }
        });
        jPanel1.add(txtBookId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 240, 240, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/image/library-background-1.jpg"))); // NOI18N
        jLabel4.setText("jLabel4");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 680, 930));

        jLabel6.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1560, 220, 50, 50));

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 220, 50, 50));

        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/return.png")), 50, 50));
        jButton2.setText("RETURN");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1600, 380, 190, 70));

        jButton3.setBackground(new java.awt.Color(153, 204, 255));
        jButton3.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/record.png")), 50, 50));
        jButton3.setText("RECORD");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1450, 580, 190, 70));

        jLabel3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/exit.png")), 50, 50));
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1850, 0, 50, 50));

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reset.png")), 75, 75));
        jButton6.setBorder(null);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 0, 75, 75));

        jButton4.setBackground(new java.awt.Color(153, 204, 255));
        jButton4.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/search.png")), 50, 50));
        jButton4.setText("FIND");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 380, 190, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1900, 1000));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtStudentIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentIdFocusLost

    }//GEN-LAST:event_txtStudentIdFocusLost

    private void txtStudentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentIdActionPerformed

    private void txtBookIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookIdFocusLost

    }//GEN-LAST:event_txtBookIdFocusLost

    private void txtBookIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBookIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookIdActionPerformed

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        HomePage page = new HomePage();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(validateData()) {
            int exist = chechExistedRecord();
            switch (exist) {
                case 0:
                    JOptionPane.showMessageDialog(this, "This book has already returned by this student!");
                    break;
                case 1:
                    if(isBookOverdue()) {
                        updateIssueDetail("Overdue");
                    }
                    else {
                        updateIssueDetail("Pending");
                    }   increaseBookQuantity(1);
                    JOptionPane.showMessageDialog(this, "Return book successfully!");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Can't find the record!");
                    break;
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        ViewRecord page = new ViewRecord(false);
        page.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        lblBookName.setText("");
        lblStudentName.setText("");
        txtBookId.setText("");
        txtStudentId.setText("");
        lblIssueDate.setText("");
        lblDueDate.setText("");
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(validateData()) {
            int exist = chechExistedRecord();
            if(exist == 0) {
                JOptionPane.showMessageDialog(this, "This book has already returned by this student!");
            }
            else if(exist == 1) {
                getIssueDetail();
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find the record!");
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReturnBook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBookName;
    private javax.swing.JLabel lblDueDate;
    private javax.swing.JLabel lblIssueDate;
    private javax.swing.JLabel lblStudentName;
    private app.bolivia.swing.JCTextField txtBookId;
    private app.bolivia.swing.JCTextField txtStudentId;
    // End of variables declaration//GEN-END:variables
}
