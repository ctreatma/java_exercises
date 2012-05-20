package com.project550.model;

import java.util.SortedMap;
import java.util.TreeMap;
import java.sql.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

public class Crawler {
    /** The URL of the MySQL database the DBConnector class accesses. */
    private static String dbURL = "jdbc:mysql://fling-l.seas.upenn.edu:3306/nif";
    /** 
     * The database username used to login to the MySQL database.
     * NOTE: This username is specific to the database and has no relation to
     * those stored in the User table.
     */
    private static String dbUsr = "nif";
    /** The password associated with the dbUsr used to login to the MySQL databse. */
    private static String dbPwd = "querytheory08";
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        Connection conn = null;
        Statement stat = null;
        ResultSet queryResults = null;
        SortedMap<Integer, String> urlMap = new TreeMap<Integer, String>();

        System.out.println("Link crawler starting...");
        try {
            System.out.println("Connecting to database...");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
            stat = conn.createStatement();
            //Get all URLs from DB
            System.out.println("Getting link ids from database...");
            queryResults = stat.executeQuery("SELECT * FROM Url order by urlid");
            while(queryResults.next()) {
                //Store URLs in id -> url map
                urlMap.put(Integer.valueOf(queryResults.getInt("urlid")), queryResults.getString("url"));
            }
            
            System.out.println("Testing links...");
            for (Integer id : urlMap.keySet()) {
                System.out.println("Testing link " + id);
                System.out.println("\tURL: " + urlMap.get(id));
                try {
                    // Create URL and attempt to connect.  If everything works w/out an
                    // exception, then we're ok (?)
                    URL url = new URL(urlMap.get(id));
                    URLConnection urlConn = url.openConnection();
                    if (urlConn instanceof HttpURLConnection) {
                        // If this is an http connection, check the response code
                        HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                        httpConn.connect();
                        int response = httpConn.getResponseCode();
                        if (response < 200 || response >= 300)
                            // Bad reponse code;  delete the URL from the db
                            deleteUrl(conn, stat, id);
                    }
                    else
                        urlConn.connect();
                } catch (Exception e) {
                    // Hit either MalformedURLException or IOException.  In either case, a bad URL,
                    // so delete the url from the DB.
                    deleteUrl(conn, stat, id);
                }
            }
            
            System.out.println("Done");
        } catch (SQLException e) {
            System.out.println("Database error.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
        } finally {            // close the result set
            try {
                queryResults.close();
            } catch (Exception e) {
                System.out.println("The result set cannot be closed.");
                e.printStackTrace();
            }
            try {
                stat.close();
            } catch (Exception e) {
                System.out.println("The statement cannot be closed.");
                e.printStackTrace();
            }
            try {
                stat.close();
            } catch (Exception e) {
                System.out.println("The statement cannot be closed.");
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (Exception e) {
                System.out.println("The database connection cannot be closed.");
                e.printStackTrace();
            }
        }
    }
    
    private static void deleteUrl(Connection conn, Statement stat, int urlId) throws Exception {
        System.out.println("\tBad URL.  Deleting from DB.");
        stat = conn.createStatement();
        stat.executeUpdate("DELETE FROM Url WHERE urlid = " + urlId);
    }
}
