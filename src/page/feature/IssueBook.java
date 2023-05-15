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
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import ultility.Formatter;
import ultility.Resizer;
import javax.swing.ImageIcon;

/**
 *
 * @author Hao
 */
public class IssueBook extends javax.swing.JFrame {

    /**
     * Creates new form IssueBook
     */
    public IssueBook() {
        initComponents();
        formatDatePicker();
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
    
    // get book detail and display it
    private boolean getBookDetails() {
        String bookId = txtBookId.getText();
        boolean exist = false;
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from books where book_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            rs = pst.executeQuery();
            
            if(rs.next()) {
                exist = true;
                lblBookName.setText(rs.getString("title"));
                lblAuthor.setText(rs.getString("author"));
                lblQuantity.setText(String.valueOf(rs.getInt("quantity")));
                ArrayList<String> genres = getDataForGenreOfBook(bookId);
                String text = "";
                int i = 0;
                for(; i < genres.size()-3; i+=4) {
                    text += String.join("|", genres.subList(i, i+4))+"\n";
                }
                text += String.join("|", genres.subList(i, genres.size()));
                lblGenres.setText(text);
                lblPrice.setText(String.valueOf(rs.getFloat("price")));
            }
            else {
                lblBookName.setText("");
                lblAuthor.setText("");
                lblQuantity.setText("");
                lblGenres.setText("");
                lblPrice.setText("");
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
    
    // get student detail and display it
    private boolean getStudentDetails() {
        String studentId = txtStudentId.getText();
        boolean exist = false;
        
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
                exist = true;
                lblStudentName.setText(rs.getString("name"));
                lblGender.setText(rs.getString("gender"));
                lblBirthday.setText(Formatter.dateToString(rs.getDate("birthday")));
                lblEmail.setText(rs.getString("email"));
                lblContact.setText(rs.getString("contact"));
            }
            else {
                lblStudentName.setText("");
                lblGender.setText("");
                lblBirthday.setText("");
                lblEmail.setText("");
                lblContact.setText("");
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
    
    // valdation
    private boolean validateData() {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        
        if(issueDatePicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please pick an issue date!");
            return false;
        }
        
        if(dueDatePicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please pick an dued date!");
            return false;
        }
        
        Date issuedDate = Date.valueOf(issueDatePicker.getDate());
        Date duedDate = Date.valueOf(dueDatePicker.getDate());
        Date today = Date.valueOf(LocalDate.now());
        
        if(bookId.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Book Id");
            return false;
        }
        
        if(studentId.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Student Id");
            return false;
        }
        
        if(today.getTime()-issuedDate.getTime() > 7*24*60*60*1000) {
            JOptionPane.showMessageDialog(this, "Issue date can't be from more than 7 days ago!");
            return false;
        }
        
        if(issuedDate.compareTo(duedDate) > 0) {
            JOptionPane.showMessageDialog(this, "Invalid Due date!");
            return false;
        }
        return true;
    }
    
    // check book's quantity
    private int countBook() {
        return Integer.parseInt(lblQuantity.getText());
    }
    
    // decrease the quantity of book in database
    private void decreaseBookQuantity(int quantity) {
        String bookId = txtBookId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "update books "
                        + "set quantity = quantity-? "
                        + " where book_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setInt(1, quantity);
            pst.setString(2, bookId);
            
            int intialCount = Integer.parseInt(lblQuantity.getText());
            lblQuantity.setText(String.valueOf(intialCount-1));
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.out.println("Can't update");
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
    
    // insert issue details to database
    private void inseretIssueDetail() {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        Date issuedDate = Date.valueOf(issueDatePicker.getDate());
        Date duedDate = Date.valueOf(dueDatePicker.getDate());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "insert into records"
                        + "(book_id, student_id, issue_date, due_date, status) "
                        + "values(?,?,?,?,?)";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, studentId);
            pst.setDate(3, issuedDate);
            pst.setDate(4, duedDate);
            pst.setString(5, "Pending");
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.out.println("Can't update");
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
    
    // check if the book is already issued for the student
    private boolean checkDuplicateIssueDetail() {
        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();
        boolean duplicate = false;
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from records "
                        + "where book_id = ? and student_id = ? "
                        + "and (status = ? or status = ?)";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, studentId);
            pst.setString(3, "Pending");
            pst.setString(4, "Overdue");           
            rs = pst.executeQuery();
            if(rs.next()) {
                duplicate = true;
            }
            return duplicate;
        } 
        catch(Exception e1) {
            System.out.println(e1);
            return duplicate;
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
        }
    }
    
    // get all genres of a book
    private ArrayList<String> getDataForGenreOfBook(String bookId) {
        ArrayList<String> genres = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select name "
                        + "from books natural join book_genres natural join genres "
                        + "where book_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            rs = pst.executeQuery();
            while(rs.next()) {
                genres.add(rs.getString("name"));
            }
            return genres;
        } 
        catch(Exception e1) {
            System.out.println(e1);
            return genres;
        } 
        finally {
            try { if (rs != null) rs.close(); } catch (Exception e2) {}
            try { if (pst != null) pst.close(); } catch (Exception e3) {}
            try { if (con != null) con.close(); } catch (Exception e4) {}
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblBookName = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblGenres = new javax.swing.JTextArea();
        jLabel20 = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblBirthday = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblStudentName = new javax.swing.JLabel();
        lblGender = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblContact = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtStudentId = new app.bolivia.swing.JCTextField();
        jLabel23 = new javax.swing.JLabel();
        txtBookId = new app.bolivia.swing.JCTextField();
        issueDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        dueDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/book.png")), 100, 100));
        jLabel1.setText("Book Details");
        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 25)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 510, 100));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, 30));

        jLabel5.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/name.png")), 45, 45));
        jLabel5.setText("Book Name:");
        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 320, 50));

        lblBookName.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblBookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 480, 60));

        jLabel7.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/quantity.png")), 45, 45));
        jLabel7.setText("Quantity:");
        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 530, 320, 50));

        jLabel9.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/author.png")), 45, 45));
        jLabel9.setText("Author:");
        jLabel9.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, 320, 50));

        jLabel4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        lblAuthor.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblAuthor, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 480, 60));

        lblQuantity.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 600, 480, 60));

        jLabel19.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/genres.png")), 45, 45));
        jLabel19.setText("Genres:");
        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 680, 320, 50));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setBorder(null);

        lblGenres.setEditable(false);
        lblGenres.setBackground(new java.awt.Color(102, 102, 255));
        lblGenres.setColumns(20);
        lblGenres.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        lblGenres.setRows(5);
        lblGenres.setToolTipText("");
        lblGenres.setBorder(null);
        lblGenres.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblGenres.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jScrollPane1.setViewportView(lblGenres);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 750, 470, 70));

        jLabel20.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/price.png")), 45, 45));
        jLabel20.setText("Price:");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 830, 320, 50));

        lblPrice.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel2.add(lblPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 900, 480, 60));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 510, 1000));

        jPanel3.setBackground(new java.awt.Color(102, 102, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/student.png")), 100, 100));
        jLabel11.setText("Student Details");
        jLabel11.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 25)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 510, 100));

        jLabel12.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, 30));

        jLabel13.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/name.png")), 45, 45));
        jLabel13.setText("Student Name:");
        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 320, 50));

        lblBirthday.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel3.add(lblBirthday, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 600, 480, 60));

        jLabel15.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/gender.png")), 45, 45));
        jLabel15.setText("Gender:");
        jLabel15.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, 320, 50));

        jLabel16.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/calender.png")), 45, 45));
        jLabel16.setText("Birthday:");
        jLabel16.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 530, 320, 50));

        lblStudentName.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel3.add(lblStudentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 480, 60));

        lblGender.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel3.add(lblGender, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 480, 60));

        jLabel17.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/email.png")), 45, 45));
        jLabel17.setText("Email:");
        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 680, 320, 50));

        lblEmail.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel3.add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 750, 480, 60));

        jLabel18.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/phone.png")), 45, 45));
        jLabel18.setText("Contact:");
        jLabel18.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 830, 320, 50));

        lblContact.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jPanel3.add(lblContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 900, 480, 60));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 0, 510, 1000));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/borrow.png")), 50, 50));
        jLabel21.setText("Issue Books");
        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 70, 390, 70));

        jLabel22.setText("Student ID");
        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 51, 51));
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(1580, 210, 200, 30));

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
        jPanel1.add(txtStudentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1580, 250, 240, -1));

        jLabel23.setText("Book ID");
        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 51, 51));
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 210, 200, 30));

        txtBookId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 51, 51)));
        txtBookId.setPlaceholder("Enter Book Id ...");
        txtBookId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBookIdFocusLost(evt);
            }
        });
        jPanel1.add(txtBookId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 250, 240, -1));

        issueDatePicker.setBackground(new java.awt.Color(255, 255, 255));
        issueDatePicker.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        issueDatePicker.setToolTipText("");
        jPanel1.add(issueDatePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 400, 330, 40));

        jLabel24.setText("ISSUE DATE");
        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 51, 51));
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 350, 110, 30));

        jLabel25.setText("DUE DATE");
        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 51, 51));
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 500, 110, 30));

        dueDatePicker.setBackground(new java.awt.Color(255, 255, 255));
        dueDatePicker.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        dueDatePicker.setToolTipText("");
        jPanel1.add(dueDatePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 550, 330, 40));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1520, 230, 50, 50));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/calender.png")), 50, 50));
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 540, 50, 50));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 230, 50, 50));

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/calender.png")), 50, 50));
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 390, 50, 50));

        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/book.png")), 50, 50));
        jButton2.setText("BOOK");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 820, 180, 70));

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
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 0, 75, 75));

        jButton3.setBackground(new java.awt.Color(153, 204, 255));
        jButton3.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/borrow.png")), 50, 50));
        jButton3.setText("ISSUE");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1440, 670, 170, 70));

        jButton4.setBackground(new java.awt.Color(153, 204, 255));
        jButton4.setFont(new java.awt.Font("Consolas", 1, 25)); // NOI18N
        jButton4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/student.png")), 50, 50));
        jButton4.setText("STUDENT");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1590, 820, 190, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1900, 1000));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtBookIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookIdFocusLost
        getBookDetails();
    }//GEN-LAST:event_txtBookIdFocusLost

    private void txtStudentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentIdActionPerformed

    private void txtStudentIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentIdFocusLost
        getStudentDetails();
    }//GEN-LAST:event_txtStudentIdFocusLost

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        HomePage page = new HomePage();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ManageBook page = new ManageBook(false, false);
        page.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        lblBookName.setText("");
        lblAuthor.setText("");
        lblQuantity.setText("");
        lblGender.setText("");
        lblStudentName.setText("");
        lblEmail.setText("");
        lblGenres.setText("");
        lblPrice.setText("");
        lblBirthday.setText("");
        lblContact.setText("");
        txtBookId.setText("");
        txtStudentId.setText("");
        issueDatePicker.setDate(null);
        dueDatePicker.setDate(null);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(validateData()) {
            if(!getStudentDetails()) {
                JOptionPane.showMessageDialog(this, "Can't find this student");
            }
            else if(!getBookDetails()) {
                JOptionPane.showMessageDialog(this, "Can't find this book");
            }
            else {
                if(countBook() == 0) {
                    JOptionPane.showMessageDialog(this, "This book is not available!");
                }
                else if(checkDuplicateIssueDetail()) {
                    JOptionPane.showMessageDialog(this, "This book has already been issued to this student!");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Issue book succesfully!");
                    decreaseBookQuantity(1);
                    inseretIssueDetail();
                }
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ManageStudent page = new ManageStudent(false, false);
        page.setVisible(true);
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
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IssueBook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DatePicker dueDatePicker;
    private com.github.lgooddatepicker.components.DatePicker issueDatePicker;
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
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblBirthday;
    private javax.swing.JLabel lblBookName;
    private javax.swing.JLabel lblContact;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGender;
    private javax.swing.JTextArea lblGenres;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblStudentName;
    private app.bolivia.swing.JCTextField txtBookId;
    private app.bolivia.swing.JCTextField txtStudentId;
    // End of variables declaration//GEN-END:variables
}
