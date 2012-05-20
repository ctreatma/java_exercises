package com.project550.model;

import java.sql.*;
import java.net.URL;
import java.util.*;

/**
 * This class handles all input to and output from the MySQL database.
 * All queries for database information pass through this class and all
 * results are loaded into the appropriate Bean objects and returned.
 */
public class DBConnector {

	/** The URL of the MySQL database the DBConnector class accesses. */
	private static final String dbURL = "jdbc:mysql://fling-l.seas.upenn.edu:3306/nif";
	/**
	 * The database username used to login to the MySQL database.
	 * NOTE: This username is specific to the database and has no relation to
	 * those stored in the User table.
	 */
	private static final String dbUsr = "nif";
	/** The password associated with the dbUsr used to login to the MySQL databse. */
	private static final String dbPwd = "querytheory08";
	/** Number of milliseconds in a day. Used by date-oriented queries. */
	private static final long oneDay = 24 * 60 * 60 * 1000;

	/**
	 * This method accepts a username-password pair. If the given username
	 * and password match the corresponding entries in the User table, the
	 * method returns the corresponding user ID. If the username-password
	 * combination does not match those stored in the database, the method
	 * returns the value -1. If the method fails to connect to the database,
	 * it returns the value -99.
	 *
	 * @param userName
	 * @param password Password to be checked against that of userName.
	 * @return If the userName-password pair are valid, the corresponding
	 *         user ID is returned. If the pair does not match those stored
	 *         in the database, the value -1 is returned. If the method fails
	 *         to connect to the database, the value -99 is returned.
	 */
	public static int login(String userName, String password) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve the Uid of the User tuple with Usrname and Password
		 * matching the method arguments.
		 */
		String query = "SELECT uid FROM User WHERE usrname = ? AND password = ?";
		int uid = -1; //The user id resulting from the query

		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(query);
			s.setString(1, userName);
			s.setString(2, password);
			qResults = s.executeQuery();
			if(qResults.next())
				uid = qResults.getInt(1);
		} catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return -99;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return -1000;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return uid;
	}

	/**
	 * This method accepts a user ID number and returns a UserBean
	 * populated with all of the corresponding user's attributes.
	 * If the password flag is set to true, the user's password will
	 * be included in the UserBean. If the flag is false, it will be
	 * omitted. If the given uid is not in the database or if the
	 * connection fails, this method returns a null value.
	 *
	 * @param uid User ID number of the user being retrieved.
	 * @param pwdFlag Password flag: True if the password is to be
	 *                included in the UserBean, False if not.
	 * @return UserBean containing all of the corresponding user's
	 *         attributes. This method returns null if the given uid
	 *         is not in the database or if the connection fails.
	 */
	public static UserBean getUser(int uid, boolean pwdFlag, boolean loadRecommendations) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		UserBean theUser = null; //The user to be returned

		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			theUser = getUser(conn, uid, pwdFlag, loadRecommendations);
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(conn);
        }
		return theUser;
	}
	
	private static UserBean getUser(Connection conn, int uid, boolean pwdFlag, boolean loadRecommendations) throws Exception {

        //Objects needed to access the database, run query and process results
        PreparedStatement s = null;
        ResultSet qResults = null;
        /* The text composing the query.
         * Retrieve all attributes from the User tuple with matching uid.
         */
        String query = "SELECT * FROM User WHERE uid = ?";
        UserBean theUser = null; //The user to be returned

        try {
            s = conn.prepareStatement(query);
            s.setInt(1, uid);
            qResults = s.executeQuery();
            if(qResults.next()) { //If the query returned a user
                theUser = new UserBean();
                theUser.setUid(qResults.getInt(1));
                theUser.setUsrName(qResults.getString(2));
                theUser.setEmail(qResults.getString(3));
                if(pwdFlag) //If the password is to be included
                    theUser.setPassword(qResults.getString(4));
                theUser.setFirstName(qResults.getString(5));
                theUser.setLastName(qResults.getString(6));
                theUser.setAge(qResults.getInt(7));
                theUser.setProfession(qResults.getString(8));
                theUser.setCountry(qResults.getString(9));
                if (loadRecommendations)
                    theUser.setRecommendations(getAllRecommendations(conn, uid));
            }
        }
        catch (SQLException e) {
            System.out.println("Error with db connection.");
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(qResults);
            close(s);
        }
        return theUser;
	}

	/**
	 * This method accepts a userName and queries the database to
	 * check if it is unique. If it is unique, the method returns
	 * true. Otherwise it returns false.
	 *
	 * @param userName The username to be checked for uniqueness.
	 * @return True if userName is unique in the database, false if not.
	 */
	public static boolean isUniqueUsrname(String userName) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve uid of all Users having the given userName.
		 */
		String query = "SELECT Uid FROM User WHERE Usrname = ?";
		boolean isUnique = false; //True if username is unique, false if not.

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(query);
			s.setString(1, userName);
			qResults = s.executeQuery();
			if(!qResults.next()) //If the query returned no tuples.
				isUnique = true; //The username is unique.
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return isUnique;
	}

	/**
	 * This method accepts an email address and queries the database
	 * to check if it is unique. If it is unique, the method returns
	 * true. Otherwise it returns false.
	 *
	 * @param email The email to be checked for uniqueness.
	 * @return True if email is unique in the database, false if not.
	 */
	public static boolean isUniqueEmail(String email) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve uid of all Users having the given email address.
		 */
		String query = "SELECT Uid FROM User WHERE Email = ?";//'" + email + "'";
		boolean isUnique = false; //True if email is unique, false if not.

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(query);
			s.setString(1, email);
			qResults = s.executeQuery();
			if(!qResults.next()) //If the query returned no tuples.
				isUnique = true; //The email is unique.
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return isUnique;
	}

	/**
	 * This method accepts a UserBean object and attempts to insert
	 * it into the database. If the insertion was successful, the
	 * user's ID number is returned. If the insertion failed, the
	 * value -1 is returned. If an error occurred with the database
	 * connection the value -99 is returned.
	 *
	 * <<NOTE: Currently, trying to insert a user violating integrity
	 *         constraints (ie. non-unique) results in an exception
	 *         and this method returns -99 instead of -1.>>
	 *
	 *
	 * @param theUser UserBean containing the user information to
	 *                insert into the database.
	 * @return If insertion is successful, inserted user's uid is
	 *         returned. If insertion fails, returns -1. If an error
	 *         with database connection occurs, returns -99.
	 */
	public static int insertUser(UserBean theUser) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the insert statement.
		 * Insert user having attributes of the given UserBean into User table.
		 */
		String insert = "INSERT INTO User(usrname,email,password,firstname," +
		                "lastname, age, profession, country)" +
		                "VALUES(?,?,?,?,?,?,?,?)";
		int uid = -1; //Assigned user ID of the inserted user.

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			s.setString(1, theUser.getUsrName());
			s.setString(2, theUser.getEmail());
			s.setString(3, theUser.getPassword());
			s.setString(4, theUser.getFirstName());
			s.setString(5, theUser.getLastName());
			s.setInt(6, theUser.getAge());
			s.setString(7, theUser.getProfession());
			s.setString(8, theUser.getCountry());
			s.executeUpdate();
			qResults = s.getGeneratedKeys();
			if(qResults.next()) //If a uid was generated.
				uid = qResults.getInt(1); //The uid of the inserted user.
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return -99;
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return -99;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return uid;
	}

	/**
	 * This method accepts a user ID and returns the uids of that user's
	 * friends. If the current user has no friends a null value is returned
	 * (this can be tested for by checking if the arrays length is 0).
	 *
	 * @param uid The user ID we are using to check for friends.
	 * @return Array of uids corresponding to the given user's friends.
	 *         Otherwise a null value is returned.
	 */
	public static UserBean[] getFriends(int uid) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve uids of all the given User's friends.
		 */
		String query = "SELECT FriendID FROM FriendList WHERE Uid = ?";
		ArrayList<UserBean> friends = new ArrayList<UserBean>(); //Friends of the given user

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(query);
			s.setInt(1, uid);
			qResults = s.executeQuery();
				//Add friendIDs from query to friends array
				while(qResults.next()) {
					friends.add(getUser(conn, qResults.getInt(1), false, false));
				}
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return friends.toArray(new UserBean[friends.size()]);
	}

	/**
	 * This method accepts two user IDs representing a friend relationship
	 * and adds them to the FriendList table in the database. The first ID
	 * corresponds to the original user and the second ID is that of the
	 * friend. This method returns true if the ID pair is added successfully,
	 * and false otherwise.
	 *
	 * @param userID The original user
	 * @param friendID The friends of the original user
	 * @return True if the ID pair is added successfully, false otherwise.
	 */
	public static boolean addFriend(int userID, int friendID) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		/* The text composing the insert statement.
		 * Insert user-friend pair into FriendList table.
		 */
		String insert = "INSERT INTO FriendList(Uid,FriendID) VALUES(?,?)";
		boolean success = false; //data inserted successfully

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(insert);
			s.setInt(1, userID);
			s.setInt(2, friendID);
			s.executeUpdate();
			success = true;
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return false;
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        } finally {
            close(s);
            close(conn);
        }

		return success;
	}

	/**
	 * This method accepts a link ID number and returns a LinkBean
	 * populated with all of the corresponding link's attributes.
	 * If the given lid is not in the database or if the connection
	 * fails, this method returns a null value. This method uses the
	 * getCategory and getURL methods.
	 *
	 * @param lid ID number of the link being retrieved.
	 * @return LinkBean containing all of the corresponding link's
	 *         attributes. This method returns null if the given lid
	 *         is not in the database or if the connection fails.
	 */
	public static LinkBean getLink(int lid) {
		//Objects needed to access the database, run query and process results
		Connection conn = null;
		LinkBean theLink = null; //The link to be returned

		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			theLink = getLink(conn, lid);
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(conn);
        }
		return theLink;
	}

	private static LinkBean getLink(Connection conn, int lid) throws Exception {
        PreparedStatement s = null;
        ResultSet qResults = null;
        /* The text composing the query.
         * Retrieve all attributes from the Link tuple with matching lid.
         */
        String query = "SELECT L.lid, L.uid, L.urlid, L.title, L.description, L.datetime, V2.rating" +
        " FROM Link L" + " LEFT OUTER JOIN (SELECT L.lid, AVG(V.rating) AS rating FROM Link L, Vote V" +
        " WHERE L.lid = V.lid GROUP BY L.lid) V2" +
        " ON V2.lid = L.lid" +
        " WHERE L.lid = ?";

        LinkBean theLink = null; //The link to be returned

        try {
            s = conn.prepareStatement(query);
            s.setInt(1, lid);
            qResults = s.executeQuery();
            if(qResults.next()) { //If the query returned a user
                theLink = new LinkBean();
                theLink.setLid(qResults.getInt("lid"));
                //Retrieve user from uid
                theLink.setUser(getUser(conn, qResults.getInt("uid"), false, false));
                //Retrieves URL from Urlid
                theLink.setLinkUrl(getURL(conn, qResults.getInt("urlid")));
                theLink.setTitle(qResults.getString("title"));
                theLink.setDescription(qResults.getString("description"));
                theLink.setCategory(getCategory(conn, lid));
                theLink.setPostDateTime(qResults.getTimestamp("datetime"));
                theLink.setRating(qResults.getFloat("rating"));
                theLink.setAccess(LinkBean.Access.PUBLIC);
            }
        }
        catch (SQLException e) {
            System.out.println("Error with db connection.");
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(qResults);
            close(s);
        }
        return theLink;
	}
	
	/**
	 * This method accepts a URL ID number and returns the URL associated
	 * with it. If the ID is not in the database or if the connection
	 * fails, this method returns a null value.
	 *
	 * @param urlid ID number of the URL being retrieved.
	 * @return The URL associated with the given urlid, or null if the
	 *         urlid is not in the database or if the connection fails.
	 */
	private static URL getURL(Connection conn, int urlid) throws Exception {

		//Objects needed to access the database, run query and process results
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve the URL associated with the given urlid.
		 */
		String query = "SELECT url FROM Url WHERE urlid=?";
		URL theURL = null;

		try {
			s = conn.prepareStatement(query);
			s.setInt(1, urlid);
			qResults = s.executeQuery();
			if(qResults.next()) //If the given urlid is in the database
				theURL = new URL(qResults.getString(1));
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(qResults);
            close(s);
        }
		return theURL;
	}

	/**
	 * This method accepts a link ID number and returns the category
	 * associated with this link. If the link ID is not in the database
	 * or if the connection fails, a null value is returned. If a link
	 * is associated with multiple categories, this method only returns
	 * the first category retrieved by the database query.
	 *
	 * @param lid ID number of the link corresponding to the category
	 *            being retrieved.
	 * @return The category associated with the given link, or null if
	 *         the given ID is not in the database or the connection
	 *         fails.
	 */
	private static String getCategory(Connection conn, int lid) throws Exception {

		//Objects needed to access the database, run query and process results
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve the category associated with the given lid.
		 */
		String query = "SELECT C.Category " +
	                   "FROM LinkCategory L, Categories C " +
	                   "WHERE L.Catid = C.Catid AND L.lid = ?";
		String category = null;

		try {
			s = conn.prepareStatement(query);
			s.setInt(1, lid);
			qResults = s.executeQuery();
			//If the given lid has a category in the database
			if(qResults.next())
				category = qResults.getString(1);
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(qResults);
            close(s);
        }

		return category;
	}

	/**
	 * This method returns all of the categories stored in the
	 * Categories table.
	 *
	 * @return All of the categories from the Categories table.
	 */
	public static ArrayList<String> getCategories() {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		Statement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve all possible categories.
		 */
		String query = "SELECT category FROM Categories ORDER BY catid";
		ArrayList<String> categories = new ArrayList<String>();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s=conn.createStatement();
			qResults = s.executeQuery(query);
			while(qResults.next()) {
			    categories.add(qResults.getString("category"));
			}
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return categories;
	}

	/**
	 * This method accepts a LinkBean object and attempts to insert
	 * it into the database. If the insertion was successful, the
	 * link's ID number is returned. If the insertion failed, the
	 * value -1 is returned. If an error occurred with the database
	 * connection the value -99 is returned. This method assumes the
	 * URL given in the LinkBean has already been tested for uniqueness
	 * and that the given Category is valid. This method uses the
	 * addCategory method.
	 *
	 * <<NOTE: Currently, trying to insert a link violating integrity
	 *         constraints (ie. non-unique) results in an exception
	 *         and this method returns -99 instead of -1.>>
	 *
	 * <<WARNING: If the insertion statement fails, the URL will still
	 *            have been added to the Url table!>>
	 *
	 *
	 * @param theLink LinkBean containing the link information to
	 *                insert into the database.
	 * @return If insertion is successful, inserted link's ID is
	 *         returned. If insertion fails, returns -1. If an
	 *         error with database connection occurs, returns -99.
	 */
	public static int insertLink(LinkBean theLink, Map<String, Integer> keywords) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the insert statement.
		 * Insert link having attributes of the given LinkBean into Link table.
		 * The postDateTime attribute is not specified so the DB defaults to
		 * entering the current time.
		 */
		String insert = "INSERT INTO Link(Uid,Urlid,Title,Description,Access) " +
				        "VALUES(?,?,?,?,?)";
		int lid = -1; //Assigned link ID of the inserted link.

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s=conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			s.setInt(1, theLink.getUser().getUid());
			s.setInt(2, addURL(conn, theLink.getLinkUrl().toString()));
			s.setString(3, theLink.getTitle());
			s.setString(4, theLink.getDescription());
			s.setString(5, theLink.getAccess().toString());
			s.executeUpdate();
			qResults = s.getGeneratedKeys();
			if(qResults.next()) { //If the lid was generated.
				lid = qResults.getInt(1); //The lid of the inserted link.
				//Adds category associated with link to LinkCategory table
				addCategory(conn, lid, theLink.getCategory());
				// Add keywords associated with this link.
				addLinkKeywords(conn, lid, keywords);
			}
		}
		catch (SQLException e) {
		    if (e.getMessage().indexOf("Duplicate") != -1)
		    {
	            System.out.println("Duplicate key.");
	            e.printStackTrace();
	            return -50;
		    }
		    else {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return -99;
		    }
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return -99;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return lid;
	}

	private static boolean addLinkKeywords(Connection conn, int lid, Map<String, Integer> keywords) throws Exception {
	    PreparedStatement getId = null;
	    PreparedStatement insert = null;
	    PreparedStatement counts = null;
	    ResultSet qResults = null;

	    try {
	        // Prepared Statement for getting id of existing keyword
            getId = conn.prepareStatement("SELECT kwid FROM Keywords WHERE keyword = ?");
            // Prepared Statement for inserting new keyword and getting inserted id
            insert = conn.prepareStatement("INSERT INTO Keywords(keyword) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            // Prepared Statement for inserting keyword count into LinkKeyword table
            counts = conn.prepareStatement("INSERT INTO LinkKeyword(kwid, lid, num) VALUES(?,?,?)");
	        for (String keyword : keywords.keySet()) {
	            // Try to get id of keyword (if it's already in DB)
	            int kwid;
	            getId.setString(1, keyword);
	            qResults = getId.executeQuery();
	            if (qResults.next()) {
	                kwid = qResults.getInt("kwid");
	            }
	            else {
	                // Keyword not in DB; insert and get generated key
	                insert.setString(1, keyword);
	                insert.executeUpdate();
	                qResults = insert.getGeneratedKeys();
	                if (!qResults.next()) throw new Exception("Error attempting to insert keyword " + keyword);
	                kwid = qResults.getInt(1);
	            }

	            // Now insert keyword count for link into LinkKeyword
	            counts.setInt(1, kwid);
	            counts.setInt(2, lid);
	            counts.setInt(3, keywords.get(keyword));
	            counts.executeUpdate();
	        }
	    }
	    catch (SQLException e) {
	        System.out.println("Error with db connection.");
	        e.printStackTrace();
	        return false;
	    }  catch (Exception e) {
	        System.out.println("Unexpected error.");
	        e.printStackTrace();
	        return false;
	    } finally {
	        close(qResults);
            close(getId);
            close(insert);
            close(counts);
        }
	    return true;
	}
	
	/**
	 * This method accepts a url and queries the database to check if
	 * it is unique. If it is unique, the method returns true. Otherwise
	 * it returns false.
	 *
	 * @param theURL The URL to be checked for uniqueness.
	 * @return True if url is unique in the database, false if not.
	 */
	public static boolean isUniqueURL(String theURL) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve Urlid of all URLs having the given Url.
		 */
		String query = "SELECT urlid FROM Url WHERE url=?";
		boolean isUnique = false; //True if URL is unique, false if not.

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(query);
			s.setString(1, theURL);
			qResults = s.executeQuery();
			if(!qResults.next()) //If the query returned no tuples.
				isUnique = true; //The url is unique.
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return isUnique;
	}

	/**
	 * Accepts a url and attempts to insert it into the database. If
	 * the insertion was successful, the url's ID number is returned.
	 * If the insertion failed, the value -1 is returned. If an error
	 * occurred with the database connection the value -99 is returned.
	 *
	 * <<NOTE: Currently, trying to insert a url violating integrity
	 *         constraints (ie. non-unique) results in an exception
	 *         and this method returns -99 instead of -1.>>
	 *
	 * @param theURL The url to be inserted into the database.
	 * @return If insertion is successful, inserted url's ID is
	 *         returned. If insertion fails, returns -1. If an
	 *         error with database connection occurs, returns -99.
	 */
	private static int addURL(Connection conn, String theURL) throws Exception {
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the insert statement.
		 * Insert url into URL table.
		 */
		String insert = "INSERT INTO Url(url) Values(?)";

		int lid = -1; //Assigned user ID of the inserted user.

		try {
			s = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			s.setString(1, theURL);
			s.executeUpdate();
			qResults = s.getGeneratedKeys();
			if(qResults.next()) //If the a key was generated.
				lid = qResults.getInt(1); //The lid of the inserted url.
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			throw e;
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(qResults);
            close(s);
        }
		return lid;
	}

	/**
	 * This method accepts a link ID number and a category and adds them
	 * to the LinkCategory table in the database. This method returns
	 * true if the pair is added successfully and false otherwise.
	 *
	 * @param lid The id number of the link
	 * @param category The category associated with the link.
	 * @return True if the category-link pair is added successfully and
	 *         false otherwise.
	 */
	private static boolean addCategory(Connection conn, int lid, String category) throws Exception {
		PreparedStatement s = null;
		/* The text composing the insert statement.
		 * Insert lid-category pair into LinkCategory table.
		 */
		String insert = "INSERT INTO LinkCategory(Lid,Catid) Values(?, (SELECT catid FROM Categories WHERE category=?))";

		try {
			s = conn.prepareStatement(insert);
			s.setInt(1, lid);
			s.setString(2, category);
			s.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			throw e;
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(s);
        }

		return true;
	}

	/**
	 * This method accepts a comment ID number and returns a CommentBean
	 * populated with all of the corresponding comment's attributes.
	 * If the given cid is not in the database or if the connection
	 * fails, this method returns a null value.
	 *
	 * @param cid ID number of the comment being retrieved.
	 * @return CommentBean containing all of the corresponding comment's
	 *         attributes. This method returns null if the given cid
	 *         is not in the database or if the connection fails.
	 */
	public static CommentBean getComment(int cid) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		CommentBean theComment = null;

		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			getComment(conn, cid);
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(conn);
        }
		return theComment;
	}
	
	private static CommentBean getComment(Connection conn, int cid) throws Exception {
        PreparedStatement s = null;
        ResultSet qResults = null;
        /* The text composing the query.
         * Retrieve Comment having the given cid from the Comment table.
         */
        String query = "SELECT * FROM Comment WHERE cid=?";// + cid;
        CommentBean theComment = null;

        try {
            s = conn.prepareStatement(query);
            s.setInt(1, cid);
            qResults = s.executeQuery();
            if(qResults.next()) { //If the query returned a user
                theComment = new CommentBean();
                theComment.setCid(qResults.getInt("cid"));
                theComment.setUid(qResults.getInt("uid"));
                theComment.setUser(getUser(conn, qResults.getInt("uid"), false, false));
                theComment.setLid(qResults.getInt("lid"));
                theComment.setTitle(qResults.getString("subject"));
                theComment.setContent(qResults.getString("content"));
                theComment.setPostDateTime(qResults.getTimestamp("datetime"));
                theComment.setReply(qResults.getInt("reply"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error with db connection.");
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            throw e;
        } finally {
            close(qResults);
            close(s);
        }
        return theComment; 
	}

	/**
	 * This method accepts a link ID number and returns all the comments
	 * associated with that link. This method returns a null value if there
	 * are no comments associated with the given lid, or if the connection
	 * fails. This method makes use of the getComment method.
	 *
	 * @param lid The link ID number associated with the comments being retrieved.
	 * @return Array of CommentBeans associated with the given lid or null if the
	 *         given lid has no corresponding comments or if the connection fails.
	 */
	public static CommentBean[] getLinkComments(int lid) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve all Cids from Comments having the given lid.
		 */
		String query = "SELECT cid FROM Comment WHERE lid=?";
		ArrayList<CommentBean> comments = new ArrayList<CommentBean>();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(query);
			s.setInt(1, lid);
			qResults = s.executeQuery();
			while(qResults.next()) {
			    comments.add(getComment(conn, qResults.getInt(1)));
			}
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return comments.toArray(new CommentBean[comments.size()]);
	}

	/**
	 * This method accepts a CommentBean object and attempts to insert
	 * it into the database. If the insertion was successful, the
	 * comment's ID number is returned. If the insertion failed, the
	 * value -1 is returned. If an error occurred with the database
	 * connection the value -99 is returned.
	 *
	 * <<NOTE: Currently, trying to insert a comment violating integrity
	 *         constraints (ie. non-unique) results in an exception
	 *         and this method returns -99 instead of -1.>>
	 *
	 * @param theComment CommentBean containing the comment information to
	 *                   insert into the database.
	 * @return If insertion is successful, inserted comment's ID is
	 *         returned. If insertion fails, returns -1. If an
	 *         error with database connection occurs, returns -99.
	 */
	public static int insertComment(CommentBean theComment) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the insert statement.
		 * Insert comment having attributes of the given CommentBean into
		 * Comment table. DateTime is not specified so the DB defaults to
		 * the current time.
		 */
		String insert;
		if (theComment.getReply() >= 0) {
		    insert = "INSERT INTO Comment(Uid,Lid,Subject,Content," +
		             "Reply) VALUES(?,?,?,?,?)";
		}
		else {
            insert = "INSERT INTO Comment(Uid,Lid,Subject,Content)" +
                     " VALUES(?,?,?,?)";
		}
		int cid = -1; //Assigned comment ID of the inserted comment.

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			//s=conn.createStatement();
			//s.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			s = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			s.setInt(1, theComment.getUid());
			s.setInt(2, theComment.getLid());
			s.setString(3, theComment.getTitle());
			s.setString(4, theComment.getContent());
			if (theComment.getReply() >= 0)
			    s.setInt(5, theComment.getReply());
			s.executeUpdate();
			qResults = s.getGeneratedKeys();
			if(qResults.next()) //If the cid was generated.
				cid = qResults.getInt(1); //The cid of the inserted comment.
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return -99;
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return -99;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return cid;
	}

	/**
	 * This method accepts a link ID number and returns all votes
	 * for the given link. This method returns a null value if the
	 * given lid is not in the database, has no votes, or if the
	 * connection fails.
	 *
	 * @param lid The link ID for which the votes are being retrieved.
	 * @return Array of votes for the given link or null if lid not in
	 *         database, link has no votes, or if connection fails.
	 */
	public static VoteBean[] getLinkVotes(int lid) {
		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve all Votes for the given lid.
		 */
		String query = "SELECT * FROM Vote WHERE lid = ?";
		ArrayList<VoteBean> theVotes = new ArrayList<VoteBean>();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s=conn.prepareStatement(query);
			s.setInt(1, lid);
			qResults = s.executeQuery();
			//Add votes from query to VoteBean array
			while(qResults.next()) {
			    VoteBean tempVote = new VoteBean();
			    tempVote.setUid(qResults.getInt("uid"));
			    tempVote.setLid(qResults.getInt("lid"));
			    tempVote.setRating(qResults.getInt("rating"));
			    tempVote.setPostDateTime(qResults.getTimestamp("datetime"));
			    theVotes.add(tempVote);
			}
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return theVotes.toArray(new VoteBean[theVotes.size()]);
	}

	   /**
     * This method accepts a link ID number and returns all votes
     * for the given link. This method returns a null value if the
     * given lid is not in the database, has no votes, or if the
     * connection fails.
     *
     * @param lid The link ID for which the votes are being retrieved.
     * @return Array of votes for the given link or null if lid not in
     *         database, link has no votes, or if connection fails.
     */
    public static LinkBean[] getUserLinks(int uid) {
        //Objects needed to access the database, run query and process results
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet qResults = null;
        /* The text composing the query.
         * Retrieve all Links for the given uid.
         */
        String query = "SELECT L.lid, L.uid, L.urlid, L.title, L.description, L.datetime, V2.rating" +
            " FROM Link L" + " LEFT OUTER JOIN (SELECT L.lid, AVG(V.rating) AS rating FROM Link L, Vote V" +
            " WHERE L.lid = V.lid GROUP BY L.lid) V2" +
            " ON V2.lid = L.lid" +
            " WHERE L.uid=?";
        
        ArrayList<LinkBean> theLinks = new ArrayList<LinkBean>();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
            s = conn.prepareStatement(query);
            s.setInt(1, uid);
            qResults = s.executeQuery();
            //Add votes from query to VoteBean array
            while(qResults.next()) {
                LinkBean tempLink = new LinkBean();
                tempLink.setLid(qResults.getInt("lid"));
                //Retrieve user from uid
                tempLink.setUser(getUser(conn, qResults.getInt("uid"), false, false));
                //Retrieves URL from Urlid
                tempLink.setLinkUrl(getURL(conn, qResults.getInt("urlid")));
                tempLink.setTitle(qResults.getString("title"));
                tempLink.setDescription(qResults.getString("description"));
                tempLink.setCategory(getCategory(conn, qResults.getInt("lid")));
                tempLink.setPostDateTime(qResults.getTimestamp("datetime"));
                tempLink.setRating(qResults.getFloat("rating"));
                tempLink.setAccess(LinkBean.Access.PUBLIC);
                theLinks.add(tempLink);
            }
        }
        catch (SQLException e) {
            System.out.println("Error with db connection.");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
        return theLinks.toArray(new LinkBean[theLinks.size()]);
    }

    /**
     * This method accepts a link ID number and returns all votes
     * for the given link. This method returns a null value if the
     * given lid is not in the database, has no votes, or if the
     * connection fails.
     *
     * @param lid The link ID for which the votes are being retrieved.
     * @return Array of votes for the given link or null if lid not in
     *         database, link has no votes, or if connection fails.
     */
    public static CommentBean[] getUserComments(int uid) {
        //Objects needed to access the database, run query and process results
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet qResults = null;
        /* The text composing the query.
         * Retrieve all Votes for the given uid.
         */
        String query = "SELECT * FROM Comment WHERE Uid = ?";
        ArrayList<CommentBean> theComments = new ArrayList<CommentBean>();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
            s = conn.prepareStatement(query);
            s.setInt(1, uid);
            qResults = s.executeQuery();
            //Add votes from query to CommentBean array
            while(qResults.next()) {
                CommentBean tempComment = new CommentBean();
                tempComment.setCid(qResults.getInt(1));
                tempComment.setUid(qResults.getInt(2));
                tempComment.setLid(qResults.getInt(3));
                tempComment.setTitle(qResults.getString(4));
                tempComment.setContent(qResults.getString(5));
                tempComment.setPostDateTime(qResults.getTimestamp(6));
                tempComment.setReply(qResults.getInt(7));
                theComments.add(tempComment);
            }
        }
        catch (SQLException e) {
            System.out.println("Error with db connection.");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
        return theComments.toArray(new CommentBean[theComments.size()]);
    }

	/**
	 * This method accepts a link ID number and returns all votes
	 * for the given link. This method returns a null value if the
	 * given lid is not in the database, has no votes, or if the
	 * connection fails.
	 *
	 * @param lid The link ID for which the votes are being retrieved.
	 * @return Array of votes for the given link or null if lid not in
	 *         database, link has no votes, or if connection fails.
	 */
	public static VoteBean[] getUserVotes(int uid) {
		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve all Votes for the given uid.
		 */
		String query = "SELECT * FROM Vote WHERE Uid = ?";
		ArrayList<VoteBean> theVotes = new ArrayList<VoteBean>();

		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
		    s = conn.prepareStatement(query);
		    s.setInt(1, uid);
		    qResults = s.executeQuery();
		    //Add votes from query to VoteBean array
		    while(qResults.next()) {
		        VoteBean tempVote = new VoteBean();
		        tempVote.setUid(qResults.getInt("uid"));
		        tempVote.setLid(qResults.getInt("lid"));
		        tempVote.setRating(qResults.getInt("rating"));
		        tempVote.setPostDateTime(qResults.getTimestamp("datetime"));
		        theVotes.add(tempVote);
		    }
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return theVotes.toArray(new VoteBean[theVotes.size()]);
	}

	/**
	 * This method accepts a VoteBean object and attempts to insert it
	 * into the database. If the insertion is successful, the method
	 * returns true. Otherwise, the method returns false.
	 *
	 * @param theVote The VoteBean object to be inserted into the database.
	 * @return True if the insertion is successful and false otherwise.
	 */
	public static boolean insertVote(VoteBean theVote) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		/* The text composing the insert statement.
		 * Insert comment having attributes of the given VoteBean into
		 * Vote table. The DateTime is left unspecified so the DB defaults
		 * to the current time.
		 */
		String insert = "INSERT INTO Vote(Uid,Lid,Rating) VALUES(?,?,?)";
		boolean success = false; //true if insertion completed

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s = conn.prepareStatement(insert);
			s.setInt(1, theVote.getUid());
			s.setInt(2, theVote.getLid());
			s.setInt(3, theVote.getRating());
			if(s.executeUpdate() == 1)
				success = true;
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return false;
		}  catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        } finally {
            close(s);
            close(conn);
        }
		return success;
	}

	/**
	 * This method accepts a category  and returns the link
	 * ID numbers of all links having the corresponding category. If
	 * no links are under the given category, or if the database
	 * connection fails, the method returns a null value. The byDate
	 * flag is true if the results are to be ordered by Date and false
	 * if they are to be ordered by average rating.
	 *
	 * @param category The category of the links to be retrieved.
	 * @param byDate True if the results are to be ordered by date.
	 *               False if results are to be ordered by rating.
	 * @return Array of lids having the given category, or null if no
	 *         links have the given category or the database connection
	 *         fails.
	 */
	public static LinkBean[] getLinksByCategory(String category , boolean byRating) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet qResults = null;
		/* The text composing the query.
		 * Retrieve lids of all links having the given category.
		 */
		String query;
		if(!byRating) { // if the results should be returned by date
			query = "SELECT LC.lid FROM Categories C, LinkCategory LC,"
	                + "(SELECT lid, DateTime FROM Link) L WHERE C.Category=? AND LC.Catid = C.Catid " +
	                "AND L.lid = LC.lid ORDER BY L.DateTime DESC";
		}
		else { // if the results are to be returned by rating
			query = "SELECT LC.lid FROM Categories C, LinkCategory LC LEFT OUTER JOIN "
		            + "(SELECT lid, AVG(rating) AS rating FROM Vote " +
		            "GROUP BY lid) V ON LC.lid = V.lid WHERE C.category=? AND " +
		            "LC.Catid = C.Catid ORDER BY V.rating DESC";
		}
		ArrayList<LinkBean> links = new ArrayList<LinkBean>(); //Links with the given category

		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
		    s = conn.prepareStatement(query);
		    s.setString(1, category);
		    qResults = s.executeQuery();
		    //Add lids from query to links array
		    while(qResults.next()) {
		        links.add(getLink(conn, qResults.getInt(1)));
		    }
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(qResults);
            close(s);
            close(conn);
        }
		return links.toArray(new LinkBean[links.size()]);
	}

	/**
	 * This method accepts a date and an operator (see list below)
	 * and returns link ID numbers or all links relating to the
	 * given date according to the given operator. The method returns
	 * a null value if no links satisfy the date comparison, or if
	 * the database connection fails. The time component (hrs, min,
	 * etc.) of the given timestamp should be set to zeroes using
	 * the .clear method.
	 *
	 * Potential Operators:
	 * =, >, <, >=, <=, !=
	 *
	 * @param theDate Date to be compared.
	 * @param op Operator to compare to the date.
	 * @return Array of lids satisfying the date range query.
	 */
	public static LinkBean[] getLinksByDate(Timestamp theDate, String op, boolean byRating) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		/* The text composing the query.
		 * Retrieve lids of all links relating to the given date according
		 * to the given operator (op).
		 */
		String query;
		if (!byRating)
		    query = "SELECT L.lid FROM Link L WHERE ";
		else {
		    query = "SELECT L.lid FROM Link L LEFT OUTER JOIN " +
		    "(SELECT lid, AVG(rating) AS rating FROM Vote " +
		    "GROUP BY lid) V ON L.lid = V.lid ";
		}
		
		if(op.equals("=")) {//all dates = theDate
			query += "L.datetime >= ? AND L.datetime < ?";
		}
		else if(op.equals("!=")) {//all dates != theDate
			query += "L.datetime < ? OR L.datetime >= ?";
		}
		else if(op.equals(">") || op.equals(">=")) {//all dates > or >=theDate
			query += "L.datetime >= ?";
		}
		else if(op.equals("<") || op.equals("<=")) {//all dates < or <=theDate
			query += "L.datetime <= ?";
		}
		if (!byRating)
		    query += " ORDER BY L.datetime DESC";
		else
		    query += " ORDER BY V.rating DESC";
		
		ArrayList<LinkBean> links = new ArrayList<LinkBean>(); //Links relating to date according to give operator

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
				s = conn.prepareStatement(query);
				s.setTimestamp(1, theDate);
				// = and != queries require a second Timestamp argument
				if(op.equals("=") || op.equals("!="))
					s.setTimestamp(2, new Timestamp(theDate.getTime() + oneDay));
				rs = s.executeQuery();
				//Add lids from query to links array
				while(rs.next()) {
					links.add(getLink(conn, rs.getInt(1)));
				}
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(rs);
            close(s);
            close(conn);
        }
		return links.toArray(new LinkBean[links.size()]);
	}

	/**
	 * Accepts two dates and returns all link ID numbers posted between
	 * the given dates. If the inclusive flag is set to true, the given
	 * dates are included in the result query. Otherwise, they are excluded.
	 * The firstDate should be earlier than the secondDate. The time component
	 * (hrs, min, etc.) of the given timestamps should be set to zeroes using
	 * the .clear() method.
	 *
	 * @param firstDate Earlier date in the range query.
	 * @param secondDate Later date in the range query.
	 * @param inclusive True if the range query is inclusive.
	 * @return The lids of links between the given dates.
	 */
	public static LinkBean[] getLinksByDate(Timestamp firstDate, Timestamp secondDate,
			                           boolean inclusive, boolean byRating) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		/* The text composing the query.
		 * Retrieve lids of all links with dates between the given dates
		 */
		String query;
		if (!byRating)
		    query = "SELECT Lid FROM Link WHERE DateTime >= ? AND DateTime < ? ORDER BY DateTime DESC";
		else {
		    query = "SELECT L.lid FROM Link L LEFT OUTER JOIN (SELECT lid, AVG(rating) AS rating FROM Vote " +
            "GROUP BY lid) V ON L.lid = V.lid WHERE L.DateTime >= ? AND L.DateTime < ? ORDER BY V.rating DESC";
		}
		/* If not inclusive, we must check for > firstDate + one day to
		 * account for any dates posted during the firstDate.
		 *
		 * eg. firstDate = 2008-11-25 00:00:00, to get all dates >
		 *     the firstDate, we must query for dates >= 2008-11-26 00:00:00
		 */
		if(!inclusive) //If inclusive add the equality
			firstDate = new Timestamp(firstDate.getTime() + oneDay);
		/* If inclusive, we must check for < secondDate + one day to
		 * account for any dates posted during the secondDate.
		 *
		 * eg. secondDate = 2008-11-25 00:00:00, to get all dates <=
		 *     the secondDate, we must query for dates < 2008-11-26 00:00:00
		 */
		else //If inclusive add the equality
			secondDate = new Timestamp(secondDate.getTime() + oneDay);
		ArrayList<LinkBean> links = new ArrayList<LinkBean>(); //Links between the given dates

		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
		    s = conn.prepareStatement(query);
		    s.setTimestamp(1, firstDate);
		    s.setTimestamp(2, secondDate);
		    rs = s.executeQuery();
		    //Add lids from query to links array
		    while(rs.next()) {
		        links.add(getLink(conn, rs.getInt(1)));
		    }
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(rs);
            close(s);
            close(conn);
        }
		return links.toArray(new LinkBean[links.size()]);
	}

	/**
	 * This method returns all of the link ID numbers ordered by
	 * their average ratings.
	 *
	 * @return lids ordered by corresponding average ratings.
	 */
	public static LinkBean[] getLinksByRating() {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		/* The text composing the query.
		 * Retrieve lids of all links ordered by average rating
		 */
		String query = "SELECT L.lid FROM Link L LEFT OUTER JOIN " +
		               "(SELECT lid, AVG(rating) AS rating FROM Vote " +
			           "GROUP BY lid) V ON L.lid = V.lid " +
			           "ORDER BY V.rating DESC";
		ArrayList<LinkBean> links = new ArrayList<LinkBean>(); //Links ordered by average rating

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
			s=conn.createStatement();
			rs = s.executeQuery(query);
			//Add lids from query to links array
			while(rs.next()) {
			    links.add(getLink(conn, rs.getInt(1)));
			}
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(rs);
            close(s);
            close(conn);
        }
		return links.toArray(new LinkBean[links.size()]);
	}

	/**
	 * This method returns all of the link ID numbers relating to
	 * the given rating according to the given operator(op). The
	 * results are ordered by their average ratings.
	 *
	 * @param rating The rating used to query the database
	 * @param op The operator used to compare ratings in the query
	 * @return Resulting lids ordered by corresponding average ratings.
	 */
	public static LinkBean[] getLinksByRating(int rating, String op) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		/* The text composing the query.
		 * Retrieve lids of all links relating to the given rating according
		 * to the given operation(op) ordered by average rating
		 */
		String query = "SELECT L.lid FROM Link L, (SELECT lid, AVG(rating)" +
		               " AS rating FROM Vote GROUP BY lid) V WHERE " +
		               "L.lid = V.lid AND V.rating " + op + " " +
			           "? ORDER BY V.rating DESC";
		ArrayList<LinkBean> links = new ArrayList<LinkBean>(); //Links ordered by average rating

		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
		    s = conn.prepareStatement(query);
		    s.setInt(1, rating);
		    rs = s.executeQuery();
		    //Add lids from query to links array
		    while(rs.next()) {
		        links.add(getLink(conn, rs.getInt(1)));
		    }
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(rs);
            close(s);
            close(conn);
        }
		return links.toArray(new LinkBean[links.size()]);
	}

	/**
	 * This method returns all of the link ID numbers relating to
	 * the given rating according to the given operator(op). The
	 * results are ordered by their average ratings.
	 *
	 * @param rating The rating used to query the database
	 * @param op The operator used to compare ratings in the query
	 * @return Resulting lids ordered by corresponding average ratings.
	 */
	public static LinkBean[] getLinksByRating(int firstRating, int secondRating,
			                             boolean inclusive, boolean byRating) {

		//Objects needed to access the database, run query and process results
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		/* The text composing the query.
		 * Retrieve lids of all links with average ratings between the
		 * given ratings, ordered by average rating
		 */
		String query = "SELECT L.lid FROM Link L, (SELECT lid, AVG(rating)" +
		               " AS rating FROM Vote GROUP BY lid) V WHERE " +
		               "L.lid = V.lid AND V.rating >";
		if(inclusive) {
			query = query + "=";
		}
		query = query + " ? AND V.rating <";
		if(inclusive) {
			query = query + "=";
		}
		if (!byRating)
		    query += " ? ORDER BY L.datetime DESC";
		else
		    query += " ? ORDER BY V.rating DESC";
		
		ArrayList<LinkBean> links = new ArrayList<LinkBean>(); //Links ordered by average rating

		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
		    s = conn.prepareStatement(query);
		    s.setInt(1, firstRating);
		    s.setInt(2, secondRating);
		    rs = s.executeQuery();
		    //Add lids from query to links array
		    while(rs.next()) {
		        links.add(getLink(conn, rs.getInt(1)));
		    }
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(rs);
            close(s);
            close(conn);
        }
		return links.toArray(new LinkBean[links.size()]);
	}

	/**
	 * This method checkes whether the specified user has already
	 * submitted a rating for the specified link.
	 * 
	 * @param uid  The uid of the user
	 * @param lid  The lid of the link
	 * @return
	 */
	public static boolean existsVote(int uid, int lid) {
	       //Objects needed to access the database, run query and process results
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;

        String query = "SELECT rating FROM Vote WHERE Uid = ? AND Lid = ?";
        boolean exists = false;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
            s = conn.prepareStatement(query);
            s.setInt(1, uid);
            s.setInt(2, lid);
            rs = s.executeQuery();
            if(rs.next()) exists = true; // The vote already exists
        }
        catch (SQLException e) {
            System.out.println("Error with db connection.");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        } finally {
            close(rs);
            close(s);
            close(conn);
        }
        return exists;
	}

	private static ArrayList<LinkBean> getAllRecommendations(Connection conn, int uid)
	{
		PreparedStatement s = null;
		ResultSet rs = null;
		ArrayList<LinkBean> links = new ArrayList<LinkBean>();

		//Query to retrieve all the links posted, voted on or commented on by the user
		String allTargetUserActivity = "SELECT DISTINCT A.lid from Link A where A.uid = ?"+//uid+
								" UNION SELECT DISTINCT B.lid from Vote B where B.uid = ?"+// uid+
								" UNION SELECT DISTINCT C.lid from Comment C where C.uid = ?";//+uid;

		try
		{
			ArrayList<Integer> userLinks = new ArrayList<Integer>();
			ArrayList<Integer> associatedUserIds = new ArrayList<Integer>();
			ArrayList<Integer> associatedUserLinks = new ArrayList<Integer>();
			s = conn.prepareStatement(allTargetUserActivity);
			s.setInt(1, uid);
            s.setInt(2, uid);
            s.setInt(3, uid);
			rs = s.executeQuery();
			while(rs.next())
			{
				userLinks.add(rs.getInt(1));
			}
			if(userLinks.size()==0)
			{
			    ArrayList<Integer> temp1 = new ArrayList<Integer>();
			    //If a user has never posted, voted or commented get the highest rated links and most commented on link in each category

			    rs = s.executeQuery ("SELECT A.Catid FROM Categories A");
			    while(rs.next())
			    {
			        temp1.add(rs.getInt(1));
			    }

			    for(int x = 0; x<temp1.size();x++)
			    {
			        rs = s.executeQuery("SELECT A.lid FROM AverageVoteByCategory A WHERE A.catid = "+ temp1.get(x)+
			                " AND A.average = (SELECT MAX(B.Average) FROM AverageVoteByCategory B WHERE B.catid = "+ temp1.get(x)+")");
			        while (rs.next())
			        {
			            if(!associatedUserLinks.contains(rs.getInt(1)))
			                associatedUserLinks.add(rs.getInt(1));
			        }
			    }
			    for(int x = 0; x<temp1.size();x++)
			    {
			        rs = s.executeQuery("SELECT A.lid FROM CountByCategory A WHERE A.catid = "+ temp1.get(x)+
			                " AND A.count = (SELECT MAX(B.count) FROM CountByCategory B WHERE B.catid = "+ temp1.get(x)+")");
			        while (rs.next())
			        {
			            if(!associatedUserLinks.contains(rs.getInt(1)))
			                associatedUserLinks.add(rs.getInt(1));
			        }
			    }
			}
			else
			{
				for (int x = 0; x< userLinks.size(); x++)
				{
					//Get the user ids of other users that have voted or commented on the same link as the user
					rs = s.executeQuery("SELECT DISTINCT A.uid from Vote A where A.lid = "+userLinks.get(x)+ " AND A.uid <> "+uid+
								" UNION SELECT DISTINCT B.uid from Comment B where B.lid = " +userLinks.get(x)+ " AND B.uid <> "+uid);
					while(rs.next())
					{
						if(!associatedUserIds.contains(rs.getInt(1)))
							associatedUserIds.add(rs.getInt(1));
					}
				}

				for (int x = 0; x< associatedUserIds.size(); x++)
				{
					//Get all the links lids for all the links in categories voted, commented or posted on by users obtained above
					rs = s.executeQuery("SELECT DISTINCT A.lid from Link A, LinkCategory B, Categories C "+
								"WHERE A.lid = B.lid AND B.catid = C.catid and A.uid <> "+ uid+ " AND C.catid in "+
								"(SELECT DISTINCT A.catid from Categories A, Link B, LinkCategory C "+
								"WHERE A.catid = C.catid AND B.lid = C.lid AND B.uid = "+ associatedUserIds.get(x)+")"+
								" UNION SELECT A.lid from Vote A, LinkCategory B, Categories C "+
								"WHERE A.lid = B.lid AND B.catid = C.catid and A.uid <> "+ uid+ " and C.catid in "+
								"(SELECT A.catid from Categories A, Link B, LinkCategory C "+
								"WHERE A.catid = C.catid AND B.lid = C.lid AND B.uid = "+ associatedUserIds.get(x)+")"+
								" UNION SELECT A.lid from Comment A, LinkCategory B, Categories C "+
								"WHERE A.lid = B.lid AND B.catid = C.catid and A.uid <> "+ uid+ " and C.catid in "+
							    "(SELECT A.catid from Categories A, Link B, LinkCategory C "+
								"WHERE A.catid = C.catid AND B.lid = C.lid AND B.uid = "+ associatedUserIds.get(x)+")");

					while (rs.next())
					{
						if(!associatedUserLinks.contains(rs.getInt(1)))
								associatedUserLinks.add(rs.getInt(1));
					}
				}
			}

            for (int lid : associatedUserLinks) {
                links.add(getLink(conn, lid));
            }
		}
		catch (SQLException e) {
			System.out.println("Error with db connection.");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return null;
        } finally {
            close(rs);
            close(s);
        }
		return links;
	}

	public static LinkBean[] getTopTenLinks(boolean byRating)
	{
	    Connection conn = null;
	    Statement s = null;
	    ResultSet rs = null;

        ArrayList<LinkBean> links = new ArrayList<LinkBean>();
        
	    String allLinks = "SELECT L.lid from Link L";
	    if (!byRating) {
	        allLinks += " ORDER BY DateTime DESC";
	    }
	    else {
	        allLinks += " LEFT OUTER JOIN" +
            "(SELECT lid, AVG(rating) AS rating FROM Vote " +
            "GROUP BY lid) V ON L.lid = V.lid " +
            "ORDER BY V.rating DESC";
	    }

	    try
	    {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
	        conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
	        s=conn.createStatement();
	        rs = s.executeQuery(allLinks);

	        int i = 0;
	        while(rs.next() && i < 10)
	        {
	            links.add(getLink(conn, rs.getInt(1)));
	            ++i;
	        }
	    }
	    catch (SQLException e) {
	        System.out.println("Error with db connection.");
	        e.printStackTrace();
	        return null;
	    } catch (Exception e) {
	        System.out.println("Unexpected error.");
	        e.printStackTrace();
	        return null;
	    } finally {
	        close(rs);
            close(s);
            close(conn);
	    }
	    return links.toArray(new LinkBean[links.size()]);
	}
	
	/**
	 * This method searches the database for links associated with
	 * the specified keywords, and returns a list of the links
	 * sorted by score.
	 * 
	 * @param keywords The array of keywords to look for
	 * @return
	 */
	public static LinkBean[] getLinksByKeyword(String[] keywords, boolean byRating) {

	    Connection conn = null;
	    PreparedStatement s = null;
	    ResultSet rs = null;

	    ArrayList<LinkBean> links = new ArrayList<LinkBean>();

	    StringBuffer sb = new StringBuffer("SELECT lk.lid, SUM(lk.num) as hits FROM Keywords k, LinkKeyword lk, Link l" +
	    		" WHERE lk.kwid = k.kwid AND l.lid = lk.lid AND k.keyword in (?");
	    for(int i = 1; i < keywords.length; i++) {
	        sb.append(",?");
	    }
	    sb.append(") GROUP BY lk.lid");
	    if (byRating)
	        sb.append(" ORDER BY hits DESC");
	    else
	        sb.append(" ORDER BY l.datetime DESC");
	    
	    try
	    {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
	        conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
	        ArrayList<Integer> sums = new ArrayList<Integer>();
	        s = conn.prepareStatement(sb.toString());
	        for(int i = 0; i < keywords.length; i++) {
	            s.setString(i+1, keywords[i]);
	        }
	        rs = s.executeQuery();

	        while(rs.next())
	        {
	            System.out.println("Got a result");
                links.add(getLink(conn, rs.getInt("lid")));
	            sums.add(rs.getInt("hits"));
	        }
	    }
	    catch (SQLException e) {
	        System.out.println("Error with db connection.");
	        e.printStackTrace();
	        return null;
	    } catch (Exception e) {
	        System.out.println("Unexpected error.");
	        e.printStackTrace();
	        return null;
	    } finally {
	        close(rs);
	        close(s);
	        close(conn);
        }
	    return links.toArray(new LinkBean[links.size()]);
    }
	
	private static void close(ResultSet rset) {
	    try {
	        if (rset != null) rset.close();
	    } catch (Exception e) {
	        System.out.println("The result set cannot be closed.");
	        e.printStackTrace();
	    }
	}
	
	private static void close(Statement stmt) {
        try {
            if (stmt != null) stmt.close();
        } catch (Exception e) {
            System.out.println("The statement cannot be closed.");
            e.printStackTrace();
        }
	}
	
	private static void close(Connection conn) {
	    try {
	        if (conn != null) conn.close();
	    } catch (Exception e) {
	        System.out.println("The database connection cannot be closed.");
	        e.printStackTrace();
	    }
	}

}