package com.project550.controller;

import java.util.Map;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.UserBean;
import com.project550.model.VoteBean;

public class SubmitRating extends ActionSupport {

    public String execute() throws Exception {
        Map session = ActionContext.getContext().getSession();
        UserBean user;
        if (session.containsKey("user")) {
            user = (UserBean) session.get("user");

            VoteBean vote = new VoteBean();
            vote.setRating(rating);
            vote.setLid(lid);
            vote.setUid(user.getUid());
            
            if (DBConnector.existsVote(vote.getUid(), vote.getLid())) {
                addActionError("You have already rated this link!");
                return INPUT;
            }
            
            if(DBConnector.insertVote(vote))
            {
                return SUCCESS;
            }
            else {
                addActionError("Sorry your request could not be processed at this time. Please try again later");
                return INPUT;
            }
        }
        else
        {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }
    }

    private int lid;
    private int rating;

    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
