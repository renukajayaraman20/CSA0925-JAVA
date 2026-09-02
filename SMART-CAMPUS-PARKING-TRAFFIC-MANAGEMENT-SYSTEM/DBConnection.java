package com.campuspulse.db;
import java.sql.*;
public final class DBConnection {
 private static final String URL="jdbc:mysql://localhost:3306/campuspulse?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
 private static final String USER="root";
 private static final String PASSWORD="1234";
 private DBConnection(){}
 public static Connection getConnection() throws SQLException{return DriverManager.getConnection(URL,USER,PASSWORD);}
 public static boolean isOnline(){try(Connection c=getConnection()){return c!=null&&!c.isClosed();}catch(SQLException e){return false;}}
}
