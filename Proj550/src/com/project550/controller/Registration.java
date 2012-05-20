package com.project550.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.UserBean;

public class Registration extends ActionSupport {

	public String execute()throws Exception
	{
	    if (!DBConnector.isUniqueEmail(user.getEmail()))   {
	    	addActionError("The selected Email address is already in use!! Please select another Email address");
	        return INPUT;
	    }
	    else if (!DBConnector.isUniqueUsrname(user.getUsrName())) {
	        addActionError("The selected User Name is already in use!! Please select a unique User Name");
	        return INPUT;
	    }
	    else {
	        DBConnector.insertUser(user);
	        return SUCCESS;
	    }

	}

	private UserBean user = new UserBean();

	public UserBean getUser() {
	    return user;
	}

	public void setUser(UserBean user) {
	    this.user = user;
	}

	public String getFirstName()
	{
		return user.getFirstName();
	}
	public void setFirstName(String firstName)
	{
		user.setFirstName(firstName);
	}

	public String getLastName()
	{
		return user.getLastName();
	}
	public void setLastName(String lastName)
	{
		user.setLastName(lastName);
	}

	public String getUsrName()
	{
		return user.getUsrName();
	}
	public void setUsrName(String usrName)
	{
		user.setUsrName(usrName);
	}

	public String getPassword()
	{
		return user.getPassword();
	}
	public void setPassword(String password)
	{
		user.setPassword(password);
	}

	public String getEmail()
	{
		return user.getEmail();
	}
	public void setEmail(String email)
	{
		user.setEmail(email);
	}

	public int getAge()
	{
		return user.getAge();
	}
	public void setAge(int age)
	{
		user.setAge(age);
	}

	public String getProfession()
	{
		return user.getProfession();
	}
	public void setProfession(String profession)
	{
		user.setProfession(profession);
	}

	public String getCountry()
	{
		return user.getCountry();
	}
	public void setCountry(String country)
	{
		user.setCountry(country);
	}
}
