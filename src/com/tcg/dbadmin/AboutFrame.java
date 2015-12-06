package com.tcg.dbadmin;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class AboutFrame extends JFrame {

    private static final long serialVersionUID = -8529317374871456644L;

    JPanel text;
    ArrayList<JLabel> labels;
    JLabel title;

    JPanel buttons;
    JButton tinycountrygames;
    JButton gitHub;

    public AboutFrame(JFrame frame) {

        labels = new ArrayList<>();

        addLabel("Database Admin");
        addLabel("Author:");
        addLabel("Jos� Rodriguez-Rivas");
        addLabel("Cuurent Version:");
        addLabel(String.valueOf(ProgramManager.version));

        text = new JPanel();
        text.setLayout(new GridLayout(labels.size(), 1, 10, 10));

        for (JLabel jLabel : labels) {
            text.add(jLabel);
        }

        tinycountrygames = new JButton("Tiny Country Games");
        tinycountrygames.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URL("http://tinycountrygames.com/").toURI());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        gitHub = new JButton("Github");
        gitHub.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/JoseRivas1998/Database-Admin").toURI());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(tinycountrygames);
        buttons.add(gitHub);

        setTitle("About | Database Admin");

        getContentPane().add(text, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        int width = getSize().width;
        int height = getSize().height;
        setSize(width + 20, height + 20);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);

    }

    private void addLabel(String text) {
        labels.add(new JLabel(text, SwingConstants.CENTER));
    }

}
