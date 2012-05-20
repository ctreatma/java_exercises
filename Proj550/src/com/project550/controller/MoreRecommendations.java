package com.project550.controller;

import java.util.Map;
import java.util.ArrayList;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.LinkBean;
import com.project550.model.UserBean;

public class MoreRecommendations extends ActionSupport {

	public String execute() throws Exception {
		Map session = ActionContext.getContext().getSession();

        if (session.containsKey("user")) {
            UserBean user = (UserBean) session.get("user");
            recommendedLinks = user.getRecommendations();

            if(recommendedLinks == null || recommendedLinks.size() == 0)
            {
                addActionError("There are no additional recommended links");
                return SUCCESS;
            }
            else
                return SUCCESS;
        }
        else {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }
    }

	private ArrayList<LinkBean> recommendedLinks;

	public ArrayList<LinkBean> getRecommendedLinks() {
        return recommendedLinks;
    }

    public void setRecommendedLinks(ArrayList<LinkBean> recommendedLinks) {
        this.recommendedLinks = recommendedLinks;
    }
}
