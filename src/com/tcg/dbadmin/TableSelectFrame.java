package com.tcg.dbadmin;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;

public class TableSelectFrame extends JFrame {

	private static final long serialVersionUID = 8787371079352707195L;
	
	private Connection sqlCon;

	private ArrayList<String> tableList;
	
	private JLabel label;
	private JComboBox<String> tcb;
	private JButton go;
	
	private JPanel dropDown;
	
	public TableSelectFrame(JFrame caller) {
		
		sqlCon = ProgramManager.getConnection();
		
		tableList = new ArrayList<String>();
		
		try {
			DatabaseMetaData meta = sqlCon.getMetaData();
			ResultSet res = meta.getTables(null, null, null, new String[]{});
		      System.out.println("List of tables: "); 
		      while (res.next()) {
		         tableList.add(res.getString("TABLE_NAME"));
		      }
		      res.close();
		      ProgramManager.tables = new String[tableList.size()];
		      for(int i = 0; i < tableList.size(); i++) {
		    	  ProgramManager.tables[i] = tableList.get(i);
		      }

		      sqlCon.close();
		} catch (SQLException e) {}
		
		label = new JLabel("Select a table");
		
		tcb = new JComboBox<String>(ProgramManager.tables);
		tcb.setSelectedIndex(ProgramManager.currentTableIndex);
		
		dropDown = new JPanel();
		dropDown.setLayout(new FlowLayout());
		
		dropDown.add(label);
		dropDown.add(tcb);

		go = new JButton("Go");
		go.addActionListener(e -> {
			ProgramManager.currentTableIndex = tcb.getSelectedIndex();
			new MainFrame(this);
		});
		
		getContentPane().add(dropDown, BorderLayout.CENTER);
		getContentPane().add(go, BorderLayout.SOUTH);
		pack();
		setTitle(ProgramManager.database + " | Database Admin");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(caller);
		setVisible(true);
	}
}
