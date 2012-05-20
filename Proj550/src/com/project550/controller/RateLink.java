package com.project550.controller;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;
import com.project550.model.UserBean;

public class RateLink extends ActionSupport {
    public String execute() throws Exception {

        Map session = ActionContext.getContext().getSession();
        
        if (session.containsKey("user")) {
            UserBean user = (UserBean) session.get("user");
            setLink(DBConnector.getLink(lid));
            
            if (DBConnector.existsVote(user.getUid(), link.getLid())) {
                addActionError("You have already rated this link!");
            }
            
            return SUCCESS;
        }
        else {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }
    }
    
    private int lid;
    private LinkBean link;

    public void setLink(LinkBean link) {
        this.link = link;
    }
    
    public LinkBean getLink() {
        return link;
    }

    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

}
