package model;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.Properties;

public class User {
    public static String getUserById(int id) {

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
    }

    public static String deleteUserById(int id){
        String callStatement = "{? = call delete_user( ? ) }";
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject inputID = new JSONObject();
        inputID.put("type",Types.INTEGER);
        inputID.put("value",id);
        jsonArray.add(inputID);
        json.put("out_type",Types.INTEGER);
        json.put("call_statement",callStatement);
        json.put("input_array",jsonArray);
        System.out.println(json.toString());
        return json.toString();
    }

    public static String signupUser(String user_name, String email, String password) throws NoSuchAlgorithmException {
        byte [] salt = SHAHashing.getSalt();
        password = SHAHashing.get_SHA_256_SecurePassword(password,salt);
        String callStatement = "{ call signup_user( ?, ?, ?, ? ) }";
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject inputUserName = new JSONObject();
        JSONObject inputEmail = new JSONObject();
        JSONObject inputPassword = new JSONObject();
        JSONObject inputSalt = new JSONObject();
        inputUserName.put("type",Types.VARCHAR);
        inputUserName.put("value",user_name);
        inputEmail.put("type",Types.VARCHAR);
        inputEmail.put("value",email);
        inputPassword.put("type",Types.VARCHAR);
        inputPassword.put("value",password);
        inputSalt.put("type",Types.VARCHAR);
        inputSalt.put("value",Base64.getEncoder().encodeToString(salt));
        jsonArray.add(inputUserName);
        jsonArray.add(inputEmail);
        jsonArray.add(inputPassword);
        jsonArray.add(inputSalt);
        json.put("out_type",0);
        json.put("call_statement",callStatement);
        json.put("input_array",jsonArray);
        return json.toString();
    }

    public static String changePasswordById(int id, String password) throws NoSuchAlgorithmException {
        String callStatement =  "{ ? = call change_password_user( ?,?,? ) }";
        byte [] salt = SHAHashing.getSalt();
        password = SHAHashing.get_SHA_256_SecurePassword(password,salt);
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject inputPassword = new JSONObject();
        JSONObject inputSalt = new JSONObject();
        JSONObject inputID = new JSONObject();
        inputPassword.put("type",Types.VARCHAR);
        inputPassword.put("value",password);
        inputSalt.put("type",Types.VARCHAR);
        inputSalt.put("value",Base64.getEncoder().encodeToString(salt));
        inputID.put("type",Types.INTEGER);
        inputID.put("value",id);
        jsonArray.add(inputID);
        jsonArray.add(inputPassword);
        jsonArray.add(inputSalt);
        json.put("call_statement",callStatement);
        json.put("out_type",Types.INTEGER);
        json.put("input_array",jsonArray);
        return json.toString();
    }
}
