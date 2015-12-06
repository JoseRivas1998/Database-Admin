package com.tcg.dbadmin.user;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 12/5/2015 at 5:24 PM.
 * Project: Database-Admin
 */
public class UserManager {
    public List<User> users;

    public UserManager(){
        users = new ArrayList<>();
        loadProfiles();
    }

    public void loadProfiles(){
        File folder = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "DBAdmin");
        if(!folder.exists()){
            folder.mkdirs();
        }
        for(File entry : folder.listFiles()){
            try {
            	FileInputStream fileIn = new FileInputStream(entry);
            	ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            	users.add((User) objectIn.readObject());
            	fileIn.close();
            	objectIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<User> getUsers(){
        return users;
    }
}
