package model;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.Properties;

public class User {
    //example
    public static String getUserById(int id) {
<<<<<<< HEAD
        String url = "jdbc:postgresql://localhost/scalable";
        System.out.println("ID is: "+id);
        Properties props = new Properties();
        props.setProperty("user", "hazemelagaty");
        props.setProperty("password", "");
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
=======
        String callStatement = "{? = call get_user_by_id( ? ) }";
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject inputObject = new JSONObject();
        inputObject.put("type",Types.INTEGER);
        inputObject.put("value",id);
        jsonArray.add(inputObject);
        json.put("call_statement",callStatement);
        json.put("out_type",Types.OTHER);
        json.put("input_array",jsonArray);

    return json.toString();
    }

    public static String getUserSalt(String email) throws NoSuchAlgorithmException {
        String callStatement = "{? = call get_user_salt(?)}";
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject inputObject = new JSONObject();
        inputObject.put("type",Types.VARCHAR);
        inputObject.put("value",email);
        jsonArray.add(inputObject);
        json.put("call_statement",callStatement);
        json.put("out_type",Types.OTHER);
        json.put("input_array",jsonArray);
//
//        try {
//            String saltString ="";
////            while (rs1.next()) {
////                saltString = rs1.getString("salt");
////            }
////            rs1.close();
////
////            byte [] salt = Base64.getDecoder().decode(saltString);
////            password = SHAHashing.get_SHA_256_SecurePassword(password,salt);
////
////            storProc.close();
////
////            CallableStatement upperProc = conn.prepareCall("{? = call login_user( ?,? ) }");
////
////            upperProc.registerOutParameter(1,Types.OTHER);
////
////            upperProc.setString(2,email);
////            upperProc.setString(3,password);
////            upperProc.execute();
////
////            ResultSet rs = (ResultSet) upperProc.getObject(1);
////
////            while (rs.next()) {
////                userObject.put("user_name",rs.getString(2));
////                userObject.put("email",rs.getString(3));
////            }
////            rs.close();
////            upperProc.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return json.toString();
    }

    public static String loginUser(String email,String password,String salt) throws NoSuchAlgorithmException {
        byte [] byteSalt = Base64.getDecoder().decode(salt);
            password = SHAHashing.get_SHA_256_SecurePassword(password,byteSalt);
        String callStatement = "{? = call login_user( ?,? )}";
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject inputEmail = new JSONObject();
        JSONObject inputPassword = new JSONObject();
        inputEmail.put("type",Types.VARCHAR);
        inputEmail.put("value",email);
        jsonArray.add(inputEmail);
        inputPassword.put("type",Types.VARCHAR);
        inputPassword.put("value",password);
        jsonArray.add(inputPassword);
        json.put("call_statement",callStatement);
        json.put("out_type",Types.OTHER);
        json.put("input_array",jsonArray);
        return json.toString();
>>>>>>> d68bec81d776aa5ffba71743d861fd9cb59a4bc5
    }

    public static String deleteUserById(int id){
        String url = "jdbc:postgresql://localhost/scalable";
        System.out.println("ID is: "+id);
        Properties props = new Properties();
        props.setProperty("user", "nagaty");
        props.setProperty("password", "61900");
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

    public static String signupUser(String user_name, String email, String password) throws NoSuchAlgorithmException {
        String url = "jdbc:postgresql://localhost/scalable";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "passw0rd");
        Connection conn = null;
        String rowsInserted ="";
        byte [] salt = SHAHashing.getSalt();
        password = SHAHashing.get_SHA_256_SecurePassword(password,salt);
        try {
            conn = DriverManager.getConnection(url, props);
            CallableStatement upperProc = conn.prepareCall("{ call signup_user( ?, ?, ?, ? ) }");
            upperProc.setString(1,user_name);
            upperProc.setString(2,email);
            upperProc.setString(3,password);
            upperProc.setString(4,Base64.getEncoder().encodeToString(salt));
            upperProc.execute();
            rowsInserted = "Created User";
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
        System.out.println("In Update password");
        String url = "jdbc:postgresql://localhost/scalable";
        Properties props = new Properties();
        props.setProperty("user", "nagaty");
        props.setProperty("password", "61900");
        Connection conn = null;
        int rowsAffected = 0;
        try {
            conn = DriverManager.getConnection(url, props);
            CallableStatement upperProc = conn.prepareCall("{ ? = call change_password_user( ?, ? ) }");
            upperProc.registerOutParameter(1,Types.INTEGER);
            upperProc.setInt(2,id);
            upperProc.setString(3,password);
            upperProc.execute();
            rowsAffected = upperProc.getInt(1);
            upperProc.close();

        } catch (SQLException e) {
            System.out.println("SQL Error State: " + e.getSQLState());
                e.printStackTrace();
        }
        return "Rows Affected: " + rowsAffected;
    }
}
