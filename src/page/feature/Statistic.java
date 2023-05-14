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
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import page.main.HomePage;
import ultility.Formatter;
import ultility.Resizer;

/**
 *
 * @author Hao
 */
public class Statistic extends javax.swing.JFrame {

    private static final int[] CHILDREN = {0, 12};
    private static final int[] TEENAGER = {13, 18};
    private static final int[] YOUNG_ADULT = {19, 35};
    private static final int[] MIDDLE_AGED_ADULT = {36, 65};
    private static final int[] ELDERLY = {66, 100};

            
    /**
     * Creates new form Statistic
     */
    public Statistic() {
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
        initComponents();
        getDataForGenreListFromDatabase();
        updateBookTable();
    }
    
    // get the criteria
    private Object[] getCriterias() {
        String gender = (String)genderBox.getSelectedItem();
        String genre = (String)genreBox.getSelectedItem();
        String ageGroup = (String)ageBox.getSelectedItem();
        int minAge = 0, maxAge = 100;
        
        if(gender.equals("All")) {
            gender = "";
        }
        if(genre.equals("All")) {
            genre = "";
        }
        switch(ageGroup) {
            case "Children(0-12)":
                minAge = CHILDREN[0]; maxAge = CHILDREN[1];
                break;
            case "Teenagers(13-18)":
                minAge = TEENAGER[0]; maxAge = TEENAGER[1];
                break;
            case "Young Adults(19-35)":
                minAge = YOUNG_ADULT[0]; maxAge = YOUNG_ADULT[1];
                break;
            case "Middle-aged Adults(36-65)":
                minAge = MIDDLE_AGED_ADULT[0]; maxAge = MIDDLE_AGED_ADULT[1];
                break;
            case "Elderly(66+)":
                minAge = ELDERLY[0]; maxAge = ELDERLY[1];
                break;
            default:
                break;
        }
        
        Object[] criterias = {gender, genre, minAge, maxAge};
        return criterias;
    }
    
    // show statistic for book
    private void updateBookTable() {
        Object[] criterias = getCriterias();
        String gender = (String)criterias[0];
        String genre = (String)criterias[1];
        int minAge = (int)criterias[2]; 
        int maxAge = (int)criterias[3];
        String input = (String)bookBox.getSelectedItem();
        String sql = getTheSQLQuery(input);
        ArrayList<Object[]> data = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        
        if(input.equals("Top borrowed books")) {
            model.addColumn("Rank");
            model.addColumn("Book Id");
            model.addColumn("Title");
            model.addColumn("Author");
            model.addColumn("Genre");
            model.addColumn("Total Borrowed");
            
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                pst = con.prepareStatement(sql);
                pst.setString(1, "%"+genre+"%");
                pst.setString(2, "%"+gender+"%");
                pst.setInt(3, minAge);
                pst.setInt(4, maxAge);
                rs = pst.executeQuery();
                int rank = 1;
                while(rs.next()) {
                    String bookId = rs.getString("book_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int totalBorrowed = rs.getInt("total_borrowed");
                    Object[] row = {rank++, bookId, title, author, null, totalBorrowed};
                    data.add(row);
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
            for(int i = 0; i < data.size(); i++) {
                String genres = String.join("|", getDataForGenreOfBook((String)data.get(i)[1]));
                data.get(i)[4] = genres;
                model.addRow(data.get(i));
            }
        }
        else {
            model.addColumn("Rank");
            model.addColumn("Genre");
            model.addColumn("Total Borrowed");
            
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                pst = con.prepareStatement(sql);
                pst.setString(1, "%"+gender+"%");
                pst.setInt(2, minAge);
                pst.setInt(3, maxAge);
                rs = pst.executeQuery();
                int rank = 1;
                while(rs.next()) {
                    String name = rs.getString("name");
                    int totalBorrowed = rs.getInt("total_borrowed");
                    Object[] row = {rank++, name, totalBorrowed};
                    model.addRow(row);
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
    
    // show statistic for student
    private void updateStudentTable() {
        Object[] criterias = getCriterias();
        String gender = (String)criterias[0];
        String genre = (String)criterias[1];
        int minAge = (int)criterias[2]; 
        int maxAge = (int)criterias[3];
        String input = (String)studentBox.getSelectedItem();
        String sql = getTheSQLQuery(input);
        String status1 = ""; 
        String status2 = "UNKNOWN";
        
        if(input.equals("Top on-time borrowers")) {
            status1 = "Timely Returned";
        }
        else if(input.equals("Top late borrowers")) {
            status1 = "Late Returned";
            status2 = "Overdue";
        }
        
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Rank");
        model.addColumn("Student Id");
        model.addColumn("Name");
        model.addColumn("Gender");
        model.addColumn("Birthday");
        model.addColumn("Age");
        model.addColumn("Email");
        model.addColumn("Contact");
        model.addColumn("Total Borrow");

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            pst = con.prepareStatement(sql);
            pst.setString(1, "%"+genre+"%");
            pst.setString(2, "%"+gender+"%");
            pst.setString(3, "%"+status1+"%");
            pst.setString(4, "%"+status2+"%");
            pst.setInt(5, minAge);
            pst.setInt(6, maxAge);
            rs = pst.executeQuery();
            int rank = 1;
            while(rs.next()) {
                String studenId = rs.getString("student_id");
                String name = rs.getString("name");
                String genderData = rs.getString("gender");
                String birthday = Formatter.dateToString(rs.getDate("birthday"));
                int age = rs.getInt("age");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                int totalBorrow = rs.getInt("total_borrow");
                Object[] row = {rank++, studenId, name, genderData, birthday, age, email, contact, totalBorrow};
                model.addRow(row);
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
    
    private static String getTheSQLQuery(String input) {
        String sql;
        switch (input) {
            case "Top borrowed books":
                sql = "select b.book_id, b.title, b.author, count(distinct(r.record_id)) as total_borrowed\n" +
                        "from books as b\n" +
                        "inner join book_genres as bg on b.book_id = bg.book_id\n" +
                        "inner join genres as g on bg.genre_id = g.genre_id\n" +
                        "inner join records as r on b.book_id = r.book_id\n" +
                        "inner join students as s on r.student_id = s.student_id\n" +
                        "where g.name like ? and s.gender like ? and year(now())-year(s.birthday) between ? and ?\n" +
                        "group by b.book_id\n" +
                        "order by total_borrowed desc";
                break;
            case "Top borrowed genres":
                sql = "select g.name, count(distinct(r.record_id)) as total_borrowed\n" +
                        "from books as b\n" +
                        "inner join book_genres as bg on b.book_id = bg.book_id\n" +
                        "inner join genres as g on bg.genre_id = g.genre_id\n" +
                        "inner join records as r on b.book_id = r.book_id\n" +
                        "inner join students as s on r.student_id = s.student_id\n" +
                        "where s.gender like ? and year(now())-year(s.birthday) between ? and ?\n" +
                        "group by g.name\n" +
                        "order by total_borrowed desc";
                break;
            default:
                sql = "select s.student_id, s.name, s.gender, s.birthday, year(now())-year(s.birthday) as age, s.email, s.contact, count(distinct(r.record_id)) as total_borrow\n" +
                        "from books as b\n" +
                        "inner join book_genres as bg on b.book_id = bg.book_id\n" +
                        "inner join genres as g on bg.genre_id = g.genre_id\n" +
                        "inner join records as r on b.book_id = r.book_id\n" +
                        "inner join students as s on r.student_id = s.student_id\n" +
                        "where g.name like ? and s.gender like ? and (r.status like ? or r.status like ?)\n" +                        
                        "group by s.student_id\n" +
                        "having age between ? and ?\n" +
                        "order by total_borrow desc";
                break;
        }
        return sql;
    }
    
    // get data for genres
    private void getDataForGenreListFromDatabase() {
        if(genreBox.getItemCount() == 0) {
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                String sql = "select name from genres order by name";    
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                genreBox.addItem("All");
                while(rs.next()) {
                    genreBox.addItem(rs.getString("name"));
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        genderBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        genreBox = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ageBox = new javax.swing.JComboBox<>();
        bookBox = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        studentBox = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        radioBtnStudent = new javax.swing.JRadioButton();
        radioBtnBook = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table = new rojerusan.RSTableMetro();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jLabel8.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/exit.png")), 50, 50));
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1850, 0, 50, 50));

        jLabel1.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/criteria.png")), 50, 50));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 60, 50, 50));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel2.setText("CRITERIA:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 60, 120, 50));

        jLabel3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/student.png")), 50, 50));
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 50, 50));

        jLabel5.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("Gender");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 10, 70, 50));

        genderBox.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        genderBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Male", "Female" }));
        genderBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(genderBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 10, 100, 50));

        jLabel6.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("Genre");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1490, 10, 70, 50));

        jLabel7.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/genres.png")), 50, 50));
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1430, 10, 50, 50));

        genreBox.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        genreBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(genreBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1560, 10, 220, 50));

        jLabel9.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/age-group.png")), 50, 50));
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 90, 50, 50));

        jLabel10.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jLabel10.setText("Age");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 90, 60, 50));

        ageBox.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        ageBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Children(0-12)", "Teenagers(13-18)", "Young Adults(19-35)", "Middle-aged Adults(36-65)", "Elderly(66+)" }));
        ageBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(ageBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 90, 320, 50));

        bookBox.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        bookBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Top borrowed books", "Top borrowed genres" }));
        bookBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bookBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookBoxMouseClicked(evt);
            }
        });
        jPanel1.add(bookBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 280, 50));

        jLabel11.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 204, 204));
        jLabel11.setText("Book");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 90, 50));

        jLabel12.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(204, 204, 204));
        jLabel12.setText("Student");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 90, 50));

        studentBox.setFont(new java.awt.Font("Consolas", 1, 20)); // NOI18N
        studentBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Top borrower", "Top on-time borrowers", "Top late borrowers" }));
        studentBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        studentBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                studentBoxMouseClicked(evt);
            }
        });
        studentBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentBoxActionPerformed(evt);
            }
        });
        jPanel1.add(studentBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, 280, 50));

        jLabel13.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/gender.png")), 50, 50));
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 10, 50, 50));

        jLabel14.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/book.png")), 50, 50));
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 50, 50));

        radioBtnStudent.setBackground(new java.awt.Color(102, 102, 255));
        buttonGroup1.add(radioBtnStudent);
        radioBtnStudent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        radioBtnStudent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        radioBtnStudent.setPreferredSize(new java.awt.Dimension(40, 40));
        radioBtnStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnStudentActionPerformed(evt);
            }
        });
        jPanel1.add(radioBtnStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 50, 50));

        radioBtnBook.setBackground(new java.awt.Color(102, 102, 255));
        buttonGroup1.add(radioBtnBook);
        radioBtnBook.setSelected(true);
        radioBtnBook.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        radioBtnBook.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        radioBtnBook.setPreferredSize(new java.awt.Dimension(40, 40));
        radioBtnBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnBookActionPerformed(evt);
            }
        });
        jPanel1.add(radioBtnBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, 50, 50));

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
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, 180, 70));

        jButton5.setBackground(new java.awt.Color(102, 102, 255));
        jButton5.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reset.png")), 75, 75));
        jButton5.setBorder(null);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 75, 75));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1900, 160));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Book ID", "Title", "Student ID", "Name", "Issue Date", "Due Date", "Status", "Return Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        table.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        table.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        table.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        table.setFont(new java.awt.Font("Segoe UI Light", 0, 25)); // NOI18N
        table.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        table.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        table.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setRowHeight(40);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(table);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 1860, 670));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 1900, 840));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        HomePage page = new HomePage();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked

    }//GEN-LAST:event_tableMouseClicked

    private void radioBtnStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnStudentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnStudentActionPerformed

    private void radioBtnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnBookActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnBookActionPerformed

    private void bookBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookBoxMouseClicked
        radioBtnBook.setSelected(true);
    }//GEN-LAST:event_bookBoxMouseClicked

    private void studentBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentBoxMouseClicked
        radioBtnStudent.setSelected(true);
    }//GEN-LAST:event_studentBoxMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        search();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void studentBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_studentBoxActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        bookBox.setSelectedIndex(0);
        radioBtnBook.setSelected(true);
        studentBox.setSelectedIndex(0);
        genderBox.setSelectedIndex(0);
        genreBox.setSelectedIndex(0);
        ageBox.setSelectedIndex(0);
        updateBookTable();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void search() {
        if(radioBtnBook.isSelected()) {
            updateBookTable();
        }
        else {
            updateStudentTable();
        }
    }
    
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
            java.util.logging.Logger.getLogger(Statistic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Statistic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Statistic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Statistic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Statistic().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ageBox;
    private javax.swing.JComboBox<String> bookBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> genderBox;
    private javax.swing.JComboBox<String> genreBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JRadioButton radioBtnBook;
    private javax.swing.JRadioButton radioBtnStudent;
    private javax.swing.JComboBox<String> studentBox;
    private rojerusan.RSTableMetro table;
    // End of variables declaration//GEN-END:variables
}
