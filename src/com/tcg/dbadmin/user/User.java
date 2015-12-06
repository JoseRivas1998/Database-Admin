package com.tcg.dbadmin.user;

import java.io.*;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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
    
    public static User load(File file) throws Exception {
    	User u = null;
    	FileInputStream fileIn = new FileInputStream(file);
    	ObjectInputStream objectIn = new ObjectInputStream(fileIn);
    	byte[] bytes = (byte[]) objectIn.readObject();
    	u = (User) User.deserialize(bytes);
    	objectIn.close();
    	fileIn.close();
    	u.setHost(u.decrypt(u.host));
    	u.setName(u.decrypt(u.name));
    	u.setPassword(u.decrypt(u.password));
    	u.setDatabase(u.decrypt(u.database));
    	return u;
    }
    
    public static void save(User u, File userFile) throws Exception {
    	u.setHost(u.encrypt(u.host));
    	u.setName(u.encrypt(u.name));
    	u.setPassword(u.encrypt(u.password));
    	u.setDatabase(u.encrypt(u.database));
    	ObjectOutputStream objectOutputStream;
	    OutputStream outputStream;
        outputStream = new FileOutputStream(userFile);
        objectOutputStream = new ObjectOutputStream (outputStream);
        objectOutputStream.writeObject(User.serialize(u));
        outputStream.close();
        objectOutputStream.close();
    }
    
	public static byte[] serialize(User u) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(b);
		out.writeObject(u);
		return b.toByteArray();
	}
	
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(b);
		return in.readObject();
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
	private static final String ALGORITHM = "AES";
	private static final byte[] keyValue = 
	    new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

	 public String encrypt(String valueToEnc) throws Exception {
	    Key key = generateKey();
	    Cipher c = Cipher.getInstance(ALGORITHM);
	    c.init(Cipher.ENCRYPT_MODE, key);
	    byte[] encValue = c.doFinal(valueToEnc.getBytes());
	    String encryptedValue = new BASE64Encoder().encode(encValue);
	    return encryptedValue;
	}

	public String decrypt(String encryptedValue) throws Exception {
	    Key key = generateKey();
	    Cipher c = Cipher.getInstance(ALGORITHM);
	    c.init(Cipher.DECRYPT_MODE, key);
	    byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedValue);
	    byte[] decValue = c.doFinal(decordedValue);
	    String decryptedValue = new String(decValue);
	    return decryptedValue;
	}

	private static Key generateKey() throws Exception {
	    Key key = new SecretKeySpec(keyValue, ALGORITHM);
	    return key;
	}
}
