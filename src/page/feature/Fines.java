/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package page.feature;

import page.main.HomePage;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import ultility.Formatter;
import ultility.Resizer;
import javax.swing.ImageIcon;
import javax.swing.table.TableRowSorter;
import ultility.ComparatorType;

/**
 *
 * @author Hao
 */
public class Fines extends javax.swing.JFrame {
    /**
     * Creates new form ManageBooks
     */
    public Fines() {
        initComponents();
        setFinesDetailToTable();
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
        sortByColumn();
    }
    
    // set fine detail to the table
    private void setFinesDetailToTable() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from fines order by record_id desc";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)fineTable.getModel();
            model.setRowCount(0);
            while(rs.next()) {
                int recordId = rs.getInt("record_id");
                float amount = rs.getFloat("amount");
                String reason = rs.getString("reason");
                String status = rs.getString("status");
                String fineDate = Formatter.dateToString(rs.getDate("fine_date"));
                String payDate = Formatter.dateToString(rs.getDate("pay_date"));    
                Object[] obj = {recordId, amount, reason, status, fineDate, payDate};
                model.addRow(obj);
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
    
    // validation
    private boolean validateData() {
        int recordId;
        float amount;
        String reason = txtReason.getText();
        String status = (String)statusComboBox.getSelectedItem();
        
        try {
            recordId = Integer.parseInt(txtRecordId.getText());
            if(recordId <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid Record Id");
                return false;
            }
        }
        catch(NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Record Id");
            return false;
        }
        
        try {
            amount = Float.parseFloat(txtAmount.getText());
            if(amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid Amount");
                return false;
            }
        }
        catch(NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Amount");
            return false;
        }        

        if(reason.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Reason");
            return false;
        }
        
        if(reason.length() > 100) {
            JOptionPane.showMessageDialog(this, "Reason can't be more than 100 characters");
            return false;
        }
        
        if(status.equals("All")) {
            JOptionPane.showMessageDialog(this, "Invalid Status (All) is used for search purpose only");
            return false;  
        }
        
        return true;
    }
    
    // add fine to data base
    private void addFine() {
        int recordId = Integer.parseInt(txtRecordId.getText());
        float amount = Float.parseFloat(txtAmount.getText());
        String reason = txtReason.getText();
        Date fineDate = new Date(new java.util.Date().getTime());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "insert into fines(record_id,amount,reason,status,fine_date) values(?,?,?,?,?)";    
            pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            pst.setFloat(2, amount);
            pst.setString(3, reason);
            pst.setString(4, "Unpaid");
            pst.setDate(5, fineDate);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Fine add successfully!");
            }
            else {
                JOptionPane.showMessageDialog(this, "Fine add failure!");
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
        
    // update fine or pay the fine
    private void updateFine() {
        int recordId = Integer.parseInt(txtRecordId.getText());
        float amount = Float.parseFloat(txtAmount.getText());
        String reason = txtReason.getText();
        String statusBefore = "All";
        String statusAfter = (String)statusComboBox.getSelectedItem();
        Date payDate = new Date(new java.util.Date().getTime());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select status from fines where record_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            rs = pst.executeQuery();
            rs.next();
            statusBefore = rs.getString("status");
        } 
        catch(Exception e1) {
            System.out.println(e1);
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
        }
        
        // if the fine is unpaid -> update detail or pay it
        if(statusBefore.equals("Unpaid")) {
            // update the detail
            if(statusAfter.endsWith("Unpaid")) {
                con = null;
                pst = null;
                rs = null;
                try {
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                    String sql = "update fines "
                            + "set amount = ?, reason = ? "
                            + "where record_id = ?";    
                    pst = con.prepareStatement(sql);
                    pst.setFloat(1, amount);
                    pst.setString(2, reason);
                    pst.setInt(3, recordId);
                    int row = pst.executeUpdate();
                    if(row > 0) {
                        JOptionPane.showMessageDialog(this, "Update fine sucessfully!");
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Update fine failure!");
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
            
            // pay it
            else {
                con = null;
                pst = null;
                rs = null;
                try {
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                    String sql = "update fines "
                            + "set status = ?, pay_date = ? "
                            + "where record_id = ?";    
                    pst = con.prepareStatement(sql);
                    pst.setString(1, "Paid");
                    pst.setDate(2, payDate);
                    pst.setInt(3, recordId);
                    int row = pst.executeUpdate();
                    if(row > 0) {
                        JOptionPane.showMessageDialog(this, "Pay fine sucessfully!");
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Pay fine failure!");
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
        
        // if the fine is paid -> only update detail
        else {
            // error
            if(statusAfter.endsWith("Unpaid")) {
                JOptionPane.showMessageDialog(this, "Can't update a paid fine to unpaid");
            }
            
            // update it
            else {
                con = null;
                pst = null;
                rs = null;
                try {
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                    String sql = "update fines "
                            + "set amount = ?, reason = ? "
                            + "where record_id = ?";    
                    pst = con.prepareStatement(sql);
                    pst.setFloat(1, amount);
                    pst.setString(2, reason);
                    pst.setInt(3, recordId);
                    int row = pst.executeUpdate();
                    if(row > 0) {
                        JOptionPane.showMessageDialog(this, "Update fine sucessfully!");
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Update fine failure!");
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
    } 
    
    private void deleteFine() {
        int recordId = Integer.parseInt(txtRecordId.getText());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "delete from fines where record_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Fine delete successfully!");
            }
            else {
                JOptionPane.showMessageDialog(this, "Fine delete failure!");
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
    
    // check if the fine id is already exited
    private boolean checkExitedFine() {
        int recordId = Integer.parseInt(txtRecordId.getText());
        boolean exit = false;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from fines where record_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            rs = pst.executeQuery();
            if(rs.next()) {
                exit = true;
            }
            return exit;
        } 
        catch(Exception e1) {
            System.out.println(e1);
            return exit;
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
        }
    }
    
    private void refresh() {
        setFinesDetailToTable();
        this.revalidate();
        this.repaint();
    }
    
    private void search() {
        int recordId;
        int min = 1, max = Integer.MAX_VALUE;
        try {
            recordId = Integer.parseInt(txtRecordId.getText());
            min = recordId; 
            max = recordId;
        }
        catch(NumberFormatException nfe) {
            
        }
        
        String reason = txtReason.getText();
        String status = (String)statusComboBox.getSelectedItem();
                
        if(status.equals("All")) {
            status = "";
        }
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            reason = reason.toLowerCase();
            
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from fines " +
                        "where record_id between ? and ? and reason like ? and status like ?";            

            pst = con.prepareStatement(sql);
            pst.setInt(1, min);
            pst.setInt(2, max);
            pst.setString(3, "%"+reason+"%");
            pst.setString(4, status+"%");
            
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) fineTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                int recordIdData = rs.getInt("record_id");
                float amount = rs.getFloat("amount");
                String reasonData = rs.getString("reason");
                String statusData = rs.getString("status");
                String fineDate = Formatter.dateToString(rs.getDate("fine_date"));
                String payDate = Formatter.dateToString(rs.getDate("pay_date"));
                Object[] obj = {recordIdData, amount, reasonData, statusData, fineDate, payDate};
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
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(fineTable.getModel());
        fineTable.setRowSorter(sorter);  
        
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
        
        sorter.setComparator(0, ComparatorType.INTERGER);
        sorter.setComparator(1, ComparatorType.FLOAT);
        for(int i = 2; i <= 3; i++) {
            sorter.setComparator(i, ComparatorType.STRING);
        }
        sorter.setComparator(4, ComparatorType.DATE);
        sorter.setComparator(5, comparatorDate);
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
        jLabel9 = new javax.swing.JLabel();
        txtRecordId = new app.bolivia.swing.JCTextField();
        jLabel10 = new javax.swing.JLabel();
        txtAmount = new app.bolivia.swing.JCTextField();
        statusComboBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtReason = new app.bolivia.swing.JCTextField();
        jLabel18 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        fineTable = new rojerusan.RSTableMetro();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setText("Record ID");
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 320, 200, 30));

        txtRecordId.setBackground(new java.awt.Color(102, 102, 255));
        txtRecordId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtRecordId.setPlaceholder("Enter Record Id ...");
        jPanel1.add(txtRecordId, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 360, 240, -1));

        jLabel10.setText("Amount");
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 320, 200, 30));

        txtAmount.setBackground(new java.awt.Color(102, 102, 255));
        txtAmount.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtAmount.setPlaceholder("Enter Amount ...");
        jPanel1.add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 360, 240, -1));

        statusComboBox.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Paid", "Unpaid" }));
        statusComboBox.setBorder(null);
        jPanel1.add(statusComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 580, 250, 30));

        jLabel12.setText("Status");
        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 540, 200, 30));

        jLabel3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jButton1.setBackground(new java.awt.Color(153, 204, 255));
        jButton1.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton1.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/search.png")), 50, 50));
        jButton1.setText("SEARCH");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 70, 200, 70));

        jLabel15.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 50, 50));

        jLabel16.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/price.png")), 50, 50));
        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 340, 50, 50));

        jLabel17.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/status.png")), 50, 50));
        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 560, 50, 50));

        jLabel13.setText("Reason");
        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 540, 200, 30));

        txtReason.setBackground(new java.awt.Color(102, 102, 255));
        txtReason.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtReason.setPlaceholder("Enter Reason  ...");
        jPanel1.add(txtReason, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 580, 240, -1));

        jLabel18.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reason.png")), 50, 50));
        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 560, 50, 50));

        jButton6.setBackground(new java.awt.Color(102, 102, 255));
        jButton6.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reset.png")), 75, 75));
        jButton6.setBorder(null);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 75, 75));

        jButton4.setBackground(new java.awt.Color(153, 204, 255));
        jButton4.setFont(new java.awt.Font("Consolas", 1, 17)); // NOI18N
        jButton4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/add.png")), 30, 30));
        jButton4.setText("ADD");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 840, 140, 60));

        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setFont(new java.awt.Font("Consolas", 1, 17)); // NOI18N
        jButton2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/update.png")), 30, 30));
        jButton2.setText("UPDATE");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 840, 140, 60));

        jButton3.setBackground(new java.awt.Color(153, 204, 255));
        jButton3.setFont(new java.awt.Font("Consolas", 1, 17)); // NOI18N
        jButton3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/delete.png")), 30, 30));
        jButton3.setText("DELETE");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 840, 140, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 1000));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Record ID", "Amount", "Reason", "Status", "Fine Date", "Pay Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fineTable.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        fineTable.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        fineTable.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        fineTable.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        fineTable.setFont(new java.awt.Font("Segoe UI Light", 0, 25)); // NOI18N
        fineTable.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        fineTable.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        fineTable.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        fineTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        fineTable.setRowHeight(40);
        fineTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fineTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(fineTable);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 1150, 760));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/fine.png")), 50, 50));
        jLabel2.setText("Fines");
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 100, 380, 60));

        jLabel5.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/exit.png")), 50, 50));
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 0, 50, 50));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, 1190, 1000));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void fineTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fineTableMouseClicked
        int rowNum = fineTable.convertRowIndexToModel(fineTable.getSelectedRow());
        TableModel model = fineTable.getModel();
        txtRecordId.setText(model.getValueAt(rowNum, 0).toString());
        txtAmount.setText(model.getValueAt(rowNum, 1).toString());
        txtReason.setText(model.getValueAt(rowNum, 2).toString());
        statusComboBox.setSelectedItem(model.getValueAt(rowNum, 3));     
    }//GEN-LAST:event_fineTableMouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        HomePage page = new HomePage();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        search();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        txtRecordId.setText("");
        txtAmount.setText("");
        txtReason.setText("");
        statusComboBox.setSelectedIndex(0);
        refresh();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(validateData()) {
            if(!checkExitedFine()) {
                addFine();
                refresh();
            }
            else {
                JOptionPane.showMessageDialog(this, "This Fine is already existed!");
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(validateData()) {
            if(checkExitedFine()) {
                updateFine();
                refresh();
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find the Fine to update!");
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(validateData()) {
            if(checkExitedFine()) {
                deleteFine();
                refresh();
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find the book to delete");
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(Fines.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Fines.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Fines.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Fines.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Fines().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSTableMetro fineTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JComboBox<String> statusComboBox;
    private app.bolivia.swing.JCTextField txtAmount;
    private app.bolivia.swing.JCTextField txtReason;
    private app.bolivia.swing.JCTextField txtRecordId;
    // End of variables declaration//GEN-END:variables
}
