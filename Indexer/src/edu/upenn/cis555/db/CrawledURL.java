package edu.upenn.cis555.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrawledURL implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private Date lastCrawled;
    private String lastCrawlId;
    private List<String> channelNames;
    private String content;
    
    public CrawledURL(String url, Date lastCrawled, String lastCrawlId,
            List<String> channelNames) {
        this.url = url;
        this.lastCrawled = lastCrawled;
        this.lastCrawlId = lastCrawlId;
        if (channelNames != null) {
            this.channelNames = channelNames;
        }
        else {
            this.channelNames = new ArrayList<String>();
        }
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastCrawled() {
        return lastCrawled;
    }

    public void setLastCrawled(Date lastCrawled) {
        this.lastCrawled = lastCrawled;
    }

    public String getLastCrawlId() {
        return lastCrawlId;
    }

    public void setLastCrawlId(String lastCrawlId) {
        this.lastCrawlId = lastCrawlId;
    }

    public List<String> getChannelNames() {
        return channelNames;
    }

    public void setChannelNames(List<String> channelNames) {
        this.channelNames = channelNames;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return "CrawledURL [channelNames=" + channelNames + ", lastCrawlId="
                + lastCrawlId + ", lastCrawled=" + lastCrawled + ", url=" + url
                + ", content=" + content + "]";
    }
}
