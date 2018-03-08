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
        props.setProperty("user", "postgres");
        props.setProperty("password", "passw0rd");
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
        props.setProperty("user", "postgres");
        props.setProperty("password", "passw0rd");
        Connection conn = null;
        int rowsDeleted =0;
        try {
            conn = DriverManager.getConnection(url, props);
            PreparedStatement st = conn.prepareStatement("DELETE FROM app_user WHERE id="+id);
             rowsDeleted = st.executeUpdate();
            System.out.println(rowsDeleted + " rows deleted");
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsDeleted+" rows deleted";
    }

    public static String signupUser(String user_name, String email, String password) {
        String url = "jdbc:postgresql://localhost/scalable";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "passw0rd");
        Connection conn = null;
        int rowsInserted =0;
        try {
            System.out.println("entered try");
            conn = DriverManager.getConnection(url, props);
            PreparedStatement st = conn.prepareStatement("INSERT INTO app_user(user_name, email, password) values("
                    + "'" + user_name + "'" + ","
                    + "'" + email + "'" + ","
                    +  "'" + password +  "'"
                    + ")"
            );

            rowsInserted = st.executeUpdate();
            System.out.println("Passed SQL Query");
            System.out.println(rowsInserted + " rows inserted");
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsInserted + " rows inserted";
    }
}
