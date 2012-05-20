package com.project550.controller;

import java.util.*;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;
import com.project550.model.UserBean;

public class ShowProfile extends ActionSupport {

    public String execute() throws Exception {
        Map session = ActionContext.getContext().getSession();

        if (session.containsKey("user")) {
            UserBean sUser = (UserBean) session.get("user");
            if (uid > 0 && uid != sUser.getUid())
                setUser(DBConnector.getUser(uid, false, false));
            else
                setUser(sUser);
            
            recommendedLinks = sUser.getTopRecommendations();

            UserBean[] friends = DBConnector.getFriends(sUser.getUid());

            for (UserBean f : friends) {
                if (f.getUid() == user.getUid())
                {
                	setFriend(true);
                }
            }
        }
        else {
            setUser(DBConnector.getUser(uid, false,false));
        }

        return SUCCESS;
    }

    private int uid;
    private UserBean user;
    private boolean friend = false;
    private LinkBean[] recommendedLinks;

	public LinkBean[] getRecommendedLinks() {
        return recommendedLinks;
    }

    public void setRecommendedLinks(LinkBean[] recommendedLinks) {
        this.recommendedLinks = recommendedLinks;
    }
    public void setUser(UserBean user) {
        this.user = user;
    }

    public UserBean getUser() {
        return user;
    }

    public boolean getFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
