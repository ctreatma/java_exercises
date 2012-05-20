package com.project550.controller;

import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.CommentBean;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;

public class ShowLink extends ActionSupport {
    public String execute() throws Exception {
        setLink(DBConnector.getLink(lid));
        setComments(DBConnector.getLinkComments(lid));
        
        return SUCCESS;
    }
    
    private int lid;
    private LinkBean link;
    private CommentBean[] comments;
    
    public CommentBean[] getComments() {
        return comments;
    }

    public void setComments(CommentBean[] comments) {
        this.comments = comments;
    }

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
