package com.tcg.dbadmin.user;

/**
 * Created by Nick on 12/5/2015 at 3:41 PM.
 * Project: Database-Admin
 */
public class User implements java.io.Serializable{
    
	private static final long serialVersionUID = -4494831668548821496L;
	
	private String host;
	private String name;
	private String password;
	private String database;

    public User(String host, String name, String password, String database){
        this.host = host;
        this.name = name;
        this.password = password;
        this.database = database;
    }

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
