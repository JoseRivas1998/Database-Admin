package com.tcg.dbadmin;

import java.awt.*;
import java.sql.*;

import javax.swing.*;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import net.proteanit.sql.DbUtils;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 8787371079352707195L;
	
	private Connection sqlCon;
	private Statement sqlState;
	
	JPanel top;
	
	JLabel changeTableLabel;
	JComboBox<String> tableComboBox;
	JButton changeTableButton;
	
	JTabbedPane tabbedView;
	JTable browseTable;
	JScrollPane browseScrollPane;

	public MainFrame(JFrame caller) {
		connect();
		
		top = new JPanel();
		top.setLayout(new FlowLayout());
		
		changeTableLabel = new JLabel("Table");
		
		tableComboBox = new JComboBox<String>(ProgramManager.tables);
		tableComboBox.setSelectedIndex(ProgramManager.currentTableIndex);
		
		changeTableButton = new JButton("Change Table");
		changeTableButton.addActionListener(e -> {
			ProgramManager.currentTableIndex = tableComboBox.getSelectedIndex();
			updateTitle();
			setTableToDefault();
		});
		
		top.add(changeTableLabel);
		top.add(tableComboBox);
		top.add(changeTableButton);
		
		tabbedView = new JTabbedPane();
		
		browseScrollPane = new JScrollPane();
		browseTable = new JTable();
		browseScrollPane.setViewportView(browseTable);
		setTableToDefault();
		
		tabbedView.addTab("Browse", browseScrollPane);

		getContentPane().add(top, BorderLayout.NORTH);
		getContentPane().add(tabbedView, BorderLayout.CENTER);
		
		setSize(800, 600);
		setLocationRelativeTo(caller);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		updateTitle();
		setVisible(true);
	}
	
	private void updateTitle() {
		setTitle(ProgramManager.host + "/" + ProgramManager.database + ": " + ProgramManager.getTable() + " | Database Admin");
	}
	
	private void setTableToDefault() {
		String sql = String.format("SELECT * FROM %s", ProgramManager.getTable());
		boolean retry = false;
		do {
			try {
				ResultSet result = sqlState.executeQuery(sql);
				browseTable.setModel(DbUtils.resultSetToTableModel(result));
			} catch (Exception e) {
				if(e instanceof CommunicationsException) {
					connect();
					retry = true;
				} else {
					e.printStackTrace();
				}
			}
		} while(retry);
	}
	
	private void connect() {
		try {
			sqlCon = ProgramManager.getConnection();
			sqlState = sqlCon.createStatement();
		} catch(Exception e){}
	}
	
}
