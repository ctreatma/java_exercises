//Charles Treatman
//CS 311 Databases
//Fussers Frame Class

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FussersPanel extends JPanel {
    Connection conn;
    JPanel jpanel1, jpanel2;
    JLabel fnlabel;
    JTextField fname;
    JLabel lnlabel;
    JTextField lname;
    JScrollPane directory;
    JTextArea dirview;

    
    public FussersPanel(Connection c) {
	this.setName("Fussers");
	conn = c;

	jpanel1 = new JPanel();
	jpanel2 = new JPanel();
	fnlabel = new JLabel("First Name ");
	lnlabel = new JLabel("Last Name ");
	fname = new JTextField(20);
	lname = new JTextField(20);
	directory = new JScrollPane();
	directory.setPreferredSize(new Dimension(600, 400));
	dirview = new JTextArea();
	dirview.setSize(new Dimension(600, 400));
	directory.setViewportView(dirview);

	jpanel1.add(fnlabel);
	jpanel1.add(fname);
	jpanel2.add(lnlabel);
	jpanel2.add(lname);
	this.add(jpanel1);
	this.add(jpanel2);
	this.add(directory);
	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery( "SELECT lastname, firstname,email, phone FROM Student ORDER BY lastname DESC");
	    while(rs.next()) {
		dirview.insert(rs.getString("lastname") + "\t" +
			       rs.getString("firstname") + "\t" +
			       rs.getString("email") + "\t\t" +
			       rs.getString("phone") + "\n", 0);
	    }	  
	    dirview.insert("Last\tFirst\temail\t\t\tPhone\n", 0);
	}
	catch (SQLException e) {
	    dirview.insert(e.getMessage() + "\n", 0);
	}
   }
    
    public void submitAction() {
	dirview.setText("");
	try {
	    PreparedStatement stmt = conn.prepareStatement("SELECT lastname, firstname,email, phone FROM Student WHERE firstname LIKE ? AND lastname LIKE ? ORDER BY lastname DESC");
	    stmt.setString(1, fname.getText() + "%");
	    stmt.setString(2, lname.getText() + "%");
	    ResultSet rs = stmt.executeQuery();
	    if (rs.next()) {
		do {
		    dirview.insert(rs.getString("lastname") + "\t" +
				   rs.getString("firstname") + "\t" +
				   rs.getString("email") + "\t\t" +
				   rs.getString("phone") + "\n", 0);
		} while (rs.next());
		dirview.insert("Last\tFirst\temail\t\t\tPhone\n", 0);
	    }
	    else {
		dirview.insert("No records found for input values:\n" +
			       "\tFirst Name: " + fname.getText() + "\n" +
			       "\tLast Name: " + lname.getText(), 0);
	    }
	}
	catch (SQLException e) {
	    dirview.insert(e.getMessage() + "\n", 0);
	}
    }

    private Object makeObj(final String item)  {
	return new Object() { public String toString() { return item; } };
    }
}
