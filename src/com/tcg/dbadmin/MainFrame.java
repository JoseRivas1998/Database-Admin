package com.tcg.dbadmin;

import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 8787371079352707195L;
	
	private Connection sqlCon;
	private Statement sqlState;

	private ArrayList<String> tables;
	
	public MainFrame(JFrame caller) {
		
		sqlCon = ProgramManager.getConnection();
		
		tables = new ArrayList<String>();
		
		try {
			DatabaseMetaData meta = sqlCon.getMetaData();
			ResultSet res = meta.getTables(null, null, null, new String[]{});
		      System.out.println("List of tables: "); 
		      while (res.next()) {
		         tables.add(res.getString("TABLE_NAME"));
		      }
	         for(String s : tables) {
	        	 System.out.println(s);
	         }
		      res.close();

		      sqlCon.close();
		} catch (SQLException e) {}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(caller);
		setVisible(true);
	}
}
