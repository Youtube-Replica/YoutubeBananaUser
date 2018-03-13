package model;


import org.json.simple.JSONObject;

import java.sql.*;
import java.util.Properties;

public class User {
    //example
    public static String getUserById(int id) {
        String url = "jdbc:postgresql://localhost/scalable";
        System.out.println("ID is: "+id);
        Properties props = new Properties();
        props.setProperty("user", "nagaty");
        props.setProperty("password", "61900");
        Connection conn = null;
        JSONObject userObject = new JSONObject();
        try {
            conn = DriverManager.getConnection(url, props);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM app_user WHERE id="+id);
            while (rs.next()) {
                userObject.put("user_name",rs.getString(2));
                userObject.put("email",rs.getString(3));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return userObject.toString();
    }

    public static String deleteUserById(int id){
        String url = "jdbc:postgresql://localhost/scalable";
        System.out.println("ID is: "+id);
        Properties props = new Properties();
        props.setProperty("user", "nagaty");
        props.setProperty("password", "61900");
        int rowsDeleted = 0;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, props);
            CallableStatement upperProc = conn.prepareCall("{ call delete_user( ? ) }");
            upperProc.setInt(1,id);
            rowsDeleted = upperProc.executeUpdate();
            System.out.println("Rows deleted:" + rowsDeleted);
            //Set SQL Function to return 1 if successful delete
            upperProc.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "rows deleted";
    }

    public static String signupUser(String user_name, String email, String password) {
        String url = "jdbc:postgresql://localhost/scalable";
        Properties props = new Properties();
        props.setProperty("user", "nagaty");
        props.setProperty("password", "61900");
        Connection conn = null;
        String rowsInserted ="";
        try {
            conn = DriverManager.getConnection(url, props);
            CallableStatement upperProc = conn.prepareCall("{ call user_signup( ?, ?, ? ) }");
            upperProc.setString(1,user_name);
            upperProc.setString(2,email);
            upperProc.setString(3,password);

            rowsInserted = "rows inserted " + upperProc.executeUpdate();
            //Set SQL Function to return 1 if successful insert
            upperProc.close();
        } catch (SQLException e) {
            System.out.println("SQL Error State: " + e.getSQLState());
            if(Integer.parseInt(e.getSQLState()) == 23505){
                rowsInserted = "This email already exists.";
            }
            else {
                rowsInserted = "-1";
                e.printStackTrace();
            }
        }
        return rowsInserted;
    }

    public static String changePasswordById(int id, String password) {
        String url = "jdbc:postgresql://localhost/scalable";
        Properties props = new Properties();
        props.setProperty("user", "nagaty");
        props.setProperty("password", "61900");
        Connection conn = null;
        String rowsAffected ="-1";
        try {
            conn = DriverManager.getConnection(url, props);
            CallableStatement upperProc = conn.prepareCall("{ call user_change_password( ?, ? ) }");
            upperProc.setInt(1,id);
            upperProc.setString(2,password);
            rowsAffected = "rows affected " + upperProc.executeUpdate();
            upperProc.close();
        } catch (SQLException e) {
            System.out.println("SQL Error State: " + e.getSQLState());
                e.printStackTrace();
        }
        return rowsAffected;
    }
}
