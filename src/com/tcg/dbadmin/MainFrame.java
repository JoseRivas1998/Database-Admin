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

	Statement editSqlState;
	JPanel editPanel;
	JPanel editButtons;
	JPanel editContentPanel;
	ResultSet editResultSet;
	ArrayList<JLabel> editLabels;
	ArrayList<JTextArea> editAreas;
	JButton editPrev;
	JButton editNext;
	JButton editUpdate;
	JButton editDeleteRow;

	Statement insertSqlState;
	JPanel insertPanel;
	JPanel insertButtons;
	JPanel insertContentPanel;
	ResultSet insertResultSet;
	ArrayList<JLabel> insertLabels;
	ArrayList<JTextArea> insertAreas;
	JButton insertButton;
	
	Statement customSqlState;
	JPanel sqlPanel;
	JPanel sqlInputButtons;
	JPanel sqlInput;
	JTextArea sqlField;
	JScrollPane sqlFieldPane;
	JButton sqlAddTable;
	JButton sqlClear;
	JButton sqlSelect;
	JButton sqlSubmit;
	JScrollPane sqlTableScrollPane;
	JTable sqlTable;
	
	final String[] updateQueries = {
			"INSERT", "UPDATE", "DELETE",
			"TRUNCATE", "ALTER"
	};
	
	public MainFrame(JFrame caller) {
		connect();
		
		initTop();
		
		tabbedView = new JTabbedPane();
		
		initTablePane();
		initEditPane();
		initInsertPane();
		initSqlPane();
		
		tabbedView.addTab("Browse", browseScrollPane);
		tabbedView.addTab("Edit", editPanel);
		tabbedView.addTab("SQL", sqlPanel);
		tabbedView.addTab("Insert", insertPanel);

		getContentPane().add(top, BorderLayout.NORTH);
		getContentPane().add(tabbedView, BorderLayout.CENTER);
		
		setSize(800, 600);
		setLocationRelativeTo(caller);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		updateTitle();
		setVisible(true);
	}
	
	private void initTop() {
		
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
			editContentPanel.removeAll();
			initEdit();
			insertContentPanel.removeAll();
			initInsert();
			sqlField.setText(String.format("SELECT * FROM %s WHERE 1", ProgramManager.getTable()));
		});
		
		top.add(changeTableLabel);
		top.add(tableComboBox);
		top.add(changeTableButton);
	}

	private void initTablePane() {
		
		browseScrollPane = new JScrollPane();
		browseTable = new JTable();
		browseScrollPane.setViewportView(browseTable);
		setTableToDefault();
	}
	
	private void initEditPane() {

		editContentPanel = new JPanel();
		editLabels = new ArrayList<>();
		editAreas = new ArrayList<>();

		editPrev = new JButton("<");
		editNext = new JButton(">");

		editUpdate = new JButton("Submit Update");
		editDeleteRow = new JButton("Delete This Row");
		
		editButtons = new JPanel();
		editButtons.setLayout(new FlowLayout());
		editButtons.add(editPrev);
		editButtons.add(editNext);
		editButtons.add(editUpdate);
		editButtons.add(editDeleteRow);
		
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
		
		editUpdate.addActionListener(e -> {
			DatabaseMetaData meta = null;
			try {
				meta = sqlCon.getMetaData();
				ResultSet set = meta.getPrimaryKeys(null, null, ProgramManager.getTable());
				String primary = "", key = "";
				while(set.next()) {
				      primary = set.getString("COLUMN_NAME");
				      System.out.println("getPrimaryKeys(): columnName=" + primary);
				}
				key = editResultSet.getString(primary);
				String sql = String.format("UPDATE %s SET", ProgramManager.getTable());
				ArrayList<String> colNames = new ArrayList<>();
				ArrayList<String> colValues = new ArrayList<>();
				for(JLabel jl : editLabels) {
					colNames.add(jl.getText());
				}
				for(JTextArea jta : editAreas) {
					colValues.add(jta.getText());
				}
				for(int i = 0; i < colNames.size(); i++) {
					String col = colNames.get(i);
					String value = String.format("'%s'", colValues.get(i));
					if(i + 1 == colNames.size()) {
						sql += String.format(" %s = %s", col, value);
					} else {
						sql += String.format(" %s = %s,", col, value);
					}
				}
				sql += String.format(" WHERE %s = '%s';", primary, key);
				System.out.println(sql);
				boolean retry = false;
				do {
					retry = false;
					try {
						editSqlState.executeUpdate(sql);
						JOptionPane.showMessageDialog(this, "Updated Row", "Success", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e2) {
						if(e2 instanceof CommunicationsException) {
							connect();
							retry = true;
						} else {
							JOptionPane.showMessageDialog(this, e2.getMessage(), e2.getClass().getName(), JOptionPane.ERROR_MESSAGE);
							e2.printStackTrace();
						}
					}
				} while(retry);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setTableToDefault();
			initEdit();
		});
		
		editDeleteRow.addActionListener(e -> {
			DatabaseMetaData meta = null;
			if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this row?", "Are you Sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try {
					meta = sqlCon.getMetaData();
					ResultSet set = meta.getPrimaryKeys(null, null, ProgramManager.getTable());
					String primary = "", key = "";
					while(set.next()) {
					      primary = set.getString("COLUMN_NAME");
					      System.out.println("getPrimaryKeys(): columnName=" + primary);
					}
					key = editResultSet.getString(primary);
					String sql = String.format("DELETE FROM %s WHERE %s = '%s';", ProgramManager.getTable(), primary, key);
					System.out.println(sql);
					boolean retry = false;
					do {
						retry = false;
						try {
							editSqlState.executeUpdate(sql);
							JOptionPane.showMessageDialog(this, "Deleted Row", "Success", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e2) {
							if(e2 instanceof CommunicationsException) {
								connect();
								retry = true;
							} else {
								JOptionPane.showMessageDialog(this, e2.getMessage(), e2.getClass().getName(), JOptionPane.ERROR_MESSAGE);
								e2.printStackTrace();
							}
						}
					} while(retry);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				setTableToDefault();
				initEdit();
				initInsert();
			}
		});
		
		editPanel = new JPanel();
		editPanel.setLayout(new BorderLayout());
		editPanel.add(editContentPanel, BorderLayout.CENTER);
		editPanel.add(editButtons, BorderLayout.SOUTH);
		initEdit();
	}

	private void initInsertPane() {
		
		insertContentPanel = new JPanel();
		insertLabels = new ArrayList<>();
		insertAreas = new ArrayList<>();
		
		insertButton = new JButton("Insert");
		
		insertButton.addActionListener(e -> {
			ArrayList<String> col, val;
			col = new ArrayList<>();
			val = new ArrayList<>();
			for(int i = 0; i < insertAreas.size(); i++) {
				if(!insertAreas.get(i).getText().isEmpty()) {
					col.add(insertLabels.get(i).getText());
					val.add(insertAreas.get(i).getText());
				}
			}
			String sql = String.format("INSERT INTO %s (", ProgramManager.getTable());
			for(int i = 0; i < col.size(); i++) {
				if(i + 1 >= col.size()) {
					sql += String.format("%s", col.get(i));
				} else {
					sql += String.format("%s, ", col.get(i));
				}
			}
			sql += ") VALUES (";
			for(int i = 0; i < val.size(); i++) {
				if(i + 1 >= val.size()) {
					sql += String.format("'%s'", val.get(i));
				} else {
					sql += String.format("'%s', ", val.get(i));
				}
			}
			sql += ")";
			if(JOptionPane.showConfirmDialog(this, String.format("Execute following query: %s?", sql), "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				boolean retry = false;
				do {
					retry = false;
					try {
						insertSqlState.executeUpdate(sql);
						JOptionPane.showMessageDialog(this, "Row Inserted", "Success", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e2) {
						if(e2 instanceof CommunicationsException) {
							connect();
							retry = true;
						} else {
							JOptionPane.showMessageDialog(this, e2.getMessage(), e2.getClass().getName(), JOptionPane.ERROR_MESSAGE);
							e2.printStackTrace();
						}
					}
				} while(retry);
			}
			setTableToDefault();
			initEdit();
			initInsert();
		});
		
		insertButtons = new JPanel();
		insertButtons.setLayout(new FlowLayout());
		
		insertButtons.add(insertButton);
		
		insertPanel = new JPanel();
		insertPanel.setLayout(new BorderLayout());
		insertPanel.add(insertContentPanel, BorderLayout.CENTER);
		insertPanel.add(insertButtons, BorderLayout.SOUTH);
		
		initInsert();
		
	}
	
	private void initSqlPane() {
		
		sqlField = new JTextArea();
		sqlField.setText(String.format("SELECT * FROM %s WHERE 1", ProgramManager.getTable()));
		sqlSubmit = new JButton("Submit SQL");
		sqlFieldPane = new JScrollPane();
		
		sqlFieldPane.setViewportView(sqlField);
		
		Dimension preffSize = sqlSubmit.getPreferredSize();
		sqlSubmit.setPreferredSize(new Dimension(preffSize.width, preffSize.height * 3));
		
		sqlAddTable = new JButton("Add Current Table");
		
		sqlClear = new JButton("Clear");
		
		sqlSelect = new JButton("Set to SELECT");

		sqlInputButtons = new JPanel();
		sqlInputButtons.setLayout(new FlowLayout());

		sqlInputButtons.add(sqlAddTable);
		sqlInputButtons.add(sqlClear);
		sqlInputButtons.add(sqlSelect);
		
		sqlInput = new JPanel();
		sqlInput.setLayout(new BorderLayout());
		
		sqlInput.add(sqlFieldPane, BorderLayout.CENTER);
		sqlInput.add(sqlSubmit, BorderLayout.EAST);
		sqlInput.add(sqlInputButtons, BorderLayout.SOUTH);
		
		sqlTable = new JTable();
		sqlTableScrollPane = new JScrollPane();
		
		sqlTableScrollPane.setViewportView(sqlTable);
		
		sqlPanel = new JPanel();
		sqlPanel.setLayout(new BorderLayout());
		sqlPanel.add(sqlInput, BorderLayout.NORTH);
		sqlPanel.add(sqlTableScrollPane, BorderLayout.CENTER);
		
		sqlSubmit.addActionListener(e -> {
			boolean retry = false;
			String sql = sqlField.getText();
			if(JOptionPane.showConfirmDialog(this, "Submit the following sql?\n" + sql, "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				do {
					retry = false;
					try {
						customSqlState = sqlCon.createStatement();
						if(stringArrayContains(updateQueries, firstWord(sql), false)) {
							customSqlState.executeUpdate(sql);
							ResultSet set = customSqlState.executeQuery(String.format("SELECT * FROM %s WHERE 1", ProgramManager.getTable()));
							sqlTable.setModel(DbUtils.resultSetToTableModel(set));
						} else {
							ResultSet set = customSqlState.executeQuery(sql);
							sqlTable.setModel(DbUtils.resultSetToTableModel(set));
						}
					} catch (Exception e2) {
						if(e2 instanceof CommunicationsException) {
							connect();
							retry = true;
						} else {
							JOptionPane.showMessageDialog(this, e2.getMessage(), e2.getClass().getName(), JOptionPane.ERROR_MESSAGE);
						}
					}
				} while (retry);
				setTableToDefault();
				initEdit();
				initInsert();
			}
		});
		
		sqlAddTable.addActionListener(e -> {
			sqlField.append(" " + ProgramManager.getTable());
		});
		
		sqlClear.addActionListener(e -> {
			sqlField.setText("");
		});
		
		sqlSelect.addActionListener(e -> {
			sqlField.setText(String.format("SELECT * FROM %s WHERE 1", ProgramManager.getTable()));
		});
		
	}

	private void updateTitle() {
		setTitle(ProgramManager.host + "/" + ProgramManager.database + ": " + ProgramManager.getTable() + " | Database Admin");
	}
	
	private void setTableToDefault() {
		String sql = String.format("SELECT * FROM %s", ProgramManager.getTable());
		boolean retry = false;
		do {
			retry = false;
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
			editSqlState = sqlCon.createStatement();
			insertSqlState = sqlCon.createStatement();
			customSqlState = sqlCon.createStatement();
		} catch(Exception e){}
	}
	
	private void initEdit() {
		String sql = String.format("SELECT * FROM %s", ProgramManager.getTable());
		boolean retry = false;
		do {
			retry = false;
			try {
				editResultSet = editSqlState.executeQuery(sql);
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
			editLabels.clear();
			editAreas.clear();
			for(int currentCol = 1; currentCol < numColumns + 1; currentCol++) {
				String colLabel = rsmd.getColumnLabel(currentCol);
				String content = editResultSet.getString(colLabel);
				editLabels.add(new JLabel(colLabel));
				JTextArea area = new JTextArea(1, 30);
				area.setText(content);
				editAreas.add(area);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		editContentPanel.setLayout(new GridLayout(numColumns + 1, 1));
		editContentPanel.removeAll();
		editPanel.remove(editContentPanel);
		for(int i = 0; i < numColumns; i++) {
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			p.add(editLabels.get(i));
			p.add(editAreas.get(i));
			editContentPanel.add(p);
		}
		editContentPanel.repaint();
		editPanel.add(editContentPanel, BorderLayout.CENTER);
		repaint();
	}
	
	private void initInsert() {
		String sql = String.format("SELECT * FROM %s", ProgramManager.getTable());
		boolean retry = false;
		do {
			retry = false;
			try {
				insertResultSet = insertSqlState.executeQuery(sql);
				insertResultSet.first();
			} catch (Exception e) {
				if(e instanceof CommunicationsException) {
					connect();
					retry = true;
				} else {
					e.printStackTrace();
				}
			}
		} while(retry);
		int numColumns = 0;
		ResultSetMetaData rsmd = null;
		insertLabels.clear();
		insertAreas.clear();
		try {
			rsmd = insertResultSet.getMetaData();
			numColumns = rsmd.getColumnCount();
			insertLabels.clear();
			insertAreas.clear();
			for(int currentCol = 1; currentCol < numColumns + 1; currentCol++) {
				String colLabel = rsmd.getColumnLabel(currentCol);
				insertLabels.add(new JLabel(colLabel));
				insertAreas.add(new JTextArea(1, 30));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		insertContentPanel.setLayout(new GridLayout(numColumns + 1, 1));
		insertContentPanel.removeAll();
		insertPanel.remove(insertContentPanel);
		for(int i = 0; i < numColumns; i++) {
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			p.add(insertLabels.get(i));
			p.add(insertAreas.get(i));
			insertContentPanel.add(p);
		}
		insertContentPanel.repaint();
		insertPanel.add(insertContentPanel, BorderLayout.CENTER);
		repaint();
	}
	
	private String firstWord(String text) {
		if(text.indexOf(' ') > -1) {
			return text.substring(0, text.indexOf(' '));
		} else {
			return text;
		}
	}
	
	private boolean stringArrayContains(String[] array, String text, boolean caseSensitive) {
		for(String s : array) {
			if(caseSensitive) {
				if(s.equals(text)) {
					return true;
				}
			} else {
				if(s.equalsIgnoreCase(text)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
