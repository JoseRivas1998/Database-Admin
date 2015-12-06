package com.tcg.dbadmin;

import com.tcg.dbadmin.user.User;
import com.tcg.dbadmin.user.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class LogInFrame extends JFrame implements KeyListener {

    private static final long serialVersionUID = -2080297185144594144L;

    private static LogInFrame instance;

    private UserManager userManager;

    private JTextArea hostTA, userTA, databaseTA;
    private JPasswordField passField;
    private JLabel hostL, userL, passL, databaseL, profileL;

    private JPanel hostP, userP, passP, databaseP, formP, buttonP, profileP;

    private JButton logIn, cancel, about;

    private JComboBox<String> profiles;

    public LogInFrame() {

        instance = this;

        userManager = new UserManager();

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

        profileP = new JPanel();
        profileP.setLayout(new FlowLayout());

        profiles = new JComboBox<String>();
        profileL = new JLabel("Profile:");
        profileP.add(profileL);
        profileP.add(profiles);

        profiles.addItem("Select a profile");

        List<User> users = userManager.getUsers();
        for(User user : users){
            profiles.addItem(String.format("%s: %s@%s", user.getDatabase(), user.getName(), user.getHost()));
        }

        profiles.addActionListener(e -> {
            if(profiles.getSelectedIndex() == 0){
                //Do nothing
                return;
            }
            User user = users.get(profiles.getSelectedIndex() - 1);
            hostTA.setText(user.getHost());
            userTA.setText(user.getName());
            passField.setText(user.getPassword());
            databaseTA.setText(user.getDatabase());
        });

        formP = new JPanel();
        formP.setLayout(new GridLayout(5, 1));
        formP.add(hostP);
        formP.add(userP);
        formP.add(passP);
        formP.add(databaseP);
        formP.add(profileP);

        logIn = new JButton("Log In");
        logIn.addActionListener(e -> {
            logIn(hostTA.getText(), userTA.getText(), String.valueOf(passField.getPassword()), databaseTA.getText());
        });

        cancel = new JButton("Cancel");
        cancel.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to canel?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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

    private void logIn(String host, String user, String password, String database) {
        ProgramManager.host = host;
        ProgramManager.user = user;
        ProgramManager.password = password;
        ProgramManager.database = database;
        if (ProgramManager.getConnection() != null) {
            JOptionPane.showMessageDialog(this, "Database Connected Successfully", "Connected", JOptionPane.INFORMATION_MESSAGE);
            new TableSelectFrame(this);
            dispose();

            FileManager fileManager = new FileManager(user, database);
            fileManager.saveData(host, user, password, database);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            e.consume();
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            logIn(hostTA.getText(), userTA.getText(), String.valueOf(passField.getPassword()), databaseTA.getText());
            e.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }


    public static LogInFrame getInstance(){
        return instance;
    }
}
