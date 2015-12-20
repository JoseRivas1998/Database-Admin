package com.tcg.dbadmin;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class ProgramManager {

    public static final double version = 1.3;

    public static String host;
    public static String user;
    public static String password;
    public static String database;
    public static Connection sqlCon;
    public static String[] tables;
    public static int currentTableIndex;

    static {
        ProgramManager.reset();
    }

    public static void reset() {
        tables = new String[]{};
        currentTableIndex = 0;
    }

    public static Connection getConnection(String host, String user, String password, String database) {
        try {
            sqlCon = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
            sqlCon = null;
        }
        return sqlCon;
    }

    public static Connection getConnection() {
        return ProgramManager.getConnection(ProgramManager.host, ProgramManager.user, ProgramManager.password, ProgramManager.database);
    }

    public static String getTable() {
        return getTable(currentTableIndex);
    }

    public static String getTable(int index) {
        return tables[index];
    }
}
