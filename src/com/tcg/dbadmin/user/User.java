package com.tcg.dbadmin.user;

/**
 * Created by Nick on 12/5/2015 at 3:41 PM.
 * Project: Database-Admin
 */
public class User {
    public String host;
    public String name;
    public String password;
    public String database;

    public User(String host, String name, String password, String database){
        this.host = host;
        this.name = name;
        this.password = password;
        this.database = database;
    }

    public String sterilize(){
        return host + ";" + name + ";" + password + ";" + database + ";";
    }
}
