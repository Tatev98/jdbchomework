package com.jdbc;

import java.sql.*;
import java.util.Scanner;

public class Application {
    static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USERNAME = "postgres";
    static final String PASSWORD = "1927";


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        createTables();
        while (true) {

            System.out.println("Input 1 to create account:");
            System.out.println("Input 2 to create role:");
            System.out.println("Input 3 to connect role and account:");
            System.out.println("Input 0 to exit:");
            int chooseAction = scan.nextInt();
            switch (chooseAction) {
                case 0:
                    System.exit(1);
                    break;
                case 1:
                    System.out.println("Insert username: ");
                    String username = scan.next();
                    System.out.println("Insert password: ");
                    String pass = scan.next();
                    System.out.println(insertAccount(username, pass));
                    break;
                case 2:
                    System.out.println("Insert role: ");
                    String rolename = scan.next();
                    System.out.println(insertRole(rolename));
                    break;

                case 3:
                    System.out.println("Insert username: ");
                    String userName = scan.next();
                    System.out.println("Insert roleName: ");
                    String roleName = scan.next();
                    connectAccountToRole(roleName, userName);
                    break;
            }
        }


    }

    public static int insertAccount(String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName(POSTGRES_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("insert into accounts(username, password) values(?,?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.executeUpdate();
        return getAccountId(username);
    }

    public static int insertRole(String roleName) throws SQLException, ClassNotFoundException {
        Class.forName(POSTGRES_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("insert into role(role_name) values(?)");
        stmt.setString(1, roleName);
        stmt.executeUpdate();
        return getRoleId(roleName);

    }

    public static void connectAccountToRole(String roleName, String username) throws SQLException, ClassNotFoundException {
        Class.forName(POSTGRES_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        int accountId = getAccountId(username);
        int roleId = getRoleId(roleName);
        PreparedStatement stmt = conn.prepareStatement("insert into account_roles(account_id, role_id) values(?,?)");
        stmt.setInt(1, accountId);
        stmt.setInt(2, roleId);
        stmt.executeUpdate();
        System.out.println("Account: " + accountId + " and role: " + roleId + " is connected");
    }

    public static int getAccountId(String username) throws SQLException, ClassNotFoundException {
        Class.forName(POSTGRES_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        Statement stmt = conn.createStatement();
        String getId = "SELECT account_id FROM accounts WHERE username =  " + "'" + username + "'";
        ResultSet rs = stmt.executeQuery(getId);
        if (rs.next()) {
            return rs.getInt("account_id");
        } else return 0;
    }

    public static int getRoleId(String roleName) throws SQLException, ClassNotFoundException {
        Class.forName(POSTGRES_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        Statement stmt = conn.createStatement();
        String getId = "SELECT role_id FROM role WHERE role_name =  " + "'" + roleName + "'";
        ResultSet rs = stmt.executeQuery(getId);
        if (rs.next()) {
            return rs.getInt("role_id");
        } else return 0;
    }

    public static void createTables() throws SQLException, ClassNotFoundException {
        Class.forName(POSTGRES_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        Statement stmt = conn.createStatement();
        String createTableAccounts = " CREATE TABLE IF NOT EXISTS accounts(account_id SERIAL NOT NULL PRIMARY KEY," +
                "username varchar(225) NOT NULL UNIQUE ," +
                "password varchar(225) NOT NULL )";
        String createTableRoles = "CREATE TABLE IF NOT EXISTS role(role_id SERIAL NOT NULL PRIMARY KEY," +
                "role_name varchar (255) UNIQUE NOT NULL)";
        String createTableAccountRoles = "CREATE TABLE IF NOT EXISTS account_roles (" +
                "  account_id INT NOT NULL," +
                "  role_id INT NOT NULL," +
                "  PRIMARY KEY (account_id, role_id)," +
                "  FOREIGN KEY (role_id)" +
                "      REFERENCES role (role_id)," +
                "  FOREIGN KEY (account_id)" +
                "      REFERENCES accounts (account_id)" +
                ")";
        stmt.execute(createTableAccounts);
        stmt.execute(createTableRoles);
        stmt.execute(createTableAccountRoles);
    }
}
