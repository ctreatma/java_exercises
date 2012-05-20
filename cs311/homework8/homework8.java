//Charles Treatman
//CS 311 Databases
//Homework 8 - JDBC

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class homework8 {
    public static void main(String[] args) {
	/*	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
	    e.printStackTrace();
	    }*/

	try {
	    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	    
	    HousingFrame houseNet = new HousingFrame(DriverManager.getConnection("jdbc:oracle:thin:@occs:1521:occsdb", "ctreatma", "ctreatma"));
	    
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = houseNet.getSize();
	    
	    if(frameSize.height > screenSize.height) {
		frameSize.height = screenSize.height;
	    }
	    if (frameSize.width > screenSize.width) {
		frameSize.width = screenSize.width;
	    }
	    houseNet.setLocation((screenSize.width - frameSize.width) / 2,
				 (screenSize.height - frameSize.height) / 2);
	    houseNet.setVisible(true);
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
