package com.tcg.dbadmin;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

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

	JPanel editPanel;
	JPanel editButtons;
	JPanel editContentPanel;
	ResultSet editResultSet;
	ArrayList<JLabel> editLabels;
	ArrayList<JTextArea> editAreas;
	JButton editPrev;
	JButton editNext;
	
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
		
		editLabels = new ArrayList<>();
		editAreas = new ArrayList<>();
		initEdit();

		editPrev = new JButton("<");
		editNext = new JButton(">");
		
		editButtons = new JPanel();
		editButtons.setLayout(new FlowLayout());
		editButtons.add(editPrev);
		editButtons.add(editNext);
		
		editPrev.addActionListener(e -> {
			try {
				if(editResultSet.isFirst()) {
					editResultSet.last();
				} else {
					editResultSet.previous();
				}
				retrieveEditRowInformation();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		editNext.addActionListener(e -> {
			try {
				if(editResultSet.isLast()) {
					editResultSet.first();
				} else {
					editResultSet.next();
				}
				retrieveEditRowInformation();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		editPanel = new JPanel();
		editPanel.setLayout(new BorderLayout());
		editPanel.add(editContentPanel, BorderLayout.CENTER);
		editPanel.add(editButtons, BorderLayout.SOUTH);
		
		tabbedView.addTab("Browse", browseScrollPane);
		tabbedView.addTab("Edit", editPanel);

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
	
	private void initEdit() {
		String sql = String.format("SELECT * FROM %s", ProgramManager.getTable());
		boolean retry = false;
		do {
			try {
				editResultSet = sqlState.executeQuery(sql);
				editResultSet.first();
			} catch (Exception e) {
				if(e instanceof CommunicationsException) {
					connect();
					retry = true;
				} else {
					e.printStackTrace();
				}
			}
		} while(retry);
		retrieveEditRowInformation();
	}
	
	private void retrieveEditRowInformation() {
		int numColumns = 0;
		ResultSetMetaData rsmd = null;
		editLabels.clear();
		editAreas.clear();
		try {
			rsmd = editResultSet.getMetaData();
			numColumns = rsmd.getColumnCount();
			for(int currentCol = 1; currentCol < numColumns + 1; currentCol++) {
				String colLabel = rsmd.getColumnLabel(currentCol);
				String content = editResultSet.getString(colLabel);
				editLabels.add(new JLabel(colLabel));
				JTextArea area = new JTextArea();
				area.setText(content);
				editAreas.add(area);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		editContentPanel = new JPanel();
		editContentPanel.setLayout(new GridLayout(numColumns + 1, 1));
		for(int i = 0; i < numColumns; i++) {
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			p.add(editLabels.get(i));
			p.add(editAreas.get(i));
			editContentPanel.add(p);
		}
		editContentPanel.repaint();
		repaint();
	}
	
}
