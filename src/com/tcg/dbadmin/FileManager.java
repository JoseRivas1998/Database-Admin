package com.tcg.dbadmin;

import com.tcg.dbadmin.user.User;

import javax.swing.*;
import java.io.*;

/**
 * Created by Nick on 12/5/2015 at 3:36 PM.
 * Project: Database-Admin
 */
public class FileManager {
    public File userFile;
    public String pathToFile;

    public FileManager(String user, String database){
        pathToFile = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "DBAdmin";
        userFile = new File(pathToFile + File.separator + user + "_" + database + ".dat");
        if(!userFile.exists()){
            try {
                userFile.createNewFile();
                System.out.println("Files created!");
                System.out.println("Path: " + userFile.getPath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(LogInFrame.getInstance(), "Could not save user data!", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void saveData(String host, String name, String password, String database){
        User userProfile = new User(host, name, password, database);
        DataOutputStream dataOutputStream;
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(userFile);
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(userProfile.sterilize());
            outputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
