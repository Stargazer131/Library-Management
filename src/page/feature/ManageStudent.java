/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package page.feature;

import page.main.HomePage;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.Font;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import ultility.Formatter;
import ultility.Resizer;
import javax.swing.ImageIcon;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Hao
 */
public class ManageStudent extends javax.swing.JFrame {
    
    private boolean comeBackToHomePage;
    /**
     * Creates new form ManageBooks
     */
    public ManageStudent(boolean comeBackToHomePage, boolean isEditable) {
        this.comeBackToHomePage = comeBackToHomePage;
        initComponents();
        formatDatePicker();
        setStudentsDetailToTable();
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
        if(isEditable == false) {
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
            jButton4.setEnabled(false);
        }
        sortByColumn();
    }
    
    // format date
    private void formatDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setAllowKeyboardEditing(false);
        Font font = jLabel10.getFont();
        dateSettings.setFontCalendarDateLabels(font);
        dateSettings.setFontCalendarWeekdayLabels(font);
        dateSettings.setFontMonthAndYearMenuLabels(font);
        dateSettings.setFontValidDate(font);
        datePicker.setSettings(dateSettings);
    }
    
    // set book detail to the table
    private void setStudentsDetailToTable() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from students order by student_id";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)studentTable.getModel();
            model.setRowCount(0);
            while(rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("name");
                String gender = rs.getString("gender");
                Date birthday = rs.getDate("birthday");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                String birthdayInText = Formatter.formatDateDisplay(birthday);
                
                Object[] obj = {studentId, studentName, gender, birthdayInText, email, contact};
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
        String studentId = txtStudentId.getText();
        String name = txtStudentName.getText();
        String gender = (String)genderComboBox.getSelectedItem();
        String email = txtEmail.getText();
        String contact = txtMobile.getText();
        
        if(studentId.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Id");
            return false;
        }
        
        if(studentId.length() > 20) {
            JOptionPane.showMessageDialog(this, "Student Id can't be more than 20 characters");
            return false;
        }

        if(name.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Name");
            return false;
        }
        
        if(name.length() > 30) {
            JOptionPane.showMessageDialog(this, "Student Name can't be more than 30 characters");
            return false;
        }
        
        if(gender.equals("All")) {
            JOptionPane.showMessageDialog(this, "Invalid Gender (All) is used for search purpose only");
            return false;  
        }
        
        if(datePicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please pick a date!");
            return false;           
        }
        
        if(email.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Email");
            return false;
        }
        
        if(email.length() > 30) {
            JOptionPane.showMessageDialog(this, "Email can't be more than 30 characters");
            return false;
        }
        
        if(contact.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Phone Number");
            return false;
        }
        
        if(contact.length() > 15) {
            JOptionPane.showMessageDialog(this, "Contact info can't be more than 15 characters");
            return false;
        }
        
        return true;
    }
    
    // add student to data base
    private void addStudent() {
        String studentId = txtStudentId.getText();
        String studentName = txtStudentName.getText();
        String gender = genderComboBox.getSelectedItem().toString();
        String email = txtEmail.getText();
        String contact = txtMobile.getText();
        Date birthday = Date.valueOf(datePicker.getDate());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "insert into students(student_id,name,gender,birthday,email,contact) values(?,?,?,?,?,?)";    
            pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
            pst.setString(2, studentName);
            pst.setString(3, gender);
            pst.setDate(4, birthday);
            pst.setString(5, email);
            pst.setString(6, contact);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            }
            else {
                JOptionPane.showMessageDialog(this, "Record inserted failure!");
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
    
    // update student to database
    private void updateStudent() {
        String studentId = txtStudentId.getText();
        String studentName = txtStudentName.getText();
        String gender = genderComboBox.getSelectedItem().toString();
        String email = txtEmail.getText();
        String contact = txtMobile.getText();
        Date birthday = Date.valueOf(datePicker.getDate());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "update students "
                        + "set name = ?, gender = ?, birthday = ?, email = ?, contact = ? "
                        + "where student_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, studentName);
            pst.setString(2, gender);
            pst.setDate(3, birthday);
            pst.setString(4, email);
            pst.setString(5, contact);
            pst.setString(6, studentId);
           
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            }
            else {
                JOptionPane.showMessageDialog(this, "Record updated failure!");
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
    
    // delete the student from data base
    private void deleteStudent() {
        String studentId = txtStudentId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "delete from students where student_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Student was deleted successfully!");
            }
            else {
                JOptionPane.showMessageDialog(this, "Student was deleted failure!");
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
    
    // check if the book id is already exited
    private boolean checkExitedStudent() {
        String studentId = txtStudentId.getText();
        boolean exit = false;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from students where student_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
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
        setStudentsDetailToTable();
        this.revalidate();
        this.repaint();
    }
    
    // delete student record in issue details
    private void deleteStudentRecord() {
        String studentId = txtStudentId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "delete from records where student_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.out.println("Error delete student records");
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
    
    private void search() {
        String studentId = txtStudentId.getText();
        String name = txtStudentName.getText();
        String gender = genderComboBox.getSelectedItem().toString();
        String email = txtEmail.getText();
        String contact = txtMobile.getText();
        //Date birthday = Date.valueOf(datePicker.getDate());
        
        if(gender.equals("All")) {
            gender = "";
        }
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            studentId = studentId.toLowerCase();
            name = name.toLowerCase();
            studentId = studentId.toLowerCase();
            email = email.toLowerCase();
            
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from students "
                        + "where lower(student_id) like ? and lower(name) like ? and "
                        + "lower(email) like ? and contact like ? and gender like ?";

            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + studentId + "%");
            pst.setString(2, "%" + name + "%");
            pst.setString(3, "%" + email + "%");
            pst.setString(4, "%" + contact + "%");
            pst.setString(5, "%" + gender + "%");
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                String studentIdData = rs.getString("student_id");
                String nameData = rs.getString("name");
                String genderData = rs.getString("gender");
                String birthdayData = Formatter.formatDateDisplay(rs.getDate("birthday"));
                String studentEmailData = rs.getString("email");
                String studentContactData = rs.getString("contact");
                Object[] obj = {studentIdData, nameData, genderData, birthdayData, studentEmailData, studentContactData};
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
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(studentTable.getModel());
        studentTable.setRowSorter(sorter);

        Comparator<Object> comparator1 = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // Implement your comparison logic here
                // For example, to compare strings, you can use the following:
                return o1.toString().compareTo(o2.toString());
            }
        };

        for(int i = 0; i < 6; i++) {
            if(i != 3) {
                sorter.setComparator(i, comparator1);
            }
        }
        
        Comparator<Object> comparator2 = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // Implement your comparison logic here
                // For example, to compare strings, you can use the following:
                return stringToDate(o1.toString()).compareTo(stringToDate(o2.toString()));
            }
        };
        
        sorter.setComparator(3, comparator2);
    }
    
    private static java.util.Date stringToDate(String input)  {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.parse(input);
        }
        catch(Exception e) {
            return null;
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
        jLabel9 = new javax.swing.JLabel();
        txtStudentId = new app.bolivia.swing.JCTextField();
        jLabel10 = new javax.swing.JLabel();
        txtStudentName = new app.bolivia.swing.JCTextField();
        jLabel11 = new javax.swing.JLabel();
        datePicker = new com.github.lgooddatepicker.components.DatePicker();
        genderComboBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtEmail = new app.bolivia.swing.JCTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtMobile = new app.bolivia.swing.JCTextField();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        studentTable = new rojerusan.RSTableMetro();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setText("Student ID");
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 200, 200, 30));

        txtStudentId.setBackground(new java.awt.Color(102, 102, 255));
        txtStudentId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
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
        jPanel1.add(txtStudentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 240, -1));

        jLabel10.setText("Name");
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 200, 200, 30));

        txtStudentName.setBackground(new java.awt.Color(102, 102, 255));
        txtStudentName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtStudentName.setPlaceholder("Enter Student Name ...");
        txtStudentName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtStudentNameFocusLost(evt);
            }
        });
        txtStudentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStudentNameActionPerformed(evt);
            }
        });
        jPanel1.add(txtStudentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 240, 240, -1));

        jLabel11.setText("Birthday");
        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 410, 200, 30));

        datePicker.setToolTipText("");
        jPanel1.add(datePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 460, 240, 30));

        genderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Male", "Female" }));
        genderComboBox.setBorder(null);
        genderComboBox.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        genderComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genderComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(genderComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 460, 250, 30));

        jLabel12.setText("Gender");
        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 420, 200, 30));

        jLabel3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

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
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 70, 200, 70));

        jLabel14.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/calender.png")), 50, 50));
        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 440, 50, 50));

        jLabel15.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 50, 50));

        jLabel16.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/name.png")), 50, 50));
        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, 50, 50));

        jLabel17.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/gender.png")), 50, 50));
        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 440, 50, 50));

        jButton3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/delete.png")), 30, 30));
        jButton3.setText("DELETE");
        jButton3.setBackground(new java.awt.Color(153, 204, 255));
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFont(new java.awt.Font("Consolas", 1, 17)); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 810, 140, 60));

        jButton2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/update.png")), 30, 30));
        jButton2.setText("UPDATE");
        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFont(new java.awt.Font("Consolas", 1, 17)); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 810, 140, 60));

        jButton4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/add.png")), 30, 30));
        jButton4.setText("ADD");
        jButton4.setBackground(new java.awt.Color(153, 204, 255));
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFont(new java.awt.Font("Consolas", 1, 17)); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 810, 140, 60));

        jLabel13.setText("Email");
        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 610, 200, 30));

        txtEmail.setBackground(new java.awt.Color(102, 102, 255));
        txtEmail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtEmail.setPlaceholder("Enter Email ...");
        txtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEmailFocusLost(evt);
            }
        });
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        jPanel1.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 650, 240, -1));

        jLabel18.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/email.png")), 50, 50));
        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 630, 50, 50));

        jLabel19.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/phone.png")), 50, 50));
        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 630, 50, 50));

        jLabel20.setText("Contact");
        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 610, 200, 30));

        txtMobile.setBackground(new java.awt.Color(102, 102, 255));
        txtMobile.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtMobile.setPlaceholder("Enter Phone Number ...");
        txtMobile.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMobileFocusLost(evt);
            }
        });
        txtMobile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMobileActionPerformed(evt);
            }
        });
        jPanel1.add(txtMobile, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 650, 240, -1));

        jButton6.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reset.png")), 75, 75));
        jButton6.setBackground(new java.awt.Color(102, 102, 255));
        jButton6.setBorder(null);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 75, 75));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 1000));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "StudentID", "Name", "Gender", "Birthday", "Email", "Contact"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        studentTable.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        studentTable.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        studentTable.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        studentTable.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        studentTable.setFont(new java.awt.Font("Segoe UI Light", 0, 25)); // NOI18N
        studentTable.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        studentTable.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        studentTable.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        studentTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        studentTable.setRowHeight(40);
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                studentTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(studentTable);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 1150, 760));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/student.png")), 50, 50));
        jLabel2.setText("Manage Students");
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

    private void txtStudentIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentIdFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentIdFocusLost

    private void txtStudentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentIdActionPerformed

    private void txtStudentNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentNameFocusLost

    private void txtStudentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentNameActionPerformed

    private void studentTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentTableMouseClicked
        int rowNum = studentTable.convertRowIndexToModel(studentTable.getSelectedRow());
        TableModel model = studentTable.getModel();
        txtStudentId.setText(model.getValueAt(rowNum, 0).toString());
        txtStudentName.setText(model.getValueAt(rowNum, 1).toString());
        genderComboBox.setSelectedItem(model.getValueAt(rowNum, 2).toString());
        datePicker.setText(model.getValueAt(rowNum, 3).toString());
        txtEmail.setText(model.getValueAt(rowNum, 4).toString());
        txtMobile.setText(model.getValueAt(rowNum, 5).toString());
    }//GEN-LAST:event_studentTableMouseClicked

    private void genderComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genderComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_genderComboBoxActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        if(comeBackToHomePage) {
            HomePage page = new HomePage();
            page.setVisible(true);
            this.dispose();
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        search();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(validateData()) {
            if(checkExitedStudent()) {
                int choice = JOptionPane.showConfirmDialog(this, "Delete this student mean delete all the issue details about it, are you sure?");
                if(choice == JOptionPane.OK_OPTION) {
                    deleteStudentRecord();
                    deleteStudent();
                    refresh();
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find student to delete!");
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(validateData()) {
            if(checkExitedStudent()) {
                updateStudent();
                refresh();
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find student to update!");
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(validateData()) {
            if(!checkExitedStudent()) {
                addStudent();
                refresh();
            }
            else {
                JOptionPane.showMessageDialog(this, "Student Id is already exited!");
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        if(comeBackToHomePage) {
            System.exit(0);
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jLabel5MouseClicked

    private void txtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmailFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailFocusLost

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtMobileFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMobileFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMobileFocusLost

    private void txtMobileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMobileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMobileActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtEmail.setText("");
        txtMobile.setText("");
        genderComboBox.setSelectedIndex(0);
        datePicker.setDate(null);
        setStudentsDetailToTable();
    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(ManageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageStudent(true, true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DatePicker datePicker;
    private javax.swing.JComboBox<String> genderComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private rojerusan.RSTableMetro studentTable;
    private app.bolivia.swing.JCTextField txtEmail;
    private app.bolivia.swing.JCTextField txtMobile;
    private app.bolivia.swing.JCTextField txtStudentId;
    private app.bolivia.swing.JCTextField txtStudentName;
    // End of variables declaration//GEN-END:variables
}
