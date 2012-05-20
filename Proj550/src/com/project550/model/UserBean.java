package com.project550.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This object stores all of the attributes from a user entity.
 */
public class UserBean {

	/** Unique id number identifying a user. */
	private int uid;
	/** User's unique user name. */
	private String usrName;
	/** User's unique email address. */
	private String email;
	/** User's website password. */
	private String password;
	/** Listed first name of the user. */
	private String firstName;
	/** Listed last name of the user. */
	private String lastName;
	/** Listed age of the user. */
	private int age;
	/** Listed profession of the user. */
	private String profession;
	/** Listed country/location of the user. */
	private String country;
	private ArrayList<LinkBean> recommendations;
	
	/**
	 * Default constructor which populates UserBean object with "null" values.
	 */
	public UserBean() {
		uid = -1;
		usrName = null;
		email = null;
		password = null;
		firstName = null;
		lastName = null;
		age = -1;
		profession = null;
		country = null;
		recommendations = null;
	}

	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * @return the usrName
	 */
	public String getUsrName() {
		return usrName;
	}

	/**
	 * @param usrName the usrName to set
	 */
	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the profession
	 */
	public String getProfession() {
		return profession;
	}

	/**
	 * @param profession the profession to set
	 */
	public void setProfession(String profession) {
		this.profession = profession;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	public LinkBean[] getTopRecommendations() {
	    if (recommendations == null)
	        return null;
	    
	    int size = Math.min(recommendations.size(), 5);
	    LinkBean[] links = new LinkBean[size];
	    for (int i = 0; i < size; ++i) {
	        links[i] = recommendations.get(i);
	    }
	    return links;
	}
	
    public ArrayList<LinkBean> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(ArrayList<LinkBean> recommendations) {
        this.recommendations = recommendations;
    }
}
