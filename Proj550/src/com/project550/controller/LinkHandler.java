package com.project550.controller;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;
import com.project550.model.UserBean;

public class LinkHandler extends ActionSupport{

	public String execute ()throws Exception
	{
		Map session = ActionContext.getContext().getSession();

		if (session.containsKey("user")) {
            UserBean user = (UserBean) session.get("user");
            linkBean.setUser(user);
            StringBuffer pageInfo = new StringBuffer();
            StringBuffer allText = new StringBuffer();
            URL newUrl;
            
            try {
                newUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();
                conn.connect();
                BufferedReader contents = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = contents.readLine()) != null) {
                    pageInfo.append(line);
                }
                // All that just to get the title...
                allText.append(pageInfo.substring(pageInfo.indexOf("<title>") + 7, pageInfo.indexOf("</title>")));
                contents.close();
            }
            catch (Exception e) {
                addActionError("URL appears to be invalid.  Please input a valid URL");
                return INPUT;
            }
            
            allText.append(" " + linkBean.getTitle() + " " + linkBean.getDescription());
            
            String allTextString = allText.toString();
            allTextString = allTextString.toLowerCase();
            // Replace all non-word chars w/ single space
            allTextString = allTextString.replaceAll("\\W+", " ");
            // Split on white space
            String[] keywords = allTextString.split("\\s+");
            
            HashMap<String, Integer> kwdCounts = new HashMap<String, Integer>();
            
            for (String keyword : keywords) {
                Integer count = kwdCounts.get(keyword);
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                count++;
                kwdCounts.put(keyword, count);
            }
            
            linkBean.setLinkUrl(newUrl);

            int response = DBConnector.insertLink(linkBean, kwdCounts);
            if(response > 0)
            {
                setLid(response);
                return SUCCESS;
            }
            else if (response == -50)
            {
                addActionError("That link is already in our database!");
                return INPUT;
            }
            else
            {
                addActionError("Sorry your request could not be completed at this time. Please try again later");
                return INPUT;
            }
        }
        else
        {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }

	}

	private ArrayList<String> categories = DBConnector.getCategories();
	private LinkBean linkBean = new LinkBean();
	private String url;
	private int lid;

	public String getUrlname()
	{
		return this.url;
	}
	public void setUrlname(String url)
	{
		this.url = url;
	}

	public String getTitle()
	{
		return linkBean.getTitle();
	}
	public void setTitle(String title)
	{
		linkBean.setTitle(title);
	}

	public String getDescription()
	{
		return linkBean.getDescription();
	}
	public void setDescription(String description)
	{
		linkBean.setDescription(description);
	}
	public void setCategory(String category)
	{
		linkBean.setCategory(category);
	}
	public String getCategory()
	{
		return linkBean.getCategory();
	}
	public ArrayList<String> getCategories()
	{
		return this.categories;
	}
    public int getLid() {
        return lid;
    }
    public void setLid(int lid) {
        this.lid = lid;
    }

}