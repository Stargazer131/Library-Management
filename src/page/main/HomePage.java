/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package page.main;

import java.awt.Color;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import page.feature.IssueBook;
import page.feature.ManageBook;
import page.feature.ManageStudent;
import page.feature.ReturnBook;
import page.feature.ViewRecord;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import page.feature.Statistic;
import ultility.Recommendator;
import ultility.Resizer;


/**
 *
 * @author Hao
 */
public class HomePage extends javax.swing.JFrame {

    /**
     * Creates new form HomePage
     */
    public HomePage() {
        initComponents();
        getRecommendationTableDetail();
        updateData();
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
    }
    
    // update data
    private void updateData() {
        updateQuantity("books");
        updateQuantity("students");
        updateQuantity("records");
    }
    
    // update quantity
    private void updateQuantity(String type) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select count(*) as quantity from " + type;    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            rs.next();
            int quantity = rs.getInt("quantity");
            if(type.equals("books")) {
                lblBookQuantity.setText(String.valueOf(quantity));
            }
            else if(type.equals("students")) {
                lblStudentQuantity.setText(String.valueOf(quantity));
            }
            else {
                lblRecordQuantity.setText(String.valueOf(quantity));
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
    
    // get data and display Recommendation table
    private void getRecommendationTableDetail() {
        ArrayList<Object[]> data = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) recommendationTable.getModel();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from students";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            model.setRowCount(0);
            while(rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("name");
                Object[] obj = {studentId, studentName, null, null};
                data.add(obj);
            }
        } catch (Exception e1) {
            System.out.println(e1);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (Exception e3) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e4) {
            }
        }
        
        for(int i = 0; i < data.size(); i++) {
            String bookId = Recommendator.getIdOfRecommendBook((String)data.get(i)[0]);
            String title = "None";
            if(bookId == null) {
                bookId = "None";
                title = "None";
            }
            else {
                Connection con1 = null;
                PreparedStatement pst1 = null;
                ResultSet rs1 = null;
                try {
                    con1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                    String sql1 = "select title from books where book_id = ?";    
                    pst1 = con1.prepareStatement(sql1);
                    pst1.setString(1, bookId);
                    rs1 = pst1.executeQuery();
                    while(rs1.next()) {
                        title = rs1.getString("title");
                    }
                } 
                catch(Exception e1) {
                    System.out.println(e1);
                } 
                finally {
                    try { if (rs1 != null) rs1.close(); } catch (Exception e2) {}
                    try { if (pst1 != null) pst1.close(); } catch (Exception e3) {}
                    try { if (con1 != null) con1.close(); } catch (Exception e4) {}
                }
            }
            data.get(i)[2] = bookId;
            data.get(i)[3] = title;
            model.addRow(data.get(i));
        }
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        lblBookQuantity = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        lblStudentQuantity = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        recommendationTable = new rojerusan.RSTableMetro();
        jLabel19 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        lblRecordQuantity = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/menu-bar.png")), 50, 50));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 8, 50, 50));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 5, 50));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/admin.png")), 50, 50));
        jLabel4.setText("Welcome, Admin");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1580, 10, 210, 50));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 25)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Library Management System");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, 50));

        jLabel3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/exit.png")), 50, 50));
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1840, 10, 50, 50));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1900, 70));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel4MouseExited(evt);
            }
        });
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/return.png")), 50, 50));
        jLabel2.setText("Return Book");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 290, 80));

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 370, 350, 80));

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 350, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Features");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 350, 50));

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel6MouseExited(evt);
            }
        });
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/book.png")), 50, 50));
        jLabel8.setText("Mange Books");
        jPanel6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 290, 80));

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 350, 80));

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel7MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel7MouseExited(evt);
            }
        });
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/student.png")), 50, 50));
        jLabel9.setText("Mange Students");
        jPanel7.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 290, 80));

        jPanel3.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 350, 80));

        jPanel8.setBackground(new java.awt.Color(51, 51, 51));
        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel8MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel8MouseExited(evt);
            }
        });
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));
        jLabel10.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/borrow.png")), 50, 50));
        jLabel10.setText("Issue Book");
        jPanel8.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 290, 80));

        jPanel3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 350, 80));

        jPanel9.setBackground(new java.awt.Color(204, 204, 204));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/home-page.png")), 30, 30));
        jLabel11.setText("HOME PAGE");
        jPanel9.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 240, 50));

        jPanel3.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 350, 50));

        jPanel10.setBackground(new java.awt.Color(51, 51, 51));
        jPanel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel10MouseExited(evt);
            }
        });
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(153, 153, 153));
        jLabel12.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/record.png")), 50, 50));
        jLabel12.setText("View Records");
        jPanel10.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 290, 80));

        jPanel3.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 450, 350, 80));

        jPanel13.setBackground(new java.awt.Color(51, 51, 51));
        jPanel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel13MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel13MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel13MouseExited(evt);
            }
        });
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(153, 153, 153));
        jLabel15.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/statistic.png")), 50, 50));
        jLabel15.setText("Statistic");
        jPanel13.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 290, 80));

        jPanel3.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 530, 350, 80));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 350, 930));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 1, 1, 1, new java.awt.Color(255, 51, 51)));
        jPanel15.setToolTipText("");

        lblBookQuantity.setFont(new java.awt.Font("Segoe UI Black", 0, 50)); // NOI18N
        lblBookQuantity.setForeground(new java.awt.Color(102, 102, 102));
        lblBookQuantity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBookQuantity.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/bookshelves.png")), 50, 50));
        lblBookQuantity.setText("1000");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBookQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblBookQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 100, 310, 110));

        jLabel18.setFont(new java.awt.Font("Segoe UI Black", 1, 17)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Number of Students");
        jPanel14.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 310, 40));

        jPanel16.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 1, 1, 1, new java.awt.Color(255, 51, 51)));
        jPanel16.setToolTipText("");

        lblStudentQuantity.setFont(new java.awt.Font("Segoe UI Black", 0, 50)); // NOI18N
        lblStudentQuantity.setForeground(new java.awt.Color(102, 102, 102));
        lblStudentQuantity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStudentQuantity.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/people.png")), 50, 50));
        lblStudentQuantity.setText("1000");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblStudentQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblStudentQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 310, 110));

        jLabel24.setFont(new java.awt.Font("Segoe UI Black", 1, 17)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Number of Books");
        jPanel14.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 60, 310, 40));

        jLabel25.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(102, 102, 102));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/recommendation.png")), 65, 65));
        jLabel25.setText("Book Recommendation for Students");
        jPanel14.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 270, 630, 70));

        recommendationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Name", "Book ID", "Title"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        recommendationTable.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        recommendationTable.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        recommendationTable.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        recommendationTable.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        recommendationTable.setFont(new java.awt.Font("Segoe UI Light", 0, 25)); // NOI18N
        recommendationTable.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        recommendationTable.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        recommendationTable.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        recommendationTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        recommendationTable.setRowHeight(40);
        jScrollPane3.setViewportView(recommendationTable);

        jPanel14.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 1480, 570));

        jLabel19.setFont(new java.awt.Font("Segoe UI Black", 1, 17)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(102, 102, 102));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Number of borrowed books");
        jPanel14.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 60, 310, 40));

        jPanel17.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 1, 1, 1, new java.awt.Color(255, 51, 51)));
        jPanel17.setToolTipText("");

        lblRecordQuantity.setFont(new java.awt.Font("Segoe UI Black", 0, 50)); // NOI18N
        lblRecordQuantity.setForeground(new java.awt.Color(102, 102, 102));
        lblRecordQuantity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRecordQuantity.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/record.png")), 50, 50));
        lblRecordQuantity.setText("1000");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblRecordQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblRecordQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, 310, 110));

        getContentPane().add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 1550, 930));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel6.setBackground(Color.lightGray);
    }//GEN-LAST:event_jPanel6MouseEntered

    private void jPanel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanel6.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_jPanel6MouseExited

    private void jPanel7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel7.setBackground(Color.lightGray);
    }//GEN-LAST:event_jPanel7MouseEntered

    private void jPanel7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanel7.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_jPanel7MouseExited

    private void jPanel8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel8.setBackground(Color.lightGray);
    }//GEN-LAST:event_jPanel8MouseEntered

    private void jPanel8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanel8.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_jPanel8MouseExited

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel4.setBackground(Color.lightGray);
    }//GEN-LAST:event_jPanel4MouseEntered

    private void jPanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanel4.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_jPanel4MouseExited

    private void jPanel10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel10.setBackground(Color.lightGray);
    }//GEN-LAST:event_jPanel10MouseEntered

    private void jPanel10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanel10.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_jPanel10MouseExited

    private void jPanel13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel13.setBackground(Color.lightGray);
    }//GEN-LAST:event_jPanel13MouseEntered

    private void jPanel13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanel13.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_jPanel13MouseExited

    private void jPanel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseClicked
        Statistic page = new Statistic();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel13MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        ManageBook page = new ManageBook(true, true);
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        ManageStudent page = new ManageStudent(true, true);
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel7MouseClicked

    private void jPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseClicked
        IssueBook page = new IssueBook();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel8MouseClicked

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        ReturnBook page = new ReturnBook();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel4MouseClicked

    private void jPanel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseClicked
        ViewRecord page = new ViewRecord(true);
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel10MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel3MouseClicked

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
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblBookQuantity;
    private javax.swing.JLabel lblRecordQuantity;
    private javax.swing.JLabel lblStudentQuantity;
    private rojerusan.RSTableMetro recommendationTable;
    // End of variables declaration//GEN-END:variables
}
