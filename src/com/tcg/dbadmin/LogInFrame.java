package com.tcg.dbadmin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogInFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = -2080297185144594144L;
	
	private JTextArea hostTA, userTA, databaseTA;
	private JPasswordField passField;
	private JLabel hostL, userL, passL, databaseL;
	
	private JPanel hostP, userP, passP, databaseP, formP, buttonP;
	
	private JButton logIn, cancel, about;
	
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
			logIn();
		});
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(e -> {
			if(JOptionPane.showConfirmDialog(this, "Are you sure you want to canel?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				dispose();
				System.exit(0);
			}
		});
		
		about = new JButton("About");
		about.addActionListener(e -> {
			new AboutFrame(this);
		});
		
		buttonP = new JPanel();
		buttonP.setLayout(new FlowLayout());

		buttonP.add(logIn);
		buttonP.add(about);
		buttonP.add(cancel);

		getContentPane().add(formP, BorderLayout.NORTH);
		getContentPane().add(buttonP, BorderLayout.SOUTH);
		setTitle("Log In | Database Admin");
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

        //Add our quick listeners for tabs and enters
        hostTA.addKeyListener(this);

        userTA.addKeyListener(this);
        
        passField.addKeyListener(this);

        databaseTA.addKeyListener(this);
    }

	private void logIn() {
		ProgramManager.user = userTA.getText();
		ProgramManager.host = hostTA.getText();
		ProgramManager.password = String.valueOf(passField.getPassword());
		ProgramManager.database = databaseTA.getText();
		if(ProgramManager.getConnection() != null) {
			JOptionPane.showMessageDialog(this, "Database Connected Successfully", "Conntected", JOptionPane.INFORMATION_MESSAGE);
			new TableSelectFrame(this);
			dispose();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_TAB){
	        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
	        e.consume();
	    }
	    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	    	logIn();
	    	e.consume();
	    }
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
