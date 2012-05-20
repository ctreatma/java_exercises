package edu.upenn.cis555.db;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    
    public User(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String toString() {
        return "[User: email=" + email + " firstName=" +
        firstName + " lastName=" + lastName + " password=" +
        password;
    }
    
    public static String encrypt(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA");
        byte[] encrypted = digest.digest(password.getBytes());
        
        StringBuffer buffer = new StringBuffer();
        for (byte code : encrypted) {
            buffer.append(Integer.toHexString(code));
        }
        
        return buffer.toString();
    }
}
