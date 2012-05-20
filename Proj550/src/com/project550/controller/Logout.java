package com.project550.controller;

import java.util.*;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Logout extends ActionSupport{

	public String execute ()throws Exception
	{
		Map session = ActionContext.getContext().getSession();
	    session.remove("user");
	    return SUCCESS;
	}

}
