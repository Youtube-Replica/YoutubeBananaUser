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
                userObject.put("date_of_birth",rs.getString(5));
                userObject.put("gender",rs.getString(6));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return userObject.toString();
    }
}
