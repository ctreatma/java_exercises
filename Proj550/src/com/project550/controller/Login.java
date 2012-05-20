package com.project550.controller;

//import java.sql.*;
import java.util.*;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.UserBean;

public class Login extends ActionSupport {

    public String execute() throws Exception {
        // TODO: Get UserBean from DB, set in session
    	Map session = ActionContext.getContext().getSession();

    	int uid = DBConnector.login(getUsername(), getPassword());

    	if (uid >= 0) {
    	    UserBean user = DBConnector.getUser(uid, true, true);
    	    session.put("user", user);

    	 return SUCCESS;
    	}
    	else {
    	    addActionError("Please enter a valid Username/Password");
    	    return INPUT;
    	}

    }

    private boolean isInvalid(String value) {
        return (value == null || value.length() == 0);
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final String PASSWORD = "pword";
    public static final String USERNAME = "uname";
}