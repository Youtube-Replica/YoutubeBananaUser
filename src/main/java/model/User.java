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
            conn.setAutoCommit(false);
            CallableStatement upperProc = conn.prepareCall("{? = call get_user_by_id( ? ) }");
            upperProc.registerOutParameter(1,Types.OTHER);
            upperProc.setInt(2,id);
            upperProc.execute();
            ResultSet rs = (ResultSet) upperProc.getObject(1);
            while (rs.next()) {
                userObject.put("user_name",rs.getString(2));
                userObject.put("email",rs.getString(3));
            }
            rs.close();
            upperProc.close();
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
            CallableStatement upperProc = conn.prepareCall("{? = call delete_user( ? ) }");
            upperProc.registerOutParameter(1,Types.INTEGER);
            upperProc.setInt(2,id);
            upperProc.execute();
            rowsDeleted = upperProc.getInt(1);
            upperProc.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(rowsDeleted + " rows deleted");
        return rowsDeleted + " rows deleted";
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
