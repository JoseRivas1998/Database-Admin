package com.tcg.dbadmin.user;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
                List<String> lines = Files.readAllLines(entry.toPath(), StandardCharsets.UTF_8);
                String line = lines.toString();
                line = line.replace("[", "");
                line = line.replace("]", "");
                String[] array = line.split(";");
                String host = array[0];
                String name = array[1];
                String password = array[2];
                String database = array[3];
                User user = new User(host, name, password, database);
                users.add(user);
                System.out.println(users.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<User> getUsers(){
        return users;
    }
}
