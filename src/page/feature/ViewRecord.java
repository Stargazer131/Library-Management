/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package page.feature;

import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.Font;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;
import page.main.HomePage;
import ultility.Formatter;
import ultility.Resizer;
import javax.swing.ImageIcon;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import ultility.ComparatorType;
import ultility.Updater;

/**
 *
 * @author Hao
 */
public class ViewRecord extends javax.swing.JFrame {

    private boolean comeBackToHomePage;
    /**
     * Creates new form ViewRecords
     */
    public ViewRecord(boolean comeBackToHomePage) {
        this.comeBackToHomePage = comeBackToHomePage;
        setOverdueBook();
        initComponents();
        formatDatePicker();
        getRecordsDetail();
        sortByColumn();
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
    }

    // format date
    private void formatDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setAllowKeyboardEditing(false);
        Font font = new Font("Tahoma", 0, 17);
        dateSettings.setFontCalendarDateLabels(font);
        dateSettings.setFontCalendarWeekdayLabels(font);
        dateSettings.setFontMonthAndYearMenuLabels(font);
        dateSettings.setFontValidDate(font);
        DatePickerSettings cloneSettings = dateSettings.copySettings();
        issueDatePicker.setSettings(dateSettings);
        dueDatePicker.setSettings(cloneSettings);
    }

    // set and display records detail
    private void getRecordsDetail() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * "
                    + "from books natural join records natural join students "
                    + "order by record_id desc";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) recordTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                int recordId = rs.getInt("record_id");
                String bookId = rs.getString("book_id");
                String bookName = rs.getString("title");
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("name");
                String issueDate = Formatter.dateToString(rs.getDate("issue_date"));
                String dueDate = Formatter.dateToString(rs.getDate("due_date"));
                String returnDate = Formatter.dateToString(rs.getDate("return_date"));
                String status = rs.getString("status");

                Object[] obj = {recordId, bookId, bookName, studentId, studentName, issueDate, dueDate, status, returnDate};
                model.addRow(obj);
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
    }

    // update all the overdue book
    private void setOverdueBook() {
        Updater.updateOverdueBook();
    }

    // search by data provied by user
    private void search() {
        int recordId, min = 1, max = Integer.MAX_VALUE;
        try {
            recordId = Integer.parseInt(txtRecordId.getText());
            min = recordId;
            max = recordId;
        }
        catch(NumberFormatException nfe) {
            
        }
        
        String bookId = txtBookId.getText(), bookName = txtBookName.getText();
        String studentId = txtStudentId.getText(), studentName = txtStudentName.getText();
        String status = statusBox.getSelectedItem().toString();
        Date issueDate = Date.valueOf("0001-01-01");
        Date dueDate = Date.valueOf("3000-01-01");

        if (status.equals("All")) {
            status = "";
        }

        if (issueDatePicker.getDate() != null) {
            issueDate = Date.valueOf(issueDatePicker.getDate());
        }

        if (dueDatePicker.getDate() != null) {
            dueDate = Date.valueOf(dueDatePicker.getDate());
        }

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            bookId = bookId.toLowerCase();
            bookName = bookName.toLowerCase();
            studentId = studentId.toLowerCase();
            studentName = studentName.toLowerCase();

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select record_id, book_id, title, student_id, name, "
                    + "issue_date, due_date, status, return_date "
                    + "from books natural join records natural join students "
                    + "where record_id between ? and ? and lower(book_id) like ? and lower(title) like ? and "
                    + "lower(student_id) like ? and lower(name) like ? and "
                    + "status like ? and issue_date >= ? and due_date <= ? "
                    + "order by record_id desc";

            pst = con.prepareStatement(sql);
            pst.setInt(1, min);
            pst.setInt(2, max);
            pst.setString(3, "%" + bookId + "%");
            pst.setString(4, "%" + bookName + "%");
            pst.setString(5, "%" + studentId + "%");
            pst.setString(6, "%" + studentName + "%");
            pst.setString(7, "%" + status + "%");
            pst.setDate(8, issueDate);
            pst.setDate(9, dueDate);
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) recordTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                int recordIdData = rs.getInt("record_id");
                String bookIdData = rs.getString("book_id");
                String bookNameData = rs.getString("title");
                String studentIdData = rs.getString("student_id");
                String studentNameData = rs.getString("name");
                String issueDateData = Formatter.dateToString(rs.getDate("issue_date"));
                String dueDateData = Formatter.dateToString(rs.getDate("due_date"));
                String returnDate = Formatter.dateToString(rs.getDate("return_date"));
                String statusData = rs.getString("status");

                Object[] obj = {recordIdData, bookIdData, bookNameData, studentIdData, studentNameData, issueDateData, dueDateData, statusData, returnDate};
                model.addRow(obj);
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
    }
    
    // sort column 
    private void sortByColumn() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(recordTable.getModel());
        recordTable.setRowSorter(sorter);
        
        Comparator<Object> comparatorDate = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Date d1 = Formatter.stringToDate(o1.toString());
                Date d2 = Formatter.stringToDate(o2.toString());
                if(d1 == null && d2 == null) return 0;
                else if(d1 == null) return -1;
                else if(d2 == null) return 1;
                else return d1.compareTo(d2);
            }
        };
        
        Comparator<Object> comparator3 = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.compare(Integer.parseInt(o1.toString()), Integer.parseInt(o2.toString()));
            }
        };

        sorter.setComparator(0, comparator3);
        for(int i = 1; i < 8; i++) {
            if(i != 5 && i != 6) {
                sorter.setComparator(i, ComparatorType.STRING);
            }
            else {
                sorter.setComparator(i, ComparatorType.DATE);
            }
        }
        sorter.setComparator(8, comparatorDate);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        recordTable = new rojerusan.RSTableMetro();
        jLabel21 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        dueDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        issueDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        statusBox = new javax.swing.JComboBox<>();
        txtBookId = new app.bolivia.swing.JCTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtBookName = new app.bolivia.swing.JCTextField();
        jLabel10 = new javax.swing.JLabel();
        txtStudentId = new app.bolivia.swing.JCTextField();
        txtStudentName = new app.bolivia.swing.JCTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtRecordId = new app.bolivia.swing.JCTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        recordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Record ID", "Book ID", "Title", "Student ID", "Name", "Issue Date", "Due Date", "Status", "Return Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        recordTable.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        recordTable.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        recordTable.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        recordTable.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        recordTable.setFont(new java.awt.Font("Segoe UI Light", 0, 25)); // NOI18N
        recordTable.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        recordTable.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        recordTable.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        recordTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        recordTable.setRowHeight(40);
        recordTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recordTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(recordTable);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 1860, 640));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/record.png")), 50, 50));
        jLabel21.setText("All records");
        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 40, 310, 80));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 1900, 810));

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dueDatePicker.setToolTipText("");
        jPanel2.add(dueDatePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 240, 30));

        issueDatePicker.setToolTipText("");
        jPanel2.add(issueDatePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 240, 30));

        jLabel3.setText("DUE DATE");
        jLabel3.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 70, 30));

        jLabel5.setText("Book ID");
        jLabel5.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 90, 30));

        jLabel6.setText("Status");
        jLabel6.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 20, 50, 30));

        statusBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Timely Returned", "Late Returned", "Pending", "Overdue" }));
        statusBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        statusBox.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        statusBox.setForeground(new java.awt.Color(51, 51, 51));
        jPanel2.add(statusBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 50, 170, 30));

        txtBookId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 2, 0, new java.awt.Color(255, 0, 0)));
        txtBookId.setPlaceholder("Enter Book ID");
        jPanel2.add(txtBookId, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, 230, -1));

        jLabel7.setText("ISSUE DATE");
        jLabel7.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 90, 30));

        jLabel9.setText("Title");
        jLabel9.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 100, 90, 30));

        txtBookName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 2, 0, new java.awt.Color(255, 0, 0)));
        txtBookName.setPlaceholder("Enter Title");
        jPanel2.add(txtBookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 130, 230, -1));

        jLabel10.setText("Student ID");
        jLabel10.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 20, 90, 30));

        txtStudentId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 2, 0, new java.awt.Color(255, 0, 0)));
        txtStudentId.setPlaceholder("Enter Student ID");
        jPanel2.add(txtStudentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 50, 230, -1));

        txtStudentName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 2, 0, new java.awt.Color(255, 0, 0)));
        txtStudentName.setPlaceholder("Enter Name");
        jPanel2.add(txtStudentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 130, 230, -1));

        jLabel11.setText("Name");
        jLabel11.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 100, 100, 30));

        jLabel4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jButton1.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/search.png")), 50, 50));
        jButton1.setText("SEARCH");
        jButton1.setBackground(new java.awt.Color(153, 204, 255));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 60, 180, 70));

        jLabel8.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/exit.png")), 50, 50));
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1850, 0, 50, 50));

        jButton5.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reset.png")), 75, 75));
        jButton5.setBackground(new java.awt.Color(102, 102, 255));
        jButton5.setBorder(null);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1720, 60, 75, 75));

        jLabel12.setFont(new java.awt.Font("Consolas", 1, 15)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(204, 204, 204));
        jLabel12.setText("Record ID");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 100, 80, 30));

        txtRecordId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 2, 0, new java.awt.Color(255, 0, 0)));
        jPanel2.add(txtRecordId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 130, 170, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1900, 190));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void recordTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recordTableMouseClicked
        int rowNum = recordTable.convertRowIndexToModel(recordTable.getSelectedRow());
        TableModel model = recordTable.getModel();
        txtBookId.setText(model.getValueAt(rowNum, 1).toString());
        txtBookName.setText(model.getValueAt(rowNum, 2).toString());
        txtStudentId.setText(model.getValueAt(rowNum, 3).toString());
        txtStudentName.setText(model.getValueAt(rowNum, 4).toString());
        issueDatePicker.setText(model.getValueAt(rowNum, 5).toString());
        dueDatePicker.setText(model.getValueAt(rowNum, 6).toString());
        statusBox.setSelectedItem(model.getValueAt(rowNum, 7).toString());
        txtRecordId.setText(model.getValueAt(rowNum, 0).toString());
    }//GEN-LAST:event_recordTableMouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        if(comeBackToHomePage) {
            HomePage page = new HomePage();
            page.setVisible(true);
            this.dispose();
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        search();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        if(comeBackToHomePage) {
            System.exit(0);
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        txtBookId.setText("");
        txtBookName.setText("");
        txtStudentId.setText("");
        txtStudentName.setText("");
        issueDatePicker.setDate(null);
        dueDatePicker.setDate(null);
        statusBox.setSelectedIndex(0);
        txtRecordId.setText("");
        getRecordsDetail();
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(ViewRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewRecord(true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DatePicker dueDatePicker;
    private com.github.lgooddatepicker.components.DatePicker issueDatePicker;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private rojerusan.RSTableMetro recordTable;
    private javax.swing.JComboBox<String> statusBox;
    private app.bolivia.swing.JCTextField txtBookId;
    private app.bolivia.swing.JCTextField txtBookName;
    private app.bolivia.swing.JCTextField txtRecordId;
    private app.bolivia.swing.JCTextField txtStudentId;
    private app.bolivia.swing.JCTextField txtStudentName;
    // End of variables declaration//GEN-END:variables
}
