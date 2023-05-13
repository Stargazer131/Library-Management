/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package page.feature;

import page.main.HomePage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import ultility.Recommendator;
import ultility.Resizer;

/**
 *
 * @author Hao
 */
public class ManageBook extends javax.swing.JFrame {
    private boolean comeBackToHomePage;
    /**
     * Creates new form ManageBooks
     */
    public ManageBook(boolean comeBackToHomePage, boolean isEditable) {
        this.comeBackToHomePage = comeBackToHomePage;
        initComponents();
        getDataForGenreListFromDatabase();
        setBookDetailToTable();
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/library.png")).getImage());
        if(isEditable == false) {
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
            jButton4.setEnabled(false);
        }
        sortByColumn();
    }
    
    // set book detail to the table
    private void setBookDetailToTable() {
        ArrayList<Object[]> data = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        DefaultTableModel model = (DefaultTableModel)bookTable.getModel();
        model.setRowCount(0);
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from books order by book_id";    
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()) {
                String bookId = rs.getString("book_id");
                String bookName = rs.getString("title");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");
                Object[] row = {bookId, bookName, author, quantity, null};
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
            String genres = String.join("|", getDataForGenreOfBook((String)data.get(i)[0]));
            data.get(i)[4] = genres;
            model.addRow(data.get(i));
        }
    }
    
    // validation
    private boolean validateData() {
        String bookId = txtBookId.getText();
        String bookName = txtBookName.getText();
        String author = txtAuthor.getText();
        int quantity;
        
        if(bookId.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Id");
            return false;
        }
        
        if(bookId.length() > 20) {
            JOptionPane.showMessageDialog(this, "Book Id can't be larger than 20 characters");
            return false;
        }

        if(bookName.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Title");
            return false;
        }
        
        if(bookName.length() > 100) {
            JOptionPane.showMessageDialog(this, "Title can't be larger than 100 characters");
            return false;
        }
        
        if(author.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter Author");
            return false;
        }
        
        if(author.length() > 30) {
            JOptionPane.showMessageDialog(this, "Author's name can't be larger than 30 characters");
        }
        
        try {
            quantity = Integer.parseInt(txtQuantity.getText());
            if(quantity < 0) {
                JOptionPane.showMessageDialog(this, "Invalid number!");
                return false;
            }
        }
        catch(NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number!");
            return false;
        }
        
        if(genreList.getSelectedValuesList().contains("All")) {
            JOptionPane.showMessageDialog(this, "Invalid Genre (All) is used for search purpose only");
            return false;  
        }
        
        if(genreList.getSelectedIndices().length == 0) {
            JOptionPane.showMessageDialog(this, "Please chose a genre");
            return false;            
        }
        return true;
    }
    
    // add book to data base
    private void addBook() {
        String bookId = txtBookId.getText();
        String bookName = txtBookName.getText();
        String author = txtAuthor.getText();
        int quantity = Integer.parseInt(txtQuantity.getText());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "insert into books(book_id,title,author,quantity) values(?,?,?,?)";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, bookName);
            pst.setString(3, author);
            pst.setInt(4, quantity);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Book added successfully!");
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
        setDataForGenreListIntoDatabase();
    }
    
    // update book to database
    private void updateBook() {
        String bookId = txtBookId.getText();
        String bookName = txtBookName.getText();
        String author = txtAuthor.getText();
        int quantity = Integer.parseInt(txtQuantity.getText());
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "update books "
                       + "set title = ?, author = ?, quantity = ? "
                       + "where book_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookName);
            pst.setString(2, author);
            pst.setInt(3, quantity);
            pst.setString(4, bookId);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
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
        deleteDataAboutGenresOfBook(bookId);
        setDataForGenreListIntoDatabase();
    }
    
    // delete the book from data base
    private void deleteBook() {
        String bookId = txtBookId.getText();
        deleteDataAboutGenresOfBook(bookId);
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "delete from books where book_id = ?";    
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Book was deleted successfully!");
            }
            else {
                JOptionPane.showMessageDialog(this, "Record was deleted failure!");
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
    
    // set the selected genres for a book
    private void setDataForGenreListIntoDatabase() {
        int[] selectedItems = genreList.getSelectedIndices();
        String bookId = txtBookId.getText();
        for(int index : selectedItems) {
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                String sql = "insert into book_genres values(?, ?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, bookId);
                pst.setInt(2, index);
                int row = pst.executeUpdate();
                if(row > 0) {
                    
                }
                else {
                    System.out.println("Can't update genres data");
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
    
    //delete all the genres of a book
    private void deleteDataAboutGenresOfBook(String bookId) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "delete from book_genres where book_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            int row = pst.executeUpdate();
            if(row > 0) {

            }
            else {
                System.out.println("Can't delete genres data");
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
    private boolean checkExitedBook() {
        String bookId = txtBookId.getText();
        boolean exit = false;
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
    
    // get all the data again
    private void refresh() {
        setBookDetailToTable();
        this.revalidate();
        this.repaint();
    }
    
    // delete book record in issue details
    private void deleteBookRecord() {
        String bookId = txtBookId.getText();
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "delete from records where book_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, bookId);
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                
            }
            else {
                System.out.println("Error delete book records");
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
     
    // get the name of all genre
    private void getDataForGenreListFromDatabase() {
        if(genreList.getFirstVisibleIndex() == -1) {
            DefaultListModel model = new DefaultListModel();
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
                String sql = "select name from genres";    
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                model.addElement("All");
                while(rs.next()) {
                    model.addElement(rs.getString("name"));
                }
                genreList.setModel(model);
                genreList.setSelectedIndex(0);
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
    
    // when click search
    private void search() {
        String bookId = txtBookId.getText();
        String title = txtBookName.getText();
        String author = txtAuthor.getText();
        List<String> selectedGenres = genreList.getSelectedValuesList();
        
        ArrayList<Object[]> data = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);       
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            bookId = bookId.toLowerCase();
            title = title.toLowerCase();
            author = author.toLowerCase();
            
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms?autoReconnect=true&useSSL=false", "root", "hayasaka131");
            String sql = "select * from books "
                        + "where lower(book_id) like ? and lower(title) like ? and lower(author) like ?";

            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + bookId + "%");
            pst.setString(2, "%" + title + "%");
            pst.setString(3, "%" + author + "%");
            rs = pst.executeQuery();

            while (rs.next()) {
                String bookIdData = rs.getString("book_id");
                String titleData = rs.getString("title");
                String authorData = rs.getString("author");
                int quantityData = rs.getInt("quantity");
                Object[] row = {bookIdData, titleData, authorData, quantityData, null};
                data.add(row);
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
        
        // if select all genre
        if(!selectedGenres.contains("All")) {
            for(int i = 0; i < data.size(); i++) {
                ArrayList<String> genres = getDataForGenreOfBook((String)data.get(i)[0]);
                boolean full = true;
                for(String genre : selectedGenres) {
                    if(!genres.contains(genre)) {
                        full = false;
                    }
                }
                if(full) {
                    data.get(i)[4] = String.join("|", genres);
                }
            }
        }
        else {
            for(int i = 0; i < data.size(); i++) {
                ArrayList<String> genres = getDataForGenreOfBook((String)data.get(i)[0]);
                data.get(i)[4] = String.join("|", genres);
            }
        }
        
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i)[4] != null) {
                model.addRow(data.get(i));
            }
        }
    }
    
    // sort column 
    private void sortByColumn() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(bookTable.getModel());
        bookTable.setRowSorter(sorter);

        Comparator<Object> comparator1 = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // Implement your comparison logic here
                // For example, to compare strings, you can use the following:
                return o1.toString().compareTo(o2.toString());
            }
        };

        for(int i = 0; i < 5; i++) {
            if(i != 3) {
                sorter.setComparator(i, comparator1);
            }
        }
        
        Comparator<Object> comparator2 = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // Implement your comparison logic here
                // For example, to compare strings, you can use the following:
                return Integer.parseInt(o1.toString())-(Integer.parseInt(o2.toString()));
            }
        };
        
        sorter.setComparator(3, comparator2);
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
        jLabel6 = new javax.swing.JLabel();
        txtBookId = new app.bolivia.swing.JCTextField();
        jLabel10 = new javax.swing.JLabel();
        txtBookName = new app.bolivia.swing.JCTextField();
        jLabel11 = new javax.swing.JLabel();
        txtAuthor = new app.bolivia.swing.JCTextField();
        jLabel12 = new javax.swing.JLabel();
        txtQuantity = new app.bolivia.swing.JCTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        genreList = new javax.swing.JList<>();
        jButton5 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        bookTable = new rojerusan.RSTableMetro();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Book ID");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 280, 200, 30));

        jLabel6.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/quantity.png")), 50, 50));
        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 430, 50, 50));

        txtBookId.setBackground(new java.awt.Color(102, 102, 255));
        txtBookId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
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
        jPanel1.add(txtBookId, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 320, 240, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Title");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 280, 200, 30));

        txtBookName.setBackground(new java.awt.Color(102, 102, 255));
        txtBookName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtBookName.setPlaceholder("Enter Title ...");
        txtBookName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBookNameFocusLost(evt);
            }
        });
        txtBookName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBookNameActionPerformed(evt);
            }
        });
        jPanel1.add(txtBookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 320, 240, -1));

        jLabel11.setText("Author");
        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 410, 200, 30));

        txtAuthor.setBackground(new java.awt.Color(102, 102, 255));
        txtAuthor.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtAuthor.setPlaceholder("Enter Author's Name ...");
        txtAuthor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAuthorFocusLost(evt);
            }
        });
        txtAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAuthorActionPerformed(evt);
            }
        });
        jPanel1.add(txtAuthor, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 450, 240, -1));

        jLabel12.setText("Quantity");
        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 410, 200, 30));

        txtQuantity.setBackground(new java.awt.Color(102, 102, 255));
        txtQuantity.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtQuantity.setPlaceholder("Enter Quantity ...");
        txtQuantity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantityFocusLost(evt);
            }
        });
        txtQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantityActionPerformed(evt);
            }
        });
        jPanel1.add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 450, 240, -1));

        jLabel3.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/previous.png")), 50, 50));
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jLabel14.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/id-card.png")), 50, 50));
        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, 50, 50));

        jLabel15.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/name.png")), 50, 50));
        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 300, 50, 50));

        jLabel16.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/author.png")), 50, 50));
        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 50, 50));

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
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 190, 70));

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

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Genres");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 560, 180, 30));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/genres.png")), 50, 50));
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 600, 50, 50));

        genreList.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        genreList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane1.setViewportView(genreList);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 600, 180, -1));

        jButton5.setBackground(new java.awt.Color(102, 102, 255));
        jButton5.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/reset.png")), 75, 75));
        jButton5.setBorder(null);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 20, 75, 75));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 1000));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bookTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BookID", "Title", "Author", "Quantity", "Genres"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bookTable.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        bookTable.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        bookTable.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        bookTable.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        bookTable.setFont(new java.awt.Font("Segoe UI Light", 0, 25)); // NOI18N
        bookTable.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        bookTable.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        bookTable.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        bookTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        bookTable.setRowHeight(40);
        bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(bookTable);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 1150, 770));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/book.png")), 50, 50));
        jLabel2.setText("Manage Books");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 310, 70));

        jLabel4.setIcon(Resizer.resizeImageIcon(new ImageIcon(getClass().getResource("/resource/icons/exit.png")), 50, 50));
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 0, 50, 50));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, 1190, 1000));

        setSize(new java.awt.Dimension(1900, 1000));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtBookIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookIdFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookIdFocusLost

    private void txtBookIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBookIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookIdActionPerformed

    private void txtBookNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookNameFocusLost

    private void txtBookNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBookNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookNameActionPerformed

    private void txtAuthorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAuthorFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAuthorFocusLost

    private void txtAuthorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAuthorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAuthorActionPerformed

    private void txtQuantityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantityFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantityFocusLost

    private void txtQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantityActionPerformed

    private void bookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookTableMouseClicked
        int rowNum = bookTable.convertRowIndexToModel(bookTable.getSelectedRow());
        TableModel model = bookTable.getModel();
        txtBookId.setText(model.getValueAt(rowNum, 0).toString());
        txtBookName.setText(model.getValueAt(rowNum, 1).toString());
        txtAuthor.setText(model.getValueAt(rowNum, 2).toString());
        txtQuantity.setText(model.getValueAt(rowNum, 3).toString());
        
        genreList.clearSelection();
        // get all genre of the selected book
        ArrayList<String> items = getDataForGenreOfBook(model.getValueAt(rowNum, 0).toString());
        DefaultListModel jListModel = (DefaultListModel)genreList.getModel();
        // create an array that hold all the selected index of each genre
        int[] index = new int[items.size()];
        for(int i = 0; i < items.size(); i++) {
            index[i] = jListModel.indexOf(items.get(i));
        }
        genreList.setSelectedIndices(index);
    }//GEN-LAST:event_bookTableMouseClicked

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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(validateData()) {
            if(checkExitedBook()) {
                updateBook();
                refresh();
                Recommendator.updateData();
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find the book to update!");
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(validateData()) {
            if(checkExitedBook()) {
                int choice = JOptionPane.showConfirmDialog(this, "Delete this book mean delete all the issue details about it, are you sure?");
                if(choice == JOptionPane.OK_OPTION) {
                    deleteBookRecord();
                    deleteBook();
                    refresh();
                    Recommendator.updateData();
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't find the book to delete");
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(validateData()) {
            if(!checkExitedBook()) {
                addBook();
                refresh();
                Recommendator.updateData();
            }
            else {
                JOptionPane.showMessageDialog(this, "This Book ID is already existed!");
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        if(comeBackToHomePage) {
            System.exit(0);
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        txtBookId.setText("");
        txtBookName.setText("");
        txtAuthor.setText("");
        txtQuantity.setText("");
        genreList.clearSelection();
        setBookDetailToTable();
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
            java.util.logging.Logger.getLogger(ManageBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageBook(true, true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSTableMetro bookTable;
    private javax.swing.JList<String> genreList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private app.bolivia.swing.JCTextField txtAuthor;
    private app.bolivia.swing.JCTextField txtBookId;
    private app.bolivia.swing.JCTextField txtBookName;
    private app.bolivia.swing.JCTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
