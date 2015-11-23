package com.tcg.dbadmin;

import javax.swing.*;

import java.awt.*;
public class LogInFrame extends JFrame {

	private static final long serialVersionUID = -2080297185144594144L;
	
	private JTextArea hostTA, userTA, databaseTA;
	private JPasswordField passField;
	private JLabel hostL, userL, passL, databaseL;
	
	private JPanel hostP, userP, passP, databaseP, formP, buttonP;
	
	private JButton logIn, cancel;
	
	public LogInFrame() {
		
		hostP = new JPanel();
		hostP.setLayout(new FlowLayout());
		
		hostTA = new JTextArea(1, 15);
		hostL = new JLabel("Host:");
		hostP.add(hostL);
		hostP.add(hostTA);

		userP = new JPanel();
		userP.setLayout(new FlowLayout());
		
		userTA = new JTextArea(1, 15);
		userL = new JLabel("User:");
		userP.add(userL);
		userP.add(userTA);

		passP = new JPanel();
		passP.setLayout(new FlowLayout());
		
		passField = new JPasswordField(15);
		passL = new JLabel("Password:");
		passP.add(passL);
		passP.add(passField);

		databaseP = new JPanel();
		databaseP.setLayout(new FlowLayout());
		
		databaseTA = new JTextArea(1, 15);
		databaseL = new JLabel("Database:");
		databaseP.add(databaseL);
		databaseP.add(databaseTA);
		
		formP = new JPanel();
		formP.setLayout(new GridLayout(4, 1));
		formP.add(hostP);
		formP.add(userP);
		formP.add(passP);
		formP.add(databaseP);
		
		logIn = new JButton("Log In");
		logIn.addActionListener(e -> {
			ProgramManager.user = userTA.getText();
			ProgramManager.host = hostTA.getText();
			ProgramManager.password = String.valueOf(passField.getPassword());
			ProgramManager.database = databaseTA.getText();
			if(ProgramManager.getConnection() != null) {
				JOptionPane.showMessageDialog(this, "Database Connected Successfully", "Conntected", JOptionPane.INFORMATION_MESSAGE);
				new TableSelectFrame(this);
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "Unable to log in", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(e -> {
			dispose();
			System.exit(0);
		});
		
		buttonP = new JPanel();
		buttonP.setLayout(new BorderLayout());

		buttonP.add(logIn, BorderLayout.NORTH);
		buttonP.add(cancel, BorderLayout.SOUTH);

		getContentPane().add(formP, BorderLayout.NORTH);
		getContentPane().add(buttonP, BorderLayout.SOUTH);
		setTitle("Log In | Database Admin");
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);	
	}
	
}
