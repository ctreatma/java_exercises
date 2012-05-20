package edu.upenn.cis555.youtube;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

public class YouTubeMessage implements Message {
    private static final long serialVersionUID = 1L;
    public static enum Type { QUERY, RESULT };
    
    private String mapKey;
    private Type type;
    private String keyword;
    private String videoFeedHtml;
    private NodeHandle from;

    public YouTubeMessage(NodeHandle from, String mapKey, Type type, String keyword) {
        this(from, mapKey, type, keyword, null);
    }
    
    public YouTubeMessage(NodeHandle from, String mapKey, Type type, String keyword, String videoFeedHtml) {
        this.from = from;
        this.mapKey = mapKey;
        this.type = type;
        this.keyword = keyword;
        this.videoFeedHtml = videoFeedHtml;
    }
    
    public NodeHandle getFrom() {
        return from;
    }
    
    public String getMapKey() {
        return mapKey;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public String getVideoFeedHtml() {
        return videoFeedHtml;
    }
    
    public String toString() {
        return "YouTubeMessage [keyword=" + keyword + ", type=" + type + "]";
    }

    public int getPriority() {
        return Message.LOW_PRIORITY;
    }
}
