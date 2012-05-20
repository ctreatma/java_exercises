package edu.upenn.cis555.db;

import java.io.Serializable;
import java.util.List;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<String> xPathExprs;
    private String styleSheetUrl;
    private String createdByEmail;
    
    public Channel(String name, List<String> xPathExprs, String styleSheetUrl,
            String createdByEmail) {
        this.name = name;
        this.xPathExprs = xPathExprs;
        this.styleSheetUrl = styleSheetUrl;
        this.createdByEmail = createdByEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getXPathExprs() {
        return xPathExprs;
    }

    public void setxPathExprs(List<String> xPathExprs) {
        this.xPathExprs = xPathExprs;
    }

    public String getStyleSheetUrl() {
        return styleSheetUrl;
    }

    public void setStyleSheetUrl(String styleSheetUrl) {
        this.styleSheetUrl = styleSheetUrl;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }

    public String toString() {
        return "Channel [createdByEmail=" + createdByEmail + ", name=" + name
                + ", styleSheetUrl=" + styleSheetUrl + ", xPathExprs="
                + xPathExprs + "]";
    }
}
