package edu.upenn.cis555.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import edu.upenn.cis555.db.User;
import edu.upenn.cis555.db.XPathDB;

import junit.framework.Assert;
import junit.framework.TestCase;

public class XPathDBTest extends TestCase {
    private static final String TEST_EMAIL = "test@test.com";
    XPathDB database;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        database = new XPathDB("TestDB");
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }

    // TODO: 2 tests for XPathDB
    @org.junit.Test public void testAddUserSucceeds() throws Exception {
        User user = new User(TEST_EMAIL, "test", "test", "test");
        database.addUser(user);
        User dbUser = database.getUser(user.getEmail());
        Assert.assertTrue(dbUser != null && user.toString().compareTo(dbUser.toString()) == 0);
    }

    @org.junit.Test public void testDeleteUserSucceeds() throws Exception {
        User user = database.getUser(TEST_EMAIL);
        Assert.assertTrue(user != null);
        database.removeUser(user);
        User deletedUser = database.getUser(user.getEmail());
        Assert.assertTrue(deletedUser == null);
    }
}
