package edu.upenn.cis555.indexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class IndexerDB {
    private static final String CLASS_CATALOG = "java_class_catalog";
    private static final String USER_STORE = "user_store";
    private static final String CHANNEL_STORE = "channel_store";
    private static final String CRAWL_STORE = "crawl_store";
    
    private Environment environment;
    private StoredClassCatalog catalog;
    
    private Database userDb;
    private Database channelDb;
    private Database crawlDb;
    private StoredMap<String,User> userMap;
    private StoredMap<String,Channel> channelMap;
    private StoredMap<String,CrawledURL> crawlMap;
    
    // Wrapper for Berkeley DB
    // Provide methods for getting/adding data
    public IndexerDB(String homeDirectory) throws IOException {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        
        File dbFile = new File(homeDirectory);
        dbFile.mkdirs();
        environment = new Environment(dbFile, envConfig);
        
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
        
        Database db = environment.openDatabase(null, CLASS_CATALOG, dbConfig);

        catalog = new StoredClassCatalog(db);
        
        userDb = environment.openDatabase(null, USER_STORE, dbConfig);
        channelDb = environment.openDatabase(null, CHANNEL_STORE, dbConfig);
        crawlDb = environment.openDatabase(null, CRAWL_STORE, dbConfig);
        
        EntryBinding<String> userKeyBinding = new SerialBinding<String>(catalog, String.class);
        EntryBinding<User> userValueBinding = new SerialBinding<User>(catalog, User.class);
        
        EntryBinding<String> channelKeyBinding = new SerialBinding<String>(catalog, String.class);
        EntryBinding<Channel> channelValueBinding = new SerialBinding<Channel>(catalog, Channel.class);

        EntryBinding<String> crawlKeyBinding = new SerialBinding<String>(catalog, String.class);
        EntryBinding<CrawledURL> crawlValueBinding = new SerialBinding<CrawledURL>(catalog, CrawledURL.class);
        
        userMap = new StoredMap<String,User>(userDb, userKeyBinding, userValueBinding, true);

        channelMap = new StoredMap<String,Channel>(channelDb, channelKeyBinding, channelValueBinding, true);

        crawlMap = new StoredMap<String,CrawledURL>(crawlDb, crawlKeyBinding, crawlValueBinding, true);

    }
    
    synchronized public User getUser(String email) {
        return userMap.get(email);
    }
    
    synchronized public ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<User>();
        allUsers.addAll(userMap.values());
        return allUsers;
    }
    
    synchronized public void addUser(User user) {
        if (!userMap.containsKey(user.getEmail())) {
            userMap.put(user.getEmail(), user);
        }
        else {
            updateUser(user);
        }
    }
    
    synchronized public void updateUser(User user) {
        userMap.remove(user.getEmail());
        userMap.put(user.getEmail(), user);
    }
    
    synchronized public void removeUser(User user) {
        userMap.remove(user.getEmail());
    }
    
    synchronized public Channel getChannel(String name) {
        return channelMap.get(name);
    }
    
    synchronized public ArrayList<Channel> getAllChannels() {
        ArrayList<Channel> allChannels = new ArrayList<Channel>();
        allChannels.addAll(channelMap.values());
        return allChannels;
    }
    
    synchronized public void addChannel(Channel channel) {
        if (!userMap.containsKey(channel.getName())) {
            channelMap.put(channel.getName(), channel);
        }
        else {
            updateChannel(channel);
        }
    }
    
    synchronized public void updateChannel(Channel channel) {
        channelMap.remove(channel.getName());
        channelMap.put(channel.getName(), channel);
    }
    
    synchronized public void removeChannel(Channel channel) {
        channelMap.remove(channel.getName());
    }
    
    synchronized public CrawledURL getCrawledURL(String url) {
        return crawlMap.get(url);
    }

    synchronized public ArrayList<CrawledURL> getAllCrawledURLs() {
        ArrayList<CrawledURL> allCrawls = new ArrayList<CrawledURL>();
        allCrawls.addAll(crawlMap.values());
        return allCrawls;
    }
    
    synchronized public void addCrawledURL(CrawledURL url) {
        if (!crawlMap.containsKey(url.getUrl())) {
            crawlMap.put(url.getUrl(), url);
        }
        else {
            updateCrawledURL(url);
        }
    }
    
    synchronized public void updateCrawledURL(CrawledURL url) {
        crawlMap.remove(url.getUrl());
        crawlMap.put(url.getUrl(), url);
    }
    
    synchronized public void removeCrawledURL(CrawledURL url) {
        crawlMap.remove(url.getUrl());
    }
    
    synchronized public void close() {
        userDb.close();
        channelDb.close();
        crawlDb.close();
        catalog.close();
        environment.close();
    }
    
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
